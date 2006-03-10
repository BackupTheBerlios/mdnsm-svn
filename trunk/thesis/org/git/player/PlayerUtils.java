/*
 * Created on Aug 16, 2004
 * Created by wooo as part of git
 */
package org.git.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;

import org.git.GITProperties;
import org.git.GITUtils;
import org.git.client.swing.GetItTogether;

/**
 * @author wooo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class PlayerUtils {

    public static final String [] PLAYER_TYPES = {"Java", "Quicktime", "External"};
    /**
     * Comment for <code>QTPLAYER</code>
     * represents a quicktime for java player
     */
    public static final int JAVAPLAYER = 0;
    /**
     * Comment for <code>JAVAPLAYER</code>
     * represents a native java song player
     */
    public static final int QTPLAYER = 1;
    
    /**
     * Comment for <code>EXTERNALPLAYER</code>
     * represents an external command line exectuted song player
     */
    public static final int EXTERNALPLAYER = 2;
    /**
     * 
     */
    public PlayerUtils() {
        super();
        // TODO Auto-generated constructor stub
    }

    // USE LOADNEWPLAYER INSTEAD
//    public static AbstractPlayer getSelectedPlayer() {
//        int type = GITProperties.playerType;
//        
//        switch(type) {
//        	case QTPLAYER:
////        	    System.out.println("Player: Quicktime");
//        	        return new QTPlayer();
////        	        return JAVAPLAYER;  // just go on and do a javaplayer...
//    	    case JAVAPLAYER:
////    	        System.out.println("Player: Java");
//    	        return new JavaPlayer();
//	        case EXTERNALPLAYER:
////	            System.out.println("Player: External");
////	            for now:;
//	            return new ExternalPlayer();
//	            // TODO create an ExternalPlayer object here
//            default:
//                return new JavaPlayer();
//        }
//        
//        
//    }
    
    public static String millisToTime(int millis) {
        double dsecs = (double)millis / (double)1000;
        int seconds = (int)Math.round(dsecs);
		String sec_zero = "";
//		int seconds = millis / 1000;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		if (seconds < 10) {
			sec_zero = "0";
		}
		return new String(minutes + ":" + sec_zero + seconds);
    }
    
    public static File downloadToFile(InputStream is, String output) throws IOException {
        FileOutputStream fos = null;
        File tmpfile = new File(output);

        final int BUFFER_SIZE = 1024 * 4;
        byte[] buffer = new byte[BUFFER_SIZE];
        fos = new FileOutputStream(tmpfile);
        for (int read = -1; (read = is.read(buffer)) != -1;) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        return tmpfile;
    }

    /**
     * @param javaplayer2
     */
    public static void loadNewPlayer(int javaplayer2) {
        try {
            if (GetItTogether.player != null) { 
                GetItTogether.player.stop();
                GetItTogether.player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (javaplayer2) {
        case AbstractPlayer.JAVAPLAYER:
            GetItTogether.player = new JavaPlayer();
        break;
        case AbstractPlayer.QTPLAYER:
            if (GITUtils.hasQuicktimeForJava())
                GetItTogether.player = new QTPlayer();
            else {
                System.err.println("Couldn't find Quicktime for Java-- using Java player instead!");
                GetItTogether.player = new JavaPlayer();
            }
        break;
        case AbstractPlayer.EXTERNALPLAYER:
            ExternalPlayer ep = new ExternalPlayer(GITProperties.externalProg);
            GetItTogether.player = ep;
        break;
        default:
            System.err.println("Could not load new player: unknown type!");
        }
        GetItTogether.player.addPlayerListener(GetItTogether.instance.playerListener);
    }
    
    
}
