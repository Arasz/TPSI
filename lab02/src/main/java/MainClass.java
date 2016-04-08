import java.io.IOException;

/**
 * Created by arasz on 08.04.2016.
 */
public class MainClass
{
    public static void main(String[] args) throws IOException
    {
        HttpProxy proxy = new HttpProxy(8080, 80);
        proxy.startProxy();
    }
}
