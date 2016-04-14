import java.io.IOException;

/**
 * Created by arasz on 08.04.2016.
 */
public class MainClass
{
    public static void main(String[] args) throws Exception
    {
        final HttpProxy proxy = new HttpProxy(8080, 80);
        proxy.startProxy();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    proxy.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }));
    }
}
