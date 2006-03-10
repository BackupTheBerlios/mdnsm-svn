/*
 SongDownload.java
 Created on May 27, 2004, 5:34 PM
 */
package org.git.downloader;

import java.io.File;
import java.util.ArrayList;

import org.git.GITProperties;
import org.git.client.Host;
import org.git.client.Song;
import org.git.client.StatusListener;
import org.git.client.local.LocalSong;
import org.git.client.swing.GetItTogether;

/**
 * @author Greg Jordan
 * @created August 6, 2004
 */

public class SongDownload  {
//    private Song song;
    // need isDup, ref. to song, 
//	private static Collection localSongs;
//	private SongDownload leader;
	
	private ArrayList status_listeners;
	
	public static final int STATUS_WAITING = 0;
	public static final int STATUS_PREPARING = 1;
	public static final int STATUS_DOWNLOADING = 2;
	public static final int STATUS_FINISHED = 3;
	// assume: Any state above STATUS_FINISHED is an error state.
	public static final int STATUS_ERROR = 4;
	public static final int STATUS_ALREADY_EXISTS = 5;
	public static final int STATUS_DUPLICATE = 6;
	public static final int STATUS_CANCELLED = 7;
	
	public String error_msg = new String();
	
	public static String[] status_strings = {"Waiting", "Preparing", "Downloading", "Finished", "Error", "Already exists", "Cancelled"};
	
	private int status;
	public int progress;
	public Song song;
	private final static char[]	badWindowsChars = { '\"', '/', '\\', '[', ']', ':', ';', '=', ',', '?'};
	private final static char[] badUnixChars = { '/' };
	private final static char[]	badFileChars =
	    (File.separatorChar == '/') ? badUnixChars : badWindowsChars;
	private final static char[]	badDirChars = {'.'};
	
	private final static char GOODFILECHAR = '_';
	private final static char GOODDIRCHAR = ' ';
	
	// windows invalid characters: . " / \ [ ] : ; = , ?
	// unix / > < ; maybe only want to do the / though...
	//	char[] chars = {'?', '/', '\\', '*', '\'', '>', '<', '|', '\"', ':'};
	
	
	public SongDownload(Song sng) {
//	    super(nayme);
	    status_listeners = new ArrayList();
	    setStatus(STATUS_WAITING);
//	    leader = ldr;
//	    localSongs = sgs;
	    song = sng;
//	    dummy_path = getDownloadPath(song).getPath();
	}

	protected boolean doesFileAlreadyExist() {
	    File f = getDownloadPath();
		if (f != null) {
		    if (f.exists() && f.length() == song.getSize())
		        return true;
		}
		return false;
	}
	
	protected boolean doWeWantToDownload() {
	    Song mySong = song;
		if (GITProperties.hideDRMProtected && mySong.format.equalsIgnoreCase("m4p"))
			return false;
		if (!GITProperties.tryToAvoidDups)
			return true;
		
		// CHECK FOR DUPLICATE USING THE SONG'S EQUALS() METHOD:
		boolean dup = GetItTogether.instance.localJPanel.source.contains(mySong);
		return !dup;
	}

	protected void checkForErrors() throws Exception {
	    try {
		    File dest = getDownloadPath();
		    if (getSong() instanceof LocalSong)
		    {
		        LocalSong song = (LocalSong)getSong();
		        File src = new File(song.getPath());
		        if (!src.exists())
		            throw new Exception("Source file does not exist!");
		        if (src.equals(dest))
		            throw new Exception("Source file is destination file!");
		    }
	    } catch (Exception e) {
	        throw e;
	    }
	    
	}
	
	protected File getDownloadPath() {
	    Song song = this.getSong();
		String filename = song.name + "." + song.format;
		//FIXME: store whether or not it is part of a compilation in the Song
		// object (already done for daapHost, but for ipod it isn't)
		boolean compilation = song.compilation;
		if (compilation) {
			if (song.artist.length() == 0) {
				filename = "Unknown Artist - " + filename;
			} else {
				filename = song.artist + " - " + filename;
			}
		}
		if (song.track > 0) {
			filename = (song.track < 10 ? "0" : "") + song.track + " " + filename;
		}
		
		filename = replaceChars(filename, badFileChars, GOODFILECHAR);

		// do the artist/album stuff
		String album = song.album;

		if (album.length() > 0) {
		    album = replaceChars(album, badFileChars, GOODFILECHAR);
		} else {
			album = "Unknown Album";
		} // end else

		String artist = song.artist;
		if (artist.length() > 0) {
		    artist = replaceChars(artist, badFileChars, GOODFILECHAR);
		} else {
			artist = "Unknown Artist";
		} // end else
		String path;
		File dir;
		File file;
		if (GITProperties.organizeDLdSongs) {
//		    artist = replaceChars(artist, badDirChars, GOODDIRCHAR);
//		    album = replaceChars(album, badDirChars, GOODDIRCHAR);
		    // TRY AND FIX SOME (WINDOWS-SPECIFIC?) DIRECTORY NAMING PROBLEMS:
		    while (artist.endsWith(".")) {
		        artist = artist.substring(0, artist.length() - 1);
		    }
		    while (album.endsWith(".")) {
		        album = album.substring(0, album.length() - 1);
		    }   
		    artist = artist.trim();
		    album = album.trim();
		    path = (compilation ? "Compilations" : artist) + File.separator + album + File.separator;
			dir = new File(GITProperties.dlDir + File.separator + path);
		} else {
			dir = new File(GITProperties.dlDir); 
			filename = (song.track < 10 ? "0" : "") + song.track + " " + (compilation ? "Compilation" : artist) + " - " + album + " - " + filename;
		}
		dir.mkdirs();
		file = new File(dir, filename);
		System.out.println(file.getPath());
		// using the GITProperties, find the download location,

//		System.out.println("file is: \"" + file.getPath() + "\"");
		return file;
		// find if they want to organize the downloads by directory
		// fix the filename to fit the allowed filenames for the platform
		// maybe have a "convert bad chars to" option in the prefs?
		// don't overwrite it if it is the same size...
		// maybe don't save it if it is protected? FIXME: this should probably be
		// done at an earlier level, such as not even showing protected files?

	}

	public String replaceChars(String s, char[] badchars, char replacement) {
	    for (int i=0; i < badchars.length; i++) {
	        s = s.replace(badchars[i], replacement);
	    }
	    return s;
	}
	
	public int getProgress() {
	    return progress;
	}
	
	public Song getSong() {
		return song;
	}

	public int getStatus() {
	    return status;
	}
	
	public void setStatus(int s) {
	    status = s;
	    fireStatusChanged();
	}
	
	protected void setError(String message) {
	    setStatus(SongDownload.STATUS_ERROR);
	    error_msg = message;
	}
	
	
public void fireStatusChanged() {
    for (int i=0; i < status_listeners.size(); i++) {
        StatusListener h = (StatusListener)status_listeners.get(i);
        h.stateUpdated(this);
    }
}

public void addStatusListener(StatusListener sl) {
    status_listeners.add(sl);
}

public boolean removeStatusListener(StatusListener sl) {
    return status_listeners.remove(sl);
}

/**
 * @return
 */
public Host getHost() {
    // TODO Auto-generated method stub
    
    return song.getHost();
}

}