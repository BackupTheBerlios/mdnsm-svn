/*
 * Created on Jan 21, 2005
 * Created by wooo as part of git
 */



package org.git.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import javax.swing.Timer;

import org.git.GITProperties;
import org.git.client.Host;
import org.git.client.Song;
import org.git.client.daap.DaapHost;
import org.git.client.local.LocalHost;
import org.git.client.local.LocalSong;
import org.git.client.swing.GetItTogether;

/**
* @author Greg Jordan
* @created August 6, 2004
*/

public class DownloadThread extends Thread {
    public static Semaphore sem = new Semaphore(0);
	static SongDownload curSong;
	
	private static final int UPDATE_DELAY = 20;
	int dlnum = 0;
	
	Timer progressTimer = new Timer(UPDATE_DELAY, new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        curSong.fireStatusChanged();
	    }
	});
    private boolean stopDownloading = false;;
	
	public void run() {
	    stopDownloading = false;
	    dlnum = 0;
	    while (true) {
	        if (stopDownloading == true) 
	            return;
	        sem.acquire();
	        if (dlnum < DownloadManager.downloads.size()) {
	            curSong = (SongDownload) DownloadManager.downloads.get(dlnum);
	            dlnum++;
	            int stat = curSong.getStatus();
	            if (stat != SongDownload.STATUS_WAITING) {
	                sem.release(); // skip this...
	                continue;
	            }
	            if (!curSong.doWeWantToDownload()) {
	                curSong.setStatus(SongDownload.STATUS_DUPLICATE);
	            } else if (curSong.doesFileAlreadyExist()) {
	                curSong.setStatus(SongDownload.STATUS_ALREADY_EXISTS);
	            } else {
	                downloadSong(curSong);
	            }
	        }
	        
	    }
	    
	}
	
	// ASSUMES: if this is called, we do want to download the song!
	// RETURNS: true if download succeeded.
	public boolean downloadSong(final SongDownload dl) {
	    dl.setStatus(SongDownload.STATUS_DOWNLOADING);
	    progressTimer.restart();
	    try {
	        dl.checkForErrors();
	        File destinationFile = dl.getDownloadPath();
	        Host host = dl.getHost();
	        if (host instanceof LocalHost) {
	            File sourceFile = new File(((LocalSong) dl.getSong()).getPath());
	            System.out.println("downloading file...");
	            downloadFile(destinationFile,sourceFile);
	        } else if (host instanceof DaapHost) {
	            DaapHost daap = (DaapHost)host;
	            daap.login(false);
	            downloadStream(destinationFile,host.getSongStream(dl.getSong()));
	            daap.logout();
	        } else {
	            downloadStream(destinationFile,host.getSongStream(dl.getSong()));
	        }
	        GITProperties.numDownloads ++;
	        dl.setStatus(SongDownload.STATUS_FINISHED);
	        return true;
	    } catch (IOException e) {
	        dl.setError("IO Exception!");
	        e.printStackTrace();
	        return false;
	    } catch (Exception e) {
	        dl.setError(e.getMessage());
	        e.printStackTrace();
	        return false;
	    } finally {
	        progressTimer.stop();
	        System.err.println("finished!");
	        notifyFinished();
	    }
	}

	public synchronized void notifyFinished() {
	    notifyAll();
	}
	
	public synchronized void waitForDownloadToFinish() {
	    stopDownloading = true;
	    try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * @param destinationFile
	* @param mySongStream
	*/
	private void downloadStream(File destinationFile, InputStream mySongStream) throws IOException{
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFile));
		final  InputStream in = mySongStream;
		curSong.progress = 0;
		int x;
		while ((x = in.read()) != -1) {
			curSong.progress++;
			out.write(x);
		}
		curSong.progress = 0;
		out.close();
		out = null;
	}
	
	/**
	* @param destinationFile
	* @param sourceFile
	*/
	private void downloadFile(File destinationFile, File sourceFile) throws IOException {
			FileInputStream fis;
			fis = new FileInputStream(sourceFile);  // might throw a FileNotFoundException
			destinationFile.createNewFile();  // might throw an IOException
			FileOutputStream fos = new FileOutputStream(destinationFile);
			FileChannel fcin = fis.getChannel();
			FileChannel fcout = fos.getChannel();
			fcin.transferTo(0, fcin.size(), fcout); // finish up fcin.close();
			fcout.close();
			fis.close();
			fos.close();
	}

	private void downloadToIpod(Song s) {
	    
	    
	}
	
}

class Semaphore {
    private int count;
    public Semaphore(int n) {
       this.count = n;
    }
    
    

    public synchronized void acquire() {
       while(count == 0) {
          try {
             wait();
          } catch (InterruptedException e) {
             //keep trying
          }
       }
       count--;
    }
   
    public synchronized void release() {
       count++;
       notify(); //alert a thread that's blocking on this semaphore
    }
 }