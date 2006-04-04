// ITunesDBSonglistHeader
// $Id: ITunesDBSonglistHeader.java,v 1.3 2004/07/31 02:01:02 woooo Exp $
//
// Copyright (C) 2002-2003 Axel Wernicke <axel.wernicke@gmx.de>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package de.axelwernicke.mypod.ipod;

import java.util.Iterator;
import java.util.Vector;
import de.axelwernicke.mypod.MP3Meta;


/**
 *  A SongListHeader object stores the informations from the mhlt tag. It has
 *  references to the songs in the list. <PRE>
 *	mhlt:
 *	x00	'mhlt'				- tag
 *	x04								- tag size
 *	x08								- total count of songs in the list
 * </PRE>
 *
 * @author     axelwe
 * @created    July 16, 2004
 * @see        techdoc for more details
 */
public class ITunesDBSonglistHeader {
	/**  tag size inn bytes */
	private int tagSize       = 92;
	/**  list of song items - all items stored on the iPod are in here !! */
	private Vector songItems;


	/**  Standard Construcor for a Song list item. */
	ITunesDBSonglistHeader() {
		songItems = new Vector();
	}


	/**
	 *  Adds a Clip to iTunes DB Songlist. a new song item containing all meta
	 *  data is created and added to the songlist
	 *
	 * @param  fileIndex  of to clip in the iTunes DB
	 * @param  meta       data of the clip
	 * @return            size of the created song item
	 */
	int addClip(int fileIndex, MP3Meta meta) {
		// create song item
		ITunesDBSongItem songItem  = new ITunesDBSongItem();

		// set meta data
		songItem.setDate(ITunesDBParser.dateToMacDate(System.currentTimeMillis()));
		songItem.setDuration((int) meta.getDuration() * 1000);  // duration is ms in iTunes DB
		songItem.setFilesize((int) meta.getFilesize());
		songItem.setRecordIndex(fileIndex);
		songItem.setTrackNumber(meta.getTrack());
		songItem.setUnknown8(256);
//		songItem.unknown6 = 0;
//		songItem.unknown7 = 0;
//		songItem.unknown13 = 0;
//		songItem.unknown14 = 0;
		songItem.setBitrate(meta.getBitrate());
//		songItem.unknown16 = 0;

		// create song content items
		songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.FILETYPE, 1,
				ITunesDBParser.stringToUTF16LittleEndian("MPEG audio file")));

		/**
		 *  directories than F00
		 */
		String path                = new StringBuffer(":iPod_Control:Music:F00:").append(fileIndex).append(".mp3").toString();
		songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.PATH, 1,
				ITunesDBParser.stringToUTF16LittleEndian(path)));

		String artist              = meta.getArtist();
		if (artist != null && artist.length() != 0) {
			songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.ARTIST, 1,
					ITunesDBParser.stringToUTF16LittleEndian(artist)));
		}

		String album               = meta.getAlbum();
		if (album != null && album.length() != 0) {
			songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.ALBUM, 1,
					ITunesDBParser.stringToUTF16LittleEndian(album)));
		}

		String comment             = meta.getComment();
		if (comment != null && comment.length() != 0) {
			songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.COMMENT, 1,
					ITunesDBParser.stringToUTF16LittleEndian(comment)));
		}

		String genre               = meta.getGenre();
		if (genre != null && genre.length() != 0) {
			songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.GENRE, 1,
					ITunesDBParser.stringToUTF16LittleEndian(genre)));
		}

		if (meta.getTitle() != null && meta.getTitle().length() != 0) {
			songItem.attachContent(new ITunesDBContentItem(ITunesDBContentItem.TITEL, 1,
					ITunesDBParser.stringToUTF16LittleEndian(meta.getTitle())));
		}

		// add song item to the songlist header and correct song count
		songItems.addElement(songItem);

		return songItem.getRecordSize();
	}


	/**
	 *  Gets the members of the object as formatted string.
	 *
	 * @return    formatted string
	 */
	public String toString() {
		return new StringBuffer("[tagSize] ").append(tagSize)
				.append('\t').append("[songCount] ").append(songItems.size())
				.toString();
	}


	/**
	 *  Getter for property tagSize.
	 *
	 * @return    Value of property tagSize.
	 */
	public int getTagSize() {
		return tagSize;
	}


	/**
	 *  Setter for property tagSize.
	 *
	 * @param  tagSize  New value of property tagSize.
	 */
	public void setTagSize(int tagSize) {
		this.tagSize = tagSize;
	}


	/**
	 *  Getter for property songCount.
	 *
	 * @return    Value of property songCount.
	 */
	public int getSongCount() {
		return songItems.size();
	}


	/**
	 *  Getter for property songItems. This is private, cause song items are
	 *  added and removed by add and remove method...
	 *
	 * @return    Value of property songItems.
	 */
	private Vector getSongItems() {
		return songItems;
	}


	/**
	 *  Setter for property songItems.
	 *
	 * @param  songItems  New value of property songItems.
	 */
	private void setSongItems(java.util.Vector songItems) {
		this.songItems = songItems;
	}


	/**
	 *  Adds a feature to the SongItem attribute of the ITunesDBSonglistHeader
	 *  object
	 *
	 * @param  songItem  The feature to be added to the SongItem attribute
	 */
	public void addSongItem(ITunesDBSongItem songItem) {
		this.songItems.add(songItem);
	}


	/**
	 *  Gets the songItem attribute of the ITunesDBSonglistHeader object
	 *
	 * @param  index  Description of the Parameter
	 * @return        The songItem value
	 */
	public ITunesDBSongItem getSongItem(int index) {
		return (ITunesDBSongItem) this.songItems.get(index);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  index  Description of the Parameter
	 */
	public void removeSongItem(int index) {
		this.songItems.removeElementAt(index);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  songItem  Description of the Parameter
	 */
	public void removeSongItem(ITunesDBSongItem songItem) {
		this.songItems.remove(songItem);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  fileIndex  Description of the Parameter
	 * @return            Description of the Return Value
	 */
	boolean containsClip(int fileIndex) {
		boolean found              = false;

		// iterate over all songs until we find the searched one...
		ITunesDBSongItem songItem;
		for (Iterator songIter = this.songItems.iterator(); !found && songIter.hasNext(); ) {
			songItem = (ITunesDBSongItem) songIter.next();
			if (songItem.getRecordIndex() == fileIndex) {
				found = true;
			}
		}

		return found;
	}

}  // classe song list header
