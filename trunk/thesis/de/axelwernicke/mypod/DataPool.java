// DataPool
// $Id: DataPool.java,v 1.5 2005/02/16 02:12:47 gjuggler Exp $
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

package de.axelwernicke.mypod;

import de.axelwernicke.mypod.util.FileUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Collator;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.DefaultListModel;


/**
 * holds information about all scanned mp3 files
 *
 * @author  axel wernicke
 */
public class DataPool
{
	/** jdk1.4 logger */	
	private static Logger logger = Logger.getLogger("de.axelwernicke.mypod");
	
	/** filename to serialize data */
	private static String DATA_FILENAME = "data" + File.separator + "data";
	/** filename for backup of serialized data */
	private static String DATA_GZ_FILENAME = "data" + File.separator + "data.gz";

	/** filename to backup data file */
	private static String DATA_GZ_BACKUP_FILENAME = "data" + File.separator + "data.gz.bak";
	
	/** contains all MP3Meta objects for scanned files, access by oid - oid is long! */
	private Hashtable data = null;
	
	/** list of filenames and oids to enhance performance */
	private Hashtable filenameCache = null;
	
	
	/** Shuts the datapool down
	 */	
	protected void shutdown()
	{
		logger.entering("DataPool", "shutdown()");
		try
		{
			serializeData();
			logger.info("data object serialized");
		}
		catch(Exception e)
		{
			logger.warning("Exception raised: " + e.getMessage() );
			e.printStackTrace();
		}
		logger.exiting("DataPool", "shutdown()");		
	}
	
	
	/** checks the size of data and filename cache.
	 *
	 * @return true, if data and filename cache have the same size
	 */
	private boolean isCacheValid()
	{
		return ( data.size() == filenameCache.size() );
	}
	
	
	/** gets the oid of a clip from its filename
	 *
	 * @param filename filename of the clip
	 * @return oid for the clip
	 */	
	protected Long getOid(String filename)
	{
		return (Long)filenameCache.get(filename);
	}
	
	
	/** Checks if an oid is in the data pool
	 *
	 * @param oid to check
	 * @return true, if oid is in the data pool
	 */
	protected boolean contains( Long oid )
	{
		return data.contains( oid );
	}
	
	
	/** gets the oids for all music in the datapool
	 *
	 * @return set of oids
	 */	
	protected java.util.Set getAllOid()
	{
		return data.keySet();
	}
	
	
	/** gets information about an mp3 from database.
	 *
	 * @param file file to get the information for
	 * @return information for an mp3 file
	 */	
	private MP3Meta getMeta(File file)
	{
		MP3Meta meta = null;
		String path = file.getPath();
		
		if( filenameCache.containsKey(path))
		{
			meta = getMeta((Long)filenameCache.get(path));
		}
		else
		{
			logger.warning("tried to get nonexisting meta data for file " + file );
		}
		return meta;
	}

	
	/** gets information about an mp3 from database
	 *
	 * @param oid oid of an mp3 in the database
	 * @return information for an mp3
	 */	
	private MP3Meta getMeta(long oid)
	{
		return getMeta(new Long(oid));
	}
	
	
	/** gets all information about a track
	 *
	 * @param oid oid of the track to get information for
	 * @return meta data for the track
	 */
	public MP3Meta getMeta(Long oid)
	{
		MP3Meta meta = null;
		if( oid != null && data.containsKey(oid) )
		{
			meta = (MP3Meta)data.get(oid);
		}
		else
		{
			logger.warning("tried to get nonexisting meta data for oid " + oid );		
		}
		
		return meta;
	}
	
	
	/** gets from data pool the date when the selected track was modified the last time.
	 *
	 * @return date in milliseconds
	 * @param file file to get the information for
	 */
	protected long getLastModified(File file)
	{
		long lastModified = -1;
		String path = file.getPath();
		
		if( filenameCache.containsKey(path) )
		{
			lastModified = getLastModified( ((Long)filenameCache.get(path)) );
		}
		
		return lastModified;
	}

	
//	/** gets the last modified date for a clip
//	 *
//	 * @param oid oid of the clip
//	 * @return last modified date
//	 */	
//	private long getLastModiefied(long oid)
//	{
//		return getLastModified(new Long(oid));
//	}
		
	
	/** gets the last modified date for a clip
	 *
	 * @param oid oid of the clip
	 * @return timestamp when the clip was modified
	 */	
	private long getLastModified(Long oid)
	{
		long lastModified = -1;
		MP3Meta meta = getMeta(oid);
		
		if( meta != null )
		{
			lastModified = meta.getLastModified();
		}
		
		return lastModified;
	}
	
	
	/** checks if the clip specified by path and filename is in the data pool
	 *
	 * @param file filename and path of the clip
	 * @return true, if the file is known to the data pool
	 */	
	protected boolean isInPool(File file)
	{
		return filenameCache.containsKey(file.getPath());
	}

	
	/** Updates the id3 tags in the clip from the meta data object.
	 *
	 * @param mp3Meta to update from
	 * @param file to set the meta data for
	 */	
	protected void updateData( File file, MP3Meta mp3Meta )
	{
		logger.entering("DataPool", "updateData");

		Long oid = null;
		
		// find the meta data to update by filename / oid
		if( file != null )
		{
			oid = this.getOid( file.getPath() );
		}
		
		// set new meta data
		if( oid != null && mp3Meta != null )
		{
			data.put( oid, mp3Meta );
			
			logger.fine("updated clip meta data for file : " + file );
		}
		
		logger.exiting("DataPool", "updateData");
	}
	
	
	/** Updates the id3 tags in the clip from the meta data object.
	 *
	 * @param mp3Meta to update from
	 * @param file to set the meta data for
	 */	
	protected void updateData( Long oid, MP3Meta mp3Meta )
	{
		logger.entering("DataPool", "updateData");
		
		// set new meta data
		if( oid != null && mp3Meta != null )
		{
			data.put( oid, mp3Meta );			
		}
		
		logger.exiting("DataPool", "updateData");
	}
	
	
	/** Adds meta data object to the data pool.
	 *	A new (unique) oid is generated.
	 *	data and filename cache are updated
	 *
	 * @param mp3Meta set of meta data to add to the pool
	 * @param file the set of meta data belongs to
	 */
	protected void addData(File file, MP3Meta mp3Meta)
	{
		logger.entering("DataPool", "addData");
		
		if(mp3Meta != null)
		{
			// get a new oid, and make sure its unique
			int cnt = 0;
			Long newOid = null;
			do
			{
				newOid = new Long( System.currentTimeMillis() );
				cnt++;
			}
			while( data.containsKey( newOid ) && cnt < 10000 );
			
			// put data and update filenamecache
			data.put( newOid, mp3Meta );
			filenameCache.put( mp3Meta.getFilePath(), newOid );
		}
		
		logger.exiting("DataPool", "addData");
	}
	
	
	/** Removes a clip from the data pool.
	 * 	data and filename cache are updated.
	 * 	Note: the clip must be removed from all playlists and iPod before calling this!!
	 *
	 * @param oid of the clip to remove
	 */
	protected void removeClip(Long oid)
	{
		logger.entering("DataPool", "removeClip");

		// remove from cache if cache is valid
		if( filenameCache.containsValue(oid) )
		{
			// check if cache is valid
			if( filenameCache.get( getMeta(oid).getFilePath() ) != null )
			{
				filenameCache.remove( getMeta(oid).getFilePath() );
				
				if( logger.isLoggable( Level.FINE ))
				{
					logger.finer("removed from filenameCache :" + oid);
				}
			}
			else
			{
				rebuildFilenameCache();
				removeClip(oid);
			}
		}
		
		// remove from data
		while( data.containsKey(oid) )
		{
			logger.finer("removed from data pool :" + oid);
			this.data.remove(oid);
		}
		
		logger.fine("cache is valid after removing: " + this.isCacheValid() );

		logger.exiting("DataPool", "removeClip");
	}
	
	
	/** Rebuilds the filename cache from scratch. */	
	protected void rebuildFilenameCache()
	{
		logger.entering("de.axelwernicke.mypod.DataPool", "rebuildFilenameCache");
		logger.info("rebuilding filename cache");
		
		// create new filename cache
		filenameCache = new Hashtable( data.size()+1 );
		
		// iterate over all clips and add them to the cache
		Long oid;
		String path;
		for( Enumeration iter = data.keys(); iter.hasMoreElements(); )
		{
			oid = (Long)iter.nextElement();
			path = getMeta(oid).getFilePath();
			filenameCache.put(path, oid);
		}
		
		logger.exiting("de.axelwernicke.mypod.DataPool", "rebuildFilenameCache");
	}
	
	
	/** Serializes mp3 meta data and filename chache.
	 *	The current files are moved to .bak
	 */
	private void serializeData()
	{
		logger.entering("DataPool", "serializeData");
		try
		{
			// move current files to .bak
			File dataFile = new File(DATA_GZ_FILENAME);
			if( dataFile.exists() )
			{
				FileUtils.copy( dataFile, new File(DATA_GZ_BACKUP_FILENAME) );
			}
			
			// save mp3 meta data
			ObjectOutputStream zos = new ObjectOutputStream( new GZIPOutputStream( new FileOutputStream( DATA_GZ_FILENAME ), 65535 ) );
			zos.writeObject(data);
			zos.close();
//			ObjectOutputStream oos = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream( DATA_FILENAME ), 65535));
//			oos.writeObject(data);
//			oos.close();
		}
		catch(Exception e)
		{
			logger.warning("Exception raised: " + e.getMessage() );
			e.printStackTrace();
		}
		logger.exiting("DataPool", "serializeData");
	}

	
	/** deserializes clip meta data
	 * @return
	 */
	protected boolean deserializeData()
	{
		logger.entering("DataPool", "deserializeData");
		logger.info("deserializing meta data");
		
		boolean success = false;
		File dataFile = null;
		
		// try original file
		try
		{
			dataFile = new File( DATA_GZ_FILENAME );

			if( FileUtils.isWritable( dataFile, 100000 ) )
			{
				ObjectInputStream ois = new ObjectInputStream( new GZIPInputStream( new FileInputStream( dataFile ), 65535));
				data = (Hashtable)ois.readObject();
				ois.close();
				ois = null;

				success = true;
			}
		}
		catch(Exception e)
		{
			logger.warning("Exception raised: " + e.getMessage() );
			e.printStackTrace();
		}
		
		// try backup file
		if( !success )
		{
			logger.warning("Deserializing data from backup file");
			try
			{
				dataFile = new File( DATA_GZ_BACKUP_FILENAME );

				if( FileUtils.isWritable( dataFile, 100000 ) )
				{
					ObjectInputStream ois = new ObjectInputStream( new GZIPInputStream( new FileInputStream( dataFile ), 65535));
					data = (Hashtable)ois.readObject();
					ois.close();
					ois = null;

					success = true;
				}
			}
			catch(Exception e)
			{
				logger.warning("Exception raised: " + e.getMessage() );
				e.printStackTrace();
			}
		}
		
		// migration to gzip forces us to read old (unzipped files too)
		if( !success )
		{
			logger.warning("Deserializing data from old unzipped file");
			try
			{
				dataFile = new File( DATA_FILENAME );

				if( FileUtils.isWritable( dataFile, 100000 ) )
				{
					ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream( new FileInputStream( dataFile ), 65535));
					data = (Hashtable)ois.readObject();
					ois.close();
					ois = null;

					success = true;
				}
			}
			catch(Exception e)
			{
				logger.warning("Exception raised: " + e.getMessage() );
				e.printStackTrace();
			}
		}
		
