package org.git.server;

import java.io.*;
import org.git.client.swing.*;

public class SettingsServer {
    
    private static final SettingsServer INSTANCE = new SettingsServer();
	
  	public static SettingsServer instance() {
  	    return INSTANCE;
  	}
  	
  	public void start() {
  		try {
  			RendezvousManager.instance().registerServer(GetItTogether.settingsService);
  		}
  		catch(IOException exc) {
  			exc.printStackTrace();
  		}
  	}
  	
  	public void stop() {
  		RendezvousManager.instance().unregisterServer(GetItTogether.settingsService);
  	}
	
}
