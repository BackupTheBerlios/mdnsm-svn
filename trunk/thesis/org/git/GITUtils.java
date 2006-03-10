/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.git.client.Song;
import org.git.client.swing.SwingWorker;
import org.git.server.RendezvousManager;

import ca.odell.glazedlists.EventList;

/**
 * Some miscellaneous static utility functions.
 */
public final class GITUtils {

    public static String SEP = File.separator;
    
    public static InetAddress getLocalInetAddress() {
        try {
	        InetAddress addr = InetAddress.getLocalHost();
	        if (addr.isLoopbackAddress() || !(addr instanceof Inet4Address)) {
	            addr = null;
			    Enumeration e = NetworkInterface.getNetworkInterfaces();
				while (e.hasMoreElements()) {
					NetworkInterface intf = (NetworkInterface)e.nextElement();
					Enumeration addrs = intf.getInetAddresses();
					while (addrs.hasMoreElements()) {
						addr = (InetAddress)addrs.nextElement();
						if (!addr.getHostAddress().equals("127.0.0.1") &&
								!addr.getHostAddress().equals("0.0.0.0") &&
								addr.getHostAddress().matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")) {
		    				break;
						}
					}
				}
	        }
	        return addr;
        } catch (Exception e) {return null;}
    }
    
    public static boolean isCloseIP(InetAddress addr) {
        byte[] a = addr.getAddress();
        
        InetAddress local = GITUtils.getLocalInetAddress();
        byte[] b = local.getAddress();
        
        if (a[0] == b[0] && a[1] == b[1])
            return true;
        else
            return false;
    }
    
    public static String getQualifiedServiceName(String name) {
        String qualified_name = (GITProperties.sharePasswordRequired ? name + "_PW." + RendezvousManager.DAAP_SERVICE_TYPE :
			name + "." + RendezvousManager.DAAP_SERVICE_TYPE);
        return qualified_name;
    }

    public static int hardIndexOfSong(EventList list, Song s) {
        for (int i=0; i < list.size(); i++) {
            if (list.get(i) == s)
                return i;
        }
        return -1;
    }
    
    public static boolean hasQuicktimeForJava() {
        try {
        Class classy = Class.forName("quicktime.std.movies.Movie");
        if (classy != null)
            return true;
        else
            return false;
        } catch (ClassNotFoundException cne) {
            return false;
        }
        catch (ExceptionInInitializerError e) {
            return false;
        }
    }
    
    // creates an <html>'ified label String from a Hashtable of keys and values.
    public static String createPropertyLabel(StringPairList pairs) {
        String label = new String("");
        label += "<html>";
        String key = new String();
        String value = new String();
        for (int i=0; i < pairs.size(); i++) {
            StringPair pair = pairs.getPair(i);
        	key = pair.a;
        	value = pair.b;
            label += "<b>" + key + ": ";
            label += "</b>" + value;
            if (i < (pairs.size() - 1))
                label += "<br>";
        }
        return label;
    }
    
    // creates a Swingworker that performs the actionPerformed() method on the given action.
    public static SwingWorker actionToWorker(final Action a) {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                a.actionPerformed(null);
                return "";
            }
        };
        return worker;
    }
    
    public static Action workerToAction(final SwingWorker sw) {
        AbstractAction a = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                sw.start();
            }
        };
        return a;
    }
    
    public static Action createSwingWorkerAction(Action a) {
        SwingWorker sw = actionToWorker(a);
        return workerToAction(sw);
    }
    
    public static void callAlwaysOnTop(Window w, boolean b) {
            if (GITUtils.isJava5())
            {
                try {
    			    Method onTop = w.getClass().getMethod("setAlwaysOnTop", new Class[] {Boolean.TYPE});
    			    onTop.invoke(w, new Object[] {new Boolean(b)});
    			    } catch (Exception exc) {System.out.println("failed alwaysontop.. no biggie");}
            }
        }

    public static boolean isWindowsXP() {
        String os = GITUtils.getOS();
        if (os.indexOf("windows xp") != -1)
            return true;
        else
            return false;
    }
    
    public static boolean isLinux() {
        String os = GITUtils.getOS();
        if (os.indexOf("linux") != -1)
            return true;
        else
            return false;
    }
    
    public static boolean isMacOSX() {
        String os = GITUtils.getOS();
        if (os.startsWith("mac os") && os.endsWith("x"))
            return true;
        else
            return false;
    }
    
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    public static boolean isJava5() {
        if (System.getProperty("java.version").compareTo("1.5.0") >= 0)
            return true;
        else
            return false;
    }
    
    public static int compareStrings(String s1, String s2) {
        if (s1.equals("")) return 1;
        else if (s2.equals("")) return -1;
        s1=s1.toLowerCase();s2=s2.toLowerCase();
        if (s1.startsWith("the "))s1=s1.substring(4);
        if (s2.startsWith("the "))s2=s2.substring(4);
        return s1.compareToIgnoreCase(s2);
    }
    
    public static String getFormattedSize(int bytes) {
        double b = Math.round(bytes / 104857.6);
		b = b / 10;
		return Double.toString(b) + " Mb";
    }
    
    public static String getFormattedTime(int millis) {
        int a = millis;
		String sec_zero = "";
		int seconds = a / 1000;
		int minutes = seconds / 60;
		int seconds_left = seconds % 60;
		if (seconds_left < 10) {
			sec_zero = "0";
		}
		return "" + minutes + ":" + sec_zero + seconds_left;
    }

    public static String getDefaultItunesLocation() {
        if (isWindowsXP()) {
            String loc = System.getProperty("user.home");
            loc += SEP + "My Documents" + SEP + "My Music" + SEP + "iTunes" + SEP + "iTunes Music Library.xml";
            System.out.println(loc);
            return loc;
        } else if (isMacOSX()) {
            String loc = System.getProperty("user.home");
            loc += SEP + "Music" + SEP + "iTunes" + SEP + "iTunes Music Library.xml";
            return loc;
        } else
            return "";
    }
    
}
