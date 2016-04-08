package com.company;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;


import com.sun.net.httpserver.*;

public class FilesShareServer 
{
	private int _port;
	private HttpServer _server;
	
	public FilesShareServer(int port, Path sharePath) throws Exception
	{
		if(port<0 && port>65535)
			throw new Exception("Wrong port number.");
		_port = port;
		_server = HttpServer.create(new InetSocketAddress(_port), 2);
		_server.createContext("/", new FileShareHandler(sharePath));		
	}
	
	public void startServer()
	{
        System.out.println("Starting server on port: " +  _port);
		_server.start();
	}
}
