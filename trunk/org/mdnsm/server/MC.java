package org.mdnsm.server;

import java.net.*;
import java.io.*;

/**
 * This class has an open socket on port 5353 to listen for Multicast
 * DNS packets.  It needs lots of work...
 *
 * @author Steve Beaty
 * @version $Id: MC.java,v 1.3 2005/11/04 00:10:51 drb80 Exp $
 */

public class MC extends Thread
{
    private static MyLogger logger = new MyLogger();

    public void run()
    {
        MulticastSocket server = null;

        try { server = new MulticastSocket(5353); }
        catch ( IOException e ) {}

        InetAddress group = null;
        try { group = InetAddress.getByName ("224.0.0.251"); }
        catch (UnknownHostException e) {}

        try { server.joinGroup (group); }
        catch ( IOException e ) {}

        while (true)
        {
            // make a new buffer for each query so this is thread-safe.
            // remember: arrays are passed by reference...

            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try { server.receive(packet); }
            catch ( IOException e ) {}

            logger.fine ("Packet length = " + packet.getLength());
            logger.fine ("From address = " + packet.getAddress());
            logger.fine ("From port = " + packet.getPort());

            Query q = new Query (packet.getData());
            logger.fine (q.toString());
        }
	/*
	** unreachable
        server.leaveGroup (group);
        server.close ();
	*/
    }
}
