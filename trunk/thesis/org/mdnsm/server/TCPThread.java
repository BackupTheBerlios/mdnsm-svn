package org.mdnsm.server;

import java.net.*;
import java.io.*;

/**
 * TODO: niet gebruiken!
 * 
 * The threads for getting and responding to TCP requests
 * 
 * @author Steve Beaty
 */
public class TCPThread extends Thread {
	// Java can't synchronize around base types. sigh.
	private Socket socket;

	private static int count = 0;

	private static MyLogger logger = new MyLogger();

	/**
	 * @param socket
	 *            the socket to talk to
	 */
	public TCPThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		InputStream is = null;
		OutputStream os = null;

		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();

			// in TCP, the first two bytes signify the length of the request
			byte buffer[] = new byte[2];

			is.read(buffer, 0, 2);

			byte query[] = new byte[Utils.addThem(buffer[0], buffer[1])];

			is.read(query);

			Query q = new Query(query);
			Response r = new Response(q, false);
			byte b[] = r.makeResponse(q);

			int count = b.length;
			buffer[0] = Utils.getByte(count, 2);
			buffer[1] = Utils.getByte(count, 1);

			os.write(Utils.combine(buffer, b));

			is.close();
			os.close();
			socket.close();
		} catch (Throwable t) {
			logger.throwing(t);
		}
	}
}
