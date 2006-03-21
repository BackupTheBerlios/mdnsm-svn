/*
 * DaapHost.java
 *
 * Created on August 9, 2004, 8:35 PM
 */
package org.git.client.daap;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import org.mdnsm.mdns.ServiceInfo;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.git.GITProperties;
import org.git.GITPropertiesPanel;
import org.git.GITUtils;
import org.git.StringPairList;
import org.git.client.Host;
import org.git.client.Song;
import org.git.client.daap.request.BadResponseCodeException;
import org.git.client.daap.request.DatabasesRequest;
import org.git.client.daap.request.HangingUpdateRequest;
import org.git.client.daap.request.LoginRequest;
import org.git.client.daap.request.LogoutRequest;
import org.git.client.daap.request.PasswordFailedException;
import org.git.client.daap.request.PlaylistsRequest;
import org.git.client.daap.request.ServerInfoRequest;
import org.git.client.daap.request.SingleDatabaseRequest;
import org.git.client.daap.request.SongRequest;
import org.git.client.daap.request.UpdateRequest;
import org.git.client.swing.SwingWorker;
import org.git.server.RendezvousManager;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * 
 * @author Greg
 */
public class DaapHost extends Host {
    
    public static final int RATING_NONE = 0;
    public static final int RATING_UP = 1;
    public static final int RATING_DOWN = 2;
    
    public int rating = RATING_NONE;
    public String address;
    protected String computer_name;
    protected int port;
    protected double daap_version;
    protected int revision_num;
    protected int database_id;
    protected int session_id;
    protected int request_num;
    protected boolean has_password;
    protected String password;
    protected HangingUpdateRequest hanging_update;
    protected int host_prog; // a program-specific integer, so we can hack together compatibility.
    protected EventList songs = new BasicEventList();
    
    public static final String[] host_strings = {"Unknown", "iTunes", "Get It Together",
            "mt-daapd", "Limewire"};
    public static final int UNKNOWN_SERVER = 0;
    public static final int ITUNES = 1;
    public static final int GIT_SERVER = 2;
    public static final int MT_DAAPD = 3;
    public static final int LIMEWIRE = 4;
    
    
    // don't change these... the authorization hashing is based on these numbers.
    public static double ITUNES_LEGACY = 1;
    public static double ITUNES_40 = 2;
    public static double ITUNES_45 = 3;
    
    // dummy constructor, used by GITProperties
    public DaapHost(String name, String pwd, boolean con, boolean vis, int rate) {
        super(name);
        password = pwd;
        visible = vis;
        auto_connect = con;
        rating = rate;
    }
    
    // dummy constructor, used by GetNewHost
    public DaapHost(String name, String pwd, InetAddress addy, int porty) {
        super(name);
        password = pwd;
        address = addy.getHostAddress();
        port = porty;
    }
    
    public DaapHost(ServiceInfo info) {
        //super(info.getPropertyString("Machine Name"));
        super(info.getName());
    }

    public void loadServiceInfo(ServiceInfo info) {        
        address = info.getHostAddress();
        computer_name = info.getServer().replaceFirst(".local.","");
        port = info.getPort();
        String hasPassword = info.getPropertyString("Password"); 
        if (name.endsWith("_PW")) {
            name = name.substring(0, name.length() - 3);
            has_password = true;
        } else if (hasPassword != null && hasPassword.equals("true")) {
            has_password = true;
        } else
            has_password = false;
        String serverType = info.getPropertyString(RendezvousManager.SERVER_PROGRAM);
        if (serverType != null && serverType.length() > 0)
        {
            if (serverType.startsWith("Get It Together"))
                host_prog = GIT_SERVER;
        }
        else
            host_prog = UNKNOWN_SERVER;
        revision_num = 1;
    }
    
    public void connect() {
        setStatus(Host.STATUS_CONNECTING);
        boolean logged_in;
        if (!GITProperties.showConnectionErrorPanels)
            logged_in = login(false);
        else
            logged_in = login(true);
        if (logged_in) {
            grabSongs();
        }
    }
    