		// validate deserialized data hashtable
		validateData();
		
		logger.exiting("DataPool", "deserializeData");		

		return success;
	} // deserialize data
	
	
	/** Sets data hashtable
	 *
	 * @param data to set
	 */
	protected void setData(Hashtable data)
	{
		this.data = data;
	}
	
	
	/** Updates meta data and filename cache if file name or path changed.
	 *
	 * @param oid of the changed clip
	 * @param path of the changed clip
	 */	
	protected boolean updateFilePath(Long oid, String newPath)
	{
		boolean success = false;
		
		try
		{
			MP3Meta meta = this.getMeta(oid);
			String oldPath = meta.getFilePath();
			
			meta.setFilePath(newPath);
			meta.setFilename( newPath.substring(newPath.lastIndexOf(File.separator)+1) );
			filenameCache.put( newPath, oid );
			filenameCache.remove( oldPath );

			success = true;
		}
		catch( Exception e )
		{
			logger.warning("Exception raised : " + e.getMessage() );
			e.printStackTrace();
		}
		
		return success;
	}
	
	
	/** Validates the data object and removes invalid entries
	 *	Checks if data is null or not,
	 *	if key is null and
	 *	if value is null 
	 */
	private void validateData()
	{
		logger.entering("DataPool", "validateData");
		
		long validCnt = 0;
		long invalidCnt = 0;
		
		if( data != null )
		{
			// check that there is no key ( oid ) without meta data object...
			Iterator oidIter = data.keySet().iterator();
			while( oidIter.hasNext() )
			{
				Long key = (Long)oidIter.next();
				if( key == null )
				{
					logger.warning("Key (oid) checked was null, removing entry");
					data.remove(key);
					invalidCnt++;
				}
				else if( data.get(key) == null )
				{
					logger.warning("value (meta data) for key (oid) " + key + " was null, removing entry");
					data.remove(key);
					invalidCnt++;
				}
				else
				{
					validCnt++;
				}
			}
		}
		else
		{
			logger.warning("data object is null");
		}
		
		if( invalidCnt > 0 )
		{
			logger.warning("checking the mypod meta data resulted in " + validCnt + " valid, but " + invalidCnt + " invalid entries");
		}
		
		logger.exiting("DataPool", "validateData");
	}


	/** Gets the titles of all clips.
	 *
	 * @param clips to get titles from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the titles
	 * @return a vector containing all titles
	 */	
	public Vector getAllTitleValues(Vector clips, boolean sort)
	{		
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( clips.size() ) : new Vector( this.getAllOid().size() );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getTitle();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all titles

	
	/** Gets the titles of all clips.
	 *
	 * @param clips to get titles from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the titles
	 * @return a vector containing all titles
	 */	
	public Vector getAllArtistValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( clips.size() ) : new Vector( this.getAllOid().size() );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;
		
		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			try
			{
				value = this.getMeta( (Long)clipIter.next() ).getArtist();

				// TODO: DEBUG
				if( value != null && values != null && !values.contains(value) )
				{
					values.addElement(value);
				}
			}
			catch( Exception e)
			{
				logger.warning("Exception raised: " + e.getMessage() );
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList( values );
		} // sorting
		
		return values;
	}

	
	/** Gets the albums of all clips.
	 *
	 * @param clips to get artists from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the artists
	 * @return a vector containing all artists
	 */	
	public Vector getAllAlbumValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( clips.size() ) : new Vector( this.getAllOid().size() );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			try
			{
				value = this.getMeta( (Long)clipIter.next() ).getAlbum();

				if( value != null && values != null && !values.contains(value) )
				{
					values.addElement(value);
				}
			}
			catch( Exception e )
			{
				logger.warning("Exception raised: " + e.getMessage() );
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all artists
	
	
	/** Gets the bands of all clips.
	 *
	 * @param clips to get bands from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the bands
	 * @return a vector containing all bands
	 */	
	public Vector getAllBandValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getBand();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all bands
	
	
	/** Gets the bpm values of all clips.
	 *
	 * @param clips to get bands from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the bpm values
	 * @return a vector containing all bpm values
	 */	
	public Vector getAllBpmValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getBpm();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all bpms

	
	/** Gets the cd identifier values of all clips.
	 *
	 * @param clips to get bands from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the cd identifier values
	 * @return a vector containing all cd identifier values
	 */	
	public Vector getAllCdIdentifierValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getCdIdentifier();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all cd identifier

	
	/** Gets the comments of all clips.
	 *
	 * @param clips to get comments from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the comments
	 * @return a vector containing all comments
	 */	
	public Vector getAllCommentValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getComment();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all comments
	
	
	/** Gets the composers of all clips.
	 *
	 * @param clips to get composers from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the composers
	 * @return a vector containing all composers
	 */	
	public Vector getAllComposerValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getComposer();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all composers

	
	/** Gets the content group set values of all clips.
	 *
	 * @param clips to get content group set values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the content group set values
	 * @return a vector containing all content group set values
	 */	
	public Vector getAllContentGroupSetValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getContentGroupset();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all content group sets

	
	/** Gets the copyright text values of all clips.
	 *
	 * @param clips to get copyright text values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the copyright text values
	 * @return a vector containing all copyright text values
	 */	
	public Vector getAllCopyrightTextValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getCopyrightText();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all copyright texts

	
	/** Gets the copyright webpage values of all clips.
	 *
	 * @param clips to get copyright webpage values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the copyright webpage values
	 * @return a vector containing all copyright webpage values
	 */	
	public Vector getAllCopyrightWebpageValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getCopyrightWebpage();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all copyright webpages

	
	/** Gets the encoded by values of all clips.
	 *
	 * @param clips to get encoded by values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the encoded by values
	 * @return a vector containing all encoded by values
	 */	
	public Vector getAllEncodedByValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getEncodedBy();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all encoded by values

	
	/** Gets the genres of all clips.
	 *
	 * @param clips to get genres from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the genres
	 * @return a vector containing all genres
	 */	
	public Vector getAllGenreValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			try
			{
				value = this.getMeta( (Long)clipIter.next() ).getGenre();

				if( value != null && values != null && !values.contains(value) )
				{
					values.addElement(value);
				}
			}
			catch( Exception e )
			{
				logger.warning( "Exception raised: " + e.getMessage() );
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all genres
	
	
	/** Gets the isrc values of all clips.
	 *
	 * @param clips to get encoded by values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the encoded by values
	 * @return a vector containing all encoded by values
	 */	
	public Vector getAllIsrcValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getIsrc();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all isrc values

	
	/** Gets the isrc values of all clips.
	 *
	 * @param clips to get encoded by values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the encoded by values
	 * @return a vector containing all encoded by values
	 */	
	public Vector getAllLanguageValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getLanguage();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all language values

	
	/** Gets the lyricist of all clips.
	 *
	 * @param clips to get lyricists from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the lyricists
	 * @return a vector containing all lyricists
	 */	
	public Vector getAllLyricistValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getLyricist();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all lyricists

		
	/** Gets the original artist values of all clips.
	 *
	 * @param clips to get original artist values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the original artist values
	 * @return a vector containing all original artist values
	 */	
	public Vector getAllOriginalArtistValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getOriginalArtist();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all original artist values

	
	/** Gets the playcounter of all clips.
	 *
	 * @param clips to get playcounter from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the playcounter values
	 * @return a vector containing all playcounter values
	 */	
	public Vector getAllPlaycounterValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getPlayCounter();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all playcounters
	
	
	/** Gets the original artist values of all clips.
	 *
	 * @param clips to get original artist values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the original artist values
	 * @return a vector containing all original artist values
	 */	
	public Vector getAllOriginalArtists( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getOriginalArtist();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all original artist values

	
	/** Gets the original lyricist values of all clips.
	 *
	 * @param clips to get original lyricist values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the original lyricist values
	 * @return a vector containing all original lyricist values
	 */	
	public Vector getAllOriginalLyricistValues( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getOriginalLyricist();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all original lyricist values
	
	
	/** Gets the original title values of all clips.
	 *
	 * @param clips to get original title values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the original title values
	 * @return a vector containing all original title values
	 */	
	public Vector getAllOriginalTitleValues( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getOriginalTitle();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all original title values
	
	
	/** Gets the original year values of all clips.
	 *
	 * @param clips to get original year values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the original year values
	 * @return a vector containing all original year values
	 */	
	public Vector getAllOriginalYearValues( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getOriginalYear();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			try
			{
				Collections.sort(values);
			}
			catch(Exception e)
			{
				logger.warning("exception raised: " + e.getMessage());
				e.printStackTrace();
			}
		} // sorting
		
		return values;
	} // get all original year values
	
	
	/** Gets the publisher values of all clips.
	 *
	 * @param clips to get publisher values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the publisher values
	 * @return a vector containing all publisher values
	 */	
	public Vector getAllPublisherValues( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getPublisher();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all publisher values
	
	
	/** Gets the publisher webpage values of all clips.
	 *
	 * @param clips to get publisher webpage values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the publisher webpage values
	 * @return a vector containing all publisher webpage values
	 */	
	public Vector getAllPublisherWebpageValues( Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		String value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = this.getMeta( (Long)clipIter.next() ).getPublishersWebpage();

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			sortList(values);
		} // sorting
		
		return values;
	} // get all publisher webpage values
	
	
	/** Gets the track values of all clips.
	 *
	 * @param clips to get track values from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the track values
	 * @return a vector containing all track values
	 */	
	public Vector getAllTrackValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<50 ? clips.size()+1 : 50) ) : new Vector(50);
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		Integer value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = new Integer( this.getMeta( (Long)clipIter.next() ).getTrack() );

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			try
			{
				Collections.sort(values);
			}
			catch(Exception e)
			{
				logger.warning("exception raised: " + e.getMessage());
				e.printStackTrace();
			}
		} // sorting
		
		return values;
	} // get all tracks

	
	/** Gets the years of all clips.
	 *
	 * @param clips to get years from. If clips is null, all clips in the data pool are evaluated.
	 * @param sort the years
	 * @return a vector containing all years
	 */	
	public Vector getAllYearValues(Vector clips, boolean sort)
	{
		// if we didn't get clips, iterate over all
		Vector values = ( clips != null ) ? new Vector( (clips.size()<201 ? clips.size()+1 : 201) ) 
																			: new Vector( (this.getAllOid().size()<201 ? this.getAllOid().size()+1 : 201) );
		Iterator clipIter = ( clips != null ) ? clips.iterator() : this.getAllOid().iterator();
		Integer value;

		// get the titles of all clips and add them to the vector
		while( clipIter.hasNext() )
		{
			value = new Integer(this.getMeta( (Long)clipIter.next() ).getYear());

			if( value != null && values != null && !values.contains(value) )
			{
				values.addElement(value);
			}
		}
		
		// sort, if we are forced to
		if( sort )
		{
			try
			{
				Collections.sort(values);
			}
			catch(Exception e)
			{
				logger.warning("exception raised: " + e.getMessage());
				e.printStackTrace();
			}
		} // sorting
		
		return values;
	} // get all years

	
	/** Gets all artists known by the data pool.
	 * The content ist sortet.
	 *
	 * @return sorted vector containing all artits
	 */	
	public DefaultListModel getAllArtistValues()
	{
		Vector tmp = getAllArtistValues( null, true );
		
		// copy sorted list to the model
		DefaultListModel model = new DefaultListModel();
		for( Iterator clipIter = tmp.iterator(); clipIter.hasNext(); )
		{
			model.addElement(clipIter.next());
		}
		
		return model;
	
	} // get all artists

	
	/** Gets all genres known by the data pool.
	 * The content ist sortet.
	 *
	 * @return sorted vector containing all genres
	 */	
	public DefaultListModel getAllGenreValues()
	{
		Vector tmp =  getAllGenreValues( null, true );
		
		// copy sorted list to the model
		DefaultListModel model = new DefaultListModel();
		for( Iterator clipIter = tmp.iterator(); clipIter.hasNext(); )
		{
			model.addElement(clipIter.next());
		}
		
		return model;
	
	} // get all genres

	
	/** Gets all years known by the data pool.
	 * The content ist sortet.
	 *
	 * @return sorted vector containing all years
	 */	
	public DefaultListModel getAllYearValues()
	{
		Vector tmp = getAllYearValues( null, true );
		
		// copy sorted list to the model
		DefaultListModel model = new DefaultListModel();
		for( Iterator clipIter = tmp.iterator(); clipIter.hasNext(); )
		{
			model.addElement(clipIter.next());
		}
		
		return model;
	
	} // get all years
	
	
	/** Sorts a list naturally
	 */
	private List sortList( List aCollection )
	{
		// sometimes there is a null pointer exception while sorting
		try
		{
			Collections.sort(aCollection, Collator.getInstance() );
		}
		catch( Exception e)
		{
			logger.warning("exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		return aCollection;
	}
	
} // data pool
