/*
 * Created on Aug 29, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.git.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.git.GITProperties;
import org.git.GITUtils;
import org.git.client.BasicStatusObject;
import org.git.client.Host;
import org.git.client.local.LocalHost;
import org.git.client.swing.GetItTogether;
import org.git.client.swing.SwingWorker;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import de.kapsi.net.daap.DaapAuthenticator;
import de.kapsi.net.daap.DaapFilter;
import de.kapsi.net.daap.DaapServer;
import de.kapsi.net.daap.DaapServerFactory;
import de.kapsi.net.daap.DaapStreamSource;
import de.kapsi.net.daap.Database;
import de.kapsi.net.daap.Library;
import de.kapsi.net.daap.Playlist;
import de.kapsi.net.daap.SimpleConfig;
import de.kapsi.net.daap.Transaction;


/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class Server extends BasicStatusObject implements DaapAuthenticator, DaapStreamSource, DaapFilter{
	
    public static final int STATUS_NOT_RUNNING = 0;
    public static final int STATUS_RUNNING = 1;
    
    private static final Server INSTANCE = new Server();
	private HashMap id2path;
	private HashMap git2kapsi;
	private Library library;
	private Database database;
  	private DaapServer server;
  	private Playlist playlist;
	private ArrayList loadedHosts;
  
  	public static Server instance() {
  	    return INSTANCE;
  	}
  	
  public synchronized void start() {
      if (!GITProperties.shareEnabled) {
          System.out.println("sharing is disabled");
          return;
      }
      if (isServerRunning())
          return;
      
      try {
      InetAddress addr = GITUtils.getLocalInetAddress();
      if (addr == null || addr.isLoopbackAddress())
      {
          System.out.println("problem with localhost address");
      }
      
      library = new Library(GITProperties.shareName);
      database = new Database(GITProperties.shareName);
      Playlist playlist = new Playlist("Recent");
      Transaction txn = library.open(false);
      playlist.setSmartPlaylist(txn, true);
      library.add(txn, database);
      database.add(txn, playlist);
      txn.commit();
      
      SimpleConfig config = new SimpleConfig(GITProperties.sharePort);
      config.setMaxConnections(GITProperties.shareLimit);
      server = DaapServerFactory.createNIOServer(library, config);
      server.setAuthenticator(this);
      server.setStreamSource(this);
      server.setFilter(this);
      
      boolean bound = false;
      while (!bound) {
	      try {
	          config.setInetSocketAddress(new InetSocketAddress(InetAddress.getLocalHost(), GITProperties.sharePort));
	      server.bind();
	      bound = true;
	      } catch (SocketException e) {
	          GITProperties.sharePort++;
	          continue;
	      }
      }
      
      id2path = new HashMap();
      git2kapsi = new HashMap();
      
      Thread serverThread = new Thread(server, "DaapServerThread");
      serverThread.setDaemon(true);
      serverThread.start();
      
      // register this server with Rendezvous
      RendezvousManager.instance().registerServer();
      
      setStatus(Server.STATUS_RUNNING);
      } catch (IOException io) {
          stop();
          io.printStackTrace();
      }
  } 

  // don't use.
  public void init() throws Exception {
	    if (!GITProperties.shareEnabled) {
	        System.out.println("sharing is disabled");
	        return;
	    }
	    
	    InetAddress addr = InetAddress.getLocalHost();

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
			
      library = new Library(GITProperties.shareName);
      database = new Database(GITProperties.shareName);
      
      Playlist playlist = new Playlist("Recent");
      Transaction txn = library.open(false);
      
      playlist.setSmartPlaylist(txn, true);
      
      
      library.add(txn, database);
          
      database.add(txn, playlist);
      // note: can use txn multiple times (ie to add multiple playlists) but commit only once
      txn.commit();
      
      
      // add songs
      
//      Playlist masterPlaylist = database.getMasterPlaylist();
      /*
      
      de.kapsi.net.daap.Song song = new Song("The Only One " );
          song.setArtist("American Analog Set");
          song.setAlbum("Know by Heart");
          song.setGenre("Rock/Pop");
          song.setTrackNumber(2);
          song.setSize((int) 6002857);
          song.setBitrate(128);
          song.setTime(377000); // milli seconds
          song.setUserRating(2);
//          song.setDataUrl("/Users/wooo/1.mp3");
      
      System.out.println(song.toString());
      
      hash.put(song.toString(), "/Users/wooo/1.mp3");
      txn = library.open(false);
      playlist.add(txn,song);
      txn.commit();
      */
      //server.update();
      
      SimpleConfig config = new SimpleConfig(GITProperties.sharePort);
      config.setMaxConnections(GITProperties.shareLimit);

      server = DaapServerFactory.createBIOServer(library, config);
      server.setAuthenticator(this);
      server.setStreamSource(this);
      server.bind();
      
