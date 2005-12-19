package org.mdnsm.server;

import java.io.*;
import java.util.logging.*;
import java.util.Date;
import java.text.DateFormat;

/**
 * A simple class to add line numbers to logging.  I use logging a lot and
 * often have multiple messages from the same method.  Sun could have done
 * it, but chose not to:
 * http://www.docjar.com/html/api/java/util/logging/Logger.java.html
 *
 * To use, create a file that looks like:
 *
 *    edu.mscd.cs.myconsolehandler.level = FINEST
 *    handlers = MyConsoleHandler
 *    MyConsoleHandler.level = FINEST
 *
 * and invoke using: -Djava.util.logging.config.file=thatfile
 * to your "java" command.
 */

class MyFormatter extends Formatter
{
    public String format (LogRecord rec)
    {
	StackTraceElement ste = null;
	StackTraceElement u[] = new Throwable().getStackTrace();

	/*
	** start at 1, ignoring this method, and march through until
	** we're out of the logging methods.
	*/
	for (int i = 1; i < u.length; i++)
	{
	    if (! u[i].getClassName().startsWith ("java.util.logging"))
	    {
		ste = u[i];
		break;
	    }
	}

	String linesep = System.getProperty ("line.separator");

	String s = DateFormat.getDateTimeInstance().format (new Date()) + " ";
	s += rec.getSourceClassName() + " ";
	s += rec.getSourceMethodName() + " ";
	s += ste.getLineNumber() + linesep;
	s += rec.getLevel() + ": ";
	s += formatMessage (rec) + linesep;

	Throwable t = rec.getThrown();
	if (t != null)
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    t.printStackTrace (new PrintStream (baos));
	    s+= baos.toString();
	}

	return (s);
    }
}

public class MyConsoleHandler extends ConsoleHandler
{
    public MyConsoleHandler()
    {
	super();
	setFormatter (new MyFormatter());
    }

    public static void main (String args[])
    {
        Logger logger = Logger.getLogger ("edu.mscd.cs.myconsolehandler");
	logger.severe ("this is a test");
	logger.finest ("this is another");
    }
}
