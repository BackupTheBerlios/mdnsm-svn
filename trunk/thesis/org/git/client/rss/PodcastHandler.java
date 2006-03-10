/*
 * Created on Jan 24, 2005
 * Created by wooo as part of git
 */
package org.git.client.rss;

/**
 * @author wooo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.git.client.Playlist;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


public class PodcastHandler extends DefaultHandler {
    public String libraryName;
    private PodcastHost host;
    private ArrayList songs = new ArrayList();
    
    private PodcastSong song;
    private PodcastPlaylist playlist;
    private StringBuffer buffer = new StringBuffer();
    
    private String elementName;
    private String url;
    private String length;
    
    private boolean inChannel = false;
    private boolean inItem = false; 
    
    private boolean inBoolean = false;
    private boolean boolean_data = false;
    
    public PodcastHandler(PodcastHost host) {
        this.host = host;
    }
    
    public void startElement (String uri, String localName, String qName, Attributes attributes)
    throws SAXException
    {
        if (inChannel) {
            if (inItem) {
                elementName = qName;
                if (elementName.equals("enclosure")) {
                    url = attributes.getValue("url");
                    length = attributes.getValue("length");
                }
            } else if (qName.equals("item")) {
                inItem = true;
                song = new PodcastSong(host);
                return;
            }
        } else if (qName.equals("channel")) {
            inChannel = true;
            return;
        }
    }
        
     public void endElement (String uri, String localName, String qName) throws SAXException
     {
         finishData();
         if (inChannel) {
             if (qName.equals("channel")) {
                 inChannel = false;
                 return;
             } else if (qName.equals("item")) {
                 song.track = 0;
                 song.time = 0;
                 song.bitrate = 0;
                 song.format = "mp3";
                 song.album = "podcast";
                 try {
                    song.url = new URL(this.url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (length != null && length.length() > 0)
                    song.size = Integer.parseInt(length);	
                 songs.add(song);
                 inItem = false;
                 elementName = null;
                 return;
             }
         }
     }
    
    public List getSongs() {
        return songs;
    }
    
    private void finishData() {
        String finishedData;
        finishedData = buffer.toString().trim();
        buffer = new StringBuffer();
        if (inChannel) {
            if (inItem) {
                getSongData(finishedData);
            }
        }
    }
    
    private void getSongData(String data) {
        if (elementName.equals("title"))
            song.name = data;
//        if (elementName.equals("link"))
//            try {
//                song.url = new URL(data);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                song.url = null;
//            }
        if (elementName.equals("author"))
            song.artist = data;
        if (elementName.equals("pubDate"))
            song.pubDate = new Date(data);
    }
    
    public void characters(char[] ch, int start, int length)
    throws SAXException { // apparantly, there can be stuff spread out on multiple lines...
        if (length == 0) {
            return;
        }

        buffer.append(ch,start, length);
    }   
}