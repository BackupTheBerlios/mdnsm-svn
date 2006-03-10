/*
 * Created on Aug 16, 2004
 * Created by wooo as part of git
 */
package org.git.player;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import org.git.client.Song;

/**
 * @author wooo
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPlayer {

    public static final int ERROR = -1;
    public static final int STOPPED = 0;
    public static final int PLAYING = 1;
    public static final int PAUSED = 2;
//    public static final int LOADING = 3;
    public static final int JAVAPLAYER = PlayerUtils.JAVAPLAYER;
    public static final int QTPLAYER = PlayerUtils.QTPLAYER;
    public static final int EXTERNALPLAYER = PlayerUtils.EXTERNALPLAYER;
    public Vector playerListeners = new Vector();
    protected int status, type;
    public Song playingSong;
    
    public synchronized void addPlayerListener(PlayerListener l) {
        playerListeners.add(l);
    }

    public synchronized boolean removePlayerListener(PlayerListener l) {
        return playerListeners.remove(l);
    }

    public synchronized void fireStatusUpdated(int st) {
        for (int i=0; i < playerListeners.size(); i++) {
            ((PlayerListener)playerListeners.get(i)).statusUpdated(this, st);
        }
    }
    
    public void fireSongFinished(ActionEvent e) {
        for (int i=0; i < playerListeners.size(); i++) {
            ((PlayerListener)playerListeners.get(i)).songFinished(e);
        }
    }
    
    public void fireSongLoaded(ActionEvent e) {
        for (int i=0; i < playerListeners.size(); i++) {
            ((PlayerListener)playerListeners.get(i)).songLoaded(e);
        }
    }
    
    public abstract void play(Song s) throws PlayerException;
    
    /**
     * begins playing a song contained in the given input stream
     * 
     * @param is
     *            the input stream representing the song
     * @throws PlayerException
     *             thrown if any type of error is encountered
     */
    public abstract void play(InputStream is, String name)
            throws PlayerException;

    /**
     * begins playing a song contained in the given File
     * 
     * @param f
     *            the file containing the song to play
     * @throws PlayerException
     *             thrown if any type of error is encountered
     */
    public abstract void play(File f) throws PlayerException;

    /**
     * query the song's progress in being played
     * 
     * @return an integer from 0 to 100 representing the percent of the song
     *         played
     */
    public abstract int getProgress();

    /**
     * skip playback of the current song to the specified postion
     * 
     * @param pos
     *            the position in the file to skip to (from 1 to 100)
     * @throws PlayerException
     */
    public abstract void skipTo(int bytes) throws PlayerException;

    /**
     * pauses playback of the current song where it is
     *  
     */
    public abstract void pause() throws PlayerException;

    /**
     * resumes playback of the currently paused song where it left off
     * 
     * @throws PlayerException
     */
    public abstract void resume() throws PlayerException;

    /**
     * stops playback of the current song, and destroys this player object,
     * releasing any resources (deleting temp files, etc.)
     */
    public abstract void stop() throws PlayerException;

    /**
     * adjusts the volume within that player to value
     */

    public abstract void setVolume(double value) throws PlayerException;

    public abstract boolean supportsSong(Song s);
    
    protected void setStatus(int st) {
        status = st;
        fireStatusUpdated(st);
    }
    
    /**
     * @return
     */
    public int getStatus() {
        return status;
    }
    
    public int getType() {
        return type;
    }
    
}

