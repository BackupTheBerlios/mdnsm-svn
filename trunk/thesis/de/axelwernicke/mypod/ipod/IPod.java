// IPod
// $Id: IPod.java,v 1.4 2004/08/18 21:29:11 woooo Exp $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import de.axelwernicke.mypod.DataPool;
import de.axelwernicke.mypod.MP3Meta;
import de.axelwernicke.mypod.Playlist;
import de.axelwernicke.mypod.gui.GuiUtils;
import de.axelwernicke.mypod.gui.IPodSyncDialog;
import de.axelwernicke.mypod.myPod;
import de.axelwernicke.mypod.util.FileUtils;


/**
 *  Class representing an iPod.
 *
 * @author     wooo
 * @created    July 16, 2004
 */
public class IPod {

	/**  platform specific file separator */
	private static final String fileSeparator          = System.getProperty("file.separator");

	/**  string to probe ipod availability */
	private static final String IPOD_CONTROL_DIR       = "iPod_Control";

	/**  Relative Path to the music directory on the iPod */
	private static final String IPOD_DEVICE_DIR        = IPOD_CONTROL_DIR + fileSeparator + "Device";

	/**  Relative Path to the music directory on the iPod */
	private static final String IPOD_MUSIC_DIR         = IPOD_CONTROL_DIR + fileSeparator + "Music";

	/**  Relative Path to the iTunes directory on the iPod */
	private static final String IPOD_ITUNES_DIR        = IPOD_CONTROL_DIR + fileSeparator + "iTunes";

	/**  names of directories containing music on the iPod */
	private static final String IPOD_CLIP_DIR[]        = {"F00", "F01", "F02", "F03", "F04", "F05", "F06", "F07",
			"F08", "F09", "F10", "F11", "F12", "F13", "F14", "F15",
			"F16", "F17", "F18", "F19"};

	/**  relative path to the SysInfo file on the iPod */
	private static final String IPOD_SYS_INFO_PATH     = IPOD_DEVICE_DIR + fileSeparator + "SysInfo";

	/**  relative path to the DeviceInfo file on the iPod */
	private static final String IPOD_DEVICE_INFO_PATH  = IPOD_CONTROL_DIR + fileSeparator + "DeviceInfo";
	/**  jdk1.4 logger */
	private static Logger logger                       = Logger.getLogger("de.axelwernicke.mypod.ipod");

	/**  iTunes Database containing song meta data and playlists * */
	private ITunesDB iTunesDB;


	/**  Standard Constructor */
	public IPod() { }


