import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by arasz on 08.04.2016.
 */
public class HttpProxy
{
    int _port;
    int _outPort;
    HttpServer _server;
    java.net.URLConnection _httpUrlConnection;

    public HttpProxy(int port, int outputPort) throws IOException
    {
        _port = port;
        _outPort = outputPort;
        _server = HttpServer.create(new InetSocketAddress(_port), 2);
        _server.createContext("/", new RootHandler());
    }

    public void startProxy()
    {
        System.out.println("Proxy started on port: "+_port);
        _server.start();
    }

    class RootHandler implements HttpHandler
    {

        public void handle(HttpExchange httpExchange) throws IOException
        {
            System.out.println("Inside handler");
            URI uri = httpExchange.getRequestURI();
            _httpUrlConnection = new URL(uri.toURL().toString()).openConnection();


            for(Map.Entry<String, List<String>> header: httpExchange.getRequestHeaders().entrySet())
            {
                _httpUrlConnection.addRequestProperty(header.getKey(), header.getValue().get(0));
            }
            _httpUrlConnection.connect();


            Object conntent = _httpUrlConnection.getContent();
            System.out.println(conntent.getClass().toString());
            System.out.println(_httpUrlConnection.getContentType().toString());

            byte[] bytes;

            try(InputStream inputStream = _httpUrlConnection.getInputStream())
            {
                int available = inputStream.available();
                bytes = new byte[available];
                while(available!=0)
                {
                    inputStream.read(bytes);
                    available = inputStream.available();
                }
            }


            for(Map.Entry<String, List<String>> header: _httpUrlConnection.getHeaderFields().entrySet())
            {
                System.out.println("Key: "+header.getKey()+" Value: "+header.getValue());
                if(header.getKey()!=null)
                    httpExchange.getResponseHeaders().add(header.getKey(), header.getValue().get(0));
            }

            httpExchange.sendResponseHeaders(200,-1);
            httpExchange.getResponseBody().write(bytes);
            try(OutputStream httpOutputStream = httpExchange.getResponseBody())
            {
                httpOutputStream.write(bytes);
            }
        }
    }

}