    public boolean login(boolean popup_info) {
        try {
            revision_num = 1;
            session_id = 0;
            System.out.println("logging in to "+name);
            if (has_password) {
                if (getPassword().length() == 0) {
                    if (popup_info) {
                        System.out.println("requesting password...");
                        GetPassword gpw = new GetPassword(null, name, true);
                        gpw.show();
                        password = gpw.getPassword();
                        switch (gpw.getStatus()) {
                        case GetPassword.STATUS_PRESSED_OK:
                            break;
                        case GetPassword.STATUS_PRESSED_CANCEL:
                            setStatus(Host.STATUS_NOT_CONNECTED);
                        return false;
                        default:
                            setStatus(Host.STATUS_NOT_CONNECTED);
                        return false;
                        }
                    }
                    else {
                        setStatus(Host.STATUS_NOT_CONNECTED);
                        return false;
                    }
                }
                System.out.println("password for \"" + name + "\" is "
                        + password);
            }
            ServerInfoRequest s = new ServerInfoRequest(this);
            daap_version = s.getServerVersion();
            if (s.getServerProgram() != null)
                host_prog = parseServerTypeString(s.getServerProgram());
            System.out.println(name + " daap_program = "+s.getServerProgram());
//            System.out.println(name + " daap_version = " + daap_version);
            LoginRequest l = new LoginRequest(this);
            session_id = l.getSessionId();
            System.out.println(name + " session_id = " + session_id);
            UpdateRequest u = new UpdateRequest(this);
            revision_num = u.getRevNum();
            System.out.println(name + " revision_num = "+revision_num);
//            hanging_update = new HangingUpdateRequest(this);
//            setStatus(Host.STATUS_CONNECTED);
            s = null;
            l = null;
            u = null;
        } catch (PasswordFailedException e) {
            setStatus(Host.STATUS_CONNECTING);
            String message = new String("The password for \"" + name
                    + "\" is not correct.  ");
            password = null;
            if (popup_info)
            {
	            int ans = JOptionPane.showConfirmDialog(null, message
	                    + "Try again with a new password?", "Incorrect Password",
	                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
	            switch (ans) {
	            case JOptionPane.YES_OPTION:
	                return login(true);
	            case JOptionPane.NO_OPTION:
	                setStatus(Host.STATUS_NOT_CONNECTED);
	                return false;
	            default:
	                return false;
	            }
            }
        } catch (BadResponseCodeException e) {
            if (e.getResponseCode() == 503)
            {
                if (popup_info)
                    tooManyUsers("login attempt");
                setStatus(Host.STATUS_NOT_AVAILABLE);
                return false;
            }
        } catch (java.net.ConnectException jce) {
            System.out.println("net connect exception");
            setStatus(Host.STATUS_NOT_AVAILABLE);
            nullify();
            return false;
        } catch (Exception e) {
            System.err.println("unknown error logging in to host");
            setStatus(Host.STATUS_NOT_AVAILABLE);
            nullify();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean logout() {
        // don't logout when connected to a de.kapsi server:
        if (host_prog == GIT_SERVER)
        {
            hanging_update.disconnect();
            session_id = 0;
            revision_num = 1;
            System.out.println("logged out from GIT server.");
            return true;
        }
        try {
            LogoutRequest lo = new LogoutRequest(this);
            if (hanging_update != null)
            {
                hanging_update.disconnect();
            }
            return true;
        } catch (BadResponseCodeException e) {
            if (e.getResponseCode() == 204)
            {
                System.out.println("logged out normally.");
                session_id = 0;
                revision_num = 1;
                return true;
            }
            else
            {
                System.out.println("error logging out?");
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("error logging out.");
            return false;
        }
    }
    
    public void grabSongs() {
        try {
            System.out.println("grabbing songs from host: "+name);
            DatabasesRequest d = new DatabasesRequest(this);
            System.out.println(d.getDbs());
            System.out.println(d.getDbs());
            database_id = ((Database) d.getDbs().get(0)).id;
            System.out.println("database id = " + database_id);
            SingleDatabaseRequest g = new SingleDatabaseRequest(this);
            songs.getReadWriteLock().writeLock().lock();
            songs.addAll(g.getSongs());
            songs.getReadWriteLock().writeLock().unlock();
            System.out.println("song count = " + songs.size());
            PlaylistsRequest p = new PlaylistsRequest(this);
            playlists = p.getPlaylists();
            System.out.println("playlist count = " + playlists.size());
            if (getServerType() == DaapHost.GIT_SERVER)
                hanging_update = new HangingUpdateRequest(this);
            setStatus(Host.STATUS_CONNECTED);
            d = null;
            g = null;
            p = null;
            logout();
        } catch (PasswordFailedException e) {
            System.out.println("password failed exception.. attempting to login and try again");
            logout();
            if (login(false))
                grabSongs();
        }
        catch (BadResponseCodeException e) {
            if (e.getResponseCode() == 503)
            {
                tooManyUsers("song grabbing");
                setStatus(Host.STATUS_NOT_AVAILABLE);
                return;
            }
            if (e.getResponseCode() == 500) {
                System.out.println("error.. trying again");
                logout();
                if (login(false))
                    grabSongs(); // try again.
            }
        } catch (java.net.ConnectException jce) {
            setStatus(Host.STATUS_NOT_AVAILABLE);
        }
        catch (Exception e) {
            e.printStackTrace();
            setStatus(Host.STATUS_NOT_AVAILABLE);
        }
    }
    
    public void loadPlaylists() {
        System.out.println("Loading playlists...");
        final DaapHost h = this;
        new SwingWorker() {
            public Object construct() {
                try {
                    if (!login(false))
                        return "";
                    for (int i = 0; i < playlists.size(); i++) {
                        DaapPlaylist playlist = (DaapPlaylist) playlists.get(i);
                        playlist.initialize();
                    }
                    setStatus(Host.STATUS_PLAYLISTS_LOADED);
                    logout();
                    return new Integer(0);
                } catch (Exception e) {
                    System.out.println("error loading playlists");
                    return new Integer(0);
                }
            }
        }.start();
    }
    
    private void nullify() {
        songs.getReadWriteLock().writeLock().lock();
        songs.clear();
        songs.getReadWriteLock().writeLock().unlock();
        playlists = null;
        revision_num = 0;
        database_id = 0;
        session_id = 0;
        request_num = 0;        
    }
    
    public void disconnect() {
        nullify();
        logout();
        setStatus(Host.STATUS_NOT_CONNECTED);
        setVisible(false);
        auto_connect = false;
    }

    public void remove() {
        nullify();
        setStatus(Host.STATUS_NOT_CONNECTED);
        setVisible(false);
    }
    
    public boolean isPasswordProtected() {
        return has_password;
    }

    public boolean isConnected() {
        if (status >= Host.STATUS_CONNECTED)
            return true;
        else
            return false;
    }
    
    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public double getDaapVersion() {
        return daap_version;
    }

    public int getRevisionNumber() {
        return revision_num;
    }

    public int getDatabaseID() {
        return database_id;
    }

    public int getSessionID() {
        return session_id;
    }

    public int getNextRequestNumber() {
        request_num++;
        return request_num;
    }

    public int getThisRequestNumber() {
        return request_num;
    }

    public String getPassword() {
        if (password != null)
            return password;
        else
            return new String("");
    }

    public Collection getPlaylists() {
        if (playlists == null)
            playlists = new ArrayList();
        return playlists;
    }

    public EventList getSongs() {
        return songs;
    }
    
    public void setSongs(EventList songs) {
        this.songs = songs;
    }
    
    public InputStream getSongStream(Song s) {
        System.out.println("Requesting song " + s.getId() + "." + s.getFormat()
                + " from " + name);
        return getSongStream(s, 0);
    }

    public InputStream getSongStream(Song s, long bytes) {
        try {
            // re-login if we're logged out, or this daaphost is a GIT server:
            if (session_id == 0 || host_prog == GIT_SERVER) {
                login(false);
            }
            //DaapSong song = (DaapSong)s;
            SongRequest sr = new SongRequest(this, s, bytes);
            return sr.getStream();
        } catch (BadResponseCodeException e) {
            if (e.getResponseCode() == 500)
            {
                // FIXME: This code here can help with failed song requests, but if the iTunes internal server error
                // really is internal, it causes an infinite loop.
//	            System.out.println("Error 500: forbidden.. attempting to re-login ONCE");
//	            if (login(false))
//	                return getSongStream(s, bytes);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Unknown error requesting song from host!");
        }
        return null;
    }

    public String getTypeString() {
        return "Host";
    }
    
    public String getStatusString() {
        switch (status) {
        	case -1:
        	    return "Not Available";
        	default:
        	    return status_strings[status];
        }
    }
    
    public String getToolTipText() {
        StringPairList a = new StringPairList();
        a.addPair("Type", (has_password ? "(Password) " : "")+"Shared Library");
        a.addPair("Address", address);
        a.addPair("Computer Name", computer_name);
        a.addPair("Server", host_strings[host_prog]);
        a.addPair("Status", getStatusString());
        if (status >= Host.STATUS_CONNECTED && songs != null)
            a.addPair("Song Count", String.valueOf(songs.size()));
        if (status >= Host.STATUS_PLAYLISTS_LOADED && playlists != null)
            a.addPair("Playlist Count", String.valueOf(playlists.size()));
        return GITUtils.createPropertyLabel(a);
    }
    
    public void tooManyUsers(String context) {
        if (!GITProperties.showTooManyUsersPanel)
            return;
        
        final int WAIT = 2;
        
        String message = "\""+name+"\" has too many users connected.  Try again later.\n(HTTP response code: 503 - SERVICE UNAVAILABLE)";
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        pane.setOptionType(JOptionPane.DEFAULT_OPTION);
        final JDialog dialog = pane.createDialog(null, "Daap Access Denied");
        GITUtils.callAlwaysOnTop(dialog, true);
        dialog.setModal(false);
        final JLabel timeLeft = new JLabel("Closing in: "+WAIT);
        JPanel panel = new JPanel();
        panel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        panel.add(timeLeft);
        JComponent p = (JComponent)dialog.getContentPane().getComponent(0);
        p.add(panel, 1);
        
        // fix up the button and "stop showing me" check box.
        JComponent removed = (JComponent)p.getComponent(2);
        p.remove(2);
        JPanel newpanel = new JPanel();
        newpanel.setLayout(new BoxLayout(newpanel, BoxLayout.X_AXIS));
        JCheckBox cbox = new JCheckBox("Do not show again");
        cbox.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("action!");
                JCheckBox cbox = (JCheckBox)e.getSource();
                GITProperties.showTooManyUsersPanel = !cbox.isSelected();
                GITPropertiesPanel.instance.tooManyUsers.setSelected(GITProperties.showTooManyUsersPanel);
                System.out.println(GITProperties.showTooManyUsersPanel);
            }
        });
        cbox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        newpanel.add(cbox);
        removed.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        newpanel.add(removed);
        newpanel.add(Box.createHorizontalGlue());
        
        p.add(newpanel, 2);
        p.validate();
        dialog.setSize(dialog.getPreferredSize());
        final Timer timer = new Timer(1000, new AbstractAction() {
            int num = WAIT;
            
            public void actionPerformed(ActionEvent e) {
                Timer t = (Timer)e.getSource();
                if (num <= 0)
                {
                    t.stop();
                    dialog.dispose();
                }
                else 
                {
                    num --;
                    timeLeft.setText("Closing in: "+num);
                    dialog.repaint();
                }
            }
        });
        timer.start();
        dialog.show();
        return;
    }

    public int getServerType() {
        return host_prog;
    }
    
    public static int parseServerTypeString(String s) {
        s = s.toLowerCase().trim();
        if (s.startsWith("itunes"))
            return ITUNES;
        else if (s.startsWith("daapserver"))
            return GIT_SERVER;
        else if (s.startsWith("mt-daapd"))
            return MT_DAAPD;
        else
            return UNKNOWN_SERVER;
            
        
        
    }
}