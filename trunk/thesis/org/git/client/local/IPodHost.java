/*
 * Created on Mar 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.git.GITProperties;
import org.git.client.Host;

import ca.odell.glazedlists.BasicEventList;
import de.axelwernicke.mypod.Backend;
import de.axelwernicke.mypod.Preferences;
import de.axelwernicke.mypod.ipod.ITunesDB;
import de.axelwernicke.mypod.ipod.ITunesDBContentItem;
import de.axelwernicke.mypod.ipod.ITunesDBParser;
import de.axelwernicke.mypod.ipod.ITunesDBPlaylistHeader;
import de.axelwernicke.mypod.ipod.ITunesDBPlaylistItem;
import de.axelwernicke.mypod.ipod.ITunesDBSongItem;
import de.axelwernicke.mypod.ipod.ITunesDBSonglistHeader;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IPodHost extends LocalHost implements FileListener{

    public FileMonitor fm;
    
    public IPodHost(String name, String root) {
        super(name, root);
        createIpodMonitor();
    }
    
    public void connect() {
        if (!validateIpodDirectory(new File(root)))
        {
            if (GITProperties.showConnectionErrorPanels)
                showErrorConnectingPanel();	
            return;
        }
//        Backend backend = new Backend();
//        Preferences p  = backend.getPreferences();
//        // String s       = prog.ipodLocation;
//        p.setIPodPath(root);
        ITunesDBParser parser = new ITunesDBParser();
        System.err.println("loading songs...");
        ITunesDB result = parser.load(new File(root));
        System.err.println("loaded!");
        result.getTotalFilesize();
        ITunesDBSonglistHeader songlist = result.getSonglistHeader();
        ITunesDBSongItem songItem = null;
        ITunesDBContentItem contentItem = null;
        // iterate over all songs
        int end = songlist.getSongCount();
        BasicEventList to_return = new BasicEventList();
        for (int i = 0; i < end; i++) {
            // get current song item and check if its our file index
            songItem = songlist.getSongItem(i);
            boolean found = false;
            int count = 0;
            String path = "";
            String title = "";
            String album = "";
            String artist = "";
            int track = songItem.getTrackNumber();
            int size = songItem.getFilesize();
            int time = songItem.getDuration();
            String genre = "";
            String type = "";
            java.util.Vector content = songItem.getContent();
            int total = content.size();
            for (int j = 0; (
                    /*
                     * count < 5 &&
                     */
                    j < total); j++) {
                contentItem = (ITunesDBContentItem) content.get(j);
                switch (contentItem.getContentTyp()) {
                case ITunesDBContentItem.PATH: {
                    path = contentItem.getContentAsString();
                    count++;
                    break;
                }
                case ITunesDBContentItem.ALBUM: {
                    album = contentItem.getContentAsString();
                    count++;
                    break;
                }
                case ITunesDBContentItem.TITEL: {
                    title = contentItem.getContentAsString();
                    count++;
                    break;
                }
                case ITunesDBContentItem.ARTIST: {
                    artist = contentItem.getContentAsString();
                    count++;
                    break;
                }
                case ITunesDBContentItem.GENRE: {
                    genre = contentItem.getContentAsString();
                    count++;
                    break;
                }
                default:
                    continue;
                }
            } // for all content items
            File file = new File(root);
            String filename = path.substring(1);
            // + File.separator);
            if (File.separator.equals("\\")) {
                filename = filename.replaceFirst(":", "\\\\");
                filename = filename.replaceFirst(":", "\\\\");
                filename = filename.replaceFirst(":", "\\\\");
            } else {
                filename = filename.replaceFirst(":", File.separator);
                filename = filename.replaceFirst(":", File.separator);
                filename = filename.replaceFirst(":", File.separator);
            } // end else
            file = new File(file.getPath() + File.separator + filename);
            LocalSong song = new LocalSong(this);
            song.host = this;
            song.path = file.getPath();
            song.name = title;
            song.album = album;
            song.artist = artist;
            song.track = track;
            song.genre = genre;
            song.time = time;
            song.size = size;
            // path = path.replace
            // File.separator;
            int ind = path.lastIndexOf(".") + 1;
            song.format = path.substring(ind, path.length());
            to_return.add(song);
        } // for all song items
        songs.addAll(to_return);
        ITunesDBPlaylistHeader playlists = result.getPlaylistHeader();
        for (int i=0; i < playlists.getPlaylistCount(); i++) {
            ITunesDBPlaylistItem playlist = playlists.getPlaylist(i);
            LocalPlaylist lp = new LocalPlaylist(this);
            lp.name = playlist.toString();
            Vector content = playlist.getSongItems();
            for (int j=0; i < content.size(); j++) {
                ITunesDBContentItem item = (ITunesDBContentItem)content.get(j);
                System.out.println(item.getContentTyp());
            }
        }
    }
    
    public boolean validateIpodDirectory(File f) {
        if (!f.exists() || !f.isDirectory() || !f.canRead())
            return false;
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].getName().equals("iPod_Control")) {
                return true;
            }
        }
        return false;
    }
    
    public String getErrorConnectingString() {
        return new String("<html><font size='+4'><center><strong>Ipod not found!</strong></center></font>" +
        		"<ul compact>" +
        		"<li>If this is your first time running GIT, you need to set your <strong>ipod root directory</strong>.</li>" +
        		"<li>If you just plugged in your iPod, wait <em>15-20 seconds</em> and try again." +
        		"</ul>" +
        		"The <strong>ipod root directory</strong> should be the location that contains the following sub-directories:" +
        		"<font size='2'><ul>" +
        		"<li>Calendars</li>" +
        		"<li>Contacts</li>" +
        		"<li>Notes</li>" +
        		"<li>iPod_Control</li>"+
        		"</ul></font>" +
        		"You can either:" +
        		"<ul><li><strong>Search for it yourself,</strong> or</li>" +
        		"<li><strong>Do nothing</strong>.</li></ul>"+
                "Would you like to manually locate your <strong>ipod root directory</strong>?"
        		);
    }
    
    public void showErrorConnectingPanel() {
        String message = getErrorConnectingString();
        Object[] options = {"Search for my iPod Directory","Do nothing"};
        int result = JOptionPane.showOptionDialog(null,
                message,
                "iPod Not Found",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        switch (result) {
        case JOptionPane.YES_OPTION:
            String fileResult = showFileChooser(JFileChooser.DIRECTORIES_ONLY,"Please locate your iPod directory.",new DirectoryFileFilter());
        System.out.println(fileResult);
        if (fileResult != null) {
            root = fileResult;
            connect();
        }
        break;
        case JOptionPane.NO_OPTION:
        default:
            break;
        }
    }
    
    public void createIpodMonitor() {
            if (fm != null)
                fm.stop();
            System.out.println("creating file...");
            File f = new File(root+"iPod_Control"+File.separator+"Device"+File.separator+"SysInfo");
            System.out.println("creating FileMonitor...");
            fm = new FileMonitor(IPOD_MONITOR_DELAY);
            System.out.println("adding file...");
            fm.addFile(f);
            fm.addListener(this);
    }
    
    public String getTypeString() {
        return "iPod";
    }

    public void fileChanged(File file) {
        if (file.exists() && status == Host.STATUS_NOT_CONNECTED)
            connect();
    }
}
