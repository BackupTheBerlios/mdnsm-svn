package org.mdnsm.server;

/**
 * Create and manipulate resource records
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc1035.html"> faqs.org </a>
 * @author Steve Beaty
 * @version $Id: ResourceRecord.java,v 1.1.1.1 2004/06/30 18:42:22 drb80 Exp $
 */

/*
**					1  1  1  1  1  1 
**	  0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
**	|                                               | 
**	/                                               / 
**	/                      NAME                     / 
**	|                                               | 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
**	|                      TYPE                     | 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
**	|                     CLASS                     | 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
**	|                      TTL                      | 
**	|                                               | 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
**	|                   RDLENGTH                    | 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--| 
**	/                     RDATA                     / 
**	/                                               / 
**	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
*/

public class ResourceRecord
{
    private String domain;
    private int rrtype, rrclass, ttl, length;
    private byte rdata[];
    private byte name[];

    /**
     * Sets the current domain name, resource record type,
     * resource record class, time to live, and resource
     * record data.
     * @param domain	the domain name
     * @param rrtype	the type of resource record to create
     * @param rrclass	the resource record class
     * @param ttl	the time to live
     */
    public ResourceRecord (String domain, int rrtype, int rrclass, int ttl,
                           byte rdata[])
    {
        this.domain = domain;
        this.rrtype = rrtype;
        this.rrclass = rrclass;
        this.ttl = ttl;
        this.rdata = rdata;
        name = Utils.convertString (domain);
    }

    /**
     * converts all the internal data to a byte array to put in a Response
     * @return the resource record to put in the response
     */
    public byte[] getRR()
    {
        int rdatalen = rdata.length;
        int count = name.length + 2 + 2 + 4 + 2 + rdatalen;

        byte a[] = new byte[count];
        int where = name.length;

        System.arraycopy (name, 0, a, 0, name.length);
        a[where++] = Utils.getByte (rrtype, 2);
        a[where++] = Utils.getByte (rrtype, 1);
        a[where++] = Utils.getByte (rrclass, 2);
        a[where++] = Utils.getByte (rrclass, 1);
        a[where++] = Utils.getByte (ttl, 4);
        a[where++] = Utils.getByte (ttl, 3);
        a[where++] = Utils.getByte (ttl, 2);
        a[where++] = Utils.getByte (ttl, 1);
        a[where++] = Utils.getByte (rdatalen, 2);
        a[where++] = Utils.getByte (rdatalen, 1);
        System.arraycopy (rdata, 0, a, where, rdata.length);

        return (a);
    }
    
    public int getTtl() {
    	return ttl;
    }
    
    public String getDomain() {
    	return domain;
    }

    /**
     * @return a readable version
     */
    public String toString()
    {
        String s = "Domain: " + domain + "\n" +
                   "Type: " + rrtype + "\n" +
                   "Class: " + rrclass + "\n" +
                   "TTL: " + ttl + "\n" +
                   Utils.toString (getRR());
        return (s);
    }

    /**
      * For testing
      */
    public static void main (String args[])
    {
        byte addr[] = { 14, 15, 16, 17 };
        ResourceRecord RR = new ResourceRecord ("www.pipes.org", 1, 2, 3, addr);

        System.out.println (RR);
    }
}
