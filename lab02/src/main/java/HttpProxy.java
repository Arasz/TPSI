import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import com.sun.xml.internal.ws.util.QNameMap;

import java.io.IOException;
import java.net.HttpURLConnection;
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
            Headers headers = httpExchange.getRequestHeaders();
            Set<?> entrySet = headers.entrySet();

            for (Iterator<?> it = entrySet.iterator(); it.hasNext();)
            {
                Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>)it.next();
                _httpUrlConnection.setRequestProperty(entry.getKey(), entry.getValue().get(0));
            }
            _httpUrlConnection.setRequestProperty("WOW", "W");
            _httpUrlConnection.connect();

        }
    }

}
