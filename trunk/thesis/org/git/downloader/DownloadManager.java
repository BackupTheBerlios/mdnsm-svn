/*
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.downloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.git.GITProperties;
import org.git.client.Song;
import org.git.client.StatusListener;
import org.git.client.local.LocalSong;
import org.git.client.swing.GetItTogether;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.impl.ThreadSafeList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadManager implements StatusListener{

    public static DownloadThread dlThread;
    protected static String[] columnNames = {"#", "Song", "Size", "Status"};
    
    protected GetItTogether prog;
    public static EventList downloads = new ThreadSafeList(new BasicEventList());
    public EventTableModel tableModel = new EventTableModel(downloads, new DownloadTableFormat());
    public EventSelectionModel selectionModel = new EventSelectionModel(downloads);
    
    
    
    public DownloadManager(GetItTogether git) {
        prog = git;
    }
    
    public void addDownload(Song s) {
        
        if (dlThread == null) {
            dlThread = new DownloadThread();
            dlThread.start();
         }
        
        // how about we check for the duplicate song here, still add it to the queue, but marked as dup
        // that way we can skip over it by default, but let them say "download me for sure" if they really want it?
        SongDownload dl = new SongDownload(s/*, String.valueOf(num_downloads)*/);
        dl.addStatusListener(this);
        add(dl);
        DownloadThread.sem.release();
        
    }
    
    public Collection getDownloads() {
        ArrayList a = new ArrayList();
        for (int i=0; i < downloads.size(); i++) {
            a.add(downloads.get(i));
        }
        return a;
    }
    
    public int getDownloadsRemaining() {
        int count = 0;
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            if (d.getStatus() < SongDownload.STATUS_FINISHED) {
                count++;
            }
        }
        return count;
    }
    
    //WRAPPER METHODS:
    
    public void add(SongDownload dl) {
        downloads.add(dl);
    }
    
    public void remove(SongDownload dl) {
        downloads.remove(dl);
        if (dl.getStatus() < SongDownload.STATUS_FINISHED)
            ;
    }
    
    // RETRY METHODS:
    
    public void retryCancelled() {
        retryByStatus(SongDownload.STATUS_CANCELLED);
    }
    
    public void retryError() {
        retryByStatus(SongDownload.STATUS_ERROR);
    }

    public void retryFailed() {
        // failed downloads = cancelled or error.
        retryByStatus(SongDownload.STATUS_CANCELLED);
        retryByStatus(SongDownload.STATUS_ERROR);
    }
    
    public void retryByStatus(int status) {
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            if (d.getStatus() == status)
            {
                d.setStatus(SongDownload.STATUS_WAITING);
                dlThread.dlnum = 0;
                DownloadThread.sem.release();
            }
        }
        tableModel.fireTableDataChanged();
    }
    
    // REMOVE METHODS:
    
    public void removeError() {
        removeByStatus(SongDownload.STATUS_ERROR);
    }
    
    public void removeFinished() {
        removeByStatus(SongDownload.STATUS_FINISHED);
        removeByStatus(SongDownload.STATUS_DUPLICATE);
        removeByStatus(SongDownload.STATUS_ALREADY_EXISTS);
    }

    public void removeFailed() {
        // Error and Cancelled downloads are considered failed:
        removeByStatus(SongDownload.STATUS_CANCELLED);
        removeByStatus(SongDownload.STATUS_ERROR);
    }
    
    public void removeCancelled() {
        removeByStatus(SongDownload.STATUS_CANCELLED);
    }
    
    public void removeDuplicates() {
        removeByStatus(SongDownload.STATUS_DUPLICATE);
    }

    public void removeDownloads(Collection c) {
        cancelDownloads(c);
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            if (c.contains(d)) {
	            int stat = d.getStatus();
	            if (stat == SongDownload.STATUS_FINISHED || stat == SongDownload.STATUS_CANCELLED || stat == SongDownload.STATUS_DUPLICATE || stat == SongDownload.STATUS_ERROR) {
	                remove(d);
	                i--;
	                dlThread.dlnum = 0;
	            }
            }
        }
        tableModel.fireTableDataChanged();
    }

    public void removeAll() {
    	cancelAll();
        removeDownloads(getDownloads());
    }

    public void removeByStatus(int status) {
        // FIXME: Do we need this check, or can we remove waiting and running downloads?
        if (status < SongDownload.STATUS_FINISHED)
            return;
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            int stat = d.getStatus();
            if (stat == status) {
                remove(d);
                i--;
                dlThread.dlnum = 0;
            }
        }
        tableModel.fireTableDataChanged();
    }
    
    // CANCEL METHODS:
    
    public void cancelDownloads(Collection c) { // maybe faster to check if c's elements are in downloads?
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            if (c.contains(d)) { //FIXME: I need a mutex on the getStatus method, since we could potentially have a race condition here!
                if (d.getStatus() == SongDownload.STATUS_WAITING) {
                    DownloadThread.sem.acquire();
                }
                d.setStatus(SongDownload.STATUS_CANCELLED);
                /*if (d.getStatus() !=SongDownload.STATUS_FINISHED) {
                    
                }*/
                dlThread.dlnum = 0;
            }
        }
        
    }
    
    public void cancelAll() {
        cancelDownloads(getDownloads());
    }
    
    // OTHER METHODS:
    
    public boolean isQueueFinished() {
        for (int i=0; i < downloads.size(); i++) {
            SongDownload d = (SongDownload)downloads.get(i);
            if (d.getStatus() < SongDownload.STATUS_FINISHED)
                return false;
        }
        return true;
    }
    
    public void deleteAndRetry(Collection c) {
       for (int i=0; i < downloads.size(); i++) {
           SongDownload d = (SongDownload)downloads.get(i);
           if (c.contains(d) && d.getStatus() >= SongDownload.STATUS_FINISHED) {
               File destinationFile = d.getDownloadPath();
               if (d.getSong() instanceof LocalSong)
               {
                   File sourceFile = new File(((LocalSong) d.getSong()).getPath());
	               if (destinationFile.equals(sourceFile))
	               {
					   d.setError("Cannot delete - Source and destination are the same file!");
					   break;
	               }
               }
               if (destinationFile != null && destinationFile.exists() && destinationFile.canWrite())
               {
                   if (!destinationFile.delete())
                   {
                       d.setError("Unable to delete file.");
                   }
                   else
                   {
                   	d.setStatus(SongDownload.STATUS_WAITING);
                   	System.out.println("file deleted!");
                   }
               }
           }
//           try {Thread.sleep(200);} catch (Exception e) {}
           dlThread.dlnum = 0;
           DownloadThread.sem.release();
       }
       tableModel.fireTableDataChanged();
    }
    
    public void stateUpdated(Object h) {
        SongDownload d = (SongDownload)h;
//        System.out.println("--"+Downloader.status_strings[d.getStatus()] + "-- " + d.getSong().getName());
        if (d.getStatus() == SongDownload.STATUS_FINISHED) {
            prog.countLabel.setText(GetItTogether.DOWNLOAD_COUNTER+GITProperties.numDownloads);
        }
        int i = downloads.indexOf(h);
        if (i >= 0)
            tableModel.fireTableRowsUpdated(i, i);
    }
    
    public synchronized void queueCount() {
        System.err.println("UPDATING");
    }
    
    public class DownloadTableFormat implements TableFormat {
        
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
            SongDownload dl = (SongDownload)o;
//            switch (col) {
//            	case 0:
//            	    return "";
//        	    case 1:
//        	        return new String(dl.song.getArtist() + " - " + dl.song.getName());
//    	        case 2:
//    	        {
//    	            double a = Math.round(dl.getSong().getSize() / 104857.6);
//					a = a / 10;
//					return new String(Double.toString(a) + " Mb");
//    	        }
//	            case 3:
//	                return new String(Downloader.status_strings[dl.getStatus()]);
//            }
            return dl;
        }
        
    }
}