	/**
	 *  Probes iPod. To ensure the iPod is connected, we check the basic file and
	 *  directory structure.
	 *
	 * @return    true, if iPod was found.
	 */
	public static boolean isConnected() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "isConnected");

		boolean result     = false;
		String iPodDevice  = null;

		try {
			iPodDevice = myPod.getBackend().getPreferences().getIPodPath();

			// check if at least a iPod_control directory exists
			if (iPodDevice != null
			// axel.wernicke@gmx.de fixed missing delimiter...
					&& new File(iPodDevice + fileSeparator + IPOD_CONTROL_DIR).exists()
					&& new File(iPodDevice + fileSeparator + IPOD_CONTROL_DIR).canWrite()) {
				// check if filesystem is completely initialized
				result = validateFilestructure();
				;
			}
		} catch (Exception e) {
			logger.warning("Exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		if (result != true) {
			logger.info("probing iPod failed at: " + iPodDevice);
		}
		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "isConnected");

		return result;
	}


	/**
	 *  Checks if the directory structure on the iPod is correct. Missing
	 *  directories are created.
	 *
	 * @return    true, if the filestructure was valid or could be created
	 *      successfully
	 */
	private static boolean validateFilestructure() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "validateFilestructure");

		File file;
		boolean success  = true;

		try {
			// get path to iPod base directory
			String iPodBase  = myPod.getBackend().getPreferences().getIPodPath();

			// check for iTunes directory
			file = new File(iPodBase + fileSeparator + IPOD_ITUNES_DIR);
			if (!file.exists()) {
				success = success && file.mkdirs();
				logger.info("Created directory " + file + " : " + success);
			}

			// check if we find the music directory
			for (int i = 0; i < IPOD_CLIP_DIR.length; i++) {
				file = new File(iPodBase + fileSeparator + IPOD_MUSIC_DIR + fileSeparator + IPOD_CLIP_DIR[i]);
				if (!file.exists()) {
					success = success && file.mkdirs();
					logger.info("Created directory " + file + " : " + success);
				}
			}  // for all music directories
		} catch (Exception e) {
			logger.warning("Exception raised: " + e.getMessage());
			e.printStackTrace();
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "validateFilestructure");

		return success;
	}


	/**
	 *  loads the iTunes database from iPod.
	 *
	 * @return    true, if loading succeeded
	 */
	private boolean loadITunesDB() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "loadITunesDB");

		boolean result         = false;
		ITunesDBParser parser  = new ITunesDBParser();
		ITunesDB db            = parser.load();

		if (db != null) {
			setITunesDB(db);

			if (logger.isLoggable(Level.FINE)) {
				boolean dbValid  = this.iTunesDB.isValid();
				if (dbValid) {
					logger.fine("Database valid after loading.");
				} else {
					logger.warning("Database not valid after loading.");
				}
			}

			result = true;
		}

		parser = null;

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "loadITunesDB");

		return result;
	}


	/**
	 *  saves the iTunes database on iPod.
	 *
	 * @param  db  Database to save.
	 * @return     true, if saving succeeded
	 */
	public boolean saveITunesDB(ITunesDB db) {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "saveITunesDB");

		boolean result  = false;

		setITunesDB(db);
		result = saveITunesDB();

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "saveITunesDB");

		return result;
	}


	/**
	 *  Saves iTunes database on iPod.
	 *
	 * @return    true, if saving succeeded
	 */
	public boolean saveITunesDB() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "saveITunesDB");

		boolean result  = false;

		if (iTunesDB != null) {
			logger.fine("Database valid before saving: " + iTunesDB.isValid());

			ITunesDBParser parser  = new ITunesDBParser();
			result = parser.save(iTunesDB, null);
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "saveITunesDB");

		return result;
	}


	/**
	 *  Gets the summarized filesize for all clips. It tries to determine clip
	 *  size iTunes database.
	 *
	 * @param  oids     vector of oids to summarize filesizes
	 * @param  iMapper  hashtable that allows to find songs on the iPod by myPod
	 *      oid
	 * @return          filesize of all specified oids
	 */
	public long getClipsSize(Vector oids, Hashtable iMapper) {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getClipsSize");

		long size      = 0;

		Iterator iter  = oids.iterator();
		while (iter.hasNext()) {
			size += getITunesDB().getFilesize(((Integer) iMapper.get((Long) iter.next())).intValue());
		}

		logger.fine("summarized size of " + oids.size() + " clips was " + GuiUtils.formatFilesize(size));

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getClipsSize");

		return size;
	}


	/**
	 *  Removes Clips from iPod. delete file on the iPod, remove song from iPods
	 *  database remove song from iMapper
	 *
	 * @param  oids     to remove from iPod
	 * @param  iMapper  to correct
	 * @param  dialog   to inform user about progress
	 * @return          true, if all clips were removed
	 */
	public boolean removeClips(Vector oids, Hashtable iMapper, IPodSyncDialog dialog) {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "removeClips");

		boolean result       = true;
		boolean fileRemoved  = false;
		int fileIndex        = -1;
		Long oid             = null;
		String iPodPath      = myPod.getBackend().getPreferences().getIPodPath();
		String filename      = null;
		String baseMessage   = GuiUtils.getStringLocalized("resource/language", "removingClips");
		int curr             = 1;
		int total            = oids.size();

		// do it for all oids to remove
		Iterator iter        = oids.iterator();

		while (iter.hasNext()) {
			try {
				// update dialog
				dialog.statusContentLabel.setText(new StringBuffer(baseMessage).append(" ( ").append(curr++)
						.append(" / ").append(total).append(" )").toString());

				// get oid, file index and file name of the clip to remove
				oid = (Long) iter.next();
				fileIndex = ((Integer) iMapper.get(oid)).intValue();
				filename = getITunesDB().getFilename(fileIndex);

				// prepare filename ( mac -> java )
				filename = filename.replace(':', File.separatorChar);
				filename = iPodPath + filename.substring(1);

				// remove from ipod - search for the file
				File file  = new File(filename);
				fileRemoved = file.delete();

				// if file could not be removed, set it to 0 bytes...
				if (!fileRemoved) {
					logger.warning("removing " + filename + " workaround activated.");
					FileOutputStream fos  = new FileOutputStream(filename);
					fos.write(0x00);
					fos.close();
				}

				// remove from db
				getITunesDB().removeClip(fileIndex);

				// remove from mapper
				iMapper.remove(oid);
			} catch (Exception e) {
				logger.warning("exception raised :" + e.getMessage());
				e.printStackTrace();
				result = false;
			}

		}  // for all

		logger.fine("db valid after removing clips: " + getITunesDB().isValid());

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "removeClips");

		return result;
	}


	/**  Removes all Clips from iPod.  */
	public void removeAllClips() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "removeAllClips");

		boolean deleted  = false;
		File music       = new File(myPod.getBackend().getPreferences().getIPodPath() + fileSeparator + IPod.IPOD_MUSIC_DIR);
		File subs[]        = music.listFiles((java.io.FileFilter) null);
		File sub;
		File files[];
		logger.fine("music dir is : " + music.toString());

		// check if anything found, activate workaround if directories are hidden
		int subsLength   = subs.length;
		if (subs.length > 0) {
			logger.fine("found " + subsLength + " dirs below music");

			for (int i = 0; i < subsLength; i++) {
				sub = subs[i];

				// TODO check this - seems to be shortcut...
				if (true || sub.isDirectory()) {
					logger.fine("found sub dir is : " + sub.toString());
					files = sub.listFiles();
					int fileCount  = files.length;
					for (int j = 0; j < fileCount; j++) {
						deleted = files[j].delete();
						if (!deleted) {
							logger.warning("file " + files[j].toString() + " deleted : " + deleted);
						}
					}
				}
			}
		} else {
			// sometimes e.g. hfs+ iPod with MacOpener hidden dirs are not found by java - so
			// do it brute force
			logger.info("did'nt find any dirs below music, workaround activated");

			for (int i = 0; i < IPOD_CLIP_DIR.length; i++) {
				sub = new File(music.getAbsolutePath() + fileSeparator + IPOD_CLIP_DIR[i]);

				if (sub.isDirectory()) {
					logger.info("found sub dir is : " + sub.toString());
					files = sub.listFiles();
					for (int j = 0; j < files.length; j++) {
						deleted = ((File) files[j]).delete();
						logger.finer("file " + ((File) files[j]).toString() + " deleted : " + deleted);
					}
				}
			}
		}
		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "removeAllClips");
	}  // removeAllClipsFromIPod


	/**
	 *  Moves Clips to the iPod. - move file to the iPod, - add song to iPods
	 *  database - add song to iMapper
	 *
	 * @param  dataPool  to determine clip meta data
	 * @param  oids      of the clips to move
	 * @param  iMapper   to correct
	 * @param  dialog    to show progress
	 * @return           true, if all clips were moved
	 */
	public boolean addClips(DataPool dataPool, Vector oids, Hashtable iMapper, IPodSyncDialog dialog) {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "addClips");

		boolean result          = true;

		float totalProgress     = 15.0f;
		float totalProgressInc  = (75.0f / oids.size());
		long totalBytes         = myPod.getBackend().getClipsTotalSize(oids);
		int fileCnt             = 0;
		//int fileProgress = 0;
		int fileIndex           = 2001;  // first fileindex on iPod

		String filePath         = myPod.getBackend().getPreferences().getIPodPath() + fileSeparator + IPod.IPOD_MUSIC_DIR + "/F00";
		String copySpeed        = "";

		Long oid                = null;
		MP3Meta meta            = null;
		File iPodFile           = null;

		long startTime          = -1;
		long bytesCopied        = 0;
		long totalBytesCopied   = 0;
		long timeUsed           = 0;
		long timeLeft           = 0;

		// if we have an iPod wiped with ephpod, there are some records missing, so lets create them...
		if (getITunesDB().getSonglistHolder() == null) {
			getITunesDB().initSonglistHolder();
		}
		if (getITunesDB().getSonglistHeader() == null) {
			getITunesDB().initSonglistHeader();
		}

		// copy all songs
		Iterator iter           = oids.iterator();
		while (iter.hasNext()) {
			try {
				startTime = System.currentTimeMillis();

				// oid of the song to move
				oid = (Long) iter.next();
				fileCnt++;
				meta = dataPool.getMeta(oid);

				// do some gui stuff
				dialog.statusContentLabel.setText(GuiUtils.getStringLocalized("resource/language", "copyingFiles")
						+ " ( " + fileCnt + " / " + oids.size() + " ) ");
				dialog.fileNameContentLabel.setText(meta.getArtist() + " - " + meta.getTitle());
				dialog.fileSizeContentLabel.setText(GuiUtils.formatFilesize(meta.getFilesize()));

				// create new file
				fileIndex = getITunesDB().getNextAvailableSongIndex(fileIndex);
				iPodFile = new File(filePath + File.separator + fileIndex + ".mp3");
				logger.fine("created new file on iPod : " + iPodFile);

				// do copying
				bytesCopied = FileUtils.copy(new File(meta.getFilePath()), iPodFile);

				// check if copy succeeded
				if (bytesCopied > 0) {
					// add song to db
					getITunesDB().addClip(fileIndex, dataPool.getMeta(oid));

					// add to iMapper
					iMapper.put(oid, new Integer(fileIndex));
					logger.finer("put into iMapper: " + oid + " : " + fileIndex);
				}

				// performance logging
				timeUsed += (System.currentTimeMillis() - startTime);
				timeUsed = (timeUsed < 1000) ? 1000 : timeUsed;  // avoid division by zero
				totalBytesCopied += bytesCopied;
				timeLeft = (timeUsed * totalBytes / totalBytesCopied) - (timeUsed);
				copySpeed = GuiUtils.formatTransferSpeedMB(totalBytesCopied / (timeUsed / 1000), 2);

				// update gui
				dialog.copySpeedContentLabel.setText(copySpeed);
				dialog.copiedSizeContentLabel.setText(GuiUtils.formatFilesize(totalBytesCopied));
				dialog.timeUsedContentLabel.setText(GuiUtils.formatTime(timeUsed / 1000));
				dialog.timeLeftContentLabel.setText(GuiUtils.formatTime(timeLeft / 1000));
			} catch (Exception e) {
				logger.warning("exception raised :" + e.getMessage());
				e.printStackTrace();
				result = false;
			}

			// update total progress bar
			totalProgress += totalProgressInc;
			dialog.totalProgressBar.setValue((int) totalProgress);

			// check if user cancelled operation
			if (Thread.interrupted()) {  // FIXME seems not to work ...

				return result;
			}
		}  // for all clips to move

		// check database validity
		logger.fine("db valid after moving: " + getITunesDB().isValid());

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "addClips");

		return result;
	}  // add clips to ipod


	/**  Removes empty playlists on iPod.  */
	public void removeEmptyPlaylists() {
		// delegate to db
		getITunesDB().removeEmptyPlaylists();
	}


	/**
	 *  Creates playlists on iPod. All myPod playlists are checked for
	 *  synchronization. If a playlist is synchronized with iPod, a playlist is
	 *  created on iPod, containing all the songs of the myPod playlist, as far
	 *  as they are transfered correctly to the iPod.
	 *
	 * @param  playlistList  list of playlists to create
	 * @param  iMapper       mapper object
	 * @param  dialog        to inform user about progress
	 */
	public void createPlaylists(javax.swing.DefaultListModel playlistList, Hashtable iMapper, IPodSyncDialog dialog) {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "createPlaylists");

		Playlist playlist  = null;
		String name        = null;
		Vector fileIdc     = null;
		Long oid           = null;
		int playlistCount  = playlistList.getSize();
		int songCount      = 0;

		// for all playlists
		for (int i = 0; i < playlistCount; i++) {
			playlist = (Playlist) playlistList.elementAt(i);
			name = playlist.getName();
			fileIdc = new Vector();
			oid = null;

			// we are interested in playlists to sync only
			if (playlist.isIPodSync()) {
				// update dialog
				dialog.statusContentLabel.setText(new StringBuffer()
						.append(GuiUtils.getStringLocalized("resource/language", "creatingPlaylists"))
						.append(" ( ").append(name).append(" )").toString());

				logger.info("synchronizing playlist " + name + " with iPod");

				// collect songs for the playlist
				songCount = playlist.getTotalClips();
				for (int y = 0; y < songCount; y++) {
					oid = playlist.getClipAt(y);

					if (iMapper.containsKey(oid)) {
						fileIdc.addElement(iMapper.get(oid));
					} else {
						logger.warning("didn't find oid in iMapper : " + oid.longValue());
					}
				}  // for all clips

				// create a new iPod playlist
				getITunesDB().createPlaylist(name, fileIdc);

			}  // if playlist is ipod sync
		}  // for all playlists

		// check iTunes db
		logger.fine("db valid after creating playlist: " + getITunesDB().isValid());

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "createPlaylists");

	}  // create playlists on iPod


	/**
	 *  Setter for property name.
	 *
	 * @param  name  New value of property name.
	 */
	public void setName(java.lang.String name) {
		// TODO implement set iPod name
	}


	/**
	 *  Gets the name of the iPod. iPods name is stored in a file calles
	 *  DeviceInfo. Its located in the iTunes directory.
	 *
	 * @return    name of the iPod
	 */
	public String getName() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getName");

		String name  = "iPod";  // standard name, just for the case the iPod isn't customized...

		try {
			// check if deviceInfo file exists
			File devInfo  = new File(myPod.getBackend().getPreferences().getIPodPath() + fileSeparator + IPOD_DEVICE_INFO_PATH);
			if (devInfo.exists()) {
				// open device info on iPod
				FileInputStream fis  = new FileInputStream(devInfo);

				//first bytes contain length of name in characters
				byte len[]             = new byte[2];
				fis.read(len);
				int nameLength       = java.nio.ByteBuffer.wrap(len).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();

				// read name from device info
				byte nameBa[]          = new byte[nameLength * 2];
				fis.read(nameBa);

				// extract name
				name = ITunesDBParser.uTF16LittleEndianToString(nameBa).trim();

				// clean up
				fis.close();
			}
		} catch (Exception e) {
			logger.warning("exception raised: " + e.getMessage());
		}

		logger.finer("got " + name + " as iPod name");
		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getName");

		return name;
	}  // get iPod name


	/**
	 *  Wipes all music from the iPod. <BR>
	 *  - create a new empty database from given name <BR>
	 *  - remove all clips from iPod <BR>
	 *  - save new database <BR>
	 *  - serialize iPodMapper
	 */
	public void wipe() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "wipe");

		// create new empty iTunes database
		setITunesDB(new ITunesDB(getName()));

		// check db vor validity
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("new db valid: " + getITunesDB().isValid());
		}

		// remove all music files from the iPod
		removeAllClips();

		// save iTunes DB
		saveITunesDB();

		// create new iMapper
		myPod.getBackend().serializeIPodMapper(new Hashtable());

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "wipe");
	}


	/**
	 *  Getter for property itunesDB.
	 *
	 * @return    Value of property itunesDB.
	 */
	public de.axelwernicke.mypod.ipod.ITunesDB getITunesDB() {
		if (iTunesDB == null) {
			loadITunesDB();
		}

		return iTunesDB;
	}


	/**
	 *  Setter for property itunesDB.
	 *
	 * @param  iTunesDB  to set
	 */
	public void setITunesDB(ITunesDB iTunesDB) {
		this.iTunesDB = iTunesDB;
	}


	/**
	 *  Getter for property version.
	 *
	 * @return    Value of property version.
	 */
	public String getVersion() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getVersion");

		String version  = GuiUtils.getStringLocalized("resource/language", "unknown");

		// check if iPod is connected
		if (isConnected()) {
			try {
				boolean found          = false;

				// get tokenizer for sysInfo file
				File sysInfoFile       = new File(myPod.getBackend().getPreferences().getIPodPath() + fileSeparator + IPOD_SYS_INFO_PATH);
				BufferedReader reader  = new BufferedReader(new FileReader(sysInfoFile));
				String line            = reader.readLine();

				// version is stored in a line like: 'buildID: 0x01308000 (1.3)'
				while (!found && line != null) {
					if (line.startsWith("buildID:")) {
						found = true;

						// prepare and parse the version string
						StringTokenizer tokenizer  = new StringTokenizer(line);
						tokenizer.nextToken();  // throw away
						String hex                 = tokenizer.nextToken().trim();  // get hex value
						String dez                 = tokenizer.nextToken().trim();  // get dez value
						dez = dez.substring(1, dez.length() - 1);  // remove parenthesis
						version = dez + " (" + hex + ")";
					}
					line = reader.readLine();
				}

			} catch (Exception e) {
				logger.warning("Exception raised: " + e.getMessage());
				e.printStackTrace();
			}
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getVersion");

		return version;
	}


	/**
	 *  Getter for property serial number.
	 *
	 * @return    Value of property serial number.
	 */
	public String getSerialNumber() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getSerialNumber");

		String serialNumber  = "";

		// check if iPod is connected
		if (isConnected()) {
			try {
				boolean found          = false;

				// get tokenizer for sysInfo file
				File sysInfoFile       = new File(myPod.getBackend().getPreferences().getIPodPath() + fileSeparator + IPOD_SYS_INFO_PATH);
				BufferedReader reader  = new BufferedReader(new FileReader(sysInfoFile));
				String line            = reader.readLine();

				// serial number is stored in a line like: 'pszSerialNumber: U2149DNMLG6'
				while (!found && line != null) {
					if (line.startsWith("pszSerialNumber:")) {
						found = true;

						// prepare and parse the version string
						StringTokenizer tokenizer  = new StringTokenizer(line);
						tokenizer.nextToken();  // throw away
						serialNumber = tokenizer.nextToken().trim();  // get value
					}
					line = reader.readLine();
				}

			} catch (Exception e) {
				logger.warning("Exception raised: " + e.getMessage());
				e.printStackTrace();
			}
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getVersion");

		return serialNumber;
	}


	/**
	 *  Getter for property discSpace.
	 *
	 * @return    Value of property discSpace.
	 */
	public long getDiscSpace() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getDiscSpace");

		long spaceTotal  = -1;

		try {
			spaceTotal = this.getDiscSpaceUsed() + this.getDiscSpaceFree();
		} catch (Exception ex) {
			logger.warning("Exception raised: " + ex.getMessage());
			ex.printStackTrace();
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getDiscSpace");
		return spaceTotal;
	}


	/**
	 *  Getter for property discSpaceFree.
	 *
	 * @return    Value of property discSpaceFree.
	 */
	public long getDiscSpaceFree() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getDiscSpaceFree");
		long free  = -1;

		try {
			free = FileUtils.getSpaceLeft(myPod.getBackend().getPreferences().getIPodPath());
		} catch (Exception ex) {
			logger.warning("Exception raised: " + ex.getMessage());
			ex.printStackTrace();
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getDiscSpaceFree");

		return free;
	}


	/**
	 *  Getter for property discSpaceUsed.
	 *
	 * @return    Value of property discSpaceUsed.
	 */
	public long getDiscSpaceUsed() {
		logger.entering("de.axelwernicke.mypod.ipod.IPod", "getDiscSpaceUsed");
		long used  = -1;

		try {
			used = FileUtils.getTotalFilesize(new File(myPod.getBackend().getPreferences().getIPodPath()),
					Pattern.compile(".+", Pattern.CASE_INSENSITIVE));
		} catch (Exception ex) {
			logger.warning("Exception raised: " + ex.getMessage());
			ex.printStackTrace();
		}

		logger.exiting("de.axelwernicke.mypod.ipod.IPod", "getDiscSpaceUsed");

		return used;
	}
}
