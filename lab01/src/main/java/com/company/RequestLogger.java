package com.company;

import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.internal.ws.Closeable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rafal on 04.03.2016.
 */
public class RequestLogger {

    private  HttpExchange _exchange;

    private Map<String, Object> _jsonWriterOptions;

    public RequestLogger(HttpExchange exchange)
    {
        _exchange = exchange;

        _jsonWriterOptions = new HashMap();
        _jsonWriterOptions.put(JsonWriter.PRETTY_PRINT, true);
    }

    public String logRequestMethod(boolean logToStdOut)
    {
        if(logToStdOut)
        {
            System.out.println("Request method: \n"+_exchange.getRequestMethod());
        }
        return _exchange.getRequestMethod();
    }

    public String logRequestBody(boolean logToStdOut)
    {
        String jsonRepresentation = JsonWriter.objectToJson(_exchange.getRequestBody(), _jsonWriterOptions);

        if(logToStdOut)
        {
            System.out.println("Request headers: \n"+ jsonRepresentation);
        }

        return jsonRepresentation;
    }

    public String logRequestHeaders(boolean logToStdOut)
    {
        String jsonRepresentation = JsonWriter.objectToJson(_exchange.getRequestHeaders(), _jsonWriterOptions);

        if(logToStdOut)
        {
            System.out.println("Request headers: \n"+ jsonRepresentation);
        }

        return jsonRepresentation;
    }

    public String  logFullRequest(boolean logToStdOut)
    {
        return logRequestMethod(logToStdOut) + logRequestHeaders(logToStdOut);
    }
}
