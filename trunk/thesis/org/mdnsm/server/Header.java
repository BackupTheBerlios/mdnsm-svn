package org.mdnsm.server;

/* Header.java */
/** 
 * This class models the header for both queries and responses
 *
 * @author Steve Beaty
 * @version $Id: Header.java,v 1.3 2005/11/02 18:04:22 drb80 Exp $
 */
public class Header
{   
   
    private int id;
    private int questions;
    private boolean RD;
    private int opcode;

    private int answers;
    private int authority;
    private int additional;
    private int rcode;
    private boolean TC;
    private boolean QR;
    private boolean AA;
    private boolean RA;

    protected byte buf[];

    /*
    ** allow derived classes to set fields as they should.  check to
    ** see if there is an actual change, and rebuild if there is.
    */
    protected void setAnswers (int a)
    { if (answers != a) { answers = a; rebuild(); } }

    protected void setAuthority (int a)
    { if (authority != a) { authority = a; rebuild(); } }

    protected void setAdditional (int a)
    { if (additional != a) { additional = a; rebuild(); } }

    protected void setRcode (int a)
    { if (rcode != a) { rcode = a; rebuild(); } }

    protected void setTC (boolean b) { if (TC != b) { TC = b; rebuild(); } }
    protected void setQR (boolean b) { if (QR != b) { QR = b; rebuild(); } }
    protected void setAA (boolean b) { if (AA != b) { AA = b; rebuild(); } }
    protected void setRA (boolean b) { if (RA != b) { RA = b; rebuild(); } }

    /**
     * Populates the object data from the byte array of the header
     * 
     * @param buf	The byte array from the message
     * @see		<a href="http://www.faqs.org/rfcs/rfc1035.html">
     */
    public Header (byte buf[])
    {
        this.buf = buf;
	setup();
    }

    /**
     * Populates the object data from an existing header, usually used to
     * create a response header from a query.
     *
     * @param h		The existing Header
     */
    public Header (Header h)
    {
	buf = new byte[h.buf.length];

	System.arraycopy (h.buf, 0, buf, 0, h.buf.length);
	setup();
    }

    /**
     * Rebuild the byte array from the object data
     */
    private void rebuild()
    {
	buf[0] = Utils.getByte (id, 2);
	buf[1] = Utils.getByte (id, 1);
        buf[2] = (byte)((QR ? 128 : 0) | (opcode << 3) | (AA ? 4 : 0) | 
	    (TC ? 2 : 0) | (RD ? 1 : 0));
        buf[3] = (byte)((RA ? 8 : 0) | rcode);	// put in real rcode
	buf[4] = Utils.getByte (questions, 2);
	buf[5] = Utils.getByte (questions, 1);
	buf[6] = Utils.getByte (answers, 2);
	buf[7] = Utils.getByte (answers, 1);
	buf[8] = Utils.getByte (authority, 2);
	buf[9] = Utils.getByte (authority, 1);
	buf[10] = Utils.getByte (additional, 2);
	buf[11] = Utils.getByte (additional, 1);
    }

    /**
     * Does the heavy lifting for populating the object's state.  Called by
     * the constructors.
     *
     */
    private void setup ()
    {
        id =		Utils.addThem (buf[0], buf[1]);
        questions =	Utils.addThem (buf[4], buf[5]);
        answers =	Utils.addThem (buf[6], buf[7]);
        authority =	Utils.addThem (buf[8], buf[9]);
        additional =	Utils.addThem (buf[10], buf[11]);

        int flags =	Utils.addThem (buf[2], buf[3]);
        QR = (flags & 0x00008000) != 0;
        opcode = (flags & 0x00007800) >> 11;
        AA = (flags & 0x00000400) != 0;
        TC = (flags & 0x00000200) != 0;
        RD = (flags & 0x00000100) != 0;
        RA = (flags & 0x00000080) != 0;
        rcode = flags & 0x0000000f;
    }

    /**
     * Each message has an ID used to map queries to responses.
     * @return id	the query's identifier
     */    
    public int getId () { return id; }
   
    /**
     * How many entries there are in the message's question section.
     * @return questions	the number of questions
     */    
    public int getQuestions () { return questions; }
   
    /**
     * How many resource records there are in the message's answer section.
     * @return answer	the number of answers
     */  
    public int getAnswers () { return answers; }
    
    /**
     * How many name server resource records are in the message's authority
     * section.
     * @return authority	the number of authoritative responses
     */
    public int getAuthority () { return authority; }
    
    /**
     * How many resource records are in the message's additional records
     * section.
     * @return additional	the number of additional records
     */
    public int getAdditional () { return additional; }
    
    /**
     * Indicates whether the message is a query or a response.
     * @return QR	true = query, false = response
     */
    public boolean getQR () { return QR; }
    
    /**
     * Indicates whether the response by authoritative name server.
     * @return AA	true = authoritative
     */
    public boolean getAA () { return AA; }
    
    /**
     * Indicates whether the massage has been truncated.
     * @return TC	true = truncated
     */
    public boolean getTC () { return TC; }
    
    /**
     * Indicates whether the querier requests a recursive query.
     * @return RD	true = recursion desired
     */
    public boolean getRD () { return RD; }
    
    /**
     * Indicates whether the name server makes recursive queries available.
     * @return RA	true = recursion available
     */
    public boolean getRA () { return RA; }
    
    /**
     * Indicates the type of request.
     * @return opcode	0 = standard, 1 = inverse, 2 = server status
     */
    public int getOpcode () { return opcode; }
    
    /**
     * Indicates the status of the query.
     * <pre>
     * 0 = No error condition.
     * 1 = Unable to interpret query due to format error.
     * 2 = Unable to process due to server failure.
     * 3 = Name in query does not exist.  
     * 4 = Type of query not supported.  
     * 5 = Query refused.
     * </pre>
     * @return rcode
     */
    public int getRcode () { return rcode; }

   /**
    * Overrides toString() in java.Object
    * @return	 A nicely formated description of the header information
    */
    public String toString ()
    {
        String s = "Id: 0x" + Integer.toHexString (id) + "\n";
        s += "Questions: " + questions + "\t";
        s += "Answers: " + answers + "\n";
        s += "Authority RR's: " + authority + "\t";
        s += "Additional RR's: " + additional + "\n";

        s += "QR: " + QR + "\t";
        s += "AA: " + AA + "\n";
        s += "TC: " + TC + "\t";
        s += "RD: " + RD + "\n";
        s += "RA: " + RA + "\t";
        s += "opcode: " + opcode + "\n";
        s += "rcode: " + rcode;
        return s;
    }
}

