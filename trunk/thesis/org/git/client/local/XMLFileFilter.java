/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class XMLFileFilter extends FileFilter {
    
    public boolean accept(File f) {
        if (!f.isDirectory() && !f.getPath().toLowerCase().endsWith(".xml"))
            return false;
        else
            return true;
    }
    
    public String getDescription() {
        return "XML files";
    }
}