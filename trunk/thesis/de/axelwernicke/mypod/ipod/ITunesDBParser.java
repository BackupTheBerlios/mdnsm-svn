// ITunesDBParser
// $Id: ITunesDBParser.java,v 1.5 2005/01/15 06:02:53 gjuggler Exp $
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.git.GITProperties;

import de.axelwernicke.mypod.Backend;
import de.axelwernicke.mypod.myPod;


/**
 *  Represents an iTunes Database, which contains clip and playlist
 *  information.
 *
 * @author     axelwe
 * @created    July 16, 2004
 */
public class ITunesDBParser {

	/**  iTunes db token */
	private static final String DB_RECORD                      = "mhbd";

	/**  list holder token */
	private static final String LIST_HOLDER                    = "mhsd";

	/**  song item token */
	private static final String SONG_ITEM                      = "mhit";

	/**  song item content token */
	private static final String SONG_ITEM_CONTENT              = "mhod";

	/**  playlist header token */
	private static final String PLAYLIST_HEADER                = "mhlp";

	/**  playlist item token */
	private static final String PLAYLIST_ITEM                  = "mhyp";

	/**  song item index token */
	private static final String PLAYLIST_INDEX_ITEM            = "mhip";
	
	/**  jdk1.4 logger */
	private static Logger logger = Logger.getLogger("de.axelwernicke.mypod");
	
	
	/**  contains temp database for coding / decoding */
	private static ITunesDB db                                 = null;

	/**  path to store the database on an ipod  */
	private static String ITUNES_DB_PATH                       = "iPod_Control" + File.separator
			+ "iTunes" + File.separator
			+ "iTunesDB";

	/**  song list header token */
	private static String SONG_LIST_HEADER                     = "mhlt";

	/**  recent playlist object */
	private ITunesDBPlaylistItem recentPlaylist                = null;

	/**  recent song object */
	private ITunesDBSongItem recentSong                        = null;

	/**  recent playlistIndexItem object */
	private ITunesDBPlaylistIndexItem recentPlaylistIndexItem  = null;


	/**  Creates a new instance of a parser  */
	public ITunesDBParser() { }


	/**
	 *  Creates a new instance of ITunesDB
	 *
	 * @param  _db  database to create a parser for
	 */
	public ITunesDBParser(ITunesDB _db) {
		db = _db;
	}


