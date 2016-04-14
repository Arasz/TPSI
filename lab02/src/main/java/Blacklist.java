import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by arasz on 14.04.2016.
 */
public class Blacklist
{
    private File _blacklistFile = new File("blacklist.blck");
    private List<String> _blacklist = new ArrayList<>();

    public boolean isBlacklisted(String url)
    {
        return _blacklist.contains(url);
    }

    public void add(String url)
    {
        _blacklist.add(url);
    }

    public void remove(String url)
    {
        _blacklist.remove(url);
    }

    public void loadFromFile() throws IOException
    {
        if(!_blacklistFile.isFile())
            _blacklistFile.createNewFile();

        try(BufferedReader reader = new BufferedReader( new FileReader(_blacklistFile)))
        {
             reader.lines()
                     .forEach(line->{if(!_blacklist.contains(line)) _blacklist.add(line);});
        }
    }

    public void saveToFile() throws IOException
    {
        if(!(_blacklistFile.isFile()))
            _blacklistFile.createNewFile();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(_blacklistFile)))
        {
            _blacklist.forEach((url) -> {
                try
                {
                    writer.write(url);
                    writer.newLine();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }
}
