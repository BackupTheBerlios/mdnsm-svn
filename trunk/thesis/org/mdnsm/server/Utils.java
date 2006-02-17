package org.mdnsm.server;

import java.util.*;

/**
 * Common methods used throughout
 *
 * @author Steve Beaty
 * @version $Id: Utils.java,v 1.6 2005/11/04 00:10:51 drb80 Exp $
 */

public class Utils {
	
	// TODO: juiste waarden
	
	// Ports on which the server daemons communicate
	public static final int DAEMON_PORT = 1337;
	// Port on which servers contact clients
	public static final int CLIENT_COM = 1338;
	// Port on which clients contact servers
	public static final int SERVER_COM = 1339;
	// Port for interserver communication
	public static final int SERVER_SERVER_COMM = 1340;
	
	public static final String SERVER_MULTICAST_GROUP = "224.0.0.251";
	
	public static final int SERVER_CHECK_INTERVAL = 5000;
	
    /** The request/respose numbers */
    public static final int A		= 1;
    public static final int NS		= 2;
    public static final int CNAME	= 5;
    public static final int SOA		= 6;
    public static final int PTR		= 12;
    public static final int HINFO	= 13;
    public static final int MX		= 15;
    public static final int TXT		= 16;
    public static final int AAAA	= 28;
    
    /**
     * an Assert that is independent of version and always executes...
     *
     * @param assertion	what to test
     */
    public static void Assert (boolean assertion)
    {
        if (!assertion) throw new AssertionError();
    }

    /**
     * an Assert that is independent of version and always executes, and
     * throws a particular Exception
     *
     * @param assertion	what to test
     * @param e what exception to throw
     */
    public static void AssertAndThrow (boolean assertion, Exception e)
	throws Exception
    {
        if (!assertion)
        {
            throw e;
        }
    }

    /**
     * Returns a formatted nicely byte array
     *
     * @param buffer	what to format
     * @return 		a String with eight bytes per line
     */
    public static String toString (byte buffer[])
    {
        String s = "";

        for (int i = 0; i < buffer.length; i++)
        {
            if (i % 8 == 0)
            {
                if (i != 0) s += "\n";
                s += i + ": ";
            }

            s += ((int) (buffer[i] & 0xFF) + " ");
        }

        return (s);
    }

    /**
     * Get one byte from an integer
     * @param from	the integer to retrive from
     * @param which	which byte (1 = lowest, 4 = highest)
     * @return 		the requested byte
     */
    public static byte getByte (int from, int which)
    {
        Assert (which >= 1 && which <= 4);
        int shift = (which - 1) * 8;
        return ((byte) ((from & (0xff << shift)) >> shift));
    }

    /**
     * Get all four bytes from an integer
     * @param from	the integer to retrive from
     * @return 		the requested byte array
     */
    public static byte[] getBytes (int from)
    {
        byte ret[] = new byte[4];
        ret[0] = getByte (from, 4);
        ret[1] = getByte (from, 3);
        ret[2] = getByte (from, 2);
        ret[3] = getByte (from, 1);
        return (ret);
    }

    /**
     * Get one nybble from an integer
     * @param from	the integer to retrive from
     * @param which	which nybble (1 = lowest, 8 = highest)
     * @return 		the requested nybble
     */
    public static byte getNybble (int from, int which)
    {
        Assert (which >= 1 && which <= 8);
        int shift = (which - 1) * 4;
        return ((byte) ((from & (0x0f << shift)) >> shift));
    }

    /**
     * Performs an unsigned addition of two bytes.
     *
     * @param a	one of the bytes to add
     * @param b	the other one
     * @return	the sum
     */
    public static int addThem (byte a, byte b)
    {
        return ((a & 0x000000ff) << 8) + (b & 0x000000ff);
    }

    /**
     * Convert the address string into a byte array
     *
     * @param s	the dotted IPV4 address
     * @return	4 byte array of the address
     */
    public static byte[] IPV4 (String s)
    {
        String a[] = s.split ("\\.");
        byte r[] = new byte[4];

        for (int i = 0; i < a.length; i++)
        {
            r[i] = (byte) Integer.parseInt (a[i]);
        }

        return (r);
    }

    /**
     * Converts a String a <character-string> -- "this" into 4this
     * @param s	the original String
     * @return	the converted form in bytes
     */
    public static byte[] toCS (String s)
    {
        byte a[] = new byte[1];
        a[0] = getByte (s.length(), 1);
        return (combine (a, s.getBytes()));
    }

    /**
     * Converts a domain string into the form needed for a response --
     * given a domain "www.foobar.org" it is converted to the 3www6foobar3org0
     * @param s	the original String
     * @return	the converted form in bytes
     */
    public static byte[] convertString (String s)
    {
        // there's an extra byte both before and after
        byte[] a = new byte[s.length() + 2];
        int pointer = 0;

        String b[] = s.split ("\\.");

        for (int i = 0; i < b.length; i++)
        {
            int l = b[i].length();
            a[pointer++] = (byte) l;
            byte c[] = b[i].getBytes();
            System.arraycopy (c, 0, a, pointer, l);
            pointer += l;
        }
        a[pointer] = 0;

        return (a);
    }

