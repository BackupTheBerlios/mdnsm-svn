/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.git.GITProperties;
import org.git.client.Song;
import org.git.client.daap.DaapSong;
import org.git.client.local.LocalSong;
import org.git.client.swing.GetItTogether;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExternalPlayer extends AbstractPlayer {


    char SEP = File.separatorChar;
    public ArrayList processes = new ArrayList();
    
    public ExternalPlayer(String cmd) {
        super();
        this.type = AbstractPlayer.EXTERNALPLAYER;
        this.status = AbstractPlayer.STOPPED;
    }
    
    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#play(org.git.client.Song)
     */
    public void play(Song s) throws PlayerException {
        // TODO Auto-generated method stub
        if (s instanceof LocalSong) {
            LocalSong local = (LocalSong)s;
            play(new File(local.getPath()));
        } else if (s instanceof DaapSong) {
            play(s.getHost().getSongStream(s), "tmp." + s.getFormat());
        }
        
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#play(java.io.InputStream, java.lang.String)
     */
    public void play(InputStream is, String name) throws PlayerException {
        // TODO Auto-generated method stub
        try {
            File f = PlayerUtils.downloadToFile(is, name);
            play(f);
        } catch (IOException e) {
            setStatus(STOPPED);
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#play(java.io.File)
     */
    public void play(File f) throws PlayerException {
        // TODO Auto-generated method stub
        Runtime rt = Runtime.getRuntime();
        try {
            String path = "\""+f.getPath()+"\"";
            System.out.println(path);
            String parsed = GITProperties.externalProg.concat(" "+path);
            System.out.println("running process: "+parsed);
            Process p = rt.exec(parsed);
            setStatus(AbstractPlayer.PLAYING);
            processes.add(p);
            int exitVal = p.waitFor();
            System.out.println("Process "+p.toString() + " exited with code: "+exitVal);
            processes.remove(p);
            setStatus(AbstractPlayer.STOPPED);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading the external player.  Please make sure you set the correct path.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            setStatus(STOPPED);
            e.printStackTrace();
        }
    }
    
    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#getProgress()
     */
    public int getProgress() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#skipTo(int)
     */
    public void skipTo(int bytes) throws PlayerException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#pause()
     */
    public void pause() throws PlayerException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#resume()
     */
    public void resume() throws PlayerException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#stop()
     */
    public void stopAll() throws PlayerException {
        // TODO Auto-generated method stub
        if (processes.size() > 0) {
            System.err.println("Stopping external players...");
            for (int i=0; i < processes.size(); i++) {
                Process p = (Process) processes.get(i);
                p.destroy();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#setVolume(double)
     */
    public void setVolume(double value) throws PlayerException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#supportsSong(org.git.client.Song)
     */
    public boolean supportsSong(Song s) {
        // TODO Auto-generated method stub
        return true;
    }
    
    public int getStatus() {
        if (processes.size() > 0)
            return AbstractPlayer.PLAYING;
        else
            return status;
    }

    /* (non-Javadoc)
     * @see org.git.player.AbstractPlayer#stop()
     */
    public void stop() throws PlayerException {
        // TODO Auto-generated method stub
        stopAll();
    }
    
}
