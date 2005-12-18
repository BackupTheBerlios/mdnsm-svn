package org.mdnsm.server;

/**
 * creates a response...
 * @author Steve Beaty
 * @version $Id: Response.java,v 1.9 2005/11/04 00:10:51 drb80 Exp $
 */

import java.net.*;
import java.io.*;
import java.util.Vector;

public class Response extends Header
{
    private boolean UDP;
    private String host, domain;
    private Zone z;
    private static MyLogger logger = new MyLogger();

    /**
     * create a Response from an existing Query -- the major way a Response
     * is created.
     * @param h		the existing Query
     */
    public Response (Header h)
    {
        super (h);

	/*
	** change the appropriate values
	*/
        setQR (true);	// response
        setAA (true);	// authoritative
        setRA (false);	// recursion not available

	UDP = true;
    }

    /**
     * create a Response from an existing Query; this one is typically used
     * to create a TCP Response.
     * @param h		the existing Query
     * @param UDP	whether this is TCP (false) or UDP (true)
     */
    public Response (Header h, boolean UDP)
    {
    	this(h);
	this.UDP = UDP;
    }

    /**
     * create a byte array that is a Response to a Query
     * @param q	the Query to respond to, null if the Query is empty
     */
    public byte[] makeResponse (Query q)
    {
        String[] names = q.getNames();
        int[] type = q.getTypes();

        for (int i = 0; i < names.length; i++)
        {
            return (doit (names[i], type[i]));
        }
        return (null);
    }

    /**
     * create an appropriate error message
     * @param error	what kind of error
     * @param name	what name or domain caused the error
     */
    private byte[] fail (String error, String name)
    {
        logger.info (error + " lookup of " + name + " failed");

	// create a response with an error code and return it
        setRcode (3);
        return (buf);
    }

    private String fix (String s)
    {
        /*
        ** if it is a PQDN add the domain, else it is a FQDN and
        ** rip off the ending dot.  do we need to see if it already
        ** endsWith the domain?
        */
        if (! s.endsWith ("."))
            s += "." + domain;
        else
            s = s.replaceFirst ("\\.$", "");

        return (s);
    }

    private byte[] getBytes (String s, int which)
    {
        switch (which)
        {
            case Utils.A:	return (Utils.IPV4 (s));
            case Utils.AAAA:	return (Utils.IPV6 (s));
            case Utils.MX:
	    {
		String t[] = s.split (" ");

		t[0] = fix (t[0]);

		int j = Integer.parseInt (t[1]);
		byte c[] = new byte[2];
		c[0] = Utils.getByte (j, 2);
		c[1] = Utils.getByte (j, 1);

		return (Utils.combine (c, Utils.convertString (t[0])));
	    }
            case Utils.CNAME:
            case Utils.NS:	return (Utils.convertString (fix (s)));
            case Utils.PTR:	return (Utils.convertString (s));
            case Utils.TXT:	return (Utils.toCS (s));
            case Utils.SOA:
	    {
	        byte a[] = Utils.combine (Utils.convertString (z.getName()), 
			Utils.convertString (z.getContact()));
	        a = Utils.combine (a, Utils.getBytes (z.getSerial()));
	        a = Utils.combine (a, Utils.getBytes (z.getRefresh()));
	        a = Utils.combine (a, Utils.getBytes (z.getRetry()));
	        a = Utils.combine (a, Utils.getBytes (z.getExpire()));
	        return (Utils.combine (a, Utils.getBytes (z.getTTL())));
	    }
        }
        return (null);
    }

    /**
     * find the name and domain name of the query
     * @param name	the whole query, which may need breaking into parts
     * @param which	the type of query
     */
    private boolean getNameDomain (String name, int which)
    {
        switch (which)
        {
            case Utils.A:
            case Utils.AAAA:
            case Utils.CNAME:
            case Utils.TXT:
	    {
		String s[] = name.split ("\\.", 2);
		if (s.length != 2) return (false);

		host = s[0];
		domain = s[1];
		return (true);
	    }
            case Utils.MX:
            case Utils.NS:
            case Utils.SOA:
	    {
		host = "";
		domain = name;
		return (true);
	    }
            case Utils.PTR:
	    {
		host = name + ".";
		if (name.endsWith ("ip6.int"))	domain = "ip6.int";
		else				domain = "in-addr.arpa";
		return (true);
	    }
        }
	return (false);
    }

    private byte[] createResponses (Vector v, int size, String name, int which)
    {
        setAnswers (size);

        // start with raw header bytes
        byte results[] = new byte[buf.length];
        System.arraycopy (buf, 0, results, 0, buf.length);

        for (int i = 0; i < size; i++)
        {
            String s = which == Utils.SOA ? "" : (String) v.elementAt (i);
            logger.finest ("s = " + s);

            ResourceRecord rr = new ResourceRecord
		(name, which, 1, z.getTTL(), getBytes (s, which));

	    logger.finest (rr.toString());

	    byte add[] = rr.getRR();

	    // check for over 512...
	    if (UDP && (results.length + add.length > 512))
	    {
		/*
		** if so, return the fact that the client needs to use TCP
		** and return what we have so far.
		*/
	    	setTC (true);
		setAnswers (i);

		// recopy the header, it was changed
		System.arraycopy (buf, 0, results, 0, buf.length);
		return (results);
	    }

            results = Utils.combine (results, add);
        }

	return (results);
    }

    /**
      * @param name	the name to look up
      * @param which	the type of query
      */
    private byte[] doit (String name, int which)
    {
	if (! getNameDomain (name, which)) return (fail ("Name", name));

        logger.finest ("host = " + host + ", domain = " + domain);

	if ((z = ServiceServer.getZone (domain)) == null)
	{
	    if (which == Utils.A || which == Utils.AAAA)
	    {
	        domain = name;
	        host = "";
	        if ((z = ServiceServer.getZone (domain)) == null)
		    return (fail ("Zone", domain));
	    }
	    else
	    {
	        return (fail ("Zone", domain));
	    }
	}

	int size;
        Vector v = null;

	/*
	** there is only one SOA and we generate the response differently
	*/
	if (which == Utils.SOA)
	{
	    size = 1;
	}
	else
	{
	    v = z.get (which, host);
	    if (v == null) return (fail ("Host", host));
	    size = v.size();
            logger.finest ("v = " + v);
	}

        return (createResponses (v, size, name, which));
    }
}
