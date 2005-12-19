package org.mdnsm.server;

import java.net.*;
import java.io.*;

/**
 * The threads for responding to UDP requests
 *
 * @author Steve Beaty
 * @version $Id: UDPThread.java,v 1.5 2005/11/04 00:10:51 drb80 Exp $
 */
public class UDPThread extends Thread
{
    private static MyLogger logger = new MyLogger();

    /*
    ** is responding through the original socket thread safe, or should we
    ** create another one here?
    */
    private DatagramSocket socket;
    private DatagramPacket packet;

    /**
     * @param socket	the socket to respond through
     * @param packet	the query
     */
    public UDPThread (DatagramSocket socket, DatagramPacket packet)
    {
        this.socket = socket;
	this.packet = packet;
    }

    /**
     * make the response
     */
    public void run()
    {
	// sleep for 10 secs for testing of threads
	// System.out.println ("before sleep");
	// try { Thread.sleep (10000); }
	// catch (InterruptedException e) { e.printStackTrace(); }
	// System.out.println ("after sleep");

	byte buf[] = new byte[packet.getLength()];
	System.arraycopy (packet.getData(), 0, buf, 0, buf.length);

	Query q = new Query (buf);
        logger.finest (q.toString());
	Response r = new Response (q);
        logger.finest (r.toString());
	byte b[] = r.makeResponse(q);

	if (b != null)
	{
	    DatagramPacket reply = new DatagramPacket(b, b.length,
		packet.getAddress(), packet.getPort());

	    try
	    {
	        socket.send(reply);
	    }
	    catch ( IOException e )
	    {
		logger.throwing (e);
	    }
	}
    }
}
