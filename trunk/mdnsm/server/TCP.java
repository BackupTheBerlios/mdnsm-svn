package org.mdnsm.server;

import java.net.*;
import java.io.*;

/**
 * TODO: niet gebruiken!
 * 
 * Listen to a TCP port for DNS requests...
 * @author Beaty
 * @version $Id: TCP.java,v 1.4 2005/11/04 00:10:51 drb80 Exp $
 */

// nslookup -port=5353 -vc www.mpcs.org localhost

public class TCP extends Thread
{   
    private Semaphore s = new Semaphore (ServiceServer.getThreads());
    private static MyLogger logger = new MyLogger();

    /**
     * listen on the given port and spawn a thread for each request.
     */    
    public void run()
    {
        ServerSocket server = null;


        try
	{
	    server = new ServerSocket (ServiceServer.getPort());
	}
	catch (IOException e)
	{
	    logger.throwing (e);
	}

        while (true)
        {
            Socket socket = null;

	    s.P();

            try
            {
                socket = server.accept();
                logger.finest ("From address = " + socket.getInetAddress());
                logger.finest ("From port = " + socket.getPort());

		new TCPThread (socket).start();
            }
	    // catch everything so we can V
            catch (Throwable t)
	    {
	        logger.throwing (t);
	    }

	    s.V();
        }
    }
}