	/**
	 *  Parses a db record
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDB parseDbRecord(InputStream fis) {
		ITunesDB db  = new ITunesDB();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			db.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			db.setRecordSize(littleEndianToInt(dword));

			fis.read(dword);
			db.setUnknown3(littleEndianToInt(dword));

			fis.read(dword);
			db.setUnknown4(littleEndianToInt(dword));

			fis.read(dword);
			db.setUnknown5(littleEndianToInt(dword));

			// log unknown tags
			long tmp;
			int to      = db.getTagSize() / 4;
			for (int i = 7; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) {	logger.warning("unknwon: " + i +" = " + tmp); }
			}

			// log
			logger.finest(db.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return db;
	}  // parse bd record


	/**
	 *  Encodes a db record.
	 *
	 * @param  fos  stream to write the db record to
	 */
	static void encodeDbRecord(OutputStream fos) {
		try {
			byte tag[]  = {'m', 'h', 'b', 'd'};
			fos.write(tag);
			fos.write(intToLittleEndian(db.getTagSize()));
			fos.write(intToLittleEndian(db.getRecordSize()));
			fos.write(intToLittleEndian(db.getUnknown3()));
			fos.write(intToLittleEndian(db.getUnknown4()));
			fos.write(intToLittleEndian(db.getUnknown5()));

			// write padding
			int to    = db.getTagSize() - (6 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// logging
			logger.finest(db.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode db record


	/**
	 *  Parses a list holder token
	 *
	 * @param  fis  stream to parse from
	 * @return      parsed object
	 */
	private static ITunesDBListHolder parseListHolder(InputStream fis) {
		ITunesDBListHolder listHolder  = new ITunesDBListHolder();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			listHolder.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			listHolder.setRecordSize(littleEndianToInt(dword));

			fis.read(dword);
			listHolder.setListType(littleEndianToInt(dword));

			// log unknown tags
			long tmp;
			int to      = listHolder.getTagSize() / 4;
			for (int i = 4; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				if (tmp != 0) {
					logger.warning("unknown: " + i + " = " + tmp);
				}
			}

			// logging
			logger.finest(listHolder.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return listHolder;
	}  // parseListHolder


	/**
	 * @param  fos
	 * @param  lh
	 */
	static void encodeListHolder(OutputStream fos, ITunesDBListHolder lh) {
		try {
			byte tag[]  = {'m', 'h', 's', 'd'};
			fos.write(tag);
			fos.write(intToLittleEndian(lh.getTagSize()));
			fos.write(intToLittleEndian(lh.getRecordSize()));
			fos.write(intToLittleEndian(lh.getListType()));

			// write padding
			int to    = lh.getTagSize() - (4 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// logging
			logger.finest(lh.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode listHolder


	/**
	 *  Parses a songlist header record.
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDBSonglistHeader parseSonglistHeader(InputStream fis) {
		ITunesDBSonglistHeader songlistHeader  = new ITunesDBSonglistHeader();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			songlistHeader.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			// this is done implicitly  by adding song items
			// songlistHeader.setSongCount(littleEndianToInt(dword) );

			// log unknown tags
			long tmp;
			int to      = songlistHeader.getTagSize() / 4;
			for (int i = 4; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) { logger.warning("unknwon: " + i + " = " + tmp); }
			}

			// logging
			logger.finest(songlistHeader.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return songlistHeader;
	}  // parse song list header


	/**
	 * @param  fos
	 */
	static void encodeSonglistHeader(OutputStream fos) {
		try {
			byte tag[]       = {'m', 'h', 'l', 't'};
			fos.write(tag);
			fos.write(intToLittleEndian(db.getSonglistHeader().getTagSize()));
			fos.write(intToLittleEndian(db.getSonglistHeader().getSongCount()));

			// write padding
			int to         = db.getSonglistHeader().getTagSize() - (3 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// encode all song list items
			int songCount  = db.getSonglistHeader().getSongCount();
			for (int i = 0; i < songCount; i++) {
				encodeSongItem(fos, db.getSonglistHeader().getSongItem(i));
			}

			// logging
			logger.finest(db.getSonglistHeader().toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode songlist header


	/**
	 *  Parses a song item record.
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDBSongItem parseSongItem(InputStream fis) {
		ITunesDBSongItem songItem  = new ITunesDBSongItem();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			songItem.setTagSize(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setRecordSize(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setContentCount(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setRecordIndex(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setUnknown6(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setUnknown7(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setUnknown8(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setDate(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setFilesize(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setDuration(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setTrackNumber(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setUnknown13(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setUnknown14(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setBitrate(littleEndianToInt(dword));
			fis.read(dword);
			songItem.setSamplerate(littleEndianToInt(dword));

			// log unknown tags
			long tmp;
			int to      = songItem.getTagSize() / 4;
			for (int i = 17; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) { logger.warning("unknwon: " + i + " = " + tmp); }
			}

			// logging
			logger.finest(songItem.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return songItem;
	}  // parse song item


	/**
	 * @param  fos
	 * @param  si
	 */
	static void encodeSongItem(OutputStream fos, ITunesDBSongItem si) {
		try {
			byte tag[]  = {'m', 'h', 'i', 't'};
			fos.write(tag);
			fos.write(intToLittleEndian(si.getTagSize()));
			fos.write(intToLittleEndian(si.getRecordSize()));
			fos.write(intToLittleEndian(si.getContentCount()));
			fos.write(intToLittleEndian(si.getRecordIndex()));
			fos.write(intToLittleEndian(si.getUnknown6()));
			fos.write(intToLittleEndian(si.getUnknown7()));
			fos.write(intToLittleEndian(si.getUnknown8()));
			fos.write(intToLittleEndian(si.getDate()));
			fos.write(intToLittleEndian(si.getFilesize()));
			fos.write(intToLittleEndian(si.getDuration()));
			fos.write(intToLittleEndian(si.getTrackNumber()));
			fos.write(intToLittleEndian(si.getUnknown13()));
			fos.write(intToLittleEndian(si.getUnknown14()));
			fos.write(intToLittleEndian(si.getBitrate()));
			fos.write(intToLittleEndian(si.getSamplerate()));

			// write padding
			int to    = si.getTagSize() - (16 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// encode all content items
			to = si.getContent().size();
			for (int i = 0; i < to; i++) {
				encodeContentItem(fos, (ITunesDBContentItem) si.getContent().get(i));
			}

			// logging
			logger.finest(si.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode song item


	/**
	 *  Parses a content item record.
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDBContentItem parseContentItem(InputStream fis) {
		ITunesDBContentItem contentItem  = new ITunesDBContentItem();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			//songItemContent.tagSize = littleEndianToInt(dword);
			long ts     = littleEndianToInt(dword);
			if (ts != contentItem.getTagSize()) {
				logger.warning("parsed tag size does not equal default" + ts + " vs. " + contentItem.getTagSize());
			}

			fis.read(dword);
			// record size is set when setting content
			//long rs = littleEndianToInt(dword);

			fis.read(dword);
			contentItem.setContentTyp(littleEndianToInt(dword));

			fis.read(dword);
			contentItem.setUnknown5(littleEndianToInt(dword));

			fis.read(dword);
			contentItem.setUnknown6(littleEndianToInt(dword));

			fis.read(dword);
			contentItem.setListPosition(littleEndianToInt(dword));

			fis.read(dword);
			// content size is set when setting content
			long cs     = littleEndianToInt(dword);

			fis.read(dword);
			contentItem.setUnknown9(littleEndianToInt(dword));

			fis.read(dword);
			contentItem.setUnknown10(littleEndianToInt(dword));

			// read til end of tag as content
			byte tmp[]    = new byte[(int) cs];
			fis.read(tmp);

			// make default endian encoded and set content
			contentItem.setContent(tmp);

			// logging
			logger.finest(contentItem.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return contentItem;
	}  // parse song item content


	/**
	 * @param  fos
	 * @param  sic
	 */
	static void encodeContentItem(OutputStream fos, ITunesDBContentItem sic) {
		try {
			byte tag[]  = {'m', 'h', 'o', 'd'};
			fos.write(tag);
			fos.write(intToLittleEndian(sic.getTagSize()));
			fos.write(intToLittleEndian(sic.getRecordSize()));
			fos.write(intToLittleEndian(sic.getContentTyp()));
			fos.write(intToLittleEndian(sic.getUnknown5()));
			fos.write(intToLittleEndian(sic.getUnknown6()));
			fos.write(intToLittleEndian(sic.getListPosition()));
			fos.write(intToLittleEndian(sic.getContentSize()));
			fos.write(intToLittleEndian(sic.getUnknown9()));
			fos.write(intToLittleEndian(sic.getUnknown10()));

			fos.write(sic.getContent());

			// logging
			logger.finest(sic.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode song item content


	/**
	 *  Parses a playlist index item record.
	 *
	 * @param  fis  stream to parse from
	 * @return      object for the parsed record
	 */
	private static ITunesDBPlaylistIndexItem parsePlaylistIndexItem(InputStream fis) {
		ITunesDBPlaylistIndexItem playlistIndexItem  = new ITunesDBPlaylistIndexItem();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			playlistIndexItem.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			playlistIndexItem.setRecordSize(littleEndianToInt(dword));

			fis.read(dword);
			// set implicitly by adding content items
			// playlistIndexItem.setContentCount(littleEndianToInt(dword));

			fis.read(dword);
			playlistIndexItem.setUnknown5(littleEndianToInt(dword));

			fis.read(dword);
			playlistIndexItem.setUnknown6(littleEndianToInt(dword));

			fis.read(dword);
			playlistIndexItem.setSongIndex(littleEndianToInt(dword));

			// log unknown tags
			long tmp;
			int to      = playlistIndexItem.getTagSize() / 4;
			for (int i = 8; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) { logger.warning("unknwon: " + i + " = " + tmp); }
			}

			// logging
			logger.finest(playlistIndexItem.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return playlistIndexItem;
	}  // parse playlistIndexItem


	/**
	 *  Encodes mhip item
	 *
	 * @param  fos  stream to write to
	 * @param  pii  playlist index item
	 */
	private static void encodePlaylistIndexItem(OutputStream fos, ITunesDBPlaylistIndexItem pii) {
		try {
			byte tag[]  = {'m', 'h', 'i', 'p'};
			fos.write(tag);
			fos.write(intToLittleEndian(pii.getTagSize()));
			fos.write(intToLittleEndian(pii.getRecordSize()));
			fos.write(intToLittleEndian(pii.getContentCount()));
			fos.write(intToLittleEndian(pii.getUnknown5()));
			fos.write(intToLittleEndian(pii.getUnknown6()));
			fos.write(intToLittleEndian(pii.getSongIndex()));

			// write padding
			int to    = pii.getTagSize() - (7 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// write attached content items
			for (int i = 0; i < pii.getContentCount(); i++) {
				encodeContentItem(fos, (ITunesDBContentItem) pii.getContentItem(i));
			}

			// logging
			logger.finest(pii.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode song item index


	/**
	 *  Parses a playlist header record.
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDBPlaylistHeader parsePlaylistHeader(InputStream fis) {
		ITunesDBPlaylistHeader playlistHeader  = new ITunesDBPlaylistHeader();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			playlistHeader.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			// done implicitly playlistHeader.playlistCount = littleEndianToInt(dword);

			// log unknown tags
			long tmp;
			int to      = playlistHeader.getTagSize() / 4;
			for (int i = 4; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) { logger.warning("unknwon: " + i + " = " + tmp); }
			}

			// logging
			logger.finest(playlistHeader.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return playlistHeader;
	}  // encode playlist header


	/**
	 *  Parses a playlist item record.
	 *
	 * @param  fis  stream to parse the record from
	 * @return      object for the parsed record
	 */
	private static ITunesDBPlaylistItem parsePlaylistItem(InputStream fis) {
		ITunesDBPlaylistItem playlistItem  = new ITunesDBPlaylistItem();

		try {
			byte dword[]  = new byte[4];

			fis.read(dword);
			playlistItem.setTagSize(littleEndianToInt(dword));

			fis.read(dword);
			playlistItem.setRecordSize(littleEndianToInt(dword));

			fis.read(dword);
			playlistItem.setContentCount(littleEndianToInt(dword));

			fis.read(dword);
			playlistItem.setSongCount(littleEndianToInt(dword));

			fis.read(dword);
			playlistItem.setListType(littleEndianToInt(dword));

			// log unknown tags
			long tmp;
			int to      = playlistItem.getTagSize() / 4;
			for (int i = 7; i < to; i++) {
				fis.read(dword);
				tmp = littleEndianToInt(dword);
				// if( tmp != 0) { logger.warning("unknwon: " + i + " = " + tmp); }
			}

			// logging
			logger.finest(playlistItem.toString());
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return playlistItem;
	}


	/*
	    --------------------------------------- some endian conversion stuff ----------------------------------
	 */
	// first of all, JVM, Mac, and others are big endian machines
	// PC and iPod are little endians.

	/**
	 *  Decodes a byte array to long
	 *
	 * @param  value  byte array to decode
	 * @return        long representation of value
	 */
	static long littleEndianToLong(byte[] value) {
		long result  = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getLong();

		return result;
	}


	/**
	 *  Decodes a byte array to int
	 *
	 * @param  value  byte array to decode
	 * @return        int representation of value
	 */
	static int littleEndianToInt(byte[] value) {
		int result  = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt();

		return result;
	}


	/**
	 *  Encodes a long to an little endianded byte array.
	 *
	 * @param  value  value to encode
	 * @return        byte array containing the value little endianded
	 */
	static byte[] longToLittleEndian(long value) {
		ByteBuffer bb  = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
		bb.putLong(value);

		return bb.array();
	}


	/**
	 *  Encodes a long to an little endianded byte array.
	 *
	 * @param  value  value to encode
	 * @return        byte array containing the value little endianded
	 */
	static byte[] intToLittleEndian(int value) {
		ByteBuffer bb  = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(value);

		return bb.array();
	}


	/**
	 *  Converts a little endianed UTF16 byte array into a java string.
	 *
	 * @param  value  byte array to decode
	 * @return        string representing the decoded value
	 */
	public static String uTF16LittleEndianToString(byte[] value) {
		// wrap the byte array
		java.nio.CharBuffer cb  = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();

		// get the string
		return cb.toString();
	}


	/**
	 *  Converts a java String to an UTF16 little endianed byte array
	 *
	 * @param  value  string to convert
	 * @return        byte array containing the encoded value
	 */
	static byte[] stringToUTF16LittleEndian(String value) {
		// allocate the byte array
		ByteBuffer bb  = ByteBuffer.allocate(value.getBytes().length * 2).order(ByteOrder.LITTLE_ENDIAN);

		// set the value
		bb.asCharBuffer().put(value);
		bb.compact();

		// get the encoded array
		return bb.array();
	}


	/**
	 *  Converts a date as long from iPod to a java date as long <PRE>
	 * mac date is seconds since 01/01/1904
	 * java date is milliseconds since 01/01/1970
	 * </PRE>
	 *
	 * @param  macDate  date on iPod
	 * @return          date in java format
	 */
	public static long macDateToDate(int macDate) {
		long javaDate  = 0;

		// offset between java & iPod (Mac) & seconds to milliseconds
		javaDate = (macDate - 2082844800) * 1000;

		return javaDate;
	}


	/**
	 *  Converts a java date as long to a mac date as long <PRE>
	 * mac date is seconds since 01/01/1904
	 * java date is milliseconds since 01/01/1970
	 * </PRE>
	 *
	 * @param  javaDate  date in java format
	 * @return           date in mac format
	 */
	public static int dateToMacDate(long javaDate) {
		int macDate  = 0;

		// milliseconds -> seconds & offset between java & iPod (Mac)
		macDate = (int) (javaDate / 1000) + 2082844800;

		return macDate;
	}


	/**
	 *  Loads the database from an iPod.
	 *
	 * @return    the loaded ITunesDB
	 */
	
	ITunesDB load() {
		ITunesDB result  = null;
		try {
			// open data input stream from file
			FileInputStream fis      = new FileInputStream(myPod.getBackend().getPreferences().getIPodPath()
					+ File.separator + ITUNES_DB_PATH);
			BufferedInputStream bis  = new BufferedInputStream(fis, 65532);

			// parse stream
			decode(bis);

			// close file
			bis.close();

			// destroy local tmp data base
			result = db;
			db = null;
		} catch (Exception fnf) {
			logger.warning("exception raised: " + fnf.getMessage());
		}

		// return created database
		return result;
	}

// EDIT 1.7.2005 by GIT: allows for loading without a myPod instance, for stanalone use
	public ITunesDB load(File ipod_directory) {
	    ITunesDB result  = null;
		try {
		    
		    logger.info(ipod_directory.getPath());
			// open data input stream from file
			String s = ipod_directory.getPath()
					+ ITUNES_DB_PATH;
			logger.info(s);
			FileInputStream fis = new FileInputStream(s);
			BufferedInputStream bis  = new BufferedInputStream(fis, 65532);

			// parse stream
			System.err.println("Decoding...");
			decode(bis);

			// close file
			System.err.println("Closing...");
			bis.close();

			// destroy local tmp data base
			result = db;
			db = null;
		} catch (Exception fnf) {
			logger.warning("exception raised: " + fnf.getMessage());
		}

		// return created database
		return result;

	}
	
	/**
	 *  saves the database to an iPod
	 *
	 * @param  database  ITunesDB to save
	 * @return           Description of the Return Value
	 */
	boolean save(ITunesDB database, File ipod_directory) {
		boolean success  = false;
		// store database temporarily
		db = database;

		try {
			// open data output stream from file
			FileOutputStream fos      = new FileOutputStream(ipod_directory
					+ File.separator + ITUNES_DB_PATH);
			BufferedOutputStream bos  = new BufferedOutputStream(fos, 65532);

			// encode stream
			encode(bos);

			// close file
			bos.close();

			success = true;
		} catch (Exception fnf) {
			logger.warning("exception raised: " + fnf.getMessage());
			fnf.printStackTrace();
		}

		return success;
	}


	/**
	 *  decodes the binary database file
	 *
	 * @param  fis  stream to decode
	 */
	private void decode(InputStream fis) {
	    logger.setLevel(Level.FINEST);
	    logger.entering("ITunesDB", "decode");
		
		try {
			// do all frames
			byte tag[]  = new byte[4];

			System.err.println(fis.available());
			// parse all tags
			while (fis.read(tag) != -1) {
				// ----------- mhbd --------------
				if (new String(tag).equalsIgnoreCase(DB_RECORD)) {
					logger.info("found DB_RECORD tag");

					// this is the root record, set temp data base
					db = parseDbRecord(fis);
				}   // ----------- mhsd --------------
				else if (new String(tag).equalsIgnoreCase(LIST_HOLDER)) {
					logger.info("found LIST_HOLDER tag");
					ITunesDBListHolder listHolder  = parseListHolder(fis);

					// there are usually two list holder records in a database, add depending on type
					if (listHolder.getListType() == ITunesDBListHolder.SONGLIST) {
						db.setSonglistHolder(listHolder);
					} else if (listHolder.getListType() == ITunesDBListHolder.PLAYLIST) {
						db.setPlaylistHolder(listHolder);
					} else {
						logger.info("unknown list holder type: " + listHolder.getListType());
					}
				}   // ----------- mhlt --------------
				else if (new String(tag).equalsIgnoreCase(SONG_LIST_HEADER)) {
					logger.info("found SONG_LIST_HEADER tag");

					// create & decode
					db.setSonglistHeader(parseSonglistHeader(fis));
				}   // ----------- mhit --------------
				else if (new String(tag).equalsIgnoreCase(SONG_ITEM)) {
					logger.info("found SONG_ITEM tag");
					ITunesDBSongItem songItem  = parseSongItem(fis);

					// mhit records always belong to the song list, so add it there and mark as recent
					db.getSonglistHeader().addSongItem(songItem);
					recentSong = songItem;
				}   // ----------- mhod --------------
				else if (new String(tag).equalsIgnoreCase(SONG_ITEM_CONTENT)) {
					logger.info("found SONG_ITEM_CONTENT tag");
					ITunesDBContentItem contentItem  = parseContentItem(fis);

					// mhod records may belong to a song, or to an playlist entry, or to an
					// playlist index item - crazy thing :)
					//
					// first check if we have an playlist index item
					if (recentPlaylistIndexItem != null) {
						// tweak content
						byte tweak[]  = {0x00, 0x00, 0x00, 0x00};
						contentItem.setContent(tweak);
						// set Content changes the content size - we don't want this ...
						contentItem.setContentSize(0);

						recentPlaylistIndexItem.addContenItem(contentItem);
					}   // now try if we have at least an playlist header
					else if (recentPlaylist != null) {
						recentPlaylist.getSongItems().addElement(contentItem);
					}   // otherwise we have an song list content
					else {
						// add to song list
						recentSong.getContent().addElement(contentItem);
					}
				}   // ----------- mhlp --------------
				else if (new String(tag).equalsIgnoreCase(PLAYLIST_HEADER)) {
					logger.info("found PLAYLIST_HEADER tag");
					ITunesDBPlaylistHeader playlistHeader  = parsePlaylistHeader(fis);

					// since there is only one playlist header, simply add it to the data base
					db.setPlaylistHeader(playlistHeader);
				}   // ----------- mhyp --------------
				else if (new String(tag).equalsIgnoreCase(PLAYLIST_ITEM)) {
					logger.info("found PLAYLIST_ITEM tag");
					ITunesDBPlaylistItem playlistItem  = parsePlaylistItem(fis);

					// add to playlist header and mark as recent playlist
					db.getPlaylistHeader().addPlaylist(playlistItem);
					recentPlaylist = playlistItem;

					// reset recent index item
					recentPlaylistIndexItem = null;
				}   // ----------- mhip --------------
				else if (new String(tag).equalsIgnoreCase(PLAYLIST_INDEX_ITEM)) {
					logger.info("found PLAYLIST_INDEX_ITEM tag");
					ITunesDBPlaylistIndexItem playlistIndexItem  = parsePlaylistIndexItem(fis);

					// mhip records always belong to a playlist, so append to the most recently added playlist
					recentPlaylist.getSongItems().addElement(playlistIndexItem);

					// make this index item the recent one
					recentPlaylistIndexItem = playlistIndexItem;
				} else {
				    logger.info("Unknown tag: " +new String(tag));
				}
			}
		} catch (Exception e) {
			logger.info("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		logger.exiting("ITunesDB", "decode");
	}


	/**
	 *  encodes the binary database file
	 *
	 * @param  fos  stream to write to
	 */
	private void encode(OutputStream fos) {
		// encode database header
		encodeDbRecord(fos);

		// encode all songs
		if (db.getSonglistHolder() != null) {
			encodeListHolder(fos, db.getSonglistHolder());
		} else {
			logger.info("song list holder was null");
		}

		if (db.getSonglistHeader() != null) {
			encodeSonglistHeader(fos);
		} else {
			logger.info("song list header was null");
		}

		// encode all playlists
		if (db.getPlaylistHolder() != null) {
			encodeListHolder(fos, db.getPlaylistHolder());
		} else {
			logger.info("playlist holder was null");
		}

		if (db.getPlaylistHeader() != null) {
			encodePlaylistHeader(fos);
		} else {
			logger.info("playlist header was null");
		}
	}  // parse playlist header


	/**
	 *  Encodes a playlist header item of an iTunes DB
	 *
	 * @param  fos  Stream to encode into
	 */
	void encodePlaylistHeader(OutputStream fos) {
		try {
			byte tag[]           =
					{'m', 'h', 'l', 'p'};
			fos.write(tag);
			fos.write(intToLittleEndian(db.getPlaylistHeader().getTagSize()));
			fos.write(intToLittleEndian(db.getPlaylistHeader().getPlaylistCount()));

			// write padding
			for (int i = 0; i < db.getPlaylistHeader().getTagSize() - (3 * 4); i++) {
				fos.write(0x00);
			}

			// encode all attached playlists
			int playlistCount  = db.getPlaylistHeader().getPlaylistCount();
			for (int i = 0; i < playlistCount; i++) {
				encodePlaylistItem(fos, db.getPlaylistHeader().getPlaylist(i));
			}

			// logging
			logger.finest(db.getPlaylistHeader().toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // parse playlist item


	/**
	 * @param  fos
	 * @param  pi
	 */
	void encodePlaylistItem(OutputStream fos, ITunesDBPlaylistItem pi) {
		try {
			byte tag[]     = {'m', 'h', 'y', 'p'};
			fos.write(tag);
			fos.write(intToLittleEndian(pi.getTagSize()));
			fos.write(intToLittleEndian(pi.getRecordSize()));
			fos.write(intToLittleEndian(pi.getContentCount()));
			fos.write(intToLittleEndian(pi.getSongCount()));
			fos.write(intToLittleEndian(pi.getListType()));

			// write padding
			int to       = pi.getTagSize() - (6 * 4);
			for (int i = 0; i < to; i++) {
				fos.write(0x00);
			}

			// encode all attached playlist entries
			int songCnt  = pi.getSongItems().size();
			for (int i = 0; i < songCnt; i++) {
				Object tmp  = pi.getSongItems().get(i);
				if (tmp instanceof de.axelwernicke.mypod.ipod.ITunesDBPlaylistIndexItem) {
					// encode all index entries
					encodePlaylistIndexItem(fos, (ITunesDBPlaylistIndexItem) tmp);
				} else {
					// encode content item
					ITunesDBParser.encodeContentItem(fos, (ITunesDBContentItem) tmp);
				}
			}

			// logging
			logger.finest(pi.toString());

		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}
	}  // encode playlist item


	/**
	 *  Calculates the _real_ size of the object. The object is encoded into a
	 *  byte array to determine size.
	 *
	 * @param  obj
	 * @return      real size of the record
	 */
	int calculateRecordSize(Object obj) {
		int size  = 0;

		try {
			ByteArrayOutputStream bau  = new ByteArrayOutputStream();

			// axelwe 030705 if( obj.getClass().getName().equals("de.axelwernicke.mypod.ipod.ITunesDB") )
			if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDB) {
				ITunesDBParser.encodeDbRecord(bau);
				ITunesDBParser.encodeListHolder(bau, db.getSonglistHolder());
				ITunesDBParser.encodeSonglistHeader(bau);
				ITunesDBParser.encodeListHolder(bau, db.getPlaylistHolder());
				this.encodePlaylistHeader(bau);
			}   // axelwe 030705 else if( obj.getClass().getName().equals("de.axelwernicke.mypod.ipod.ITunesDBContentItem") )
			else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBContentItem) {
				ITunesDBParser.encodeContentItem(bau, (ITunesDBContentItem) obj);
			}   // axelwe 030705 else if( obj.getClass().getName().equals("de.axelwernicke.mypod.ipod.ITunesDBListHolder") )
			else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBListHolder) {
				ITunesDBParser.encodeListHolder(bau, (ITunesDBListHolder) obj);
				if (((ITunesDBListHolder) obj).getListType() == ITunesDBListHolder.SONGLIST) {
					ITunesDBParser.encodeListHolder(bau, db.getSonglistHolder());
					ITunesDBParser.encodeSonglistHeader(bau);
				} else {
					ITunesDBParser.encodeListHolder(bau, db.getPlaylistHolder());
					this.encodePlaylistHeader(bau);
				}
			} else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBPlaylistHeader) {
				this.encodePlaylistHeader(bau);
			} else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBPlaylistIndexItem) {
				ITunesDBParser.encodePlaylistIndexItem(bau, (ITunesDBPlaylistIndexItem) obj);
			} else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBPlaylistItem) {
				this.encodePlaylistItem(bau, (ITunesDBPlaylistItem) obj);
			} else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBSongItem) {
				ITunesDBParser.encodeSongItem(bau, (ITunesDBSongItem) obj);
			} else if (obj instanceof de.axelwernicke.mypod.ipod.ITunesDBSonglistHeader) {
				ITunesDBParser.encodeSonglistHeader(bau);
			}

			bau.flush();
			size = bau.size();
			bau.close();
		} catch (Exception e) {
			logger.warning("Exception raised :" + e.getMessage());
		}

		return size;
	}
}  // ITunes db Parser
