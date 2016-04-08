package com.company;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Authenticator.Result;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

/**
 * Created by Rafal on 04.03.2016.
 */
public class TPSIServer
{

    private HttpServer _server;
    private int _port = 80;
    

    public TPSIServer(int port) throws Exception
    {
        try
        {
            _port = port;
            _server = HttpServer.create( new InetSocketAddress(port), 2);
            _server.createContext("/", new RootHandler());
            _server.createContext("/echo/", new EchoHandler());
            _server.createContext("/redirectPage/", new RedirectPageHandler());
            _server.createContext("/redirect302/", new RedirectHandler(302));
            _server.createContext("/redirect303/", new RedirectHandler(303));
            _server.createContext("/redirect307/", new RedirectHandler(307));
            _server.createContext("/cookies/", new CookieHandler("/"));
            _server.createContext("/auth/", new AuthHandler());
            _server.createContext("/auth2/", new Auth2Handler("safePages"));
        }
        catch (IOException ioex)
        {
            System.err.println("IO exception thrown when creating server and context \nMessage:\n"+ioex.getMessage());
            throw ioex;
        }
        catch (IllegalArgumentException argex)
        {
            System.err.println("Argument exception thrown when creating server and context \nMessage:\n"+argex.getMessage());
            throw argex;
        }
    }

    public void startServer()
    {
        System.out.println("Starting server on port: " +  _port);
        _server.start();
    }

    static class RootHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            byte[] byteResponse ;

