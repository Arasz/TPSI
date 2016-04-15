
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.logging.*;

/**
 * Created by arasz on 08.04.2016.
 */
public class HttpProxy implements AutoCloseable
{
    Logger _logger;
    int _port;
    int _outPort;
    HttpServer _server;
    StatisticProvider _statistics;
    Blacklist _blacklist;


    public HttpProxy(int port, int outputPort) throws IOException
    {
        _blacklist = new Blacklist();
        _blacklist.loadFromFile();
        _statistics = new StatisticProvider();
        _port = port;
        _outPort = outputPort;
        _server = HttpServer.create(new InetSocketAddress(_port), 2);
        _server.createContext("/", new RootHandler());
        _logger = Logger.getLogger("httpProxyLogger");
        _logger.setLevel(Level.OFF);
        _logger.addHandler(new FileHandler("%t/Java/HttpProxy/proxy%g.log",1000000, 5, true));
        //_logger.addHandler(new ConsoleHandler());
    }

    public void startProxy()
    {
        System.out.println("Proxy started on port: "+_port);
        _server.start();
    }

    @Override
    public void close() throws Exception
    {
        _statistics.close();
        _server.stop(0);
    }

    class RootHandler implements HttpHandler
    {

        public void handle(HttpExchange httpExchange) throws IOException
        {

            try
            {
                URI uri = httpExchange.getRequestURI();
                if(_blacklist.isBlacklisted(uri.toString()))
                {
                    httpExchange.sendResponseHeaders(403, -1);
                    return;
                }
                HttpURLConnection httpUrlConnection = (HttpURLConnection)new URL(uri.toURL().toString()).openConnection();
                httpUrlConnection.setRequestMethod(httpExchange.getRequestMethod());
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                //httpUrlConnection.setConnectTimeout(10000);
                //httpUrlConnection.setReadTimeout(10000);

                _logger.info("Opening connection with: "+ uri.toString());
                _logger.info("Request method: " + httpUrlConnection.getRequestMethod());

                for(Map.Entry<String, List<String>> header: httpExchange.getRequestHeaders().entrySet())
                {
                    String values = join(header.getValue(),", ");
                    httpUrlConnection.setRequestProperty(header.getKey(), values);
                    _logger.info("Header: ["+header.getKey()+" : "+values+"]");
                }

                try(InputStream inputStream = httpExchange.getRequestBody())
                {
                    _logger.info("Request body size [bytes]: "+inputStream.available());
                    if(inputStream.available()>0)
                    {
                        try(OutputStream outputStream = httpUrlConnection.getOutputStream())
                        {
                            outputStream.write(IOUtils.readFully(inputStream, -1, true));
                        }
                    }
                }


                httpUrlConnection.connect();

                int response = httpUrlConnection.getResponseCode();
                long contentLength= httpUrlConnection.getContentLengthLong();

                byte[] bytes = new byte[0];

                if(response >= 400 && response< 600)
                {

                    try(InputStream errorStream = httpUrlConnection.getErrorStream())
                    {
                        bytes = IOUtils.readFully(errorStream, -1, true);
                    }
                    catch (Exception ex)
                    {
                        _logger.log(Level.SEVERE, ex.getMessage());
                        _logger.log(Level.SEVERE, httpUrlConnection.getRequestMethod());
                    }
                }
                else
                {
                    try (InputStream inputStream = httpUrlConnection.getInputStream())
                    {
                        bytes = IOUtils.readFully(inputStream, -1, true);
                    }
                    catch (Exception ex)
                    {
                        _logger.log(Level.SEVERE, ex.getMessage());
                        _logger.log(Level.SEVERE, httpUrlConnection.getRequestMethod());
                    }
                }

                if(contentLength!=bytes.length)
                {
                    System.out.println("-------> "+uri.toString()+", "+response+", "+httpUrlConnection.getRequestMethod());
                    System.out.println("-------> Bytes.length: "+bytes.length+", Cont-Len: "+contentLength);
                }

                for(Map.Entry<String, List<String>> header: httpUrlConnection.getHeaderFields().entrySet())
                {
                    if(header.getKey()!=null)
                    {
                        String key = header.getKey();
                        if(key!=null)
                        {
                            if(key.equals("Transfer-Encoding"))
                            {
                                contentLength = 0;
                                System.out.println("-------> Transfer encoding exists");
                            }

                            String values = join(header.getValue(), ", ");
                            httpExchange.getResponseHeaders().set(key, values.substring(0, values.length()-2));
                            _logger.info("Header: ["+header.getKey()+" : "+values+"]");
                        }
                    }
                }

                _statistics.openFromFile(this.getClass().getName()+_port);
                _statistics.add(uri.getHost(), bytes.length);

                //httpExchange.setAttribute("Content-Type", httpUrlConnection.getContentType());
                httpExchange.sendResponseHeaders(response, contentLength);
                try(OutputStream httpOutputStream = httpExchange.getResponseBody())
                {
                    httpOutputStream.write(bytes);
                }
                catch (Exception ex)
                {
                    _logger.log(Level.SEVERE, ex.getMessage());
                }
                _logger.info("Response code: "+response + "; Content-Length: "+contentLength);
                httpUrlConnection.disconnect();
            }
            catch (Exception ex)
            {
                System.err.println(ex.toString());
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    _statistics.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
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
