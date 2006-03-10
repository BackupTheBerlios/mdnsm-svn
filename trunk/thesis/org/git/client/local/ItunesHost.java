/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.git.GITUtils;
import org.git.client.Host;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ItunesHost extends LocalHost {
    
    public ItunesHost(String nayme, String rhoot) {
        super(nayme, rhoot);
    }
    
    public void connect() {
        setStatus(Host.STATUS_CONNECTING);
        File f = new File(root);
        if (!f.exists()) {
            f = new File(GITUtils.getDefaultItunesLocation());
            if (!f.exists()) {
                showErrorConnectingPanel();
                return;
            } else {
                root = f.getPath();
            }
        }
            loadLibraryXML();
            setVisible(true);
            setStatus(Host.STATUS_CONNECTED);
            final Host h = this;
    }
    
    protected void showErrorConnectingPanel() {
        String message = new String("<html><font size='+4'><center><strong>iTunes Music Library not found!</strong></center></font>" +
                "<ul compact>" +
        		"<li>iTunes usually stores its library in a file called \"iTunes Music Library.xml\".</li>" +
        		"<li>Unfortunately, we could not locate that file.</li>"+
        		"</ul>" +
                "You can either:" +
                "<ul>" +
                "<li><strong>Search for it yourself, </li>or" +
                "<li><strong>Do nothing</strong>.</li>" +
                "</ul>"
        		);
        Object[] options = {"Search for my iTunes library","Do nothing"};
        int result = JOptionPane.showOptionDialog(null,
                message,
                "iTunes Library Not Found",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        switch (result) {
        case JOptionPane.YES_OPTION:
            String fileResult = showFileChooser(JFileChooser.FILES_AND_DIRECTORIES,"Please locate your \"iTunes Music Library .xml\" file.",new XMLFileFilter());
        System.out.println(fileResult);
        if (fileResult != null) {
            root = fileResult;
            connect();
        }
        break;
        case JOptionPane.NO_OPTION:
        default:
            break;
        }
    }
    
    
    
    protected void loadLibraryXML() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            ITunesHandler handler = new ITunesHandler(this);
            xmlReader.setContentHandler(handler);
            final File file = new File(root);
            if (!file.exists() || !file.isFile())
                return;
            try {
            xmlReader.parse(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No Internet Connection!");
            }
            playlists = handler.getPlaylists();
//            System.out.println(handler.libraryName);
            songs.getReadWriteLock().writeLock().lock();
            songs.addAll(handler.getSongs());
            songs.getReadWriteLock().writeLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    public String getTypeString() {
        return "iTunes Library";
    }
    
}

