/*
 * Created on Aug 16, 2004
 * Created by wooo as part of git
 */
package org.git.player;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.git.GITProperties;
import org.git.client.Song;
import org.git.client.daap.DaapSong;
import org.git.client.local.LocalSong;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.time.TaskAllMovies;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTConstants4;
import quicktime.std.StdQTException;
import quicktime.std.clocks.ExtremesCallBack;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.media.DataRef;

/**
 * @author wooo
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class QTPlayer extends AbstractPlayer {
    static MovieController movieController;
    private double volume = 1.0;
    private PlayerListener playerListener;
    private static SongEndCallBack songEndCallBack;
    private Movie movie;
    public static final String SUPPORTED_FORMATS = "mp3 wav m4a aiff";
    /**
     * Creates an instance of the QTPlayer object, and attempts to initialize
     * quicktime, throwing an error if it isn't available
     * 
     * @throws PlayerException
     */
    public QTPlayer() {
        super();
        try {
            QTSessionCheck.check();
            setVolume((double)GITProperties.playerVolume / 100);
        } catch (QTException qte) {
            qte.printStackTrace();
        } catch (PlayerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setStatus(STOPPED);
    }

    class SongEndCallBack extends ExtremesCallBack {
        public SongEndCallBack(Movie m) throws QTException {
            super(m.getTimeBase(), StdQTConstants.triggerAtStop);
        }

        public void execute() {
//            System.out.println("SongEndCallBack.execute()");
            // clean up resources used by this callback
            try {
                stop();
            } catch (PlayerException e) {
                e.printStackTrace();
            } finally {
                cancelAndCleanup();
                fireSongFinished(new ActionEvent(QTPlayer.this,
                        0, "QTPlayer: song ended!"));
            }
        }
    }

    private static File tmpfile;
    private long sizeInBytes = 1;

    public synchronized void setVolume(double volume) throws PlayerException {
        try {
            this.volume = volume;
            if (movieController != null) {
                movieController.setVolume((float) volume);
            }
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    public void play(Song s) throws PlayerException {
        playingSong = s;
        if (s instanceof DaapSong) {
            play(s.getHost().getSongStream(s), "tmp." + s.getFormat());
        }
        else if (s instanceof LocalSong){
            LocalSong local = (LocalSong)s;
            play(new File(local.getPath()));
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see itunes.player.AbstractPlayer#play(java.io.InputStream)
     */
    public synchronized void play(InputStream is, String file) throws PlayerException {
//        this.stop();
        if (getStatus() != STOPPED) {
            return;
        }

        try {
            play(PlayerUtils.downloadToFile(is, file));
            
        } catch (IOException e) {
            setStatus(STOPPED);
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * @param myF
     * @throws PlayerException
     */
    public void play(File f) throws PlayerException {
        fireSongLoaded(new ActionEvent(QTPlayer.this,
                0, "QTPlayer: song loaded!"));
        setStatus(PLAYING);
        try {
            sizeInBytes = f.length();
            if (!f.exists()) {
                throw new PlayerException("File \"" + f.getCanonicalPath()
                        + "\" Error: File not found!");
            }
            DataRef ref = new DataRef("file:///" + f.getCanonicalPath());
            this.movie = Movie
                    .fromDataRef(ref, StdQTConstants4.newMovieAsyncOK);
            movieController = new MovieController(this.movie);
            movieController.setVolume((float)volume);
            /*
             * for (int i = 1; i <= movie.getTrackCount(); i++) {
             * System.out.println("track " + i); Track track =
             * movie.getTrack(i); System.out.println("got track"); Media media =
             * track.getMedia(); System.out.println("got media"); MediaHandler
             * handler = media.getHandler(); System.out.println("got handler"); }
             */
            TaskAllMovies.addMovieAndStart();
            this.movie.setActive(true);
            
            this.movie.start();
            movieController.play(1.0f);
//            setStatus(PLAYING);
            songEndCallBack = new SongEndCallBack(movie);
            songEndCallBack.callMeWhen();
            fireSongLoaded(new ActionEvent(this, 0,
                    "QTPlayer: song loaded from file"));
        } catch (QTException e) {
            setStatus(STOPPED);
            e.printStackTrace();
            throw new PlayerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            setStatus(STOPPED);
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itunes.player.AbstractPlayer#getProgress()
     */
    public int getProgress() {
        if (movie == null) {
            return 0;
        }
        try {
            return (movie.getTime()/movie.getTimeScale()*1000);
        } catch (QTException e) {
            e.printStackTrace();
            //            throw new PlayerException(e.getMessage(), e.getCause());
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itunes.player.AbstractPlayer#skipTo(int)
     */
    public synchronized void skipTo(int bytes) throws PlayerException {
        System.err.println("This is QTPlayer: call skipTo(double) instead!!");
    }

    public synchronized void skipTo(double fraction) throws PlayerException {
        if (movie == null)
            return;
        try {
            movieController.goToTime(new TimeRecord(movie.getTimeScale(), (long) ((double)fraction * (double)movie.getDuration())));
        } catch (Exception e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see itunes.player.AbstractPlayer#pause()
     */
    public synchronized void pause() throws PlayerException {
        if (getStatus() != PLAYING) 
            return;
        try {
            System.out.println("QT pause");
            movie.stop();
            setStatus(PAUSED);
        } catch (Exception e) {
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itunes.player.AbstractPlayer#resume()
     */
    public synchronized void resume() throws PlayerException {
        if (getStatus() != PAUSED)
            return;
        try {
            System.out.println("QT resume");
            movie.start();
            setStatus(PLAYING);
        } catch (Exception e) {
            setStatus(STOPPED);
            throw new PlayerException(e.getMessage(), e.getCause());
        }
    }
    
    public int getType() {
        return QTPLAYER;
    }
    
    public boolean supportsSong(Song s) {
        if (SUPPORTED_FORMATS.indexOf(s.format.toLowerCase()) == -1)
            return false;
        else
            return true;
    }
    
    public synchronized void stop() throws PlayerException {
        if (getStatus() != PLAYING && getStatus() != PAUSED) 
            return;
        setStatus(STOPPED);
        this.sizeInBytes = 1;
        try {
            if (movie != null) {
                movie.stop();
                this.movie.setActive(false);
            }
            TaskAllMovies.removeMovie();
            if (movieController != null) {
                movieController.removeMovie();
                movieController.disposeQTObject();
                movieController = null;
            }
            movie = null;
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
        
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlayerException(e.getMessage(), e.getCause());
        }
        // delete the temp file and kill all this stuff
    }
}