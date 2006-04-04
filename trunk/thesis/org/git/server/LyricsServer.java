package org.git.server;

import java.io.*;
import org.git.client.swing.*;

public class LyricsServer {
    
    private static final LyricsServer INSTANCE = new LyricsServer();
	
  	public static LyricsServer instance() {
  	    return INSTANCE;
  	}
  	
  	public void start() {
  		try {
  			RendezvousManager.instance().registerServer(GetItTogether.lyricsService);
  		}
  		catch(IOException exc) {
  			exc.printStackTrace();
  		}
  	}
  	
  	public void stop() {
  		RendezvousManager.instance().unregisterServer(GetItTogether.lyricsService);
  	}
	
}
