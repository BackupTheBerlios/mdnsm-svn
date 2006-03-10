/*
 * Created on Aug 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.git.GITProperties;
import org.git.GITUtils;
import org.git.StringPairList;
import org.git.client.Host;
import org.git.client.Song;
import org.git.client.swing.GetItTogether;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterable;

/**
 * @author Greg
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class LocalHost extends Host implements TextFilterable{
    protected String root = "";
    static public final String[] type_strings = { "iTunes Library",
            "MP3 Directory", "iPod" };
    
    protected JLabel current_scan = new JLabel();
    
    public static final int IPOD_MONITOR_DELAY = 1000;
    
    public static final int GIT_LIBRARY = 0;
    public static final int IPOD = 1;
    public static final int ITUNES_XML = 2;
    
    
    public EventList songs = new BasicEventList(new ArrayList(200));

    public static final char HACK_MARKER = '*';
    
    public LocalHost(String nayme, String rhoot) {
        super(nayme);
        root = rhoot;
    }
    
    // renames this host.  Accessed by the TreeCellRenderer when renaming local hosts.
    public void setName(String nayme) {
        this.name = nayme;
    }

    
    public void connect() {
        setStatus(Host.STATUS_CONNECTING);
        File f = new File(root);
        if (f == null || !f.exists()) {
            if (!GITProperties.showConnectionErrorPanels)
            {
                setStatus(Host.STATUS_NOT_CONNECTED);
                return;
            }
            int find = JOptionPane
                    .showConfirmDialog(
                            null,
                            "Could not find the local library "
                                    + (root.length() > 0 ? "at the specified location: "
                                            + root + ".  "
                                            : ".  ")
                                    + "Search for it yourself?",
                            "File Not Found", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
            switch (find) {
            case JOptionPane.YES_OPTION:
//                int result = showFileChooser();
                switch (0) {
                case JFileChooser.APPROVE_OPTION:
                    connect();
                    return;
                case JFileChooser.CANCEL_OPTION:
                    setStatus(Host.STATUS_NOT_CONNECTED);
                    return;
                case JFileChooser.ERROR_OPTION:
                    setStatus(Host.STATUS_NOT_CONNECTED);
                    return;
                }
            case JOptionPane.NO_OPTION:
                setStatus(Host.STATUS_NOT_CONNECTED);
                return;
            }
        } else {
//            loadSongs();
            setVisible(true);
            setStatus(Host.STATUS_CONNECTED);
            final Host h = this;
        }
    }

    public void disconnect() {
        songs.clear();
        setStatus(Host.STATUS_NOT_CONNECTED);
        setVisible(false);
    }
    
    public static String showFileChooser(int type, String message,FileFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(type);
        fileChooser.setDialogTitle(message);

        if (filter != null)
            fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(GetItTogether.instance.frame);
        switch (result) {
        case JFileChooser.APPROVE_OPTION:
            File file = fileChooser.getSelectedFile();
            return file.getPath();
        case JFileChooser.CANCEL_OPTION:
            return null;
        case JFileChooser.ERROR_OPTION:
            JOptionPane.showMessageDialog(null, "Error choosing File.");
            return null;
        default:
            return null;
        }
        
    }
 
     public void loadPlaylists() {
        try {
            if (status != Host.STATUS_CONNECTED)
                return;
            if (playlists == null)
                return;
            for (int i=0; i < playlists.size(); i++) {
                LocalPlaylist p = (LocalPlaylist)playlists.get(i);
                p.initialize();
            }
            getUnavailableSongs();
        } catch (Exception e) {
            playlists = null;
            return;
        }
    }
    
    

    public static String getTitleFromFile(File f) {
        String filename = f.getName();
        return HACK_MARKER+filename.substring(0, filename.lastIndexOf("."))+HACK_MARKER;
    }
    
    public static String getArtistFromFile(File f) {
        return HACK_MARKER+f.getParentFile().getParentFile().getName()+HACK_MARKER;
    }
    
    public static int getTrackFromFile(File f) {
        String filename = f.getName();
        if (Character.isDigit(filename.charAt(0)) && Character.isDigit(filename.charAt(1)))
            return Integer.parseInt(filename.substring(0,2));
        else
            return 0;
    }
    
    public static String getAlbumFromFile(File f) {
        return HACK_MARKER+f.getParentFile().getName()+HACK_MARKER;
    }
    
    public EventList getSongs() {
        return songs;
    }

    public EventList getUnavailableSongs() {
        System.out.println("searching for unavailable songs...");
        File f;
        BasicEventList unavailable = new BasicEventList();
        for (int i=0; i < getSongs().size(); i++) {
            final LocalSong s = (LocalSong)getSongs().get(i);
            f = new File(s.path);
            if (!f.exists()) {
                s.status = Song.STATUS_NOT_FOUND;
//                songs.remove(s);
                unavailable.add(s);
            }
        }
        System.out.println("found "+unavailable.size() + " unavailable songs!");
        return unavailable;
    }

    public String getRoot() {
        return root;
    }
    
    public String getTypeString() {
        return "Local Library";
    }

    public static String getNewName() {
        return JOptionPane.showInputDialog(null,
                "Please type in a name for your new Library", "New Library",
                JOptionPane.QUESTION_MESSAGE);
    }

    public boolean equals(Object o) {
        return (super.equals(o)/* && o instanceof LocalHost*/);    }
    
    public String getToolTipText() {
        StringPairList a = new StringPairList();
        a.addPair("Type", getTypeString());
        a.addPair("Location", root);
        a.addPair("Status", status_strings[status]);
        if (status >= Host.STATUS_CONNECTED && songs != null)
            a.addPair("Song Count", String.valueOf(getSongs().size()));
        if (status >= Host.STATUS_CONNECTED && playlists != null)
            a.addPair("Playlist Count", String.valueOf(playlists.size()));
        a.addPair("Read-Only",(isWritable()? "No" : "Yes"));
        return GITUtils.createPropertyLabel(a);
    }

    public void setSongs(EventList songs) {
        this.songs = songs;
    }
    
    public InputStream getSongStream(Song s) {
        // TODO Auto-generated method stub
        File f = new File(((LocalSong) s).getPath());
        try {
            return new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean isWritable() {
        return false;
    }
    
    public void getFilterStrings(List baseList) {
        // TODO Auto-generated method stub
        
    }

    public int getLibraryType() {
        if (this instanceof GITLibraryHost)
            return LocalHost.GIT_LIBRARY;
        else if (this instanceof IPodHost)
            return LocalHost.IPOD;
        else
            return LocalHost.ITUNES_XML;
    }
    
    public Collection getPlaylists() {
        return playlists;
    }
}