import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by arasz on 12.04.2016.
 */
public class StatisticProvider implements AutoCloseable
{
    private static String _fileAppendix = ".properties";
    private Properties _properties;
    private File _propertiesFile;


    public StatisticProvider()
    {
        _properties = new Properties();
    }

    public StatisticProvider(String fileName) throws IOException
    {
        this();
        openFromFile(fileName);
    }

    public void openFromFile(String fileName) throws IOException
    {
        _propertiesFile = new File(fileName + _fileAppendix);

        if(!_propertiesFile.exists())
            _propertiesFile.createNewFile();

        if(_propertiesFile.isFile())
        {
            try(FileInputStream inputStream = new FileInputStream(_propertiesFile))
            {
                _properties.load(inputStream);
            }
        }
    }

    public void add(String pageUri)
    {
        add(pageUri, 0);
    }

    public void add(String pageUri, long dataInBytes)
    {
        if(_properties.containsKey(pageUri))
        {
           String[] values = ((String)_properties.get(pageUri)).split(",");
            long uniqueEntries = Long.parseLong(values[0].trim());
            long dataSent = Long.parseLong(values[1].trim());
            uniqueEntries++;
            dataSent += dataInBytes;
            _properties.setProperty(pageUri, uniqueEntries + ", "+ dataSent);
        }
        else
        {
            _properties.setProperty(pageUri, "1" + ", "+ dataInBytes);
        }
    }

    public String getReadableProperties()
    {
        StringBuilder builder = new StringBuilder("Properties: \n");
        Set<Map.Entry<Object,Object>> entrySet = _properties.entrySet();
        for(Map.Entry<Object, Object> property : entrySet)
            builder.append("[ "+ property.getKey() + " : "+property.getValue()+" ]\n");
        return builder.toString();
    }

    @Override
    public void close() throws Exception
    {
        if(_propertiesFile == null)
            return;

        try(OutputStream outputStream = new FileOutputStream(_propertiesFile))
        {
            _properties.store(outputStream, "Statistics file. \nData format: Unique entries, data sent in bytes");
        }
    }
}
