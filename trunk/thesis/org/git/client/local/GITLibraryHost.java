/*
 * Created on Jun 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParserFactory;

import org.git.GITProperties;
import org.git.client.Host;
import org.git.client.Song;
import org.git.client.swing.GetItTogether;
import org.git.client.swing.SwingWorker;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import ca.odell.glazedlists.BasicEventList;
import de.ueberdosis.mp3info.ID3Reader;
import de.ueberdosis.mp3info.id3v2.FrameAPIC;
import de.ueberdosis.mp3info.id3v2.ID3V2Frame;
import de.ueberdosis.mp3info.id3v2.ID3V2Tag;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GITLibraryHost extends LocalHost{

    private JLabel current_scan = new JLabel();
    private int skipped = 0;
    
    public static String DEFAULT_LIBRARY_FILENAME = "git library.xml";
    
    
    public GITLibraryHost(String name, String root) {
        super(name,root);
    }
    
    public void connect() {
        setStatus(Host.STATUS_CONNECTING);
        System.out.println(root);
        File f = new File(root);  // first, try real root
        if (!f.exists() || !f.isFile()) {
            f = new File(DEFAULT_LIBRARY_FILENAME); // if that doesn't exist, try default root
            if (!f.exists()) {
                showErrorConnectingPanel();
                return;
            } else {
                root = f.getPath();
            }
        }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ITunesHandler handler = new ITunesHandler(this);
            xmlReader.setContentHandler(handler);
//            File file = new File(root);
//            if (!file.exists() || !file.isFile()) {
//                System.out.println("file still doesn't exist!");
//                return;
//            }
            xmlReader.parse(f.getAbsolutePath());
            playlists = handler.getPlaylists();
            System.out.println(handler.libraryName);
            songs.addAll(handler.getSongs());
            setStatus(Host.STATUS_CONNECTED);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Host.STATUS_NOT_AVAILABLE);
            return;
        } finally {
            if (songs.size() == 0) {
                showEmptyLibraryPanel();
            }
        }
    }

    public void disconnect() {
        outputXML();
        super.disconnect();
    }
    
    public void showEmptyLibraryPanel() {
        if (!GITProperties.showConnectionErrorPanels)
        {
            setStatus(Host.STATUS_NOT_CONNECTED);
            return;
        }
        String message = new String("<html><font size='+4'><center><strong>No songs in GIT Library!</strong></center></font>" +
                "<ul compact>" +
        		"<li>Your GIT Library is empty...</li>" +
        		"<li>Perhaps you have not added any music to it yet?.</li>"+
        		"</ul>" +
                "You can either:" +
                "<ul>" +
                "<li><strong>Add some MP3s now</strong> by locating your MP3 directory, or</li>" +
                "<li><strong>Add some MP3s later</strong> by right-clicking on the library icon when you're ready.</li>" +
                "</ul>"
        		);
        Object[] options = {"Add some MP3s now", "Do nothing"};
        int result = JOptionPane.showOptionDialog(null,
                message,
                "Library is empty!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        switch (result) {
        case JOptionPane.YES_OPTION:
            importMp3Directory();
        break;
        case JOptionPane.NO_OPTION:
        default:
            break;
        }
    }
    
    public void showErrorConnectingPanel() {
        if (!GITProperties.showConnectionErrorPanels)
        {
            setStatus(Host.STATUS_NOT_CONNECTED);
            return;
        }
        String message = getErrorConnectingString();
        Object[] options = {"Browse for library file","Create a new library","Cancel"};
        int result = JOptionPane.showOptionDialog(null,
                message,
                "Library Not Found",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        switch (result) {
        case JOptionPane.YES_OPTION:
            String fileResult = showFileChooser(JFileChooser.FILES_ONLY,"Please locate your GIT Library .xml file.",new XMLFileFilter());
        if (fileResult != null) {
            root = fileResult;
            connect();
        }
        break;
        case JOptionPane.NO_OPTION:
            createNewLibraryFile();
        connect();
        break;
        case JOptionPane.CANCEL_OPTION:
            setStatus(Host.STATUS_NOT_CONNECTED);
        break;
        }
    }
    
    public void createNewLibraryFile() {
        File f = new File(DEFAULT_LIBRARY_FILENAME);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        root = f.getPath();
        songs.clear();
        playlists = new ArrayList();
        outputXML();
    }
    
    public String getErrorConnectingString() {
        return new String("<html><font size='+4'><center><strong>GIT library not found!</strong></center></font>" +
                "<ul compact>" +
        		"<li>The GIT library is stored in an XML file usually named 'git-library.xml'.</li>" +
        		"<li>It appears as if your library file got deleted or moved.</li>"+
        		"</ul>" +
                "You can either:" +
                "<ul>" +
                "<li><strong>Manually browse for a GIT library file</strong>, or</li>" +
                "<li><strong>Allow GIT to create a new, empty library file</strong></li>" +
                "</ul>"
        		);
    }
    
    public void importMp3Directory() {
        if (getStatus() != Host.STATUS_CONNECTED) {
            try {
                boolean whatev = GITProperties.showConnectionErrorPanels;
                GITProperties.showConnectionErrorPanels = false;
                connect();
                GITProperties.showConnectionErrorPanels = whatev;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        
        String dirResult = showFileChooser(JFileChooser.DIRECTORIES_ONLY,"Please choose a directory to import (subdirectories will be included).",
                null);
        if (dirResult == null)
            return;
        final File f = new File(dirResult);
        if (!f.exists() || !f.isDirectory())
            return;
        
        JOptionPane pane = new JOptionPane("asdf");
        pane.setOptions(null);
        JDialog dialog = pane.createDialog(GetItTogether.instance.frame, "Loading mp3 directory");
        current_scan.setMaximumSize(new Dimension(500, 30));
        current_scan.setMinimumSize(new Dimension(500, 30));
        dialog.setModal(false);
        dialog.getContentPane().removeAll();
        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.getContentPane().add(current_scan);
        dialog.validate();
        dialog.setSize(400,60);
//        dialog.pack();
        if (GITProperties.showConnectionErrorPanels)
            dialog.show();
        try {
            recurseDirs(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.dispose();
        dialog = null;
        outputXML();
        if (skipped > 0) {
            JOptionPane.showMessageDialog(GetItTogether.instance.frame,
                    "<html><font size='+4'><center><strong>Duplicates skipped!</strong></center></font>" +
                    "<ul compact>" +
                    "<li><strong>" + skipped + " songs were not added</strong> because they are likely duplicates of <br>" +
                    		"songs that already exist in the library.</li>" +
                    "</ul>","Songs skipped",JOptionPane.PLAIN_MESSAGE
            );
        }
        skipped = 0;
        return;
    }
    
    protected void outputXML() {
        try {
            System.out.println("outputting xml...");
            long start = System.currentTimeMillis();
            ITunesXMLWriter d = new ITunesXMLWriter(new FileWriter(root));
            d.setIndentStep(2);
            
            d.startDocument();
            d.startElement("dict");
            
            d.integer("Major Version",1);
            d.integer("Minor Version",1);
            d.string("Application Version","0.7.0");
            d.string("Music Folder",new File(root).getAbsolutePath());
            d.string("Library Persistent ID","N/A");
            d.string("Library Name", this.name);
            
            d.key("Tracks");
            d.startElement("dict");
            for (int i=0; i < songs.size(); i++) {
                LocalSong s = (LocalSong) songs.get(i);
                d.key(String.valueOf(s.id));
                d.startElement("dict");
                d.integer("Track ID",s.id);
                d.string("Name",s.name);
                d.string("Artist",s.artist);
                d.string("Album",s.album);
                d.string("Genre",s.genre);
                d.string("Kind","MPEG audio file");
                d.integer("Size",s.size);
                d.integer("Total Time",s.time);
                d.integer("Track Number",s.track);
                d.integer("Bit Rate",s.bitrate);
                d.string("Track Type","File");
                d.string("Location",s.path);
                d.endElement("dict");
            }
            d.endElement("dict");
            
            d.key("Playlists");
            d.startElement("array");
            for (int i=0; i < playlists.size(); i++) {
                LocalPlaylist p = (LocalPlaylist)playlists.get(i);
                d.startElement("dict");
                
                d.string("Name", p.name);
                d.bool("Master",p.master);
                d.integer("Playlist ID", p.id);
                d.string("Playlist Persistent ID", p.persistent_id + "");
                d.bool("All Items", p.all_items);
                
                d.startElement("array");
                for (int j=0; j < p.songs.size(); j++) {
                    try {
                    Song s = (Song)p.songs.get(j);
                    if (s != null) {
                    d.startElement("dict");
                        d.integer("Track ID",s.id);
                    d.endElement("dict");
                    }
                    } catch (Exception e) {e.printStackTrace();}
                }
                d.endElement("array");
                
                d.endElement("dict");
            }
            d.endElement("array");
            
            d.endElement("dict");
            d.endDocument();
            long end = System.currentTimeMillis();
            start = end - start;
            float time = (float) start / 1000;
            System.out.println("Done! time: "+ time + " seconds");
        } catch (SAXException sax) {
            sax.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    
    // currently the best option for ID3 loading.
    protected void processMp3Javazoom(File f) {
        
        current_scan.setText(f.getName());
        current_scan.repaint();
        
        AudioFileFormat baseFileFormat = null;
        AudioFormat baseFormat = null;
        
        // Try to do the SPI stuff:
        try {
            baseFileFormat = AudioSystem.getAudioFileFormat(f);
        } catch (UnsupportedAudioFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        
        // It worked, we're good to continue:
        final LocalSong s = new LocalSong(this);
        s.host = this;
        s.size = (int)f.length();
        s.path = f.getPath();
        s.format = "mp3";
        
        //  create a positive integer id based on the hashcode of filename:
        s.id = Math.abs(f.getName().hashCode());
        
        baseFormat = baseFileFormat.getFormat();
        //TAudioFileFormat properties
        if (baseFileFormat instanceof TAudioFileFormat) {
            Map properties = ((TAudioFileFormat)baseFileFormat).properties();
            s.artist = getString(properties,"author");
            s.name = getString(properties,"title");
            s.album = getString(properties,"album");
            s.genre = getString(properties,"mp3.id3tag.genre");
            Integer bps = (Integer)getValue(properties,"bitrate.nominal.bps");
            if (bps != null)
                s.bitrate = bps.intValue() / 10000;
            s.time = ((Long)getValue(properties,"duration")).intValue() / 1000;
            
        }
        //TAudioFormat properties
        if (baseFormat instanceof TAudioFormat) {
            Map properties = ((TAudioFormat)baseFormat).properties();
            Integer bitrate = (Integer)getValue(properties,"bitrate");
            if (bitrate != null)
                s.bitrate = bitrate.intValue();
        }
        try {
            ID3Reader.readExtendedTag(new RandomAccessFile(f,"r"));
            ID3V2Tag tag = ID3Reader.getV2Tag();
            if (tag != null) {
                Vector v = tag.getFrames();
                Iterator it = v.iterator();
                while (it.hasNext()) {
                    ID3V2Frame frame = (ID3V2Frame)it.next();
                    if (frame.getFrameID().equals("APIC")) {
                        FrameAPIC apic = (FrameAPIC)frame;
                        byte[] photo = apic.getPictureData();
                        Image img = Toolkit.getDefaultToolkit().createImage(photo);
                        img = img.getScaledInstance(80,80,Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(img);
                        s.setPhoto(icon);
                    }
                }
            }
        } catch (IOException e1) {
            //  e1.printStackTrace();
        }
        if (!songs.contains(s)) {
            songs.getReadWriteLock().writeLock().lock();
            songs.add(s);
            songs.getReadWriteLock().writeLock().unlock();
        } else
            skipped++;
            
    }
    
    protected void recurseDirs(File f)
            throws FileNotFoundException, IOException {
        if (f.isDirectory()) {
            File[] contents = f.listFiles();
            for (int i = 0; i < contents.length; i++) {
                recurseDirs(contents[i]);
            }
        }
        else if (isMp3(f)){
            //processMp3(s, f);
            processMp3Javazoom(f);
        }
    }

    protected void recurseDi(File f) {
        final BasicEventList temp = new BasicEventList(new ArrayList(200));
        Stack dirs = new Stack();
        dirs.push(f);
        while (!dirs.isEmpty()) {
            if (temp.size() >= 5) {
                new SwingWorker() {
                    public Object construct() {
                        GetItTogether.instance.addSongs(temp);
                temp.getReadWriteLock().writeLock().lock();
                temp.clear();
                temp.getReadWriteLock().writeLock().unlock();
                        return "";
                    }
                }.start();
            }
            f = (File)dirs.pop();
            if (f.isDirectory()) {
                File[] contents = f.listFiles();
                for (int i=0; i < contents.length; i++) {
                    dirs.push(contents[i]);
                }
            } else if (isMp3(f)) {
                current_scan.setText(f.getName());
                current_scan.repaint();
                
                AudioFileFormat baseFileFormat = null;
                AudioFormat baseFormat = null;
                
                // Try to do the SPI stuff:
                try {
                    baseFileFormat = AudioSystem.getAudioFileFormat(f);
                } catch (UnsupportedAudioFileException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
                
                // It worked, we're good to continue:
                final LocalSong s = new LocalSong(this);
                s.host = this;
                s.size = (int)f.length();
                s.path = f.getPath();
                s.format = "mp3";
                
                //  create a positive integer id based on the hashcode of filename:
                s.id = Math.abs(f.getName().hashCode());
                
                baseFormat = baseFileFormat.getFormat();
                //TAudioFileFormat properties
                if (baseFileFormat instanceof TAudioFileFormat) {
                    Map properties = ((TAudioFileFormat)baseFileFormat).properties();
                    s.artist = getString(properties,"author");
                    s.name = getString(properties,"title");
                    s.album = getString(properties,"album");
                    s.genre = getString(properties,"mp3.id3tag.genre");
                    Integer bps = (Integer)getValue(properties,"bitrate.nominal.bps");
                    if (bps != null)
                        s.bitrate = bps.intValue() / 10000;
                    s.time = ((Long)getValue(properties,"duration")).intValue() / 1000;
                    
                }
                //TAudioFormat properties
                if (baseFormat instanceof TAudioFormat) {
                    Map properties = ((TAudioFormat)baseFormat).properties();
                    Integer bitrate = (Integer)getValue(properties,"bitrate");
                    if (bitrate != null)
                        s.bitrate = bitrate.intValue();
                }
                try {
                    ID3Reader.readExtendedTag(new RandomAccessFile(f,"r"));
                    ID3V2Tag tag = ID3Reader.getV2Tag();
                    if (tag != null) {
                        Vector v = tag.getFrames();
                        Iterator it = v.iterator();
                        while (it.hasNext()) {
                            ID3V2Frame frame = (ID3V2Frame)it.next();
                            if (frame.getFrameID().equals("APIC")) {
                                FrameAPIC apic = (FrameAPIC)frame;
                                byte[] photo = apic.getPictureData();
                                Image img = Toolkit.getDefaultToolkit().createImage(photo);
                                img = img.getScaledInstance(80,80,Image.SCALE_SMOOTH);
                                ImageIcon icon = new ImageIcon(img);
                                s.setPhoto(icon);
                            }
                        }
                    }
                } catch (IOException e1) {
                    //  e1.printStackTrace();
                    continue;
                }
                if (!songs.contains(s)) {
                    temp.add(s);
                    songs.add(s);
                } else
                    skipped++;
            }
        }
        
    }
    
    public static boolean isMp3(File f) {
        if (!f.getPath().endsWith(".mp3"))
            return false;
        else
            return true;    
    }
    
    public boolean isWritable() {
        return true;
    }
    
    public static void findPhotoInID3(LocalSong s) {
        try {
            File f = new File(s.getPath());
            ID3Reader.readExtendedTag(new RandomAccessFile(f,"r"));
            ID3V2Tag tag = ID3Reader.getV2Tag();
            if (tag != null) {
                Vector v = tag.getFrames();
                Iterator it = v.iterator();
                while (it.hasNext()) {
                    ID3V2Frame frame = (ID3V2Frame)it.next();
                    if (frame.getFrameID().equals("APIC")) {
                        FrameAPIC apic = (FrameAPIC)frame;
                        byte[] photo = apic.getPictureData();
                        Image img = Toolkit.getDefaultToolkit().createImage(photo);
                        img = img.getScaledInstance(80,80,Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(img);
                        s.setPhoto(icon);
                    }
                    
                }
            }
        } catch (IOException e1) {
            //  e1.printStackTrace();
        }
        
    }
    
    public String getTypeString() {
        return "GIT Library";
    }
    
    protected static String getString(Map map, Object key) {
        Object o = getValue(map,key);
        if (o == null)
            return new String();
        else
            return (String)o;
    }
    
    // Tests for and grabs the value from a map.  Returns an empty string if key doesn't exist.
    protected static Object getValue(Map map, Object key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else
            return null;
    }
}
