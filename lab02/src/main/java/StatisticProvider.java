import com.sun.jndi.toolkit.url.Uri;

import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by arasz on 12.04.2016.
 */
public class StatisticProvider implements AutoCloseable
{
    private static String _fileAppendix = ".properties";
    private Properties _properties;
    private File _propertiesFile;


    public StatisticProvider(String fileName)
    {
        _properties = new Properties();
        _propertiesFile = new File(fileName + _fileAppendix);

        if(_propertiesFile.isFile() && _propertiesFile.canRead())
        {
            try(FileInputStream inputStream = new FileInputStream(_propertiesFile))
            {
                _properties.load(inputStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void add(Uri pageUri)
    {
        String uri = pageUri.toString();

        if(_properties.containsKey(uri))
        {
            long uniqueEntries = (long) _properties.get(uri);
            uniqueEntries++;
            _properties.setProperty(uri, uniqueEntries + "");
        }
        else
        {
            _properties.setProperty(uri, "1");
        }
    }

    @Override
    public void close() throws Exception
    {
        try(OutputStream outputStream = new FileOutputStream(_propertiesFile))
        {
            _properties.store(outputStream, Calendar.getInstance().getTime().toString());
        }
    }
}