    /**
     * Combine two byte arrays into one
     *
     * @param one	one of the arrays
     * @param two	the other one
     * @return		byte array made from one and two
     */
    public static byte[] combine (byte one[], byte two[])
    {
        byte[] temp = new byte[one.length + two.length];
        System.arraycopy (one, 0, temp, 0, one.length);
        System.arraycopy (two, 0, temp, one.length, two.length);
        return (temp);
    }

    /**
     * Return a string in reverse order
     * @param s	String to reverse
     * @return	The reversed string
     */
    public static String reverse (String s)
    {
        String r = "";

        for (int i = s.length() - 1; i >= 0; i--)
        {
            r += s.charAt (i);
        }
        return (r);
    }

    /**
     * How many times does one string exist in another?
     *
     * @param s	the String to search
     * @param c	what to search for
     * @return	the number of matches
     */
    public static int count (String s, String c)
    {
        int count = 0;
        int where = 0;

        while ((where = s.indexOf (c, where)) != -1)
        {
            where++;
            count++;
        }

        return (count);
    }

    /**
     * A mixed v6/v4 address -- convert both and return the result
     *
     * @return	the final answer
     */
    private static byte[] dodots (String s)
    {
        // split at the v6/v4 boundary
        int splitat = s.lastIndexOf (":");
        String colons = s.substring (0, splitat);
        String dots = s.substring (splitat + 1);

        if (colons.equals (":")) colons = "::";

        return (combine (docolons (colons, 12), IPV4 (dots)));
    }

    /**
     * Do all the v6 conversion
     *
     * @param s		the v6 String
     * @param length	how long the String is (16 for v6, 12 for v6/v4)
     * @return		the conversoin
     */
    private static byte[] docolons (String s, int length)
    {
        int colons = count (s, ":");
        byte ret[] = new byte[length];

        // nothing but colons
        if (s.equals ("::"))
        {
            for (int i = 0; i < length; i++) ret[i] = 0;
            return (ret);
        }

        // the number of missing bytes
        int len = (length / 2 - colons) * 2;

        String split[] = s.split ("\\:");
        int i = 0;
        int where = 0;

        // leading "::"
        if (split[0].equals ("") && split[1].equals (""))
        {
            // the leading two bytes are also missing
            len = length - 2;
            i = 1;
        }

        for (; i <= colons; i++)
        {
            // if this is where things are missing, fill in zeros
            if (split[i].equals (""))
            {
                for (int j = 0; j < len; j++)
                {
                    ret[where++] = 0;
                }
            }
            else
            {
                int conv = Integer.parseInt (split[i], 16);
                ret[where++] = getByte (conv, 2);
                ret[where++] = getByte (conv, 1);
            }
        }
        return (ret);
    }

    /**
     * Convert an IPv6 String into its byte array
     *
     * @param s	the IPv6 String
     * @return	the IPv6 bytes
     */
    public static byte[] IPV6 (String s)
    {
        // if this is an v6t/v4 address
        if (count (s, ".") > 0)
            return (dodots (s));

        return (docolons (s, 16));
    }

    /**
     * Some unit tests
     */
    public static void main (String args[])
    {
        Assert (false);
        System.out.println (toString (IPV4 ("65.66.67.68")));
        System.out.println (toString (IPV6 ("::")));
        System.out.println (toString (IPV6 ("::1")));
        System.out.println (toString (IPV6 ("1::2:3:4:5")));
        System.out.println (toString (IPV6 ("0:1:2:3:4:5:6:7")));
        System.out.println (toString
	    (IPV6 ("FFFF:00FF:FF00:F00F:0FF0:F0F0:0F0F:0000")));
        System.out.println (toString
	    (IPV6 ("FEDC:BA98:7654:3210:FEDC:BA98:7654:3210")));
        System.out.println (toString (IPV6 ("0:0:0:0:0:0:13.1.68.3")));
        System.out.println (toString (IPV6 ("0:0:0:0:0:FFFF:129.144.52.38")));
        System.out.println (toString (IPV6 ("::13.1.68.3")));
        System.out.println (toString (IPV6 ("::FFFF:129.144.52.38")));
        System.out.println (count ("65.66.67.68", "."));
        System.out.println (count ("65.66.67.68", "6"));
        System.out.println (reverse ("123"));
        System.out.println (reverse ("1234"));
    }
    
    /**
     * Get the IP out of a fully qualified type.
     */
    public static String getIPFromType(String type) {
    	StringTokenizer tok = new StringTokenizer(type, ".");
    	Vector tokens = new Vector();
    	while(tok.hasMoreTokens()) {
    		tokens.add(tok.nextToken());
    	}
    	int index = tokens.indexOf("local");
    	return (String)tokens.get(index-4) + (String)tokens.get(index-3) + (String)tokens.get(index-2) + (String)tokens.get(index-1);
    }
}
