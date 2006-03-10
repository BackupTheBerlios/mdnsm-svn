/*
 * Created on Jan 24, 2005
 * Created by wooo as part of git
 */
package org.git.client.local;

/**
 * @author wooo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.io.File;
import java.util.ArrayList;

import org.git.client.Playlist;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


public class ITunesHandler extends DefaultHandler {
    public String libraryName;
    private LocalHost host;
    private ArrayList songs = new ArrayList();
    private ArrayList playlists = new ArrayList();
    private ArrayList playlistSongIDs;  // holds song IDs stored as Strings.
    private LocalSong song;
    private LocalPlaylist playlist;
    private StringBuffer buffer = new StringBuffer();
    private boolean saveData = false;
    private boolean inTracks = false;
    private boolean inTracksDict = false;
    private boolean inTrack = false;
    private boolean inKey =false;
    private byte elementID = UNKNOWN;
    private static final byte UNKNOWN = 0;
    private static final byte LIBRARY_FOLDER_COUNT = 1;
    private static final byte FILE_FOLDER_COUNT = 2;
    private static final byte LOCATION = 3;
    private static final byte COMMENTS = 4;
    private static final byte VOLUME_ADJUSTMENT = 5;
    private static final byte BIT_RATE = 6;
    private static final byte DATE_ADDED = 7;
    private static final byte DATE_MODIFIED = 8;
    private static final byte YEAR = 9;
    private static final byte SAMPLE_RATE = 10;
    private static final byte SIZE = 11;
    private static final byte KIND = 12;
    private static final byte GENRE = 13;
    private static final byte ALBUM = 14;
    private static final byte ARTIST = 15;
    private static final byte NAME = 16;
    private static final byte TOTAL_TIME = 17;
    private static final byte TRACK_TYPE = 18;
    private static final byte TRACK_NUMBER = 19;
    private static final byte TRACK_ID = 20;
    private static final byte NONE = -1;
    private static final byte TRACK_COUNT = 21;
    private static final byte ARTWORK_COUNT = 22;
    private static final byte GROUPING = 23;
    private static final byte DISC_COUNT = 24;
    private static final byte DISC_NUMBER = 25;
    private static final byte BPM = 26;
    private static final byte COMPILATION = 27;
    private static final byte FILE_CREATOR = 28;
    private static final byte FILE_TYPE = 29;
    private static final byte PLAY_COUNT = 30;
    private static final byte PLAY_DATE = 31;
    private static final byte PLAY_DATE_UTC = 32;
    private static final byte RATING = 33;
    private static final byte COMPOSER = 34;
    private static final byte MASTER = 35;
    private static final byte PLAYLIST_ID = 36;
    private static final byte PLAYLIST_PERSISTENT_ID = 37;
    private static final byte PLAYLIST_ITEMS = 38;
    private static final byte PLAYLIST_ALL_ITEMS = 39;
    private static final byte PLAYLIST_SMART_INFO = 40;
    private static final byte PLAYLIST_SMART_CRITERIA = 41;
    private static final byte LIBRARY_NAME = 42;
    
    
    
    private boolean inPlaylists = false;
    private boolean inPlaylistDict = false;
    private boolean inPlaylistTrackDict = false;
    
    private boolean inBoolean = false;
    private boolean boolean_data = false;
    
    public ITunesHandler(LocalHost host) {
        this.host = host;
        //	    this.songs = new 
    }
    //  <key>Tracks</key>
    
    public void anotherTry(String qualifiedName, boolean start) {
        
        if (inTracks) {
            if (inTracksDict) {
                if (qualifiedName.equals("dict")) {
                    inTrack = start;
                    if (start) song = new LocalSong(host);
                    else songs.add(song);
                    return;
                }
                else if (inTrack && qualifiedName.equals("key")) {
                    inKey = start;
                    return;
                }
            } else if (qualifiedName.equals("dict")) {
                inTracksDict = start;
                return;
            }
        } else if (inPlaylists) {
            if (inPlaylistDict) {
                if (qualifiedName.equals("key")) {
                    inKey = start;
                    return;
                } else if (!start && (qualifiedName.equals("true") || qualifiedName.equals("false"))) {
                    boolean_data = new Boolean(qualifiedName).booleanValue();
                } else if (inPlaylistTrackDict && qualifiedName.equals("key")) {
                    inKey = start;
                    return;
                } else if (qualifiedName.equals("dict")) {
                    inPlaylistTrackDict = start;
                }
            } else if (qualifiedName.equals("dict")) {
                inPlaylistDict = start;
                if (start) {
                    playlist = new LocalPlaylist(host);
                    playlistSongIDs = new ArrayList();
                } else {
                    playlist.songIDs = playlistSongIDs;
                    playlists.add(playlist);
                }
                return;
            }
        } else if (qualifiedName.equals("dict")) {
            inPlaylistDict = start;
            return;
        }
    }

    public void startElement (String uri, String localName, String qName, Attributes attributes)
    throws SAXException
    {
        if (qName.equals("key")) {
            inKey = true;
            return;
        }
        if (inTracks) {
            if (inTracksDict) {
                if (inTrack) {
                    ;
                } else if (qName.equals("dict")) {
                    inTrack = true;
                    song = new LocalSong(host);
                }
            } else if (qName.equals("dict")) {
                inTracksDict = true;
            }
        } else if (inPlaylists) {
            if (inPlaylistDict) {
                if (inPlaylistTrackDict) {
                    // we've reached the final depth
                } else if (qName.equals("dict")) {
                    inPlaylistTrackDict = true;
                } else if (qName.equals("true") || qName.equals("false")) {
                    boolean_data = new Boolean(qName).booleanValue();
                }
            } else if (qName.equals("dict")) {
                inPlaylistDict = true;
                playlist = new LocalPlaylist(host);
                playlistSongIDs = new ArrayList();
            }
        }
    }
    
    public void endElement (String uri, String localName, String qName)
    throws SAXException
    {
        finishData();
        if (qName.equals("key")) {
            inKey = false;
            return;
        }
        if (inTracks) {
            if (inTracksDict) {
                if (inTrack) {
                    if (qName.equals("dict")) {
                        inTrack = false;
                        songs.add(song);
                        return;
                    }
                }
                if (qName.equals("dict")) {
                    inTracksDict = false;
                    return;
                }
            }
        } else if (inPlaylists) {
            if (inPlaylistDict) {
                if (inPlaylistTrackDict) {
                    if (qName.equals("dict")) {
                        inPlaylistTrackDict = false;
                        return;
                    }
                }
                if (qName.equals("dict")) {
                    inPlaylistDict = false;
                    playlist.songIDs = playlistSongIDs;
                    playlists.add(playlist);
                    return;
                }
            }
        }
    }
    
    public ArrayList getSongs() {
        return songs;
    }
    
    public ArrayList getPlaylists() {
        return playlists;
    }
    
    private void finishData() {
        // TODO Auto-generated method stub
        String finishedData;
        finishedData = buffer.toString().trim();
        buffer = new StringBuffer();
        if (inKey) {
            captureElementID(finishedData);
            return;
        }
        if (!inTracks && "Tracks".equals(finishedData)) {
            inTracks = true;
            inPlaylists = false;
            return;
        }
        if (inTracks && inTracksDict && inTrack) { // this is something I want!
            captureSongData(finishedData);
            return;
        }
        if (inPlaylists && inPlaylistDict) {
            capturePlaylistData(finishedData);
            return;
        }
    }
    
    public void captureElementID(String finishedData) {
//        System.out.println(finishedData);
        switch (finishedData.charAt(0)) {
        // also need to know if I'm in a key...
        // create an int for what key I am in
        case 'T': //Track ID || total time || Track Number || Track Type
            if (finishedData.equals("Track ID")) 
                elementID = TRACK_ID;
            else if (finishedData.equals("Total Time")) 
                elementID = TOTAL_TIME;
            else if (finishedData.equals("Track Number")) 
                elementID = TRACK_NUMBER;
            else if (finishedData.equals("Track Type")) 
                elementID = TRACK_TYPE;
            else if (finishedData.equals("Track Count")) 
                elementID = TRACK_COUNT;
            else if (finishedData.equals("Tracks")) {
                inTracks = true;
                inPlaylists = false;
            }
            else System.out.println("nonmatching T = " + finishedData);
            break;
        case 'M':
            if (finishedData.equals("Master"))
                elementID = MASTER;
            else
                System.out.println("nonmatching M = " + finishedData);
            break;
        case 'N': //Name
            if (finishedData.equals("Name")) 
                elementID = NAME;
            else System.out.println("nonmatching N = " + finishedData);
            break;
        case 'A': //Artist || Album
            if (finishedData.equals("Artist"))
                elementID = ARTIST;
            else if (finishedData.equals("Album")) 
                elementID = ALBUM;
            else if (finishedData.equals("Artwork Count")) 
                elementID = ARTWORK_COUNT;
            else if (finishedData.equals("All Items"))
                elementID = PLAYLIST_ALL_ITEMS;
            else System.out.println("nonmatching A = " + finishedData);
            break;
        case 'G': //Genre
            if (finishedData.equals("Genre")) 
                elementID = GENRE;
            else if (finishedData.equals("Grouping")) 
                elementID = GROUPING;
            else System.out.println("nonmatching G = " + finishedData);
            break;
        case 'K': //Kind
            if (finishedData.equals("Kind")) 
                elementID = KIND;
            else System.out.println("nonmatching K = " + finishedData);
            break;
        case 'S': //size || Sample rate
            if (finishedData.equals("Size")) 
                elementID = SIZE;
            else if (finishedData.equals("Sample Rate"))
                elementID = SAMPLE_RATE;    
            else if (finishedData.equals("Smart Info"))
                elementID = PLAYLIST_SMART_INFO;
            else if (finishedData.equals("Smart Criteria"))
                elementID = PLAYLIST_SMART_CRITERIA;
            else System.out.println("nonmatching S = " + finishedData);
            break;
        case 'Y': // Year
            if (finishedData.equals("Year")) 
                elementID = YEAR;
            else System.out.println("nonmatching Y = " + finishedData);
            break;
        case 'D': //Date mod || date Added
            if (finishedData.equals("Date Modified")) 
                elementID = DATE_MODIFIED;
            else if (finishedData.equals("Date Added"))
                elementID = DATE_ADDED;
            else if (finishedData.equals("Disc Count"))
                elementID = DISC_COUNT;
            else if (finishedData.equals("Disc Number"))
                elementID = DISC_NUMBER;
            else System.out.println("nonmatching D = " + finishedData);
            break;
        case 'B': //Bit Rate
            if (finishedData.equals("Bit Rate")) 
                elementID = BIT_RATE;
            else if (finishedData.equals("BPM")) 
                elementID = BPM;
            else System.out.println("nonmatching B = " + finishedData);
            break;
        case 'V': //Volume adg
            if (finishedData.equals("Volume Adjustment")) 
                elementID = VOLUME_ADJUSTMENT;
            else System.out.println("nonmatching V = " + finishedData);
            break;
        case 'C': //Comments
            if (finishedData.equals("Comments")) 
                elementID = COMMENTS;
            else if (finishedData.equals("Compilation")) 
                elementID = COMPILATION;
            else if (finishedData.equals("Composer")) 
                elementID = COMPOSER;
            else System.out.println("nonmatching C = " + finishedData);
            break;
        case 'L': //Location || Library Folder Count
            if (finishedData.equals("Location")) 
                elementID = LOCATION;
            else if (finishedData.equals("Library Folder Count"))
                elementID = LIBRARY_FOLDER_COUNT;
            else if (finishedData.equals("Library Name"))
                elementID = LIBRARY_NAME;
            else System.out.println("nonmatching L = " + finishedData);
            break;
        case 'F': //File Folder Count
            if (finishedData.equals("File Folder Count")) 
                elementID = FILE_FOLDER_COUNT;
            else if (finishedData.equals("File Creator")) 
                elementID = FILE_CREATOR;
            else if (finishedData.equals("File Type")) 
                elementID = FILE_TYPE;
            else System.out.println("nonmatching F = " + finishedData);
            break;
        case 'P' :
            if (finishedData.equals("Play Count")) 
                elementID = PLAY_COUNT;
            else if (finishedData.equals("Play Date")) 
                elementID = PLAY_DATE;
            else if (finishedData.equals("Play Date UTC")) 
                elementID = PLAY_DATE_UTC;
            else if (finishedData.equals("Playlist ID"))
                elementID = PLAYLIST_ID;
            else if (finishedData.equals("Playlist Persistent ID"))
                elementID = PLAYLIST_PERSISTENT_ID;
            else if (finishedData.equals("Playlist Items"))
                elementID = PLAYLIST_ITEMS;
            else if (finishedData.equals("Playlists")) {
                inPlaylists = true;
                inTracks = false;
            }
            else System.out.println("nonmatching P = " + finishedData);
            break;
        case 'R': //Location || Library Folder Count
            if (finishedData.equals("Rating")) 
                elementID = RATING;
            else System.out.println("nonmatching R = " + finishedData);
            break;
        default :
//            System.out.println("songinfo element not dealt with = " + finishedData);
        elementID = UNKNOWN;
        }
        //              System.out.println("elementID = " +elementID);
    }
    
    private void captureSongData(String finishedData){
        
        if (elementID != UNKNOWN) {
            // not unknown, so we must have seen something in the key, 
        }
        
        switch (elementID) {
        case UNKNOWN:  break;
        case LIBRARY_FOLDER_COUNT:  
            
            break;
        case FILE_FOLDER_COUNT:  
            break;
        case LOCATION:
            
            File file;
            
            String filename= finishedData;//.replaceFirst("file://localhost","");
            // FIXME filename not handled correctly, and maybe my lib is diff because it is on another drive?
            // Also, getting a bunch of mistakes in reading the xml?  why?
            //System.out.println("filename = " + filename + " and separator = "
            // + File.separator);
            /*if (File.separator.equals("\\")) {
             filename = filename.replaceFirst(":", "\\\\");
             filename = filename.replaceFirst(":", "\\\\");
             filename = filename.replaceFirst(":", "\\\\");
             } else {
             filename = filename.replaceFirst(":", File.separator);
             filename = filename.replaceFirst(":", File.separator);
             filename = filename.replaceFirst(":", File.separator);
             } // end else
             */                        //System.out.println("filename = " + filename);
            //                        file = new File(filename);
            
            song.setPath(filename);
            if (song.format == null)
                song.format = filename.substring(0,filename.length()-3);
            break;
        case COMMENTS:
            
            break;
        case VOLUME_ADJUSTMENT:  
            break;
            
        case BIT_RATE:
            
            break;
        case DATE_ADDED:
            
            break;
            
        case DATE_MODIFIED:  
            break;
            
        case YEAR:
            
            break;
        case SAMPLE_RATE:
            
            break;
        case SIZE:
            try {
                song.size = Integer.parseInt(finishedData);
            } catch (NumberFormatException nfe)
            {
                song.size = 0;
            }
            break;
        case KIND:
            if (finishedData.equals("AAC audio file"))
                song.format = "m4a"; 
            else if (finishedData.equals("AIFF audio file"))
                song.format = "aiff";
            else if (finishedData.equals("MPEG audio file"))
                song.format = "mp3";
            else if (finishedData.equals("MPEG audio stream"))
                song.format = "mps";
            else if (finishedData.equals("Protected AAC audio file"))
                song.format = "m4p";
            else if (finishedData.equals("QuickTime movie file"))
                song.format = "mov";
            else 
                System.out.println("unknown song type: " + finishedData);
            break;
            
        case GENRE:
            song.genre = finishedData;
            break;
        case ALBUM:
            song.album = finishedData;
            break;
        case ARTIST:
            song.artist = finishedData;
            break;
        case NAME:
            song.name = finishedData;
            break;
        case TOTAL_TIME:
            try {
                song.time = Integer.parseInt(finishedData);
            } catch (NumberFormatException nfe)
            {
                song.time = 0;
            }
            break;
        case TRACK_TYPE:
            
            break;
        case TRACK_NUMBER:
            try {
                song.track = Integer.parseInt(finishedData);
            } catch (NumberFormatException nfe)
            {
                song.track = 0;
            }
            break;
        case TRACK_ID:
            try {
                song.id = Integer.parseInt(finishedData);
            } catch (NumberFormatException nfe)
            {
                song.id = 0;
            }
            break;
        case COMPOSER: 
            break;
        case TRACK_COUNT:
            break;
        case ARTWORK_COUNT:
            break;
        case GROUPING:
            break;
            
        case DISC_COUNT:
            break;
        case DISC_NUMBER:
            break;
        case BPM:
            break;
        case COMPILATION:
            song.compilation = true;
            break;
        case FILE_CREATOR:
            break;
        case FILE_TYPE:
            break;
        case PLAY_COUNT:
            break;
        case PLAY_DATE:
            break;
        case PLAY_DATE_UTC:
            break;
        case RATING:
            break;
        default :
            System.err.println("invalid id!");
        }
        elementID = 0; // reset it
    
    }
    
    private void capturePlaylistData(String finishedData) {
//        System.out.println(finishedData);
        switch (elementID) {
        case NAME:
            playlist.name = finishedData;
            break;
        case PLAYLIST_ALL_ITEMS:
            break;
        case TRACK_ID:
            playlistSongIDs.add(finishedData);
            elementID = -1;
            break;
        default:
            break;
        }
    }
    
    private void captureLibraryData(String finishedData) {
        switch (elementID) {
        case LIBRARY_NAME:
            libraryName = finishedData;
            break;
        default:
            break;
        }
    }
    
    public void characters(char[] ch, int start, int length)
    throws SAXException { // apparantly, there can be stuff spread out on multiple lines...
        if (length == 0) {
            return;
        }

        buffer.append(ch,start, length);
    }   
}