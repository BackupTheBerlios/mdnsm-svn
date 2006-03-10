/*
 * Created on Aug 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.ImageIcon;

import org.git.client.Host;
import org.git.client.Song;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LocalSong extends Song implements Comparable{
	protected String	path;

	protected ImageIcon photo;
	
	public LocalSong(Host h) {
	    super();
	    setPath("");
	    host = h;
	}
	
	public String getPath() {
		return path;
		
	}

	public void setPath(String path) {
		// convert path to a real fileSystemPath
	    try {
            this.path = URLDecoder.decode(path,"utf-8").replaceFirst("file://localhost", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	}

	public ImageIcon getPhoto() {
	    return photo;
	}
	
	public void setPhoto(ImageIcon photo) {
	    this.photo = photo;
	}
	
	
	
}