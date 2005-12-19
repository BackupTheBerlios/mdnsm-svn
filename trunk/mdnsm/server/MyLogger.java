package org.mdnsm.server;

import java.util.logging.*;

// someday, this could be extended to deal with package names...

// java -Djava.util.logging.config.file=MyLogger.finest MyLogger one two three

/*
** MyLogger.finest:

MyLogger.level = FINEST
handlers = java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level = FINEST

*/

public class MyLogger extends Logger
{
    private void printStackTrace ()
    {
	StackTraceElement ste[] = new Throwable().getStackTrace();

	for (int i = 0; i < ste.length; i++)
	{
	    System.out.println (ste[i]);
	}
    }

    /**
     * Create a logger whose name is the calling class's.
     */
    public MyLogger ()
    {
	super (new Throwable().getStackTrace()[1].getClassName(), null);
	LogManager.getLogManager().addLogger (this);
    }

    /**
     * Create a logger whose name passed in
     *
     * @param s the name of the logger.
     */
    public MyLogger (String s)
    {
        super (s, null);
	LogManager.getLogManager().addLogger (this);
    }

    protected MyLogger (String s, String t)
    {
        super (s, t);
	LogManager.getLogManager().addLogger (this);
    }

    /**
     * Get a logger named after the class.
     */
    public static MyLogger getLogger()
    {
	String name = new Throwable().getStackTrace()[1].getClassName();
	return ((MyLogger) getLogger (name, null));
    }

    /**
     * Log entering a method without having to pass the class and method
     * names.
     */
    public void entering()
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	entering (ste.getClassName(), ste.getMethodName());
    }

    /**
     * Log entering a method without having to pass the class and method
     * names.
     *
     * @param o the Object logged along with the class and method.
     */
    public void entering (Object o)
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	entering (ste.getClassName(), ste.getMethodName(), o);
    }

    /**
     * Log entering a method without having to pass the class and method
     * names.
     *
     * @param o the array of Objects logged along with the class and method.
     */
    public void entering (Object[] o)
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	entering (ste.getClassName(), ste.getMethodName(), o);
    }

    /**
     * Log leaving a method without having to pass the class and method
     * names.
     */
    public void exiting()
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	exiting (ste.getClassName(), ste.getMethodName());
    }

    /**
     * Log leaving a method without having to pass the class and method
     * names.
     *
     * @param o the Object logged along with the class and method.
     */
    public void exiting (Object o)
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	exiting (ste.getClassName(), ste.getMethodName(), o);
    }

    /**
     * Log throwing without having to pass the class and method names.
     *
     * @param t the Throwable.
     */
    public void throwing (Throwable t) 
    {
	StackTraceElement ste = new Throwable().getStackTrace()[1];
	throwing (ste.getClassName(), ste.getMethodName(), t);
    }

    public static void main (String args[])
    {
        MyLogger l = new MyLogger();
	l.severe ("this is a test");
	l.entering("not", "needed");	// check for call to base class
	l.entering();
	l.entering (new Integer (10));
	l.entering (args);
	l.entering (new Object[]{new Integer (1), "one"});
	l.exiting ();
	l.throwing (new AssertionError ("AE message"));

        MyLogger m = new MyLogger("one");
	m.severe ("this is another test");

        MyLogger n = new MyLogger("two", null);
	n.severe ("this is a third test");

	MyLogger o = MyLogger.getLogger();
	o.severe ("this is a fourth test");
    }
}
