package org.mdnsm.server;

import java.net.*;
import java.io.*;

/**
 * Receive request packets from a UDP port and create a thread for each
 *
 * @author Steve Beaty
 * @version $Id: UDP.java,v 1.4 2005/11/04 00:10:51 drb80 Exp $
 */

public class UDP extends Thread
{
    // one semaphore for all the threads
    private static Semaphore s = new Semaphore (ServiceServer.getThreads());

    // no reason to call for more than one logger
    private static MyLogger logger = new MyLogger();

    public void run()
    {
        DatagramSocket server = null;
        
        try
	{
	    server = new DatagramSocket(ServiceServer.getPort());
	}
        catch ( SocketException e )
        {
            logger.throwing (e);
            return;
        }

        while (true)
        {
            // make a new buffer for each query so this is thread-safe.
            // remember: arrays are passed by reference...

            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

	    s.P();

            try
	    {
	        server.receive(packet);
	    }
	    // catch everything so we can V
            catch (Throwable t)
	    {
	        logger.throwing (t);
	    }
            
            logger.finest ("Packet length = " + packet.getLength());
            logger.finest ("From address = " + packet.getAddress());
            logger.finest ("From port = " + packet.getPort());

	    s.V();

            new UDPThread (server, packet).start();
        }
    }
}
