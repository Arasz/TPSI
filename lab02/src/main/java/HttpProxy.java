import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.media.jfxmedia.track.Track;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Created by arasz on 08.04.2016.
 */
public class HttpProxy
{
    int _port;
    int _outPort;
    HttpServer _server;

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

            URI uri = httpExchange.getRequestURI();
            HttpURLConnection httpUrlConnection = (HttpURLConnection)new URL(uri.toURL().toString()).openConnection();
            httpUrlConnection.setRequestMethod(httpExchange.getRequestMethod());
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            //httpUrlConnection.setConnectTimeout(10000);
            //httpUrlConnection.setReadTimeout(10000);

            System.out.println("----> Opening connection with: "+ uri.toString());
            System.out.println("----> Request method: " + httpUrlConnection.getRequestMethod());

            for(Map.Entry<String, List<String>> header: httpExchange.getRequestHeaders().entrySet())
            {
                String values = join(header.getValue(),", ");
                httpUrlConnection.setRequestProperty(header.getKey(), values);
                System.out.println("--->"+header.getKey()+" : "+values);
            }

            try(InputStream inputStream = httpExchange.getRequestBody())
            {
                System.out.println("--> Request body size [bytes]: "+inputStream.available());
                if(inputStream.available()>0)
                {
                    try(OutputStream outputStream = httpUrlConnection.getOutputStream())
                    {
                        outputStream.write(IOUtils.readFully(inputStream, -1, true));
                    }
                }
            }


            httpUrlConnection.connect();

            byte[] bytes = new byte[0];
            try (InputStream inputStream =  httpUrlConnection.getInputStream())
            {
                bytes = IOUtils.readFully(inputStream, -1, true);
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
                System.err.print(httpUrlConnection.getRequestMethod());
            }

            int response = httpUrlConnection.getResponseCode();
            long contentLength= httpUrlConnection.getContentLengthLong();


            for(Map.Entry<String, List<String>> header: httpUrlConnection.getHeaderFields().entrySet())
            {
                if(header.getKey()!=null)
                {
                    String key = header.getKey();
                    if(key!=null)
                    {
                        if(key.equals("Transfer-Encoding"))
                            contentLength = 0;

                        String values = join(header.getValue(), ", ");
                        httpExchange.getResponseHeaders().set(key, values.substring(0, values.length()-2));
                        System.out.println("->"+header.getKey()+" : "+values);
                    }
                }
            }


            //httpExchange.setAttribute("Content-Type", httpUrlConnection.getContentType());
            httpExchange.sendResponseHeaders(response, contentLength);
            try(OutputStream httpOutputStream = httpExchange.getResponseBody())
            {
                httpOutputStream.write(bytes);
            }
            System.out.println(response + "; "+contentLength);
            httpUrlConnection.disconnect();
        }
    }


    private String join(Collection<String> collection, String joint)
    {
        StringBuilder values = new StringBuilder();
        for (String val : collection)
        {
            values.append(val);
            values.append(joint);
        }
        return values.toString();
    }
}
