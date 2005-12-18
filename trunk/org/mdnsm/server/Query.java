package org.mdnsm.server;

/**
 * parses the incoming DNS queries
 * @author Steve Beaty
 * @version $Id: Query.java,v 1.6 2005/11/04 00:10:51 drb80 Exp $
 */

import java.util.Vector;

public class Query extends Header
{
    private String name[];
    private int type[];
    private int qclass[];
    private static MyLogger logger = new MyLogger();

    /**
     * creates a Query from raw bytes; calls the base class Header to
     * populate those values.
     */
    public Query (byte buf[])
    {
        super (buf);
	// Utils.Assert (! getQR());

        int j = getQuestions();
        name = new String[j];
        type = new int[j];
        qclass = new int[j];

        parseQueries();
    }

    /**
     * @param location	current location in the buffer
     * @param which	which name we're working on
     */
    private void uncompress (int start, int location, int which)
    {
        logger.finest ("In uncompress with location = " + location);
        logger.finest ("buf[location] = " +
	    Integer.toHexString (buf[location]));
        logger.finest ("buf[location+1] = "
	    + Integer.toHexString (buf[location+1]));

	// the high order bits specify that compression is being used, so
	// strip them off.
	int tmp1 = buf[location] & 0x3f;
        int tmp2 = buf[location + 1];
        int tmp = tmp1 + tmp2;
        logger.finest ("start = " + start);
        logger.finest ("tmp1 = " + tmp1);
        logger.finest ("tmp2 = " + tmp2);
        logger.finest ("tmp = " + tmp);

        // make a recursive call to get the real string
	Utils.Assert (tmp > 11);
	Utils.Assert (tmp < start);
        parseName (tmp, which);

        logger.finest ("Leaving uncompress, location = " + location);
    }

    /**
     * @param start	current offset in the buffer
     * @param which	which name we're working on
     * @return		the current location in the buffer 
     */
    private int parseName (int start, int which)
    {
	int location = start;
        logger.finest ("Entering parseName, location = " + location);

        // if the first thing is a compression
        if ((buf[location] & 0xc0) == 0xc0)
        {
            uncompress (start, location, which);
            return location + 2;
        }

        int length = buf[location++] & 0x3f;

        while (length > 0)
        {
            for (int i = 1; i <= length; i++)
            {
                logger.finest ("Adding" + ((char) buf[location]));
                name[which] += ((char) buf[location++]);
            }

            // if we get to the end of a real string and there's a
            // compression...
            if ((buf[location] & 0xc0) == 0xc0)
            {
                name[which] += ".";
                uncompress (start, location, which);
                return location + 2;
            }

            length = buf[location++] & 0x3f;
            if (length > 0)
            {
                name[which] += ".";
            }
        }
        logger.finest ("Leaving parseName, location = " + location);
        return location;
    }

    /**
     * Evaluates and saves all questions
    */
    private void parseQueries ()
    {
	// the offset of the queries
        int location = 12;

        for (int i = 0; i < getQuestions(); i++)
        {
            name[i] = "";
            location = parseName (location, i);
            type[i] = Utils.addThem (buf[location], buf[location + 1]);
            location += 2;
            qclass[i] = Utils.addThem (buf[location], buf[location + 1]);
            location += 2;
        }
    }

    /**
     * get an array of all the names in this request
     * @return all the names
     */
    public String[] getNames()
    {
        return (name);
    }

    /**
     * get an array of all the types in this request
     * @return all the types
     */
    public int[] getTypes()
    {
        return (type);
    }

    /**
     * get an array of all the classes in this request
     * @return all the classes
     */
    public int[] getClasses()
    {
        return (qclass);
    }

    /**
     * return a rational representation of the type; used in toString()
     * @return a string representation of the type of request
     */
    private String typeString (int i)
    {
        switch (i)
        {
            case 1 : return "A";
            case 2 : return "NS";
            case 3 : return "MD";
            case 4 : return "MF";
            case 5 : return "CNAME";
            case 6 : return "SOA";
            case 7 : return "MB";
            case 8 : return "MG";
            case 9 : return "MR";
            case 10 : return "NULL";
            case 11 : return "WKS";
            case 12 : return "PTR";
            case 13 : return "HINFO";
            case 14 : return "MINFO";
            case 15 : return "MX";
            case 16 : return "TXT";
            case 17 : return "RP";
            case 18 : return "AFSDB";
            case 20 : return "ISDN";
            case 21 : return "RT";
            case 22 : return "NSAP";
            case 23 : return "NSAP-PTR";
            case 24 : return "SIG";
            case 25 : return "KEY";
            case 26 : return "PX";
            case 28 : return "AAAA";
            case 29 : return "LOC";
            case 30 : return "NXT";
            case 33 : return "SRV";
            case 35 : return "NAPTR";
            case 36 : return "KX";
            case 37 : return "CERT";
            case 38 : return "A6";
            case 42 : return "APL";
            case 249 : return "TKEY";
            case 250 : return "TSIG";
            case 252 : return "AXFR";
            case 255 : return "ANY";
            default : return "unknown";
        }
    }

    /**
     * create a text representation of the Query, including the information
     * from the Header base class.
     * @return a string containing all the contents of this Query
     */
    public String toString()
    {
        String s = super.toString();

        for (int i = 0; i < getQuestions(); i++)
        {
            s += "\nName: " + name[i] +
                 " Number: " + type[i] +
                 " Class: " + qclass[i] +
                 " Name: " + typeString (type[i]);
        }
        return s;
    }
}
