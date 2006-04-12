/*
 * Created on Aug 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

import org.git.client.daap.DaapHost;
import org.git.client.local.GITLibraryHost;
import org.git.client.local.GITLyricsHost;
import org.git.client.local.GITSettingsHost;
import org.git.client.local.IPodHost;
import org.git.client.local.ItunesHost;
import org.git.client.local.LocalHost;
import org.git.client.swing.GetItTogether;
import org.git.player.PlayerUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.mdnsm.server.Utils;

/**
 * @author Greg
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class GITProperties implements Serializable{
    
    // MUSIC SERVER SETTINGS:
    public static String specialFriend = "";
    public static int sharePort = 5371;
    public static String shareName = "GITunes@"+Utils.getHostName();
    public static boolean shareEnabled = true;
    public static int shareLimit = 5;
    public static String sharePassword = "pw";
    public static boolean sharePasswordRequired = false;
    
    // LYRICS SERVER SETTINGS:
    public static int lyricsSharePort = 5372;
    public static String lyricsShareName = "gitlyrics@"+Utils.getHostName();
    public static boolean lyricsShareEnabled = true;
    public static int lyricsShareLimit = 5;
    public static String lyricsSharePassword = "lpw";
    public static boolean lyricsSharePasswordRequired = false;
    
    // SETTINGS SERVER SETTINGS:
    public static int settingsSharePort = 5373;
    public static String settingsShareName = "gitsettings@"+Utils.getHostName();
    public static boolean settingsShareEnabled = true;
    public static int settingsShareLimit = 5;
    public static String settingsSharePassword = "spw";
    public static boolean settingsSharePasswordRequired = false;
    
    // CLIENT SETTINGS:
    public static boolean hideUnsupportedSongs = false;
    public static boolean autoConnect = false;
    public static boolean locatePlayingSong = true;
    public static int numDownloads = 0;
    public static int playerVolume = 75;
    public static boolean expandLocal = true;
    public static boolean expandRemote = true;
    protected static GetItTogether git;
    private static Document xml_doc;
    public static boolean showSplash = false;
    public static int shuffleValue = 0;
    public static String dlDir = "./";
    public static String tempDir = "./";
    public static String externalProg = "";
    public static int playerType = PlayerUtils.JAVAPLAYER;
    public static Dimension bigSize = new Dimension(9999, 9999);
    public static Point bigPos = new Point(9999, 9999);
    public static int miniWidth = 100;
    public static Point miniPos = new Point(0, 0);
    public static int ITUNES_PORT = 3690;
    public static ArrayList savedLocalHosts = new ArrayList();
    public static DefaultListModel knownHosts;
    public static boolean organizeDLdSongs = true;
    public static boolean tryToAvoidDups = true;
    public static boolean hideDRMProtected = true;
    public static boolean alwaysOnTop = true;
    public static boolean showTooManyUsersPanel = true;
    
    public static boolean searchEveryKey = true;
    public static Hashtable savedDaapHosts = new Hashtable();
    
    // not a public setting, only for within-GIT use.
    public static boolean showConnectionErrorPanels = true;
    
    static {
        savedLocalHosts.add(new GITLibraryHost("GIT Music",""));
        savedLocalHosts.add(new GITLyricsHost("GIT Lyrics", ""));
        savedLocalHosts.add(new GITSettingsHost("GIT Settings", ""));
        //savedLocalHosts.add(new IPodHost("iPod","g:/"));
        //savedLocalHosts.add(new ItunesHost("iTunes Music",""));
    }
        
    static public void loadPropertyString(String base, String property, String field) {
        try {
        String str = GITProperties.getProperty(base, property).getValue();
        GITProperties.class.getField(field).set(new String(""), str);
        } catch (Exception e) {}
    }
    
    static public void loadPropertyInt(String base, String property, String field) {
        try {
            int i = GITProperties.getProperty(base, property).getIntValue();
            GITProperties.class.getField(field).setInt(null, i);
        } catch (Exception e) {}
    }
    
    static public void loadPropertyBoolean(String base, String property, String field) {
        try {
            boolean b = GITProperties.getProperty(base, property).getBooleanValue();
            GITProperties.class.getField(field).setBoolean(null, b);
        } catch (Exception e) {System.err.println("loadPropertyBoolean: problem with "+property);}
    }
    
    static public void loadProperties() {
        if (!readXML()) {
            System.out
                    .println("Error reading xml file; creating new structure.");
            createXML();
        }
            
            loadPropertyInt("settings.downloads", "count", "numDownloads");
            loadPropertyInt("settings.gui.shuffle", "type", "shuffleValue");
            loadPropertyInt("settings.player", "volume", "playerVolume");
            loadPropertyInt("settings.player", "type", "playerType");
            loadPropertyInt("settings.server", "share_port", "sharePort");
            
            loadPropertyBoolean("settings.gui", "locate_playing_song", "locatePlayingSong");
            loadPropertyBoolean("settings.gui", "auto_connect", "autoConnect");
            loadPropertyBoolean("settings.gui", "always_on_top", "alwaysOnTop");
            loadPropertyBoolean("settings.gui", "hide_unsupported_songs", "hideUnsupportedSongs");
            loadPropertyBoolean("settings.gui.splash", "show", "showSplash");
            loadPropertyBoolean("settings.gui.search", "everykey", "searchEveryKey");
            loadPropertyBoolean("settings.downloads", "createdirs", "organizeDLdSongs");
            loadPropertyBoolean("settings.downloads", "avoidduplicates", "tryToAvoidDups");
            loadPropertyBoolean("settings.downloads", "hideprotected", "hideDRMProtected");
            loadPropertyBoolean("settings.gui.autoexpand", "local", "expandLocal");
            loadPropertyBoolean("settings.gui.autoexpand", "remote", "expandRemote");
            loadPropertyBoolean("settings.gui", "show_too_many_users_panel", "showTooManyUsersPanel");
            loadPropertyBoolean("settings.server","share_enabled","shareEnabled");
            loadPropertyBoolean("settings.server", "share_require_password", "sharePasswordRequired");
            
            loadPropertyString("settings.downloads", "location", "dlDir");
            loadPropertyString("settings.temp", "directory", "tempDir");
            loadPropertyString("settings.player", "external_prog", "externalProg");
            loadPropertyString("settings.server", "share_password", "sharePassword");
            loadPropertyString("settings.server", "share_name", "shareName");
            loadPropertyString("settings.server", "share_limit", "shareLimit");
            loadPropertyString("settings.server", "special_friend", "specialFriend");
            
            try {
            int w = (int) GITProperties
                    .getProperty("settings.gui.big", "width").getDoubleValue();
            int h = (int) GITProperties.getProperty("settings.gui.big",
                    "height").getDoubleValue();
            //            if (w != -1 && h != -1)
            bigSize = new Dimension(w, h);
            int x = (int) GITProperties.getProperty("settings.gui.big", "x")
                    .getDoubleValue();
            int y = (int) GITProperties.getProperty("settings.gui.big", "y")
                    .getDoubleValue();
            bigPos = new Point(x, y);
            w = (int) GITProperties.getProperty("settings.gui.mini", "width")
                    .getDoubleValue();
            miniWidth = w;
            x = (int) GITProperties.getProperty("settings.gui.mini", "x")
                    .getDoubleValue();
            y = (int) GITProperties.getProperty("settings.gui.mini", "y")
                    .getDoubleValue();
            miniPos = new Point(x, y);
            } catch (Exception e) {createXML();}
            
            ArrayList llist = GITProperties.retrieveSavedLibraries();
            if (llist != null) {
                System.out.println("Local!");
                savedLocalHosts = llist;
            }
            
            Hashtable dlist = GITProperties.retrieveSavedDaapHosts();
            if (dlist != null) {
                savedDaapHosts = dlist;
            }

    }

    public GITProperties() {
        loadProperties();
    }

    protected static boolean readXML() {
        System.out.println("reading xml...");
        try {
            File f = new File("git.xml");
            if (f.exists() && f.canRead()) {
                //  Load the XML file into a Document
                FileInputStream in = new FileInputStream(f);
                System.out.println(f.getAbsolutePath());
                SAXBuilder builder = new SAXBuilder();
                xml_doc = builder.build(in);
                System.out.println("done!");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static void createXML() {
        System.out.println("creating xml...");
        Element cur = null; // the current element that is being added to.
        xml_doc = new Document();
        xml_doc.setRootElement(new Element("GIT"));
        Element settings = new Element("settings");
        xml_doc.getRootElement().addContent(settings);
        
        // SERVER settings:
        Element server = new Element("server");
        settings.addContent(server);
        cur = server;
        cur.setAttribute("share_enabled", "" + shareEnabled);
        cur.setAttribute("share_password", "" + sharePassword);
        cur.setAttribute("share_require_password", "" + sharePasswordRequired);
        cur.setAttribute("share_port", "" + sharePort);
        cur.setAttribute("share_name", "" + shareName);
        cur.setAttribute("share_limit", "" + shareLimit);
        cur.setAttribute("special_friend", "" + specialFriend);
        
        //  GUI settings
        Element gui = new Element("gui");
        settings.addContent(gui);
        cur = gui;
        gui.setAttribute("auto_connect", "" + autoConnect);
        gui.setAttribute("locate_playing_song", "" + locatePlayingSong);
        gui.setAttribute("always_on_top", "" + alwaysOnTop);
        gui.setAttribute("hide_unsupported_songs", "" + hideUnsupportedSongs);
        gui.setAttribute("show_too_many_users_panel", "" + showTooManyUsersPanel);
        Element shuffle = new Element("shuffle");
        shuffle.setAttribute("type", "" + shuffleValue);
        cur.addContent(shuffle);
        Element splash = new Element("splash");
        splash.setAttribute("show", "" + showSplash);
        cur.addContent(splash);
        Element search = new Element("search");
        search.setAttribute("everykey", "" + searchEveryKey);
        cur.addContent(search);
        Element bigPlayer = new Element("big");
        bigPlayer.setAttribute("width", "" + bigSize.getWidth());
        bigPlayer.setAttribute("height", "" + bigSize.getHeight());
        bigPlayer.setAttribute("x", "" + bigPos.getX());
        bigPlayer.setAttribute("y", "" + bigPos.getY());
        cur.addContent(bigPlayer);
        Element miniPlayer = new Element("mini");
        miniPlayer.setAttribute("width", "" + miniWidth);
        miniPlayer.setAttribute("x", "" + miniPos.getX());
        miniPlayer.setAttribute("y", "" + miniPos.getY());
        cur.addContent(miniPlayer);
        Element expand = new Element("autoexpand");
        expand.setAttribute("local", "" + expandLocal);
        expand.setAttribute("remote", "" + expandRemote);
        cur.addContent(expand);
        
        //  temp settings
        Element temp = new Element("temp");
        settings.addContent(temp);
        cur = temp;
        cur.setAttribute("directory", "" + tempDir);
                
        //  player settings
        Element player = new Element("player");
        settings.addContent(player);
        cur = player;
        cur.setAttribute("type", "" + playerType);
        cur.setAttribute("volume", "" + playerVolume);
        cur.setAttribute("external_prog", "" + externalProg);
        
        
        //  downloader settings
        Element download = new Element("downloads");
        settings.addContent(download);
        cur = download;
        cur.setAttribute("location", "" + dlDir);
        cur.setAttribute("createdirs", "" + organizeDLdSongs);
        cur.setAttribute("avoidduplicates", "" + tryToAvoidDups);
        cur.setAttribute("hideprotected", "" + hideDRMProtected);
        cur.setAttribute("count", "" + numDownloads);
        
        //  server shares
        Element shares = new Element("shares");
        cur = shares;
        cur.addContent(new Element("share").setAttribute("location",
                "c:/Documents and Settings").setAttribute("name", "My Music"));
        xml_doc.getRootElement().addContent(cur);
        
        // 	libraries
        Element libs = new Element("libraries");
        cur = libs;
        xml_doc.getRootElement().addContent(cur);
        Iterator itera = savedLocalHosts.iterator();
        while (itera.hasNext()) {
            LocalHost host = (LocalHost) itera.next();
            GITProperties.addLibraryToXML(host);
        }
        
        //  daap host settings
        Element daap = new Element("daap_hosts");
        Enumeration e = savedDaapHosts.elements();
        while (e.hasMoreElements()) {
            DaapHost h = (DaapHost)e.nextElement();
            Element host = new Element("host");
            host.setAttribute("name", h.getName());
            if (h.getPassword().length() > 0)
                host.setAttribute("password", h.getPassword());
                if (h.isAutoConnect())
                    host.setAttribute("auto_connect", "true");
                if (h.isVisible())
                    host.setAttribute("visible","true");
                if (h.rating != 0)
                    host.setAttribute("rating", String.valueOf(h.rating));
            daap.addContent(host);
        }
        xml_doc.getRootElement().addContent(daap);
    }

    private static Hashtable retrieveSavedDaapHosts() {
        try {
            Hashtable hash = new Hashtable();
	        Element cur = xml_doc.getRootElement().getChild("daap_hosts");
	        List dh = cur.getChildren();
	        for (int i=0; i < dh.size(); i++) {
	            cur = (Element) dh.get(i);
	            String name = cur.getAttributeValue("name");
	            String password = cur.getAttributeValue("password");
	            
	            boolean autoConnect = false;
	            boolean visible = false;
	            int rating = 0;
	            if (cur.getAttribute("auto_connect") != null)
	                autoConnect = cur.getAttribute("auto_connect").getBooleanValue();
	            if (cur.getAttribute("visible") != null)
	                visible = cur.getAttribute("visible").getBooleanValue();
	            if (cur.getAttribute("rating") != null)
	                rating = cur.getAttribute("rating").getIntValue();
	            DaapHost host = new DaapHost(name, password, autoConnect, visible, rating);
	            hash.put(name, host);
	        }
	        return hash;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void addDaapHost(DaapHost host) {
        if (savedDaapHosts.containsKey(host.getName()))
            savedDaapHosts.remove(host.getName());
        savedDaapHosts.put(host.getName(), host);
    }
    
    public static String getPassword(String name) {
        DaapHost host = (DaapHost) savedDaapHosts.get(name);
        return host.getPassword();
    }

    public static void writeXML() {
        try {
            //        	setAllProperties();
            createXML();
            File f = new File("git.xml");
            OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            System.out.println("writing XML to " +f.getAbsolutePath() + " ...");
            XMLOutputter output = new XMLOutputter();
            output.setNewlines(true);
            output.setIndent("    ");
            output.output(xml_doc, out);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static Attribute getProperty(String loc, String attr) {
        try {
            String[] locs = loc.split("[.]");
            Element cur = xml_doc.getRootElement();
            for (int i = 0; i < locs.length; i++) {
                cur = cur.getChild(locs[i]);
            }
            return cur.getAttribute(attr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DaapHost getDaapHost(String name) {
        return (DaapHost)savedDaapHosts.get(name);
    }
    
    // used to easily retrieve information about a saved Daap host.
    public static Object getSavedDaapHostInfo(String name, String key) {
        try {
            Element cur = xml_doc.getRootElement().getChild("daap_hosts");
            List host_ele = cur.getChildren();
            for (int i=0; i < host_ele.size(); i++) {
                Element ele = (Element)host_ele.get(i);
                if (ele.getAttribute("name").getValue().equals(name))
                    return ele.getAttribute(key).getValue();
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
    
    private static ArrayList retrieveSavedLibraries() {
        try {
            ArrayList localHosts = new ArrayList();
            Element cur = xml_doc.getRootElement().getChild("libraries");
            List libraries = cur.getChildren();
            for (int i = 0; i < libraries.size(); i++) {
                cur = (Element) libraries.get(i);
                String name = cur.getAttributeValue("name");
                String location = cur.getAttributeValue("location");
                LocalHost host = null;
                int type = 0;
                switch (type = cur.getAttribute("type").getIntValue()) {
                case LocalHost.IPOD:
                    //host = new IPodHost(name, location);
                break;
                case LocalHost.GIT_LIBRARY:
                    host = new GITLibraryHost(name,location);
                break;
                case LocalHost.GIT_LYRICS:
                	host = new GITLyricsHost(name, location);
                break;
                case LocalHost.GIT_SETTINGS:
                	host = new GITSettingsHost(name, location);
                break;
                case LocalHost.ITUNES_XML:
                    //host = new ItunesHost(name,location);
                default:
                
                }
                if (cur.getAttribute("visible") != null)
                    host.setVisible(cur.getAttribute("visible").getBooleanValue());
                if (cur.getAttribute("auto_connect") != null)
                    host.setAutoConnect(cur.getAttribute("auto_connect").getBooleanValue());
                localHosts.add(host);
            }
            return localHosts;
        } catch (Exception e) {
            			e.printStackTrace();
            return null;
        }
    }

    public static void addLibrary(LocalHost host) {
        savedLocalHosts.add(host);
    }
    
    public static boolean removeLibrary(LocalHost host) {
        return savedLocalHosts.remove(host);
    }
    
    private static boolean addLibraryToXML(LocalHost host) {
        try {
            Element lb_ele = xml_doc.getRootElement().getChild("libraries");
            Element e = new Element("library");
            e.setAttribute("location",host.getRoot());
            e.setAttribute("name", host.getName());
            e.setAttribute("type", String.valueOf(host.getLibraryType()));
            if (host.isAutoConnect())
                e.setAttribute("auto_connect", String.valueOf(host.isAutoConnect()));
            if (host.getStatus() >= LocalHost.STATUS_CONNECTED && host.isVisible())
                e.setAttribute("visible", String.valueOf(host.isVisible()));
            lb_ele.addContent(e);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static InetAddress getSpecialFriend() {
        try {
            InetAddress addr = InetAddress.getByName(GITProperties.specialFriend);
            return addr;
        } catch (Exception e) {return GITUtils.getLocalInetAddress();}
    }
    
}