            try
            {
                byteResponse = Files.readAllBytes(FileSystems.getDefault().getPath("index.html"));
            }
            catch (IOException ex)
            {
                System.err.println("Exception thrown when reading file. \nMessage:\n"+ex.getMessage());
                throw ex;
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, byteResponse.length);

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(byteResponse);
            outputStream.close();
        }
    }
    static class  EchoHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException
        {
            byte[] byteResponse;

            RequestLogger logger = new  RequestLogger(httpExchange);

            byteResponse = logger.logRequestHeaders(true).getBytes();
            
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            httpExchange.sendResponseHeaders(200, byteResponse.length);

           try(OutputStream outputStream = httpExchange.getResponseBody()) {
               outputStream.write(byteResponse);
           }
        }
    }
    
    static class RedirectPageHandler implements HttpHandler
    {
        @SuppressWarnings("restriction")
		public void handle(HttpExchange httpExchange) throws IOException
        {
            byte[] byteResponse;
            
            byteResponse = Files.readAllBytes(FileSystems.getDefault().getPath("redirectionPage.html"));


            httpExchange.getResponseHeaders().set("Content-Type","text/html");
            httpExchange.sendResponseHeaders(200, byteResponse.length);

            try(OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(byteResponse);
            }
        }
    }

    static class RedirectHandler implements HttpHandler
    {
    	private int _redirectCode;
    	
    	public RedirectHandler(int redirectCode) 
    	{
    		if(redirectCode>300 && redirectCode< 400)
    			_redirectCode = redirectCode;
    		else
    			_redirectCode = 302;
		}
    	
        @SuppressWarnings("restriction")
		public void handle(HttpExchange httpExchange) throws IOException
        {
            byte[] byteResponse;

            RequestLogger logger = new  RequestLogger(httpExchange);

            String redirectLocationUri = "/echo/";
            byteResponse = ("Redirect from "+ httpExchange.getRequestURI() + "to " + redirectLocationUri).getBytes();


            httpExchange.getResponseHeaders().set("Content-Type","text/plain");
            httpExchange.getResponseHeaders().set("Location",redirectLocationUri);
            httpExchange.sendResponseHeaders(_redirectCode, byteResponse.length);

            try(OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(byteResponse);
            }
            logger.logFullRequest(true);
        }
    }
    
    static class CookieHandler implements HttpHandler
    {
    	private String _cookiePath;
    	
    	public CookieHandler(String cookiePath)
    	{
    		_cookiePath = cookiePath;
    	}
    	
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
            byte[] byteResponse;

            RequestLogger logger = new  RequestLogger(httpExchange);

            int cookie =  httpExchange.hashCode() ^ (int)(100*Math.random());
            String response = "Cookie sent";
            byteResponse = response.getBytes();
                   
            httpExchange.getResponseHeaders().set("Content-Type","text/plain");
            
            String cookieHeader = "wowCookie="+cookie+"; expires=Tuesday, 05-Nov-2022 08:30:09 GMT; path="+_cookiePath+"; ";
            
            httpExchange.getResponseHeaders().set("Set-Cookie",cookieHeader);
            
            httpExchange.getResponseHeaders().set("Connection", "keep-alive");
            httpExchange.sendResponseHeaders(200, byteResponse.length);

            try(OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(byteResponse);
            }
			
		}
    	
    }
    
    static class AuthHandler implements HttpHandler
    {
        static private String _userName = "Pesel";
        static private String _passwd = "wow";

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			
            Headers headers = httpExchange.getRequestHeaders();
            String headerName = "Authorization";
            
            if(headers.containsKey(headerName))
            {
            	 String userCred = headers.get(headerName).get(0).split(" ")[1];
            	 String decodedUserCred = new String(Base64.getDecoder().decode(userCred), StandardCharsets.UTF_8);
            	 String userName = decodedUserCred.split(":")[0];
            	 String passwd = decodedUserCred.split(":")[1];
            	 
            	 if( userName.equals(_userName) && passwd.equals(_passwd) )
            	 {
         			
                     byte[] byteResponse;
                     
                     RequestLogger logger = new  RequestLogger(httpExchange);
                     
                     String response = "You are logged in";
                     
                     byteResponse = response.getBytes();
                     
                     httpExchange.getResponseHeaders().set("Content-Type","text/plain");
                     httpExchange.getResponseHeaders().set("WWW-Authenticate","Basic realm=safePages");             
                     httpExchange.getResponseHeaders().set("Connection", "keep-alive");
                     httpExchange.sendResponseHeaders(200, byteResponse.length);

                     try(OutputStream outputStream = httpExchange.getResponseBody()) {
                         outputStream.write(byteResponse);
                     }
                     
                     return;
            	 }
            }
			
            byte[] byteResponse;
            
            String response = "Restricted page!";
            
            byteResponse = response.getBytes();
            
            httpExchange.getResponseHeaders().set("Content-Type","text/plain");
            
            httpExchange.getResponseHeaders().set("WWW-Authenticate","Basic realm=Podaj dane");
            
            httpExchange.sendResponseHeaders(401, byteResponse.length);

            try(OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(byteResponse);
            }
		}
    	
    }
    
    static class Auth2Handler implements HttpHandler
    {
        private String _userName = "Pesel";
        private String _passwd = "wow";
        
        private String _relam;
        private Authenticator _authenticator;
        
        public Auth2Handler(String relam) {
			_relam = relam;
			_authenticator = new BasicAuthenticator(_relam) 
			{	
				@Override
				public boolean checkCredentials(String username, String password) {
					return (username.equals(_userName))&&(password.equals(_passwd));
				}
			};
		}

		@Override
		public void handle(HttpExchange httpExchange) throws IOException 
		{	
			String responseBody;
			byte[] byteResponse;
			
			responseBody = "Acces restricted!";
			byteResponse = responseBody.getBytes();
			
			httpExchange.getHttpContext().setAuthenticator(_authenticator);
			
			Authenticator.Success result = (Authenticator.Success)_authenticator.authenticate(httpExchange);
			
			if(result != null)
			{
				responseBody = "Succes!";
				byteResponse = responseBody.getBytes();
				httpExchange.sendResponseHeaders(200, byteResponse.length);
			}
			
            try(OutputStream outputStream = httpExchange.getResponseBody()) {
				outputStream.write(byteResponse );
            }
			
		}
    	
    }
}
