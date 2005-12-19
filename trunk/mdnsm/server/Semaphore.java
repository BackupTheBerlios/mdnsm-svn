package org.mdnsm.server;

import java.util.logging.Logger;

class TestSem extends Thread
{
    private static Semaphore s = new Semaphore (5);

    public void run()
    {
	s.P();
	try { Thread.sleep (10 * 1000); }
	catch (InterruptedException e) { e.printStackTrace(); }
	s.V();
    }
}

public class Semaphore
{
    private int total;
    private int count = 0;
    private static MyLogger logger = new MyLogger();

    public Semaphore (int total)
    {
    	this.total = total;
    }
    
    public synchronized void P()
    {
	String name = Thread.currentThread().getName();
	logger.finest ("P: count = " + count + " " +  name);

	while (count >= total)
	{
	    logger.finest (name + " blocked");

	    try
	    {
	        wait();
	    }
	    catch (Exception e)
	    {
	        logger.throwing (e);
	    }

	    logger.finest (name + " unblocked");
	}

	count++;
    }

    public synchronized void V()
    {
	count--;
	logger.finest ("V: count = " + count + " " +  
	    Thread.currentThread().getName());
	if (count > 0) notify();
    }

    public static void main (String args[])
    {
        for (int i = 0; i < 10; i++)
	{
	    new TestSem().start();
	}
    }
}
