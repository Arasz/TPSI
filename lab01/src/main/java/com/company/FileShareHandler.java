package com.company;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.text.html.HTMLDocument;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sun.awt.CharsetString;

public class FileShareHandler implements HttpHandler{

	private Path _sharePath;
	private byte[] _output;
	private String _fileType;
	
	public FileShareHandler(Path sharePath)
	{
		_sharePath = sharePath;
		
		//processFile(_sharePath.toFile());
	}

	private void processFile(File file, String uriPath) 
	{
		StringBuilder output = new StringBuilder();
		
		if(file.isDirectory())
		{
			_fileType = "text/html";
			
			output.append("<html>");
			output.append("<head>");
			output.append("<title>"+file.getAbsolutePath()+"</title>");
			output.append("</head>");
			output.append("<body>");
			output.append("<h4>"+(_sharePath.getNameCount()!=0?file.getName():file.getAbsolutePath())+"</h4>");
			output.append("<ul>");
			
			File[] files = file.listFiles();
			if(files != null)
			{
				for(File f : files)
				{
					//System.out.println("\t"+f.getName());
					output.append("<li><a href="+(uriPath+f.getName()).replace(" ", "%20")+">"+f.getName()+"</a></li>");
				}
			}
			
			output.append("</ul>");
			output.append("</body>");
			output.append("</html>");
			_output = output.toString().getBytes(StandardCharsets.UTF_8);
		}
		else if(file.isFile())
		{
			try 
			{
				if (file.canRead()) 
				{
					_fileType = Files.probeContentType(file.toPath());
					if (_fileType == null)
						_fileType = "text/plain";
					
					_output = Files.readAllBytes(FileSystems.getDefault().getPath(_sharePath.toAbsolutePath()+uriPath));
				}
			} 
			catch (IOException ex) {
				System.err.println("Read input file exception.\nFile type: "+_fileType+"\n" + ex.getMessage());
				//System.exit(-1);

			}
		}
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException 
	{
		int responseCode = 200;
		
		URI requestURI = httpExchange.getRequestURI();
		String uriString = requestURI.getPath().replace("%20", " ");
		
		File file = new File(_sharePath + uriString);
		

		
		
		if(file.exists())
		{
			processFile(file, (uriString.length()>1?uriString+"/":uriString));
		}
		
		if(_output == null)
		{
			responseCode = 404;
			_output = "<html><head><title>404 NOT FOUND</title></head><body><h1>404 NOT FOUND</h1></body></html>"
					.getBytes(StandardCharsets.UTF_8);
		}
		
		// sanitize path
		String canonicalPath = file.getCanonicalPath();

		if((_sharePath.toString().length()>canonicalPath.length())&&
				_sharePath.toString().startsWith(canonicalPath))
		{
			responseCode = 403;
			_output = "<html><head><title>FORBIDDEN</title></head><body><h1>FORBIDDEN</h1></body></html>"
					.getBytes(StandardCharsets.UTF_8);
		}
		
		httpExchange.getResponseHeaders().set("Content-Type", _fileType+"; charset=utf-8");
		httpExchange.sendResponseHeaders(responseCode, _output.length);
		
		try(OutputStream responseBody = httpExchange.getResponseBody())
		{
			responseBody.write(_output);
		}
	}
}
