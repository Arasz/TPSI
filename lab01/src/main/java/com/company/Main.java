package com.company;

import java.nio.file.FileSystems;

public class Main {

    public static void main(String[] args) {
	    try
        {
	    	
	    	
	    	if(args.length>0&&!args[0].equals(""))
	    	{
	    		FilesShareServer server = new FilesShareServer(8888, FileSystems.getDefault().getPath(args[0]));
	    		server.startServer();
	    		TPSIServer firstServer = new TPSIServer(8000);
	    		firstServer.startServer();
	    	}
	    	else
	    		System.out.println("Usage: @programName -@sharedPath");
        }
        catch (Exception ex)
        {
            System.err.println("WOW EXCEPTION\n"+ex.getMessage());
            ex.printStackTrace();
        }
    }
}
