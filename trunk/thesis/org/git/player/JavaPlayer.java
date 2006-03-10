/*
 Created on May 17, 2003
 To change the template for this generated file go to
 Window>Preferences>Java>Code Generation>Code and Comments
 Copyright 2003 Joseph Barnett
 This File is part of "one 2 oh my god"
 "one 2 oh my god" is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 Free Software Foundation; either version 2 of the License, or
 your option) any later version.
 "one 2 oh my god" is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with "one 2 oh my god"; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.git.player;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import org.git.GITProperties;
import org.git.client.Song;
import org.git.client.daap.DaapSong;
import org.git.client.local.LocalSong;
import org.git.client.rss.PodcastSong;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * @author jbarnett To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 * @created
 */
public class JavaPlayer extends AbstractPlayer {
    //private One2OhMyGod prog;
    protected long time;
    private double volume;
    protected BasicPlayer player;
    private PlayerListener playerListener;   
    public static final String SUPPORTED_FORMATS = "mp3 wav";
    public static final double MAX_VOLUME = .699;
    public static final int type = PlayerUtils.JAVAPLAYER;
    
    public static boolean DEBUG = true;
    
    private static Logger log = Logger.getLogger(JavaPlayer.class.getName());
    
    private BasicPlayerListener bpl = new BasicPlayerListener() {
        /*
         * (non-Javadoc)
         * 
         * @see javazoom.jlgui.basicplayer.BasicPlayerListener#opened(java.lang.Object,
         *      java.util.Map)
         */
        public void opened(Object stream, Map properties) {
            // TODO Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         * 
         * @see javazoom.jlgui.basicplayer.BasicPlayerListener#progress(int,
         *      long, byte[], java.util.Map)
         */
        public void progress(int bytesread, long microseconds, byte[] pcmdata,
                Map properties) {
            // TODO Auto-generated method stub
            time = microseconds;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javazoom.jlgui.basicplayer.BasicPlayerListener#stateUpdated(javazoom.jlgui.basicplayer.BasicPlayerEvent)
         */
        public void stateUpdated(BasicPlayerEvent event) {
            // TODO Auto-generated method stub
            switch (event.getCode()) {
            	case BasicPlayerEvent.EOM:
            	    setStatus(STOPPED);
            		fireSongFinished(new ActionEvent(JavaPlayer.this, 0,
            		        "JavaPlayer: song ended!"));
            		break;
        		case BasicPlayerEvent.OPENING:
//        		    setStatus(LOADING);
        			break;
    			case BasicPlayerEvent.PLAYING:
    			    setStatus(PLAYING);
    				break;
				case BasicPlayerEvent.STOPPED:
				    setStatus(STOPPED);
					break;
				case BasicPlayerEvent.PAUSED:
				    setStatus(PAUSED);
					break;
				case BasicPlayerEvent.UNKNOWN:
				    setStatus(STOPPED);
					break;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see javazoom.jlgui.basicplayer.BasicPlayerListener#setController(javazoom.jlgui.basicplayer.BasicController)
         */
        public void setController(BasicController controller) {
            // TODO Auto-generated method stub

        }
    };
    
    public JavaPlayer() {
        status = STOPPED;
        player = new BasicPlayer();
        player.addBasicPlayerListener(bpl);
        volume = (double) GITProperties.playerVolume / 100 * MAX_VOLUME;
    }

    public synchronized void pause() throws PlayerException {
        if (getStatus() != PLAYING) 
            return;
        try {
            player.pause();
            setStatus(PAUSED);
        } catch (BasicPlayerException e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }


    public synchronized void resume() throws PlayerException {
        if (getStatus() != PAUSED) 
            return;
        try {
            this.player.resume();
            setStatus(PLAYING);
        } catch (BasicPlayerException e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public void play(Song s) throws PlayerException {
        int status = player.getStatus();
        if (status != BasicPlayer.STOPPED && status != BasicPlayer.UNKNOWN) {
            log.warning("player not stopped!");
            return;
        }
        
        if (s instanceof LocalSong) {
            File file = new File(((LocalSong) s).getPath());
            System.out.println("stopping...");
            stop();
            System.out.println("playing...");
            play(file);
        } else if (s instanceof PodcastSong) {
            URL url = ((PodcastSong)s).url;
            System.out.println("stopping...");
            stop();
            System.out.println("playing...");
            play(url);
        } else {
            System.out.println("stopping...");
            stop();
            System.out.println("playing...");
            play(s.getHost().getSongStream(s), null);
        }
        playingSong = s;
    }
    
    public void play(File f) throws PlayerException {
        log.finest("playing file "+f.getName());
        int status = player.getStatus();
        if (status != BasicPlayer.STOPPED && status != BasicPlayer.UNKNOWN) 
        {
            log.warning("player is not stopped!");
            return;
        }
        BasicController control = (BasicController) player;

        try {
            log.finest("control.open");
            control.open(f);
            log.finest("control.play");
            control.play();
            log.finest("control.setGain");
            control.setGain(volume);
            control.setPan(0.0);
            time = 0;
        } catch (BasicPlayerException e) {
            setStatus(AbstractPlayer.ERROR);
            try {
                stop();
            } catch (PlayerException g) {
                System.err.println(g.getMessage());
            }
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public void play(URL url) throws PlayerException {
        BasicController control = (BasicController)player;
        
        try {
            control.open(url);
	        control.play();
	        control.setGain(volume);
        } catch (BasicPlayerException e) {
            setStatus(AbstractPlayer.ERROR);
            try {
                stop();
            } catch (PlayerException e2) {
                e2.printStackTrace();
            }
            throw new PlayerException(e.getMessage(),e.getCause());
        }
    }
    
    public void play(InputStream f, String ignored_in_javaplayer) throws PlayerException {
        int status = player.getStatus();
//        if (status != BasicPlayer.STOPPED && status != BasicPlayer.UNKNOWN) 
//        {
//            System.out.println("player is not stopped!");
//            return;
//        }
//        setStatus(LOADING);
        
        BasicController control = (BasicController) player;
        try {
//            setStatus(PLAYING);
            control.open(f);
            control.play();
            control.setGain(volume);

//            control.setPan(0.0);
            time = 0;
        } catch (BasicPlayerException e) {
            setStatus(AbstractPlayer.ERROR);
            try {
                stop();
            } catch (PlayerException g) {
                g.printStackTrace();
            }
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public void stop() throws PlayerException {
        try {
            if (player != null) {
                player.stop();
                time = 0;
            }
            
        } catch (BasicPlayerException e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public synchronized void skipTo(int bytes) throws PlayerException {
        System.out.println(bytes);
        try {
            long realBytes = player.seek(bytes);
            System.out.println(realBytes);
            setVolume(volume);
        } catch (BasicPlayerException e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public void setVolume(double volume) throws PlayerException {
        try {
            //double vol = (double) prog.volume / 200;
            this.volume = volume * MAX_VOLUME;
//            System.out.println(this.volume);
            if (player != null && player.hasGainControl()) {
//                System.out.println(player.getMaximumGain());
                player.setGain(this.volume);
            }
        } catch (BasicPlayerException e) {
            e.printStackTrace();
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public int getType() {
        return JAVAPLAYER;
    }
    
    public boolean supportsSong(Song s) {
        if (SUPPORTED_FORMATS.indexOf(s.format.toLowerCase()) == -1)
            return false;
        else
            return true;
    }
    
    public int getStatus() {
        return status;
    }
    
    public int getProgress() {
        return (int)time/1000;
    }

}

