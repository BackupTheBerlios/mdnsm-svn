package org.mdnsm.server;

/**
 * @author Steve Beaty
 * @version $Id: Zone.java,v 1.4 2004/09/14 21:12:47 drb80 Exp $
 */

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class Zone
{
    private String name;
    private String contact;
    private int serial;
    private int refresh;
    private int retry;
    private int expire;
    private int ttl;

    /*
    ** might not be the best to have different tables for each, but
    ** otherwise would have to find a way to differentiate the types
    ** in the single table...
    */
    
    private Hashtable hA;	
    private Hashtable hAAAA;
    private Hashtable hNS;
    private Hashtable hMX;
    private Hashtable hCNAME;
    private Hashtable hPTR;
    private Hashtable hTXT;
    
    /**
     * Constructor: set the current values of the domnain name,
     * Zone serial number, refresh threshold, retry threshold, 
     * expiration threshold, and time to live. <BR>
     * Also instantiate the Zone tables for A and AAAA, Name Server,
     * Mail Server, CName, and PTR Records 
     */
    public Zone (String name, String contact, int serial, int refresh,
        int retry, int expire, int ttl)
    {
            this.name = name;
            this.contact = contact;
            this.serial = serial;
            this.refresh = refresh;
            this.retry = retry;
            this.expire = expire;
            this.ttl = ttl;

            hA = new Hashtable();
            hAAAA = new Hashtable();
            hNS = new Hashtable();
            hMX = new Hashtable();
            hCNAME = new Hashtable();
            hPTR = new Hashtable();
            hTXT = new Hashtable();
    }

    /**
     * @return the domain name
     */
    public String getName() { return (name); }

    /**
     * @return the contact email address
     */
    public String getContact() { return (contact); }

    /**
     * @return the serial number
     */
    public int getSerial() { return (serial); }

    /**
     * @return the refresh value
     */
    public int getRefresh() { return (refresh); }

    /**
     * @return the retry value
     */
    public int getRetry() { return (retry); }

    /**
     * @return the expiration value
     */
    public int getExpire() { return (expire); }

    /**
     * @return the time to live value
     */
    public int getTTL() { return (ttl); }

    /**
     * Create a printable String
     * @param h	a Hashtable
     * @return	the contents in String form
     */
    private String dumphash (Hashtable h)
    {
            String s = "";
            Enumeration e = h.keys();

            while (e.hasMoreElements())
            {
                    Object o = e.nextElement();
                    s +=  o + ": " + h.get (o) + " ";
            }
            return s;
    }
		
    /**
     * A printable version of the Zone
     * @return the string
     */
    public String toString ()
    {
            String s = "---- Zone " + name + " -----" + '\n';
            s += "contact = " + contact + '\n';
            s += "serial = " + serial + '\n';
            s += "refresh = " + refresh + '\n';
            s += "retry = " + retry + '\n';
            s += "expire = " + expire + '\n';
            s += "ttl = " + ttl + '\n';


            s += "A: " + dumphash (hA) + "\n";
            s += "AAAA: " + dumphash (hAAAA) + "\n";
            s += "CNAME: " + dumphash (hCNAME) + "\n";
            s += "MX: " + dumphash (hMX) + "\n";
            s += "NS: " + dumphash (hNS) + "\n";
            s += "PTR: " + dumphash (hPTR) + "\n";
            s += "TXT: " + dumphash (hTXT) + "\n";
            s += "--------";

            return (s);
    }

    /**
     * Add an address to a name.  There may be multiple addresses per name
     * @param type	the type of RR
     * @param name	the name
     * @param address	the address
     */
    public void add (int type, String name, String address)
    {
            Vector v;
            Hashtable t = null;

            switch (type)
            {
                    case Utils.A: t = hA; break;
                    case Utils.AAAA: t = hAAAA; break;
                    case Utils.NS: t = hNS; break;
                    case Utils.MX: t = hMX; break;
                    case Utils.CNAME: t = hCNAME; break;
                    case Utils.PTR: t = hPTR; break;
                    case Utils.TXT: t = hTXT; break;
                    default: Utils.Assert (false);
            }

            /*
            ** if there isn't already a entry
            */
            if ((v = (Vector) t.get (name)) == null)
            {
                    v = new Vector();
                    t.put (name, v);
            }

            v.add (address);
    }

    /**
     * Get a Vector of addresses for a particular name
     * @param type	the query type
     * @param name	the name
     * @return a Vector with the appropriate addresses for the given name 
     */
    public Vector get (int type, String name)
    {
            switch (type)
            {
                    case Utils.A: return ((Vector) hA.get (name));
                    case Utils.AAAA: return ((Vector) hAAAA.get (name));
                    case Utils.NS: return ((Vector) hNS.get (name));
                    case Utils.MX: return ((Vector) hMX.get (name));
                    case Utils.CNAME: return ((Vector) hCNAME.get (name));
                    case Utils.PTR: return ((Vector) hPTR.get (name));
                    case Utils.TXT: return ((Vector) hTXT.get (name));
                    default: return (null);
            }
    }

    /**
     * For Testing. <BR>
     * Tests the Zones given the domain mpcs.org
     */
    public static void main (String[] args)
    {
            Zone z = new Zone ("mpcs.org", "root.mpcs.org", 1, 2, 3, 4, 5);
            z.add (Utils.A, "www", "1.2.3.4");
            z.add (Utils.A, "www", "4.3.2.1");
            z.add (Utils.MX, "", "4.3.2.1");

            Vector v = z.get (Utils.A, "www");
            System.out.print (z);
    }
}