//      id2path = new Hashtable();
      Thread serverThread = new Thread(server, "DaapServerThread");
      serverThread.setDaemon(true);
      serverThread.start();
      
	    
	    System.out.println("done with Server.init()");
	}
	
  	public synchronized void stop() {
  	    if (server != null)
  	        server.stop();
  	    if (id2path != null)
  	        id2path.clear();
  	    
  	    // unregister this server with Rendezvous
  	    RendezvousManager.instance().unregisterServer();
  	    
  	    server = null;
  	    id2path = null;
  	    playlist = null;
  	    database = null;
  	    
  	    setStatus(Server.STATUS_NOT_RUNNING);
  	}
  
  	public synchronized void restart() {
  	    if (isServerRunning())
  	        server.stop();
  	    
  	    start();
  	}
  	
  	public boolean isServerRunning() {
  	    if (server != null)
  	        return server.isRunning();
  	    else
  	        return false;
  	}
  	
  	public synchronized void update() {
  	    if (isServerRunning())
  	    {
  	        Transaction txn = library.open(false);
  	        library.setName(txn, GITProperties.shareName);
  	        database.setName(txn, GITProperties.shareName);
  	        txn.commit();
  	        server.update();
  	        
  	        // update this server with Rendezvous
  	        RendezvousManager.instance().update();
  	    }
  	}
  	
	public static void createPlaylist(String name, org.git.client.Song song) {
//	    Playlist list = new Playlist(name);
	    
//      Transaction txn = library.open(false);
      
//      list.setSmartPlaylist(txn, false);
      
//      database.add(txn, list);
      
//      txn.commit();
      
      
	}
	
	public synchronized void addSongs(EventList songs) {
	    if (!GITProperties.shareEnabled) {
	        System.out.println("Sharing disabled.  Doing nothing");
	        return;
	    }
	    if (server == null || !server.isRunning()) {
	        System.out.println("Server isn't running.  doing nothing");
	        return;
	    }
	    
	    Transaction txn = library.open(false);
	    org.git.client.local.LocalSong song;
	    de.kapsi.net.daap.Song serverSong;
	    for (int i = 0; i < songs.size(); i ++)
	    {
	        try {
	            song = (org.git.client.local.LocalSong) songs.get(i);
	        } catch (ClassCastException e) {
	            System.err.println("Server.addSongs(): Can't Share " + songs.get(i) + " because it is not a local song!");
	           continue;
	        }
	        serverSong = serveSong(song);
	        serverSong.itemId.setValue(song.getId());
	        id2path.put(new Integer(song.getId()), song.getPath());
	        git2kapsi.put(song, serverSong);
	        database.getMasterPlaylist().add(txn, serverSong);
	    }
	    txn.commit();
	    server.update();
	    System.out.println(database.getMasterPlaylist().size());
	}
	
	public synchronized void removeSongs(EventList songs) {
	    if (server == null || !server.isRunning()) {
	        return;
	    }
	    
	    Transaction txn = library.open(false);
	    org.git.client.local.LocalSong song;
	    de.kapsi.net.daap.Song serverSong;
	    for (int i=0; i < songs.size(); i++) {
	        song = (org.git.client.local.LocalSong)songs.get(i);
	        serverSong = (de.kapsi.net.daap.Song) git2kapsi.get(song);
	        if (serverSong != null)
	            database.getMasterPlaylist().remove(txn, serverSong);
	        git2kapsi.remove(song);
	    }
	    txn.commit();
	    server.update();
	    System.out.println(database.getMasterPlaylist().size());
	}
	
	// don't use.
	public synchronized void  updateSongs(LocalHost disconnecting) {
	    if (!GITProperties.shareEnabled) {
	        System.out.println("Sharing disabled.  Doing nothing");
	        return;
	    }
	    if (server == null || !server.isRunning()) {
	        System.out.println("Server isn't running.  doing nothing");
	        return;
	    }
	    
	    // This is kind of a hack.  When a localhost disconnects, it calls this method,
	    // which then waits a couple of seconds and then re-loads all hosts.
	    new SwingWorker() {
	        public Object construct() {
	            try{Thread.sleep(2000);}catch(Exception e){}
	            id2path = new HashMap();
	            Transaction txn = library.open(false);
	            // Remove all songs from the master playlist:
	            Playlist p = database.getMasterPlaylist();
	            Set s = p.getSongs();
	            Iterator itr = s.iterator();
	            while (itr.hasNext()) {
	                p.remove(txn,(de.kapsi.net.daap.Song)itr.next());
	            }
	            
	            // Add songs from all connected local hosts:
	            ArrayList lhosts = GetItTogether.instance.getLocalHosts();
	            for (int i=0; i < lhosts.size(); i++) {
	                LocalHost host = (LocalHost)lhosts.get(i);
	                if (host.getStatus() >= Host.STATUS_CONNECTED);
//	                    addSongs(host.getSongs());
	            }
	            
	            txn.commit();
	            server.update();
	            return new Integer(0);
	        }
	    }.start();
	}

	private static de.kapsi.net.daap.Song serveSong(org.git.client.local.LocalSong song) {
	  de.kapsi.net.daap.Song servedSong = new de.kapsi.net.daap.Song(song.getName());
      servedSong.setArtist(song.getArtist());
      servedSong.setAlbum(song.getAlbum());
      servedSong.setGenre(song.getGenre());
      servedSong.setTrackNumber(song.getTrack());
      servedSong.setSize(song.getSize());
      servedSong.setBitrate(128); //FIXME: store and return the correct bitrate!
      servedSong.setTime(song.getTime()); // FIXME: make sure this time is in milli seconds
      servedSong.setUserRating(3); //FIXME: store and return the user rating
      servedSong.setCompilation(song.compilation);
      servedSong.setFormat(song.getFormat());
//      servedSong.setDataUrl(((LocalSong)song).getPath()); // use this for streamed files...
//      System.out.println("serveSong returning " + servedSong.toString());
      return servedSong;
  }

  public boolean requiresAuthentication() {
      return GITProperties.sharePasswordRequired;
  }

  public boolean authenticate(String username, String password) {
      return password.equals(GITProperties.sharePassword);
  }

  public FileInputStream getSource(de.kapsi.net.daap.Song song) throws IOException {
      System.out.println("getting source!");
      File file = new File((String)id2path.get(new Integer(song.getId())));
//      File file = new File((String)song.getDataUrl());
      if (file != null && file.isFile()) {
          return new FileInputStream(file);
      }

      return null;
  }

/* (non-Javadoc)
 * @see de.kapsi.net.daap.DaapFilter#accept(java.net.InetAddress)
 */
public boolean accept(InetAddress address) {
    
    if (GITUtils.isCloseIP(address))
        return true;
    if (address.equals(GITProperties.getSpecialFriend()))
    {
        System.out.println("Special friend connected from "+address.getHostAddress());
        return true;
    }
    else
        return false;
}


/*
 * 
 * A simple mapping to convert GIT 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
private final class KapsiGITMap {

    private HashMap ktog;
    private HashMap gtok;
    
}

}