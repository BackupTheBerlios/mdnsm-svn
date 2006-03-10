/*
 * Created on Feb 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.git.GITProperties;
import org.git.GITUtils;
import org.git.client.Playlist;
import org.git.client.Song;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.impl.ThreadSafeList;
import ca.odell.glazedlists.swing.EventSelectionModel;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QueuePlaylist extends Playlist {

    private static ArrayList queueRandomPlayed = new ArrayList();
    public static Stack history = new Stack();
    public static EventList queue = new ThreadSafeList(new BasicEventList());
    public EventSelectionModel selectionModel;
    public static Logger log = Logger.getLogger(QueuePlaylist.class.getName());
    {
        log.setLevel(Level.ALL);
    }
    private GetItTogether git;

    private static int MAX_RANDOM_TRIES = 100;
    
    public QueuePlaylist() {
        super();
        git = GetItTogether.instance;
    }
        
    public Collection getSongs() {
        return queue;
    }
    
    public void appendQueueAndPlay(Collection songs) {
        int first_new_song_index = queue.size();
        enqueueSongs(songs);
        playQueueAt(first_new_song_index);
    }
    
    public void clearQueueAndPlay(Collection songs) {
        queue.clear();
        queue.addAll(songs);
        playQueueAt(0);
    }
    
    public void enqueueSongs(Collection songs) {
        queue.addAll(songs);
    }
    
    public void playNext() {
        switch (GITProperties.shuffleValue) {
        case GetItTogether.RANDOM_OFF:
            // first play through all the queued songs:
            if (playNextFrom(queue)) {
                log.finest("playing from playlist");
                return;
            }
            else if (playNextFrom(git.getVisibleEventList())) {
                log.finest("playing from visible list");
                return;
            }
            else if (playNextFrom(GetItTogether.instance.daapJPanel.visible)) {
                log.finest("playing from daap list");
                return;
            }
            else if (playNextFrom(GetItTogether.instance.localJPanel.visible)) {
                log.finest("playing from list");
                return;
            }
            break;
        case GetItTogether.RANDOM_PLAYLIST:
            if (queue.size() <= 1) {
                // drop down to the RANDOM_BROWSER case.
            } else if (playRandomFrom(queue)) {
                log.finest("playing random from queue");
                return;
            }
        case GetItTogether.RANDOM_BROWSER:
            if (playRandomFrom(git.getVisibleEventList())) {
                log.finest("playing random from visible list");
            } else if (playRandomFrom(GetItTogether.instance.daapJPanel.visible)) {
                log.finest("playing random from daap list");
            } else if (playRandomFrom(GetItTogether.instance.daapJPanel.visible)) {
                log.finest("playing random from local list");
            } else {
                log.finest("couldn't find random song!");
            } return;
        default:
            playNextFrom(git.getVisibleEventList());
            break;
        }
        
        // last ditch if we couldn't play anything: play from the visible list:
        if (!playNextFrom(git.getVisibleEventList()))
            playFirstVisible();
    }
    
    public void playPrevious() {
        if (history.size() > 0) {
            log.finest("Playing previous song from history");
            if (history.peek() == git.playingSong)
                history.pop();
            playSongNoHistory((Song)history.pop());
        } else
            log.finest("No history to play song from!");
    }
    
    private void playQueueAt(int ind) {
        if (ind >= queue.size() || queue.size() == 0) {
            log.info("Tried to play at an invalid queue index! Playing next song instead...");
            playNext();
            return;
        }
        Song s = (Song)queue.get(ind);
        playSong(s);
    }
    
    private boolean playNextFrom(EventList list) {
        int lastPlayed = GITUtils.hardIndexOfSong(list, git.playingSong);
        if (lastPlayed > -1 && lastPlayed+1 < list.size()) {
            playSong((Song)list.get(lastPlayed+1));
            return true;
        } else
            return false;
        
    }
    
    private boolean playFirstVisible() {
        EventList list = git.getVisibleEventList();
        if (list.size() == 0) {
            log.info("no visible songs!");
            return false;
        }
        else {
            playSong((Song)list.get(0));
            return true;
        }
    }
    
    // returns true if a suitable random song was found, otherwise false.
    private boolean playRandomFrom(EventList original) {
        EventList list;
        if (original == queue) {
            log.finest("playing random from queue!");
            EventList temp = new BasicEventList();
            temp.addAll(queue);
            temp.removeAll(queueRandomPlayed);
            System.out.println(queueRandomPlayed);
            if (temp.size() == 0) {
                queueRandomPlayed.clear();
                list = original;
            } else
                list = temp;
        } else
            list = original;
        int max = list.size();
        if (list.size() == 0) {
            log.warning("tried to play random from an empty list");
            return false;
        }
        Random rand = new Random();
        int playme;
        int i = 0;
        Song s;
        do { // for random play, we won't play songs marked as unavailable or not supported.
            playme = rand.nextInt(max);
            s = (Song)list.get(playme);
            i++;
            if (i >= MAX_RANDOM_TRIES)
                log.warning("TROUBLE SEARCHING FOR RANDOM SONG!");
        } while (i < MAX_RANDOM_TRIES && (s.status != Song.STATUS_OK) || !GetItTogether.player.supportsSong(s));
        playSong(s);
        if (original == queue)
            queueRandomPlayed.add(s);
        return true;
    }
    
    private void playSongNoHistory(Song s) {
        git.playSong(s);
    }
    
    public void playSong(Song s) {
        git.playSong(s);
        history.push(s);
    }
    
}

class QueueTableFormat implements TableFormat {

    public int getColumnCount() {
        return 4;
    }
    
    public String getColumnName(int col) {
        switch (col) {
        	case 0:
        	    return "#";
        	case 1:
        	    return "Song";
    	    case 2:
    	        return "Size";
	        case 3:
	            return "Status";
            default:
                return "Other";
        }
    }
    
    public Object getColumnValue(Object o, int col) {
        if (o == null) return "";
        return (Song)o;
    }    
}