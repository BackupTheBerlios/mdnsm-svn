/*
 Created on May 7, 2003
 To change the template for this generated file go to
 Window>Preferences>Java>Code Generation>Code and Comments
 Copyright 2003 Joseph Barnett
 This File is part of "one 2 oh my god"
 "one 2 oh my god" is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 Free Software Foundation; either version 2 of the License or
 your option) any later version.
 "one 2 oh my god" is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with "one 2 oh my god"; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.git.client.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import javazoom.jlgui.basicplayer.BasicPlayer;

import org.git.GITProperties;
import org.git.GITPropertiesPanel;
import org.git.GITUtils;
import org.git.StringPairList;
import org.git.client.Host;
import org.git.client.Playlist;
import org.git.client.Song;
import org.git.client.StatusListener;
import org.git.client.daap.DaapHost;
import org.git.client.daap.GetNewHost;
import org.git.client.local.FileListener;
import org.git.client.local.GITLibraryHost;
import org.git.client.local.IPodHost;
import org.git.client.local.LocalHost;
import org.git.client.local.LocalSong;
import org.git.client.rss.PodcastHost;
import org.git.downloader.DownloadManager;
import org.git.downloader.SongDownload;
import org.git.player.AbstractPlayer;
import org.git.player.PlayerAdapter;
import org.git.player.PlayerException;
import org.git.player.PlayerUtils;
import org.git.player.QTPlayer;
import org.git.server.RendezvousManager;
import org.git.server.Server;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.io.CachingList;
import ca.odell.glazedlists.CompositeList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

//import java.awt.event.*;

/**
 * @author jbarnett To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 * @created
 */
public class GetItTogether implements ItemListener,
		MouseWheelListener, ServiceListener, FileListener {

	public static final String SEARCHTEXT = "Search: ";
    public static final String FRAME_TITLE = "Get It Together";
    public static final int RANDOM_OFF = 0;
    public static final int RANDOM_BROWSER = 1;
    public static final int RANDOM_PLAYLIST = 2;
    public static final String[] shuffleStrings = { "Off", "Global", "Playlist" };
    public static final String[] shuffleToolTips = { "Off: Songs play sequentially", "Global: Shuffles songs in the main browser",
            "Playlist: Shuffles songs in the queue."};
    public static final int DISPLAY_TITLE = 0;
    public static final int DISPLAY_ARTIST = 1;
    public static final int DISPLAY_ALBUM = 2;
    
    public static final String DOWNLOAD_COUNTER = "Downloaded Songs: ";
    
    protected static final String CARD_LOCAL = "Local Music";
    protected static final String CARD_SHARED = "Shared Music";
    protected static final String CARD_PLAYLISTS = "Playlists";
    protected static final String CARD_SETTINGS = "Settings";
    protected static final String CARD_DOWNLOADS = "Downloads";
    protected static final String CARD_SERVER = "Server";
    protected static final String CARD_QUEUE = "Queue";
    protected static final String CARD_PODCASTS = "Podcasts";
    
    protected static String iTunesService = "_daap._tcp.local.";
    
    public static String SPLASH_PATH = "/images/splash.jpg";
    
    protected static final int SONG_UNAVAILABLE_DELAY = 300;
    
    public static final Color PLAYING_SELECTED = new Color(205, 216, 225);
    public static final Color PLAYING_UNSELECTED = new Color(210, 232, 210);
    public static final Color PAUSED_SELECTED = new Color(220, 200, 220);
    public static final Color PAUSED_UNSELECTED = new Color(240, 200, 200);
    
    public static final Color GIT_BLUE = new Color(120, 120, 160);
    
    public static final String ACTION_DESC = AbstractAction.SHORT_DESCRIPTION;
    
    public JFrame frame;
    protected JPanel hostsPane;
    protected JButton button;
    protected JmDNS jmdns;
    public static AbstractPlayer player;
    protected boolean pause = false;
    public DownloadTableModel dls;
    protected JTable pltable, dltable;
    protected JSplitPane splitPane;
    protected JPanel rightPane, songPanel, bottomLine, bottomPanel,
    buttonPane, searchPanel, volumePanel, dspanel, buttons;
    
    protected DisplayTableModel dspmodel;
    protected JTable displayTable, displayTable2;
    protected Song playingSong;
    public DownloadManager gopher;
    public AbstractAction toggleHelp, pausePlay, switchTabs,
    enqueueSongs, clearPlay, download, scrollHostUp, scrollHostDown,
    locatePlayingSong, revSwitchTabs, browseHost,
    chooseDirectory, acceptSearch, openSearch, cancelSearch, playNext,
    playPrevious, hostButton, removee, randomPlay, changeShuffle,
    toggleMini, dremove, dremoveAll, plPlayNext, plPlayPrevious,
    hideOthers, updateTables, dremoveFinished, dremoveError, 
    dretryError, dremoveDuplicates, dremoveCancelled, dretryCancelled,
    dremoveFailed, dretryFailed;
    protected ActionEvent blankAction;
    protected Component browserTab, playlistTab;
    protected HashSet newKeys, revNewKeys;
    protected boolean hide_help, song_opening, seeking, showElapsed = false,openingWindow;
    protected String lastSearch;
    protected int pausePosition;
    protected int pauseBytes = 0;
    protected int resumeOffset = 0;
    protected int millisPosition = 0;
    protected BrowserRenderer browserRender;
    protected JSlider displayProgress;
    protected JLabel progressLabel, playButton, pauseButton, shuffleLabel, ml,miniLabel;
    public boolean miniPlay;
    protected Box.Filler filler;
    protected JProgressBar volbar;
    protected JPanel fpanel, center, gpanel;
    protected int displayValue = 0;
    protected Component box;
    protected GITNode root;
    protected GITTreeModel tree_model;
    protected JTree tree;
    protected java.util.Timer timer;
    public GITNode hosts;
    protected StatusListener status_listener, plistener;
    protected GITNode playlists;
    protected GITNode settings;
    protected JTabbedPane views;
    protected JPanel sview, dview;
    protected JPanel main;
    protected GITNode local;
//    protected HostNode podcasts;
    private CardLayout cl;
    private JPanel qview;
    private JPanel music; 
    protected SongDownload downloader;
    private AbstractAction connectAllHosts;
    private AbstractAction connectAllLocal;
    private AbstractAction connectAll;
    private AbstractAction disconnectAll;
    private AbstractAction addNewHost;
    protected ArrayList playedSongs;
    protected int currentSongIndex;
    protected int thisShuffle, lastShuffle;
    protected Timer dlTimer;
    protected boolean played_through = false;
    public JLabel countLabel;
    public QueuePlaylist queue;
    public GlazedGITSongJPanel localJPanel;
    public GlazedGITSongJPanel daapJPanel;
    public GlazedGITSongJPanel playlistJPanel;
    private FocusAdapter focus;
    private JLabel helpLabel;
    protected TimerTask tableTimerTask;
    public static IPodHost iPodHost;
    protected Server server;
    protected RendezvousManager rendezvous;
    private AbstractAction reconnectHost;
    private AbstractAction removeLibrary;
    private AbstractAction changeLibrary;
    private AbstractAction setFocusToTable;
    public static GetItTogether instance;
    private AbstractAction disconnectHost;
    private AbstractAction connectHost;
    private boolean loading_song;
    private AbstractAction ddeleteRetry;
    private JTable qtable;
    private AbstractAction plRemoveSongs;
    private AbstractAction plPlay;
    protected Host playingHost;
    protected ImageIcon waiting, arrows;
    protected ImageIcon up = new ImageIcon(GetItTogether.class.getResource("/images/up.png"));
	protected ImageIcon down = new ImageIcon(GetItTogether.class.getResource("/images/down.png"));
    protected HashMap fileFormatIcons;
	public Thread shutdownHook;
    protected BasicEventList playlistSongs;
    public CompositeList localSongs = new CompositeList();
    public FilterList localSongsFilter;
    protected CompositeList daapSongs;
    public FilterList daapSongsFilter;
        
    public PlayerAdapter playerListener = new PlayerAdapter() {
            
            public void songFinished(ActionEvent e) {
                System.out.println("SongFinished: event received");
                queue.playNext();
            }
            
            public void songLoaded(ActionEvent e) {
                System.err.println("SongLoaded: event received");
//                updateTables.actionPerformed(blankAction);	
            }
            
            public void statusUpdated(Object o, int status) {
                System.err.println("Status update: "+status);
                switch(status) {
                case AbstractPlayer.PLAYING:
                    loading_song = false;
                	startProgressTimer();
                	playingHost = playingSong.getHost();
                	tree.repaint();
                	switchButtons(pauseButton);
                break;
                case AbstractPlayer.PAUSED:
                    stopProgressTimer();
                	switchButtons(playButton);
                break;
                case AbstractPlayer.STOPPED:
                    stopProgressTimer();
                	loading_song = false;
                	playingHost = null;
                	tree.repaint();
                break;
                case AbstractPlayer.ERROR:
                    loading_song = false;
                	stopProgressTimer();
                break;
                }
                //            updateTables.actionPerformed(null);
            }
        };
    private AbstractAction removeSongs;
    private Runnable dummyServer;
    private AbstractAction songInfoAction;
    private AbstractAction songExploreAction;
    private JLabel windowClosingLabel;
    protected ImageIcon magnifier = new ImageIcon(GetItTogether.class.getResource("/images/magnifier.png"));
    private AbstractAction addSongstoGITLibrary;
    private CachingList cacheSongs;
    private AbstractAction addPodcast;
    public SupportedSongMatcherEditor localSongsMatcher;
    public SupportedSongMatcherEditor daapSongsMatcher;
    
	private class ExitHook implements Runnable {

		public void run() {
		    GetItTogether.instance.stopPlaying();
		    
		    GITProperties.writeXML();
		    Server.instance().stop();
		    jmdns.close();
			System.out.println("exit hook complete!");
		}
	}

	public GetItTogether(final SplashWindow splash) {
        instance = this;
	    openingWindow = true;
        
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIDefaults uiDefaults = UIManager.getDefaults();
            uiDefaults.put("Label.font", uiDefaults.get("TextPane.font"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory
                .createEmptyBorder(1, 1, 1, 1));
        
        focus = new FocusAdapter() {
        
            public void focusGained(FocusEvent e) {
                updateHelpPanel((JComponent)e.getComponent());
            }
        };
        
        shutdownHook = new Thread() {
            public void run() {
                GetItTogether.instance.stopPlaying();
                GITProperties.writeXML();
                Server.instance().stop();
                jmdns.close();
                if (DownloadManager.dlThread != null && !gopher.isQueueFinished())
                    DownloadManager.dlThread.waitForDownloadToFinish();
                System.out.println("Exit hook complete!");
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        frame = (JFrame)splash.getParent();
        frame.setTitle(FRAME_TITLE);
        
        new GITProperties();
        
        System.out.println("Show Splash: "+GITProperties.showSplash);
        
        if (GITProperties.showSplash) {
        JCheckBox cb = new JCheckBox("Don't hang around next time");
        cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setSize(cb.getPreferredSize());
            cb.setLocation(0, splash.getHeight() - cb.getHeight() + cb.getInsets().bottom / 2);
        splash.getLayeredPane().add(cb, new Integer(200));
        cb.addItemListener(this);
        }
                
        URL imageURL = GetItTogether.class
                .getResource("/images/puzzle_super_mini.png");
        frame.setIconImage(frame.getToolkit().getImage(imageURL));
        
        //		define all the actions to be used.
        createActions();
        blankAction = new ActionEvent(new Integer(0), ActionEvent.ACTION_PERFORMED, "");       
        
        //		create the player object.
        PlayerUtils.loadNewPlayer(GITProperties.playerType);
        Logger.getLogger(BasicPlayer.class.getName()).setLevel(Level.OFF);
        
        try {
        player.setVolume(GITProperties.playerVolume / 100.0);
        } catch (PlayerException e) {}

        //		create the downloader object.
        gopher = new DownloadManager(this);
        
        // create the played songs stack.
        // TODO: Make a separate Class to handle the player queue / logic stuff
        playedSongs = new ArrayList();
        thisShuffle = GITProperties.shuffleValue;
        
        // create the main split pane
        createSplitPane();
        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));
        main.add(splitPane);
        main.setBorder(BorderFactory.createRaisedBevelBorder());
        
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(main);
        
        createDisplay();
        
        //        dspanel.setInputMap(ancestor, tabs.getInputMap(ancestor));
        //        dspanel.setInputMap(focused, tabs.getInputMap(ancestor));
        //        dspanel.setActionMap(tabs.getActionMap());
        
        // help panel:
        createHelpPanel();
        
        frame.getContentPane().add(dspanel);
        frame.getContentPane().add(bottomPanel);
        
        tree.addFocusListener(focus);
        
        lastSearch = "";
        //dirField.setText(dldir);
        miniPlay = false;
        if (GITProperties.shuffleValue < RANDOM_OFF)
            GITProperties.shuffleValue = RANDOM_OFF;
        GITProperties.shuffleValue--;
        changeShuffle.actionPerformed(blankAction);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!gopher.isQueueFinished()) {
                    System.err.println("UNFINISHED DOWNLOAD QUEUE!!!");
                    // ask if we really want to quit
                    String quitter = "You still have queued downloads remaining.  Are you sure you want to quit?";
                    String[] options = {"Quit","Cancel"};
                    JOptionPane pane = new JOptionPane(
                            quitter,
                            JOptionPane.WARNING_MESSAGE,
                            JOptionPane.YES_NO_OPTION,
                            null,
                            options,
                            options[0]
                    );
                    JDialog dialog = pane.createDialog(frame,"Unfinished Downloads");
                    dialog.show();
                    dialog.dispose();
                    Object result = pane.getValue();
                    int num=-1;
                    for (int i=0; i < options.length; i++) {
                        if (result.equals(options[i]))
                            num = i;
                    }
                    switch (num) {
                    case JOptionPane.YES_OPTION:
                        System.exit(0);
                    break;
                    case JOptionPane.NO_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                    default:
                        break;
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        frame.getContentPane().addMouseWheelListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                if (miniPlay) {
                    GITProperties.miniPos = frame.getLocation();
                } else {
                    GITProperties.bigPos = frame.getLocation();
                }
            }

            public void componentResized(ComponentEvent e) {
                if (openingWindow) {
                    return;
                }
                //System.out.println("Resize source: "+e.getSource());
                int min_width = dspanel.getMinimumSize().width;
                int min_height = dspanel.getMinimumSize().height;
                //JFrame f = (JFrame)e.getComponent();
                Insets g = frame.getInsets();
                Component c = frame.getContentPane();
                //System.out.println(c.getSize());
                int hei = c.getHeight();
                int wid = c.getWidth();
                if (wid < min_width || hei < min_height) {
                    int nwid = (wid < min_width ? min_width : wid);
                    int nhei = (hei < min_height ? min_height : hei);
                    frame.setSize(nwid + g.left + g.right, nhei + g.top
                            + g.bottom);
                    if (hei < min_height) {
                        frame.setLocation(frame.getX(), frame.getY()
                                - (min_height - hei));
                    }
                }
                if (miniPlay) {
                    if (hei > min_height) {
                        frame.setSize(frame.getSize().width, min_height + g.top
                                + g.bottom);
                    }
                    GITProperties.miniPos = frame.getLocation();
                    GITProperties.miniWidth = frame.getWidth();
                } else {
                    GITProperties.bigPos = frame.getLocation();
                    if (hide_help) {
                        GITProperties.bigSize = frame.getSize();
                    }
                    fixDisplay();
                }
            }
        });
        
        bottomPanel.setVisible(false);
        hide_help = true;
        if (GITProperties.bigSize.height != 9999) {
            frame.setSize(GITProperties.bigSize);
        } else {
            frame.setSize(850, 650);
        }
        if (GITProperties.bigPos.x != 9999) {
            frame.setLocation(GITProperties.bigPos);
        } else {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension img = frame.getSize();
            frame.setBounds(d.width / 2 - (img.width / 2), d.height / 2
                    - (img.height / 2), img.width, img.height);
        }
        GITProperties.bigPos = frame.getLocation();
        GITProperties.bigSize = frame.getSize();

        // create the Playlists' status listener.
        plistener = new StatusListener() {
            public void stateUpdated(Object h) {
                Playlist p = (Playlist)h;
//                System.out.println("playlist "+p.name + " updated");
                if (p.getStatus() == Playlist.STATUS_INITIALIZED && noHostsOrPlaylistsConnecting()) {
                    killWaitingIcon();
                }
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tree.revalidate();
                            tree.repaint();
                        }
                    });
                } catch (Exception e) {e.printStackTrace();}
            }
        };
        
//        create the Hosts' status listener.
        status_listener = new StatusListener() {
            public void stateUpdated(Object h) {
                final GITNode node = root.getChildByObject(h);
                if (node == null)
                    return;
                final Host host = (Host)h;

                // do everything that needs to be run on the Event Dispatcher thread:
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tree_model.nodeChanged(node);
                            updateButton(host);
                            tree.layout();
                            tree.repaint();
                        }
                    });
                } catch (Exception e) {e.printStackTrace();}
                
                if (noHostsOrPlaylistsConnecting()) {
                    killWaitingIcon();
                }
                
                // load the playlists if necessary
                if (node.getChildCount() == 0 && host.getStatus() == Host.STATUS_CONNECTED)
                {
                    Iterator i = host.getPlaylists().iterator();
					while (i.hasNext()) {
						Playlist p = (Playlist)i.next();
					    PlaylistNode pnode = new PlaylistNode(p);
						p.addStatusListener(plistener);
						node.add(pnode);
					}
                }
            }
        };
        
        
//        create the tree framework.
        
        local = new GITNode("Local Music", GITNode.LOCAL_ROOT);
        root.add(local);
        
        hosts = new GITNode("Shared Music", GITNode.DAAP_ROOT);
        root.add(hosts);
        
//        podcasts = new HostNode(new PodcastHost("Podcasts"));
//        podcasts.setUserObject(new PodcastHost("Podcasts (beta)"));
//        root.add(podcasts);
        
        playlists = new GITNode("Playlist / Queue", GITNode.PLAYLIST_ROOT);
        root.add(playlists);
        
        GITNode downloads = new GITNode("Downloads", GITNode.DOWNLOADER);
        root.add(downloads);
        
        settings = new GITNode("Settings", GITNode.SETTINGS);
        root.add(settings);
        
        
        tree.expandPath(new TreePath(root.getPath()));
        if (GITProperties.expandLocal)
            tree.expandPath(new TreePath(local.getPath()));
        
        if (!GITProperties.showSplash) {
            splash.dispose();
        }
        
//        make things go!
        frame.setVisible(true);
        setFocusToTable();
        openingWindow = false;
        
        server = Server.instance();
        
        // load and auto-connect the local hosts:
        for (int i = 0; i < GITProperties.savedLocalHosts.size(); i++) {
            final LocalHost host = (LocalHost)GITProperties.savedLocalHosts.get(i);
            host.setSongs(localSongs.createMemberList());
            localSongs.addMemberList(host.getSongs());
            if (host instanceof IPodHost)
            {
                IPodHost iPodHost = (IPodHost)host;
                iPodHost.fm.addListener(this);
            }
            host.addStatusListener(status_listener);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    System.out.println("adding local host: " + host.getName());
                    HostNode node = new HostNode(host);
                    local.insertAlphabetically(node);
                    if (local.getChildCount() == 1 && GITProperties.expandLocal)
                        tree.expandPath(new TreePath(local.getPath()));
//                    tree.repaint();
                }
            });
            new SwingWorker() {
                public Object construct() {
//                    while (server == null || !server.isServerRunning()){try {Thread.sleep(100);} catch (Exception e){}}
                    try {Thread.sleep(400);}catch (Exception e){}
                    File f = new File(host.getRoot());
                    if (host.isAutoConnect())
                    {
                        if (host instanceof IPodHost) {
                            IPodHost ipod = (IPodHost)host;
                            if (!ipod.validateIpodDirectory(f))
                                return new Integer(0);
                        }
	                    //if root location doesn't exist, or iPod doesn't validate, don't connect:
	                    if (f == null || !f.exists()) 
	                        return new Integer(0);
	                    else
	                    {
	                        host.connect();
						    if (host.isVisible())
						        host.setVisible(true);
						    addSongsGlazed(host);
						    host.loadPlaylists();
		                    }
	                    }
                    return new Integer(0);
                }
            }.start();
        }
        
        try {
            InetAddress addr = GITUtils.getLocalInetAddress();
            if (addr == null)
                jmdns = new JmDNS();
            else
                jmdns = new JmDNS(addr);
            jmdns.addServiceListener(iTunesService, this);
            RendezvousManager.instance().setJmDNS(jmdns);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		new GetItTogether(null);
	}

	public void updateQueuedDownloadsNumber(int num) {
	    if (num == 0)
	        views.setTitleAt(2,"Downloads");
	    else
	        views.setTitleAt(2,"Downloads - "+num);
	}
	
	public void stopPlaying() {
		try {
			player.stop();
			if (playingSong == null)
			    return;
			Host h = playingSong.getHost();
			if (h instanceof DaapHost)
			    ((DaapHost)h).logout();
		} catch (PlayerException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		    try {
				player.pause();
			} catch (PlayerException e) {
				e.printStackTrace();
				return;
			}
			updateTables.actionPerformed(null);
			pause = true;
	}

	protected void seekPlay(Song s, double fraction) {
	    System.out.println("seekplay!");
	    if (player.getStatus() == AbstractPlayer.STOPPED) {
	        System.err.println("Not seeking: player appears to be stopped.");
	        return;
	    }
	    try {
	        if (player instanceof QTPlayer) {
		        ((QTPlayer)player).skipTo(fraction);
		        resumeOffset = 0;
		        startProgressTimer();
		        return;
	        } else if (s instanceof LocalSong) {
		        int bytes = (int) (fraction * s.getSize());
		        player.skipTo(bytes);
		        startProgressTimer();
	        } else {
		        // only a remote song played with JavaPlayer needs to request a new stream
		        int bytes = (int) (fraction * s.getSize());
		        InputStream is = ((DaapHost)s.getHost()).getSongStream(s, bytes);
		        player.stop();
		        player.play(is, "");
		        startProgressTimer();
//		        resumeOffset = bytes;
		    }
	    } catch (PlayerException pc) {
	        pc.printStackTrace();
	    } finally {
	        startProgressTimer();
	    }
    }

	protected void switchButtons(JLabel l) {
		if (buttons.getComponent(2).equals(l)) {
			return;
		} else {
			buttons.remove(2);
			buttons.add(l, 2);
			l.repaint();
		}
		buttons.validate();
	}

	protected void playSong(final Song s) {
	    if (true)
	    {
	        System.out.println(player.getClass().getName());
			resumeOffset = 0;
		    final Host h = s.getHost();
		    updateTables.actionPerformed(blankAction);
				if (!player.supportsSong(s))
				{
				    s.status = Song.STATUS_ERROR;
				    
				    // update this song in the table view, to add the "!"
				    getVisibleCard().table.repaint();
//				    EventTableModel model = (EventTableModel)getVisibleCard().table.getModel();
//	                EventList list = getVisibleEventList();
//	                int ind = list.indexOf(s);
//	                if (ind != -1)
//	                    model.fireTableRowsUpdated(ind, ind);
	                
				    
				    //TODO: Find a way to "nicely" try to play the next song...
				    return;
			    }
				loading_song = true;
//				System.out.println("making swingworker");
				    new SwingWorker() {
				        public Object construct() {
				            playingSong = s;
				            try {
				                player.stop();
				                loading_song = true;
				                player.play(s);
				                System.out.println("loading!");
				                startProgressTimer();
				                s.status = Song.STATUS_OK;
				                if (GITProperties.locatePlayingSong)
				                    locatePlayingSong(false);
				            } catch (PlayerException pe) {
				                if (pe.getCause() instanceof FileNotFoundException) {
				                    System.err.println("File not found!");
				                } else
				                    pe.printStackTrace();
				                s.status = Song.STATUS_ERROR;
				                stopProgressTimer();
				            } catch (Exception e) {
				                e.printStackTrace();
				                switchButtons(playButton);
				            } finally {
				                loading_song = false;
				                updateTables.actionPerformed(null);
				                // just in case we ever need something here.
				            }
				            return new Integer(0);
				        }
				    }.start();
	    }
	}

	protected synchronized void startProgressTimer() {
	    if (timer == null)
	        timer = new java.util.Timer();
	    if (tableTimerTask != null)
	        tableTimerTask.cancel();
	    tableTimerTask = new TableTimer();
        timer.scheduleAtFixedRate(tableTimerTask, 0, 1000);
	}
		
	protected void stopProgressTimer() {
	    if (timer == null)
	        return;
	    if (tableTimerTask != null)
	        tableTimerTask.cancel();
	}
	
	protected void startDownloads() {
	    // FIXME  There is a huge gui lag since this timer is on the same queue as gui updates and it takes "a lot" of time updating/downloading...
	    // need to have it's own thread for this, perhaps the one that calls this at the beginning?
//	    new Timer(10, new AbstractAction() {
//	        public void actionPerformed(ActionEvent e) {
	    while(dls.num < dls.getRowCount()) {
//	            JTable t = dltable;
//	            int[] rows = t.getSelectedRows();
//	            dls.fireTableDataChanged();
//	            if (rows.length >0) {
//	            t.setRowSelectionInterval(rows[0],rows[rows.length-1]);
//	            }
//	            if (dls.num < dls.getRowCount())
//	                downloader.downloadSong(dls.getSongAt(dls.num));
	                System.out.println("update!");
	                dls.fireTableRowsUpdated(dls.num, dls.num);
	                dls.num++;

	    }
	    System.out.println("last update!");
	    dls.fireTableRowsUpdated(dls.num-1, dls.num-1);
//	        }
//	    }).start();
	    
	}
	
	public void locatePlayingSong(boolean center) {
		//TODO: make the current table scroll to the playing song, if it's found.
			EventList model = getVisibleEventList();
			JTable table = getVisibleCard().table;
			if (model.size() == 0 || model.indexOf(playingSong) == -1) {
				return;
			}
			int row = model.indexOf(playingSong);
			if (!(table.getParent() instanceof JViewport)) {
			    System.out.println("no viewport to scroll");
				return;
			}
			JViewport viewport = (JViewport) table.getParent();
			Rectangle rect = table.getCellRect(row, 0, true);
			Rectangle viewRect = viewport.getViewRect();
			// don't re-center if not requested
			if (viewRect.contains(rect) && !center)
			    return;
			    
			rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);
			int centerX = (viewRect.width - rect.width) / 2;
			int centerY = (viewRect.height - rect.height) / 2;
			if (rect.x < centerX) {
				centerX = -centerX;
			}
			if (rect.y < centerY) {
				centerY = -centerY;
			}
			rect.translate(centerX, centerY);
			viewport.scrollRectToVisible(rect);
			viewport.repaint();
			
//			table.clearSelection();
//			table.addRowSelectionInterval(model.indexOf(playingSong), model
//					.indexOf(playingSong));
//			frame.validate();
	}

	public void searchText(String text) {
	    getVisibleCard().search_field.setText(text);
	}

	public void resolveService(JmDNS jmdns, String type, String name,
            ServiceInfo info) {
        if (info == null) {
            System.out.println("Unable to find service info!");
            return;
        } else {
            if (info.getAddress().equals(GITUtils.getLocalInetAddress().getHostAddress()) && info.getPort() == GITProperties.sharePort)
            {
                System.out.println("NOT ignoring GIT's own share");
//                return;
            }
        }
        DaapHost host;
        if (GITProperties.getDaapHost(info.getName()) != null) {
            host = GITProperties.getDaapHost(info.getName());
            host.loadServiceInfo(info);
        } else {
            host = new DaapHost(info);
            host.loadServiceInfo(info);
            GITProperties.addDaapHost(host);
        }
//        if (!h.getName().equals("Andrew Berman"))
//            return;
        final DaapHost h = host;
        
//        GITProperties.addDaapHost(h);
        
        h.addStatusListener(status_listener);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HostNode node = new HostNode(h);
                hosts.insert(node, hosts.getIndexForInsertion(node));
                if (hosts.getChildCount() == 1 && GITProperties.expandRemote)
                    tree.expandPath(new TreePath(hosts.getPath()));
//                tree.repaint();
            }
        });
        host.setSongs(daapSongs.createMemberList());
        daapSongs.getReadWriteLock().writeLock().lock();
        daapSongs.addMemberList(host.getSongs());
        daapSongs.getReadWriteLock().writeLock().unlock();
        // auto connect the DaapHost if necessary
        new SwingWorker() {
			public Object construct() {
			    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			    if (!GITProperties.autoConnect)
			        return new Integer(0);
			    try {Thread.sleep(200);}catch (Exception e) {}
			    if (GITProperties.getSavedDaapHostInfo(h.getName(), "auto_connect").equals("true"))
			    {
			        h.setAutoConnect(true);
			        if (h.login(false))
				    {
				        h.grabSongs();
				        addSongsGlazed(h);
				        h.loadPlaylists();
				        if (GITProperties.getSavedDaapHostInfo(h.getName(), "visible").equals("true"))
				            hostClicked(hosts.getChildByName(h.getName()));
				    }
			        return new Integer(0);
			    } else {
		        h.setAutoConnect(false);
		        return new Integer(0);
			    }
			}
		}.start();
    }

	public void addService(JmDNS jmdns, String type, String name) {
		if (type.equals(iTunesService))
			jmdns.requestServiceInfo(iTunesService, name);
	}

	public void removeService(JmDNS jmdns, String type, String name) {
		if (name.equals(GITUtils.getQualifiedServiceName(GITProperties.shareName)))
		{
		    System.out.println("caught ya");
		    return;
		}
	    
	    ServiceInfo si = jmdns.getServiceInfo(type, name);
	    if (si == null || si.getName() == GITProperties.shareName)
	        return;
		System.out.println(name);
		DaapHost h = new DaapHost(si);
		String hname = h.getName();
		System.out.println("losing host: "+hname);
		if (hname.endsWith("_PW"))
            hname = hname.substring(0, hname.length() - 3);
		final GITNode node = hosts.getChildByName(hname);
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        hosts.remove(node);
//		        tree.repaint();
		        DaapHost actual_host = (DaapHost)node.getUserObject();
		        removeSongsGlazed(actual_host);
		        actual_host.remove();
		    }
		});
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();

		int volume = volbar.getValue();

		volume = volume + (-3 * notches);
		if (volume > 100) {
			volume = 100;
		}
		if (volume < 0) {
			volume = 0;
		}
		volbar.setValue(volume);
		GITProperties.playerVolume = volume;
		try {
			player.setVolume(volume / 100.0);
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	public void disconnectHostNode(final HostNode node) {
	    new SwingWorker() {
	        public Object construct() {
	            Host host = (Host)node.getUserObject();
	            if (playingSong != null && playingSong.host == host)
	            {
	                stopPlaying();
	                if (host instanceof DaapHost)
	                    ((DaapHost)host).logout();
	            }
	            removeSongsGlazed(host);
	            host.disconnect();
	            node.removeAllChildren();
	            return new String();
	        }
	    }.start();
	}
	
	public void connectHostNode(HostNode node) {
	    Host cur = node.getHost();
	    cur.setVisible(true);
	    cur.connect();
		if (cur.getStatus() >= Host.STATUS_CONNECTED)
		{
		    addSongsGlazed(cur);
			cur.loadPlaylists();
//			getVisibleTable().repaint();
		}
	}
	
	public JPanel getViewForNode(GITNode node) {
	        int t = node.getType();
	        switch (t) {
	        	case GITNode.DAAP_ROOT:
	        	    System.out.println("Found DAAP ROOT");
	        	    return daapJPanel;
	        	case GITNode.LOCAL_ROOT:
	        	    return localJPanel;
	        	case GITNode.LOCAL:
	        	    return localJPanel;
	        	case GITNode.PLAYLIST_ROOT:
	        	    return qview;
	        	case GITNode.PLAYLIST:
	        	    System.out.println("Found PLAYLIST");
	        	    return playlistJPanel;
	        	case GITNode.DOWNLOADER:
	        	    return dview;
	        	case GITNode.SETTINGS:
	        	    return sview;
	        	case GITNode.DAAP:
	        	    return daapJPanel;
	        	default:
	        	    return null;
	        }
	    }
	
	protected void disconnectSelectedHost() {
	    GITNode node = getSelectedNode();
	    if (node instanceof HostNode)
	        disconnectHostNode((HostNode)node);
	}

	protected void repaintFrame() {
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            frame.repaint();
	        }
	    });
	}
	
    public void fileChanged(File f) {
    // listens for connect or disconnect of the iPod host (so far, only one ipod host allowed):
        if (f.exists() && iPodHost != null && iPodHost.getStatus() == Host.STATUS_NOT_CONNECTED)
        {
            hostClicked(local.getChildByObject(iPodHost));
        }
        else if (!f.exists() && iPodHost.getStatus() >= Host.STATUS_CONNECTED)
        {
            removeSongsGlazed(iPodHost);
            iPodHost.disconnect();
        }
    }
	
	protected void playlistClicked(final Playlist plist) {
	    new SwingWorker() {
	        public Object construct() {
	            playlistSongs.getReadWriteLock().writeLock().lock();
	            
	            playlistSongs.clear();
	            playlistSongs.addAll(plist.getSongs());
	            playlistSongs.getReadWriteLock().writeLock().unlock();
	            return new Integer(0);
	        }
	    }.start();
	}
	
	protected void nodeClicked(final GITNode node) {
	    int type = node.getType();
	    switch(type) {
	        case GITNode.LOCAL_ROOT:
	            views.setSelectedComponent(music);
	        	cl.show(music, CARD_LOCAL);
	        break;
	        case GITNode.PLAYLIST_ROOT:
	            views.setSelectedComponent(qview);
	        break;
	        case GITNode.DAAP_ROOT:
	            views.setSelectedComponent(music);
	        	cl.show(music, CARD_SHARED);
	        break;
	        case GITNode.PLAYLIST:
            	views.setSelectedComponent(music);
		        Playlist p = (Playlist)node.getUserObject();
		        cl.show(music, CARD_PLAYLISTS);
		        playlistClicked(p);
	        break;
	        case GITNode.DAAP:
	            views.setSelectedComponent(music);
		        cl.show(music, CARD_SHARED);
		        hostClicked(node);
	        break;
	        case GITNode.LOCAL:
	            views.setSelectedComponent(music);
		        cl.show(music, CARD_LOCAL);
		        hostClicked(node);
	        break;
	        case GITNode.PODCAST_ROOT:
	            views.setSelectedComponent(music);
	        	PodcastHost pd = (PodcastHost)node.getUserObject();
	        	cl.show(music, CARD_PLAYLISTS);
	        	playlistSongs.addAll(pd.getSongs());
//	        	playlistClicked(p);
	        break;
	        case GITNode.DOWNLOADER:
	            views.setSelectedComponent(dview);
	        break;
	        case GITNode.SETTINGS:
	            views.setSelectedComponent(sview);
	        break;
	        default:
	            break;
	    }
	}
	
	public void addNewHost() {
	
	GetNewHost gnh = new GetNewHost(frame, true);
	gnh.show();
	
	if (gnh.getStatus() == GetNewHost.STATUS_PRESSED_CANCEL) {
	    return;
	}
	
	InetAddress ip = gnh.getHostAddress();
	int port = gnh.getPort();
	String name = gnh.getHostName();
	
	gnh.dispose();
	
	final DaapHost nhost = new DaapHost(name, "", ip, port);
	SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              HostNode node = new HostNode(nhost);
              hosts.insert(node, hosts.getIndexForInsertion(node));
              if (hosts.getChildCount() == 1 && GITProperties.expandRemote)
                  tree.expandPath(new TreePath(hosts.getPath()));
              tree.repaint();
          }
      });
	}
	
	 public void hostClicked(final GITNode node) {   
	     new SwingWorker() {
			public Object construct() {
			    Host cur = (Host)node.getUserObject();
			    GlazedGITSongJPanel sp = getVisibleCard();
				if (cur == null /* || cur.getStatus() == Host.STATUS_NOT_AVAILABLE*/) {
					return new Integer(0);
				} else if (cur.getStatus() >= Host.STATUS_CONNECTED) {
					cur.setVisible(!cur.isVisible());
					if (cur.isVisible())
					{
					    sp.vis_matcher.hostShown();
					}
					else
					{
					    sp.vis_matcher.hostHidden();
					}
				} else if (cur.getStatus() == Host.STATUS_NOT_CONNECTED || cur.getStatus() == Host.STATUS_NOT_AVAILABLE) {
				    	connectHostNode((HostNode)node);
//						System.gc();System.gc();System.gc();System.gc();System.gc();
				}
				return new Integer(0);
			}

		}.start();
	}

	public GITNode getSelectedNode() {
	    return (GITNode)tree.getLastSelectedPathComponent();
	}
	
	public GlazedGITSongJPanel getVisibleCard() {
	    if (localJPanel.isVisible())
	        return localJPanel;
	    else if (daapJPanel.isVisible())
	        return daapJPanel;
	    else if (playlistJPanel.isVisible())
	        return playlistJPanel;
	    else
	        return daapJPanel;
	}
	
	public JTable getVisibleTable() {
	    switch (views.getSelectedIndex()) {
	    case 0:
	        return getVisibleCard().table;
	    case 1:
	        return qtable;
	    default:
	        return getVisibleCard().table;
	    }
	}
	
	public JComponent getFocusableComponentForTab() {
	    switch (views.getSelectedIndex())
	    {
	    	case 0:
	    	    return getVisibleCard().table;
	    	case 1:
	    	    return qtable;
    	    case 2:
    	        return dltable;
	        case 3:
	            return sview;
	        default:
	            return getVisibleCard().table;
	    }
	}
	
	public EventList getVisibleEventList() {
	    switch (views.getSelectedIndex()) {
	    case 0:
	        return getVisibleCard().visible;
	    case 1:
	        return QueuePlaylist.queue;
	    default:
	        return getVisibleCard().visible;
	    }
	}
	
	public EventList getCurrentlySelectedSongs() {
	    JTable table = getVisibleTable();
	    EventSelectionModel model = (EventSelectionModel)table.getSelectionModel();
	    EventList sel = model.getEventList();
	    return sel;
	}
	
	public Host getSelectedHost() {
        GITNode node = getSelectedNode();
        if (node == null)
            return null;
        if (node.getUserObject() instanceof Host)
            return (Host)node.getUserObject();
        else
            return null;
	}
	
	public void addSongsGlazed(final Host h) {
	    if (h instanceof LocalHost && server != null)
	        server.addSongs(h.getSongs());
	}
	
	protected void removeSongsGlazed(Host h) {
	    if (h instanceof LocalHost)
	        server.removeSongs(h.getSongs());
	}

	public void deleteSongs(EventList songs) {
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	    
	    HashSet hosts = new HashSet();
	    
	    Iterator i = songs.iterator();
	    while (i.hasNext()) {
	        Song s = (Song)i.next();
	        s.status = Song.STATUS_NOT_FOUND;
	        hosts.add(s.getHost());
	    }
	    getVisibleCard().table.repaint();
	    
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	    
	    i = hosts.iterator();
	    while (i.hasNext()) {
	        Host h = (Host)i.next();
	        System.out.println(h);
	        EventList deleteFrom = h.getSongs();
	        deleteFrom.getReadWriteLock().writeLock().lock();
	        deleteFrom.removeAll(songs);
	        deleteFrom.getReadWriteLock().writeLock().unlock();
	    }
	    
	    EventList viewList = localSongs;
	    viewList.getReadWriteLock().writeLock().lock();
	    viewList.removeAll(songs);
	    viewList.getReadWriteLock().writeLock().unlock();
	    
	}
	
	// ditto for this method; just a demo!
	public void addSongs(EventList songs) {
	    try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	    
	    Iterator i = songs.iterator();
	    while (i.hasNext()) {
	        Song s = (Song)i.next();
	        s.status = Song.STATUS_NOT_FOUND;
	    }
	    
//	    Host h = s.getHost();
//	    EventList addTo = h.getSongs();
//	    addTo.getReadWriteLock().writeLock().lock();
//	    addTo.addAll(songs);
//	    addTo.getReadWriteLock().writeLock().unlock();
	    
	    EventList viewList = localSongs;
	    viewList.getReadWriteLock().writeLock().lock();
	    viewList.addAll(songs);
	    viewList.getReadWriteLock().writeLock().unlock();
	    
	    try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	    
	    i = songs.iterator();
	    while (i.hasNext()) {
	        Song s = (Song)i.next();
	        s.status = Song.STATUS_OK;
	    }
	    getVisibleCard().table.repaint();
	}
	
	public void addSong(Song s) {
	    EventList viewList = localSongs;
	    viewList.add(s);
	}
	
	public void deleteSong(Song s) {
	    EventList viewList = localSongs;
	    viewList.remove(s);
	}
	
	public EventList getSongListForHost(Host h) {
	    if (h instanceof LocalHost)
	        return localSongs;
	    else if (h instanceof DaapHost)
	        return daapSongs;
	    else
	        return null;
	}
	
	public ArrayList getLocalHosts() {
	    ArrayList lhosts = new ArrayList();
	    for (int i=0; i < local.getChildCount(); i++) {
	        GITNode node = (GITNode)local.getChildAt(i);
	        LocalHost host = (LocalHost)node.getUserObject();
	        lhosts.add(host);
	    }
	    return lhosts;
	}
	
	public boolean noHostsOrPlaylistsConnecting() {
	    for (int i=0; i < root.getChildCount(); i++) {
	        GITNode node = (GITNode)root.getChildAt(i);
	        for (int j=0; j < node.getChildCount(); j++) {
	            HostNode hnode = (HostNode)node.getChildAt(j);
	            Host h = (Host)hnode.getUserObject();
	            if (h.getStatus() == Host.STATUS_CONNECTING)
	                return false;
//	            for (int k=0; k < h.getPlaylists().size(); k++) {
//	                Playlist p = (Playlist)hnode.getUserObject();
//	                if (p.getStatus() == Playlist.STATUS_INITIALIZING)
//	                    return false;
//	            }
	        }
	    }
	    return true;
	}
	
	public void updateButton(Host h) {
		int s = h.getStatus();
		String str = "Host";
		if (h instanceof LocalHost)
			str = "Library";
		switch (s) {
			case Host.STATUS_PLAYLISTS_LOADED:
				s = Host.STATUS_CONNECTED;
			case Host.STATUS_CONNECTED:
				button.setText("Disconnect");
				button.setEnabled(true);
				return;
			case Host.STATUS_NOT_CONNECTED:
				button.setText("Browse " + str);
				button.setEnabled(true);
				return;
			case Host.STATUS_CONNECTING:
				button.setText("Disconnect");
				button.setEnabled(false);
				return;
			case Host.STATUS_NOT_AVAILABLE:
				button.setText("Browse" + str);
				button.setEnabled(false);
				return;
			default:
				button.setText("Browse" + str);
				button.setEnabled(true);
				return;
		}
	}

	public void showPopup(Component component, int x, int y) {}

	public void fixDisplay() {
		Runnable doSplitThing = new Runnable() {
			public void run() {
				Dimension size = new Dimension(splitPane.getDividerLocation(), 50);
				buttons.setMaximumSize(size);
				buttons.setPreferredSize(size);
				buttons.setMinimumSize(size);
				buttons.setSize(size);
				dspanel.validate();
			}
		};
		System.out.println("fixdisplay");
		SwingUtilities.invokeLater(doSplitThing);
	}

	protected void createSplitPane() {

		hostsPane = new JPanel();
		hostsPane.setFocusable(false);
		//hostsPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		hostsPane.setLayout(new BoxLayout(hostsPane, BoxLayout.Y_AXIS));
//		knownHosts = new DefaultListModel();
		
		// Create the hosts tree:
		root = new GITNode(GITNode.ROOT);
		tree_model = new GITTreeModel(root);
		tree = new JTree(tree_model) {
		    public boolean ok_to_edit = true;
		    HostNode cacheNode = null;
		    int num = 0;
		    ImageIcon speaker = new ImageIcon(GetItTogether.class.getResource("/images/speaker.png"));
		    ImageIcon thumbsDown = new ImageIcon(GetItTogether.class.getResource("/images/tdown.gif"));
		    ImageIcon thumbsUp = new ImageIcon(GetItTogether.class.getResource("/images/tup.gif"));
		    
		    
		    public boolean isPathEditable(TreePath tp) {
		        if (isEditable())
		        {
			        GITNode node = (GITNode)tp.getLastPathComponent();
			        if (node.getType() == GITNode.LOCAL)
			            return true;
			        else
			            return false;
		        }
		        else
		            return false;
		    }
		    
		    public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        
		        if (cacheNode == null || cacheNode.getHost() != playingHost) {
		            HostNode h = (HostNode)root.getChildByObject(playingHost);
		            if (h != null)
		                cacheNode = h;
		        }
		        if (cacheNode != null && playingHost != null) {
		            // paint the speaker icon
		            TreePath p = tree.getNextMatch(cacheNode.getName(),0,Position.Bias.Forward);
		            Rectangle r = tree.getPathBounds(p);
		            if (r == null)
		                return;
		            Graphics2D g2 = (Graphics2D)g;
		            r.x = r.x - speaker.getIconWidth() - 16;
		            g2.drawImage(speaker.getImage(), r.x,r.y,speaker.getIconWidth(),speaker.getIconHeight(),null);
		        }
		        if (GITProperties.expandRemote) {
		            for (int i=0; i < hosts.getChildCount(); i++) {
		                HostNode hnode = (HostNode)hosts.getChildAt(i);
		                if (hnode.getHost().getStatus() == Host.STATUS_CONNECTING) {
		                    paintConnectingIcon(hnode.getHost(),g);
		                }
		                DaapHost daapHost = (DaapHost)hnode.getHost();
		                paintRatingIcon((DaapHost)hnode.getHost(),g);
		            }
		        }
		        if (GITProperties.expandLocal) {
		            for (int i=0; i < local.getChildCount(); i++) {
		                HostNode hnode = (HostNode)local.getChildAt(i);
		                if (hnode.getHost().getStatus() == Host.STATUS_CONNECTING) {
		                    paintConnectingIcon(hnode.getHost(),g);
		                }
		            }
		        }
		    }
		    
		    public void paintRatingIcon(DaapHost h,Graphics g) {
//		        System.out.println("painting rating icon!");
		        ImageIcon i;
		        switch (h.rating) {
		        case DaapHost.RATING_NONE:
		            return;
		        case DaapHost.RATING_UP:
		            i = thumbsUp;
		        break;
		        case DaapHost.RATING_DOWN:
		            i = thumbsDown;
		        break;
		        default:
		            return;
		        }
//		        TreePath p = tree.getNextMatch(h.getName(),0,Position.Bias.Forward);
		        GITNode node = root.getChildByObject(h);
		        if (node == null)
		            return;
		        TreePath p = new TreePath(node.getPath());
		        Rectangle r = tree.getPathBounds(p);
		        if (r == null) {
		            System.err.println("tree error: r == null!");
		        }
		        r.x = r.x - 16 - i.getIconWidth();
		        Graphics2D g2 = (Graphics2D)g;
		        AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.25f);
		        Composite old = g2.getComposite();
		        g2.setComposite(a);
		        g2.drawImage(i.getImage(), r.x,r.y,i.getIconWidth(),i.getIconHeight(),tree);
		        g2.setComposite(old);
		    }
		    
		    public void paintConnectingIcon(Host h,Graphics g) {
		        if (arrows == null)
		            arrows = new ImageIcon(GetItTogether.class.getResource("/images/arrows.gif"));
//		        TreePath p = tree.getNextMatch(h.getName(),0,Position.Bias.Forward);
		        GITNode node = root.getChildByObject(h);
		        if (node == null)
		            return;
		        TreePath p = new TreePath(node.getPath());
	            Rectangle r = tree.getPathBounds(p);
	            if (r == null)
	                return;
	            r.x = r.x - 16 - arrows.getIconWidth();
	            g.drawImage(arrows.getImage(), r.x,r.y,arrows.getIconWidth(),arrows.getIconHeight(),tree);
		    }
		    
		};
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(16);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(true);
		tree.setCellEditor(new DefaultTreeCellEditor(tree,(DefaultTreeCellRenderer)tree.getCellRenderer()) {
		    public boolean shouldStartEditingTimer(EventObject obj) {
		        return false;
		    }
		});
		tree.setInvokesStopCellEditing(true);
		tree.setCellRenderer(new HostTreeRenderer());
		ToolTipManager.sharedInstance().registerComponent(tree); 
		tree.setUI(new BasicTreeUI() {
			protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
					Insets insets, Rectangle bounds, TreePath path, int row,
					boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {}

			protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
					Insets insets, TreePath path) {}
		});
		BasicTreeUI basicTreeUI = (BasicTreeUI) tree.getUI();
		basicTreeUI.setRightChildIndent(9);
		tree.addTreeExpansionListener(new TreeExpansionListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
                if (event.getPath().getLastPathComponent() == hosts)
                    GITProperties.expandRemote = false;
                else if (event.getPath().getLastPathComponent() == local)
                    GITProperties.expandLocal = false;
            }

            public void treeExpanded(TreeExpansionEvent event) {
                if (event.getPath().getLastPathComponent() == hosts)
                    GITProperties.expandRemote = true;
                else if (event.getPath().getLastPathComponent() == local)
                    GITProperties.expandLocal = true;
            }
		    
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				GITNode node = (GITNode) tree.getLastSelectedPathComponent();
				if (node == null)
					return;
				switch (node.getType()) {
					case GITNode.LOCAL:
					case GITNode.DAAP:
						Host h = ((HostNode) node).getHost();
						if (h == null)
						    return;
						updateButton(h);
				}
			}
		});
		MouseAdapter popupAdapter = new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            checkPopup(e);
		            return;
		        }
		        if (e.getButton() == MouseEvent.BUTTON1) {
		            tree.setToggleClickCount(2);
		            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		            if (path == null) {
		                Rectangle end = tree.getRowBounds(tree.getRowCount()-1);
		                if (e.getY() > end.y+end.height)
		                    return;
		                else
		                    path = tree.getClosestPathForLocation(e.getX(),e.getY());
		            }
		            GITNode node = (GITNode) path.getLastPathComponent();
		            
		            Rectangle r = tree.getPathBounds(path);
		            Rectangle leftMost = new Rectangle(0,r.y,16,16);
		            Rectangle puzzIcon = new Rectangle(32,r.y,16,16);
		            if (leftMost.contains(e.getPoint()) && node instanceof HostNode && node.getType() == GITNode.DAAP) {
		                DaapHost h = (DaapHost)((HostNode)node).getHost();
		                h.rating++;
		                if (h.rating == 3)
		                    h.rating = 0;
//		                System.out.println("changed!");
		                tree.repaint();
		                return;
		            } else if (puzzIcon.contains(e.getPoint())) {
		                nodeClicked(node);
		                tree.setToggleClickCount(0);
		                return;
		            } else if (r.contains(e.getPoint())){
		                int type = node.getType();
		                switch (node.getType()) {
		                case GITNode.DAAP_ROOT:
		                case GITNode.DAAP:
		                    views.setSelectedComponent(music);
		                cl.show(music, CARD_SHARED);
		                break;
		                case GITNode.LOCAL_ROOT:
		                case GITNode.LOCAL:
		                    views.setSelectedComponent(music);
		                cl.show(music, CARD_LOCAL);
		                break;
		                }
		            }
		        }
		    }
		
		    public void mouseReleased(MouseEvent e) {
		        checkPopup(e);
		    }
		    
		    public void mouseClicked(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            checkPopup(e);
		            return;
		        }
		        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
		            GITNode node = null;
		            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		            if (path != null)
		                node = (GITNode) path.getLastPathComponent();
		            if (node == null)
		                return;
                    if (node.getType() == GITNode.DAAP || node.getType() == GITNode.LOCAL) {
                        Host host = (Host) node.getUserObject();
                        if (host.getStatus() < Host.STATUS_CONNECTED) { // CLICKED ON AN UNCONNECTED HOST
                            nodeClicked(node);
                        }
                    }
                    else
                        nodeClicked(node);
                }
            }
		    
		    public void createGenericHostRootPopup(JPopupMenu popup, final GITNode node) {
		        String label;
		        switch (node.getType()) {
	        		case GITNode.LOCAL_ROOT:
	        		    label = "Libraries";
	        			break;
        			case GITNode.DAAP_ROOT:
	        		default:
	        		    label = "Hosts";
	        			break;
		        }
		        
		        JMenuItem connect_all = new JMenuItem("Connect all "+label);
                connect_all.addActionListener(GITUtils.createSwingWorkerAction(connectAll));
                popup.add(connect_all);
                
                JMenuItem disconnect_all = new JMenuItem("Disconnect all "+label);
                disconnect_all.addActionListener(GITUtils.createSwingWorkerAction(disconnectAll));
                popup.add(disconnect_all);
                
                
		    }
		    
		    public void createGenericHostPopup(JPopupMenu popup, final GITNode node) {
		        final Host host = (Host)node.getUserObject();
		        int status = host.getStatus();
		        String h = host.getTypeString();
		        
		        String i = "Host";
		        if (node.getType() == GITNode.LOCAL)
		            i = "Library";
		        
		        if (status >= Host.STATUS_CONNECTED) {
                    JMenuItem disconnect = new JMenuItem(
                            "Disconnect from " + h);
                    disconnect.addActionListener(disconnectHost);
                    popup.add(disconnect);
                    
                    JMenuItem reconnect = new JMenuItem("Reconnect "+h);
                    reconnect.addActionListener(GITUtils.createSwingWorkerAction(reconnectHost));
                    popup.add(reconnect);
                    
                } else {
                    JMenuItem connect = new JMenuItem("Connect to " + h);
                    connect.addActionListener(GITUtils.createSwingWorkerAction(connectHost));
                    popup.add(connect);
                }
                if (host.isVisible()) {
                    JMenuItem hide = new JMenuItem("Hide " + i);
                    hide.addActionListener(browseHost);
                    if (status < Host.STATUS_CONNECTED)
                        hide.setEnabled(false);
                    popup.add(hide);
                } else {
                    JMenuItem show = new JMenuItem("Show " + i);
                    show.addActionListener(browseHost);
                    if (status < Host.STATUS_CONNECTED)
                        show.setEnabled(false);
                    popup.add(show);
                }
                JMenuItem showOnly = new JMenuItem("Show only this " + i);
                showOnly.addActionListener(GITUtils.createSwingWorkerAction(hideOthers));
                if (status < Host.STATUS_CONNECTED)
                    showOnly.setEnabled(false);
                popup.add(showOnly);
		    }
		    
		    public void addAutoConnectBox(JPopupMenu p, final GITNode node) {
		        final Host host = (Host)node.getUserObject();
		        final JCheckBox autoConnectCheckBox = new JCheckBox();
                autoConnectCheckBox.setAction(new AbstractAction() {

                    public void actionPerformed(ActionEvent e) {
                        boolean already_auto = host.isAutoConnect();
                        host.setAutoConnect(autoConnectCheckBox.isSelected());
                        if (!already_auto && host.isAutoConnect() && !(host.getStatus() >= Host.STATUS_CONNECTED))
                        {
                            if (host instanceof DaapHost)
                                hostClicked(hosts.getChildByObject(host));
                            else if (host instanceof LocalHost)
                                hostClicked(local.getChildByObject(host));
                            else
                                return;
                        }
                    }
                    
                });
                autoConnectCheckBox.setSelected(host.isAutoConnect());
                autoConnectCheckBox.setText("Auto connect");
                p.add(autoConnectCheckBox);
		    }
		    
		    public void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    tree.setSelectionPath(tree.getPathForLocation(e.getX(), e
                            .getY()));
                    final GITNode node = (GITNode) tree
                            .getLastSelectedPathComponent();
                    if (node == null)
                        return;
                    JPopupMenu popup = new JPopupMenu();
                    
                    switch (node.getType()) {
                    case GITNode.DAAP_ROOT: {
                        popup = new JPopupMenu();
                        createGenericHostRootPopup(popup, node);
                        break;
                    }
                    case GITNode.DAAP: {
//                        System.out.println("Daap Host Popup Menu!");
                        final DaapHost host = (DaapHost) node.getUserObject();
                        popup = new JPopupMenu();
                        createGenericHostPopup(popup, node);
                        addAutoConnectBox(popup, node);
                        break;
                    }
                    case GITNode.LOCAL: {
//                        System.out.println("Local Host Popup Menu!");

                        final LocalHost host = (LocalHost) node.getUserObject();
                        int status = host.getStatus();

                        popup = new JPopupMenu();

                        createGenericHostPopup(popup, node);
                        
                        JMenuItem rename = new JMenuItem("Rename Library");
                        rename.addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                                tree.startEditingAtPath(tree.getSelectionPaths()[0]);
                            }
                        });
                        popup.add(rename);
                        
//                        JMenuItem remove = new JMenuItem("Remove Library");
//                        remove.addActionListener(removeLibrary);
//                        popup.add(remove);
//                        
//                        JMenuItem change = new JMenuItem(
//                                "Change Library's root location...");
//                        change.addActionListener(changeLibrary);
//                        popup.add(change);
                        
                        if (host instanceof GITLibraryHost) {
                            JMenuItem addFiles = new JMenuItem("Import MP3 Directory...");
                            addFiles.addActionListener(GITUtils.createSwingWorkerAction(new AbstractAction() {
                                public void actionPerformed(ActionEvent e) {
                                    ((GITLibraryHost)host).importMp3Directory();
                                }
                            }));
                            if (host.getStatus() < Host.STATUS_CONNECTED) {
                                addFiles.setEnabled(false);
                                addFiles.setToolTipText("You must connect this Library before adding files.");
                            }
                            popup.add(addFiles);
                        }
                        
                        addAutoConnectBox(popup, node);
                        break;
                    }
                    case GITNode.LOCAL_ROOT: {
                        popup = new JPopupMenu();
                        createGenericHostRootPopup(popup, node);
                    }
                    case GITNode.PLAYLIST: {
//                        System.out.println("Playlist Host Popup Menu!");
                        break;
                    }
                    case GITNode.PODCAST_ROOT: {
                        popup = new JPopupMenu();
                        JMenuItem add = new JMenuItem(GITUtils.createSwingWorkerAction(addPodcast));
                        add.setText("Add a podcast");
                        popup.add(add);
                    }
                    default:
                        break;
                    }
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
		    }
		};
		tree.addMouseListener(popupAdapter);

		tree.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "ENTER");
        tree.getActionMap().put("ENTER", clearPlay);
        tree.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "Space");
        tree.getActionMap().put("Space", pausePlay);
        
		hostsPane.setMinimumSize(new Dimension(130, 0));
		hostsPane.add(new JScrollPane(tree));

		// add Browse Hosts button:
		button = new JButton("Browse Host");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setMnemonic(KeyEvent.VK_I);
		button.addActionListener(hostButton);
		hostsPane.add(button);
		
		// Create the split pane:
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hostsPane, createCards());
		splitPane.setOneTouchExpandable(false);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(180);
		splitPane.setFocusable(false);
		splitPane.resetKeyboardActions();
		splitPane.setDividerSize(4);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider()
			.addMouseListener(new MouseAdapter() {
			    
			    public void mouseReleased(MouseEvent e) {
			        fixDisplay();
			    }
			});
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseDragged(MouseEvent e) {
		        fixDisplay();
		    }
		});
		splitPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
		        KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false), "F1");
		splitPane.getActionMap().put("F1", toggleHelp);
		splitPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
		        KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK, false), "C-m");
		splitPane.getActionMap().put("C-m", toggleMini);
		
	}

	protected JTabbedPane createCards() {
	    views = new JTabbedPane() {
	        ImageIcon info = new ImageIcon(GetItTogether.class
					.getResource("/images/i.png"));
	        
	        public void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D)g;
	            g2.setColor(GIT_BLUE);
//	            g2.drawRect(views.getWidth()-16-2, 2,16,16);
	            int s = 16;
	            int x = views.getWidth()-s-2;
	            int y = 2;
	            g2.drawImage(info.getImage(),x,y,info.getIconWidth(),info.getIconHeight(),views);
	        }
	        
	        
	    };
	    
	    views.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
	            Rectangle r = new Rectangle(views.getWidth()-16-2,2,16,16);
	            if (r.contains(e.getPoint())) {
	                ImageIcon i = new ImageIcon(GetItTogether.class.getResource(SPLASH_PATH));
	                SplashWindow window = new SplashWindow(frame, i.getImage());
	                window.toFront();
	                window.show();
	            }
	        }
	    });
	    
	    browserRender = new BrowserRenderer();
	    
	    ImageIcon local = new ImageIcon(GetItTogether.class
				.getResource("/images/tree_local_filled.png"));
	    ImageIcon hosts = new ImageIcon(GetItTogether.class
				.getResource("/images/tree_host_filled.png"));
	    ImageIcon dl = new ImageIcon(GetItTogether.class
				.getResource("/images/tree_downloads_filled.png"));
	    ImageIcon pl = new ImageIcon(GetItTogether.class
				.getResource("/images/tree_pl_filled.png"));
	    ImageIcon settings = new ImageIcon(GetItTogether.class
				.getResource("/images/tree_settings_filled.png"));
	    
	    music = new JPanel();
	    cl = new CardLayout();
	    music.setLayout(cl);
	    
	    // CREATE THE QUEUE PANEL.
	    qview = new JPanel();
	    qview.setLayout(new BoxLayout(qview, BoxLayout.Y_AXIS));
	    qview.add(new JScrollPane(createQueue()));
	    
	    // Create the download panel:
	    dview = new JPanel();
	    dview.setName(CARD_DOWNLOADS);
	    dview.setLayout(new BoxLayout(dview, BoxLayout.Y_AXIS));
	    dview.add(new JScrollPane(createDownloader()));
	    dview.add(createDownloadButtons());
	    countLabel = new JLabel() {
	        public String[] ranks = new String[] {
	            "0:n00b","20:Casual downloader","50:Hardened criminal",
	            "100:Downloading fiend","200:RIAA's most wanted",
	            "400:l33t h@x02","600:OMG h4x!!!","1000:Needs a life"
	        };
	        
	        public String getRank() {
	            int cnt = GITProperties.numDownloads;
	            for (int i=ranks.length - 1; i >= 0; i--) {
	                String[] s = ranks[i].split(":");
	                if (Integer.parseInt(s[0]) <= cnt)
	                    return s[1];
	            }
	            return "GIT peon";
	        }
	        
	        public String getToolTipText(MouseEvent e) {
	            String str = new String();
	            str += "<html><b>Downloads: </b>" + GITProperties.numDownloads
	            + "<br><b>Rank: </b>" + getRank();
	            return str;
	        }
	        
	    };
	    countLabel.setToolTipText("");
	    countLabel.setText(DOWNLOAD_COUNTER+GITProperties.numDownloads);
		countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		dview.add(countLabel);
		
		// Create the local song list:
		localSongs = new CompositeList();
		VisibleHostMatcherEditor local_vis_matcher = new VisibleHostMatcherEditor();
		FilterList filter = new FilterList(localSongs, local_vis_matcher);
		localSongsMatcher = new SupportedSongMatcherEditor();
		localSongsFilter = new FilterList(filter, localSongsMatcher);
		// Create the local JPanel view:
		localJPanel = createGITSongTable(localSongsFilter, true);
			localJPanel.table.getActionMap().put("DELETE", removeSongs);
			localJPanel.table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0,false),"DELETE");
			localJPanel.table.getActionMap().put("I", songInfoAction);
			localJPanel.table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I,0,false),"I");
		localJPanel.vis_filter = filter;
		localJPanel.vis_matcher = local_vis_matcher;
		localJPanel.setName(CARD_LOCAL);
		music.add(localJPanel, CARD_LOCAL);
		localJPanel.search_label.setIcon(local);
		
		// Create the host song list:
		daapSongs = new CompositeList();
		VisibleHostMatcherEditor daap_vis_matcher = new VisibleHostMatcherEditor();
		FilterList filter2 = new FilterList(daapSongs, daap_vis_matcher);
		daapSongsMatcher = new SupportedSongMatcherEditor();
		daapSongsFilter = new FilterList(filter2, daapSongsMatcher);
		// Create the shared (browser) JPanel view:
		daapJPanel = createGITSongTable(daapSongsFilter, true);
		daapJPanel.vis_filter = filter2;
		daapJPanel.vis_matcher = daap_vis_matcher;
		daapJPanel.setName(CARD_SHARED);
		music.add(daapJPanel, CARD_SHARED);
		daapJPanel.search_label.setIcon(hosts);
		
		// Create the playlist song list:
		playlistSongs = new BasicEventList();
		// Create the playlist JPanel view: 
		playlistJPanel = createGITSongTable(playlistSongs, false);
		playlistJPanel.setName(CARD_PLAYLISTS);
		music.add(playlistJPanel, CARD_PLAYLISTS);
		playlistJPanel.search_label.setIcon(pl);
		
		
		// Create the preferences panel.
	    sview = new JPanel();
	    sview.setFocusTraversalKeysEnabled(false);
	    sview.addFocusListener(focus);
	    sview.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                        KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
	    sview.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                        KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
        sview.getActionMap().put("Ctrl-Tab", switchTabs);
	    sview.setName(CARD_SETTINGS);
	    JPanel outer = new JPanel();
	    outer.add(new GITPropertiesPanel());
	    JScrollPane scroller = new JScrollPane(outer);
	    scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    sview.setLayout(new BoxLayout(sview, BoxLayout.Y_AXIS));
	    sview.add(scroller);
	    
	    views.addTab("Browser", music);
	    views.addTab(CARD_QUEUE, qview);
	    views.addTab(CARD_DOWNLOADS, dview);
	    views.addTab(CARD_SETTINGS, sview);
	    views.setSelectedComponent(music);
	    cl.show(music, CARD_LOCAL);
	    
	    views.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                setFocusToTable.actionPerformed(null);
            }
	        
	    });
	    
	    return views;
	}
		
	public GlazedGITSongJPanel createGITSongTable(EventList source, boolean sorted) {
	    
	    
	    //	  SET UP THIS JPANEL:
        final GlazedGITSongJPanel p = new GlazedGITSongJPanel();
	    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setMinimumSize(new Dimension(300, 200));

        // ADD THE SEARCH PANEL:
		JTextField search_field = new JTextField();
		search_field.addFocusListener(focus);
		search_field.setMaximumSize(new Dimension(1000, 21));
		search_field.setMinimumSize(new Dimension(100, 21));
		search_field.setPreferredSize(new Dimension(100, 21));
		
		search_field.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                        0, false), "Enter");
        search_field.getActionMap().put("Enter", setFocusToTable);
		
		JLabel search_label = new JLabel("Search:");
		search_label.setHorizontalTextPosition(JLabel.TRAILING);
		search_label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(search_label);
        searchPanel.add(search_field);
        
        p.add(searchPanel);

	    // SORT AND SEARCH THE LIST:
	    FilterList search;
	    SortedList sort;
	    if (sorted) {
	        sort = new SortedList(source);
	        search = new FilterList(sort, new TextComponentMatcherEditor(search_field, new SongTextFilterator()));
	    } else {
	        sort = null;
	        search = new FilterList(source);
	    }
	    
        // CREATE THE TABLE:
        EventTableModel songTableModel = new EventTableModel(search, GlazedSongTableFormat.INSTANCE);
        
        JTable songJTable = new JTable(songTableModel);
        if (sorted) {
            sort.setComparator(GlazedSongTableFormat.INSTANCE.song);
            TableComparatorChooser tableSorter = new TableComparatorChooser(songJTable, sort, true);
        }
        // use a good selection model.
        EventSelectionModel songSelectionModel = new EventSelectionModel(search);
        songSelectionModel.setSelectionMode(EventSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        songJTable.setSelectionModel(songSelectionModel);
        songJTable.setRowHeight(16);
//        songJTable.setFocusTraversalKeysEnabled(false);
//        songJTable.setColumnSelectionAllowed(false);
//        songJTable.setRowSelectionAllowed(true);
//        songJTable.setDragEnabled(true);
        songJTable.setShowHorizontalLines(false);
        songJTable.setGridColor(new Color(180, 180, 180));
        songJTable.getColumnModel().setColumnMargin(1);
//		songJTable.setDoubleBuffered(true);
        JScrollPane songJScrollPane = new JScrollPane(songJTable);
        songJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		songJScrollPane
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
//		p.info_view = new JPanel();
//		p.info_view.setLayout(new BoxLayout(p.info_view,BoxLayout.X_AXIS));
//		
//		p.info_label = new JLabel();
//		p.info_label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
//		p.info_label.setAlignmentX(Component.LEFT_ALIGNMENT);
//		p.info_label.setMinimumSize(new Dimension(200,100));
//		p.info_label.setIconTextGap(10);
//		
//		
//		p.info_label2 = new JPanel();
//		p.info_label2.setLayout(new BoxLayout(p.info_label2,BoxLayout.Y_AXIS));
//		p.info_label2.add(new JLabel("Show song in Finder"));
//		p.info_label2.add(new JLabel("other stuff goes here"));
//		
//		p.info_view.add(p.info_label);
//		p.info_view.add(Box.createHorizontalGlue());
//		p.info_view.add(p.info_label2);

		p.info_view = new SongInfoPanel();
		
		p.split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,songJScrollPane,p.info_view);
		p.split_pane.setAlignmentX(Component.CENTER_ALIGNMENT);
		BasicSplitPaneUI ui = (BasicSplitPaneUI)p.split_pane.getUI();
		BasicSplitPaneDivider divider = ui.getDivider();
		MouseListener[] ml = p.split_pane.getMouseListeners();
		for (int i=0; i < ml.length; i++) {
		    System.out.println(ml[i]);
		    p.split_pane.removeMouseListener(ml[i]);
		}
		MouseMotionListener[] mml = p.split_pane.getMouseMotionListeners();
		for (int i=0; i < mml.length; i++) {
		    p.split_pane.removeMouseMotionListener(mml[i]);
		}
		ml = divider.getMouseListeners();
		for (int i=0; i < ml.length; i++) {
		    System.out.println(ml[i]);
		    divider.removeMouseListener(ml[i]);
		}
		mml = divider.getMouseMotionListeners();
		for (int i=0; i < mml.length; i++) {
		    divider.removeMouseMotionListener(mml[i]);
		}
		divider.setEnabled(false);
		
		p.split_pane.setDividerSize(5);
		p.split_pane.setOneTouchExpandable(false);
//		p.split_pane.setContinuousLayout(true);
		p.split_pane.setResizeWeight(1);
		
		final JPanel asdf = p.info_view;
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        asdf.setVisible(false);
		    }
		});
		
		
		p.add(p.split_pane);
//		p.add(songJScrollPane);
        
        // ADD THE TOTALS LABEL:
        JLabel totalsLabel = new JLabel();
		totalsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel totalsPanel = new JPanel();
		totalsPanel.setLayout(new BoxLayout(totalsPanel,BoxLayout.X_AXIS));
		
		p.info_button = createLabel("song info (beta)", songInfoAction, "");
		p.info_button.setIcon(up);
		p.info_button.setHorizontalTextPosition(SwingConstants.TRAILING);
		p.info_button.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		totalsPanel.add(p.info_button);
		totalsPanel.add(Box.createHorizontalGlue());
		totalsPanel.add(totalsLabel);
		totalsPanel.add(Box.createHorizontalGlue());
		p.add(totalsPanel);
		
		// ADD THIS GITSONGJPANEL AS A LIST EVENT LISTENER:
		search.addListEventListener(p);
		
		// MAKE CERTAIN VARIABLES VISIBLE FROM P:
        p.source = source;
        p.visible = search;
        p.table = songJTable;
        p.search_label = search_label;
        p.totals_label = totalsLabel;
        p.search_field = search_field;
		
		p.updateTotals();
		p.addFocusListener(focus);
		
		// CUSTOMIZE THE JTABLE:
		for (int i = 0; i <= 5; i++) {
            TableColumn column = songJTable.getColumnModel().getColumn(i);
            column.setCellRenderer(browserRender);
            switch (i){
                case 0:
                    column.setPreferredWidth(25);
                    column.setMaxWidth(25);
                    break;
                case 1:
                    column.setPreferredWidth(90);
                    break;
                case 2:
                    column.setPreferredWidth(80);
                    break;
                case 3:
                    column.setPreferredWidth(150);
                    column.setCellEditor(new SeekEditor());
                    break;
                case 4:
                    column.setPreferredWidth(40);
                    column.setMinWidth(40);
                    column.setMaxWidth(40);
                    //column.setCellRenderer(new BrowserTimeRenderer());
                    break;
                case 5:
                    column.setPreferredWidth(50);
                    column.setMaxWidth(50);
                    column.setMinWidth(45);
                    //column.setCellRenderer(new FileSizeRenderer());
                    break;
            }
	    }
	    
	    // add shortcut keys.
		songJTable.setFocusTraversalKeysEnabled(false);
	    songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
        songJTable.getActionMap().put("Ctrl-Tab", switchTabs);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "ENTER");
        songJTable.getActionMap().put("ENTER", clearPlay);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "Left");
        songJTable.getActionMap().put("Left", playPrevious);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "Right");
        songJTable.getActionMap().put("Right", playNext);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), "Q");
        songJTable.getActionMap().put("Q", enqueueSongs);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "Space");
        songJTable.getActionMap().put("Space", pausePlay);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "D");
        songJTable.getActionMap().put("D", download);
        songJTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, 0, false), ";");
        songJTable.getActionMap().put(";", openSearch);
        
        songJTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
            }
            
            public void mouseDragged(MouseEvent e) {
                JTable t = (JTable)e.getComponent();
                if (t.convertColumnIndexToModel(t.columnAtPoint(e.getPoint())) == 3) {
                    System.out.println("column 3!");
                    e.consume();
                }
            }
        });

		// handles popups, double-clicks, and single clicks on the title column.
		songJTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			public void mouseClicked(final MouseEvent e) {
			    checkPopup(e);
			    final EventList el = getVisibleEventList();
			    final JTable t = (JTable)e.getComponent();
			    if (e.getButton() == MouseEvent.BUTTON1) {
			        if (e.getClickCount() == 2) {
			            clearPlay.actionPerformed(blankAction);
			            return;
			        }
			        if (e.getClickCount() == 1
			                && t.convertColumnIndexToModel(t.columnAtPoint(e.getPoint())) == 3
			                && t.rowAtPoint(e.getPoint()) == el.indexOf(playingSong)) {
			            showElapsed = (showElapsed ? false : true);
			            updateTables.actionPerformed(blankAction);
			        }
			    }
			}

			public void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
				    final JTable songJTable = (JTable)e.getComponent();
					JPopupMenu tPopup = new JPopupMenu();
					JMenuItem play = new JMenuItem("Play Song(s)");
					play.addActionListener(clearPlay);
					tPopup.add(play);
					JMenuItem queue = new JMenuItem("Enqueue Song(s)");
					queue.addActionListener(enqueueSongs);
					tPopup.add(queue);
					JMenuItem downloader = new JMenuItem("Download Song(s)");
					downloader.addActionListener(download);
					tPopup.add(downloader);
					final JMenuItem showOnly = new JMenuItem();
					final int row = songJTable.rowAtPoint(e.getPoint());
					final int col = songJTable.columnAtPoint(e.getPoint());
					showOnly.setText("Search for this " + songJTable.getColumnName(col));
					final Object value = songJTable.getValueAt(row, col);
					Song s = (Song)value;
					
					System.out.println(s);
					if (s != null && s instanceof LocalSong) {
					    LocalSong ls = (LocalSong)s;
					    LocalHost lh = (LocalHost)ls.getHost();
					    JMenuItem remove = new JMenuItem("Remove Song(s)...");
					    remove.addActionListener(GITUtils.createSwingWorkerAction(removeSongs));
					    tPopup.add(remove);
					}
					JComponent rendered_component = (JComponent)songJTable.getCellRenderer(row,col).getTableCellRendererComponent(songJTable,value,true,false,row,col);
					if (!(rendered_component instanceof JLabel)) {
					    showOnly.setEnabled(false);
					} else {
					    JLabel label = (JLabel)rendered_component;
					    final String search_string = label.getText();
					    showOnly.addActionListener(new AbstractAction() {
						public void actionPerformed(ActionEvent f) {
						    searchText(search_string);
						}
					    });
					}
					tPopup.add(showOnly);
					PopupListener pl = new PopupListener(tPopup);
					pl.maybeShowPopup(e);
				}
			}
		});
		songJTable.addFocusListener(focus);
		songJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    AbstractAction task = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    updateSongInfoPanel(p);
                }
            };
            
            Timer timer = new Timer(100, task);
            {
                timer.setRepeats(false);
            }
		    
            public void valueChanged(final ListSelectionEvent e) {
                timer.restart();
            }
		});
		return p;
	}
	
	// NOT CURRENTLY USED.
	public void addStuffToSongTable(final GITSongTable t) {
        final JTable table = t.table;
        
        for (int i = 0; i <= 5; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(browserRender);
            switch (i){
                case 0:
                    column.setPreferredWidth(25);
                    column.setMaxWidth(25);
                    break;
                case 1:
                    column.setPreferredWidth(90);
                    break;
                case 2:
                    column.setPreferredWidth(80);
                    break;
                case 3:
                    column.setPreferredWidth(150);
                    column.setCellEditor(new SeekEditor());
                    break;
                case 4:
                    column.setPreferredWidth(40);
                    column.setMinWidth(40);
                    column.setMaxWidth(40);
                    //column.setCellRenderer(new BrowserTimeRenderer());
                    break;
                case 5:
                    column.setPreferredWidth(50);
                    column.setMaxWidth(50);
                    column.setMinWidth(45);
                    //column.setCellRenderer(new FileSizeRenderer());
                    break;
            }
    }
        
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                        KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
        table.getActionMap().put("Ctrl-Tab", switchTabs);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "ENTER");
        table.getActionMap().put("ENTER", clearPlay);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "Left");
        table.getActionMap().put("Left", playPrevious);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "Right");
        table.getActionMap().put("Right", playNext);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), "Q");
        table.getActionMap().put("Q", enqueueSongs);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "Space");
        table.getActionMap().put("Space", pausePlay);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "D");
        table.getActionMap().put("D", download);
        table.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, 0, false), ";");
        table.getActionMap().put(";", openSearch);
        

        table.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                System.out.println(e);
                if (table.columnAtPoint(e.getPoint()) == 1) {
                    System.out.println("HEY");
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else
                    table.setCursor(Cursor.getDefaultCursor());
            }
        });
        
		// handles popups, double-clicks, and single clicks on the title column.
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			public void mouseClicked(MouseEvent e) {
			    checkPopup(e);
			    EventList el = getVisibleEventList();
			    
			    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			        clearPlay.actionPerformed(blankAction);
				}
			    if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 1
						&& table.getColumnName(table.columnAtPoint(e.getPoint())) == "Title"
						&& el.get(table.rowAtPoint(e.getPoint())) == playingSong) {
					showElapsed = (showElapsed ? false : true);
					updateTables.actionPerformed(null);
				}
			}

			public void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu tPopup = new JPopupMenu();
					JMenuItem play = new JMenuItem("Play Song(s)");
					play.addActionListener(clearPlay);
					tPopup.add(play);
					JMenuItem queue = new JMenuItem("Enqueue Song(s)");
					queue.addActionListener(enqueueSongs);
					tPopup.add(queue);
					JMenuItem downloader = new JMenuItem("Download Song(s)");
					downloader.addActionListener(download);
					tPopup.add(downloader);
					JMenuItem showOnly = new JMenuItem();
					final int row = table.rowAtPoint(e.getPoint());
					final int col = table.columnAtPoint(e.getPoint());
					showOnly.setText("Search for this " + table.getColumnName(col));
					showOnly.addActionListener(new AbstractAction() {
						public void actionPerformed(ActionEvent f) {
							Object value = table.getValueAt(row, col);
							if (value instanceof String) {
								searchText((String) value);
							} else if (table.getColumnName(col).equals("Tr.")) {
								searchText("track:" + ((Integer) value).toString());
							} else if (table.getColumnName(col).equals("Size")) {
								searchText("size:" + ((Integer) value).toString());
							} else if (table.getColumnName(col).equals("Length")) {
								searchText("length:" + ((Integer) value).toString());
							}
						}
					});
					tPopup.add(showOnly);
					PopupListener pl = new PopupListener(tPopup);
					pl.maybeShowPopup(e);
				}
			}
			
		});
		
        JTextField search_field = t.search_field;
	    search_field.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "Enter");
		search_field.getActionMap().put("Enter", acceptSearch);
		search_field.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Escape");
		search_field.getActionMap().put("Escape", cancelSearch);
    }
	
	public String getTotals(EventList el) {
	    int songs        = 0;
		long tme         = 0;
		double sze       = 0;
		for (int i = 0; i < el.size(); i++) {
			Song s  = (Song)el.get(i);
			songs++;
			tme = tme + s.time;
			sze = sze + s.size;
		}
		String totals    = new String();
		totals = totals.concat(songs + " songs, ");

		double secs      = tme / 1000;
		double yrs       = secs / 31556926;
		String years     = String.valueOf(yrs);
		double round     = (double) Math.round(yrs * 1000) / (double) 1000;
		totals = totals.concat(round + " years, ");
		double gigs      = sze / 1073741824;
		double giground  = (double) Math.round(gigs * 100) / (double) 100;
		totals = totals.concat(giground + " GB");
		return totals;
	}

	protected JButton downloadButton(String label, Action action) {
	    JButton butt = new JButton(action);
	    butt.setText(label);
	    butt.setAlignmentX(Component.CENTER_ALIGNMENT);
	    butt.setMargin(new Insets(0, 0, 0, 0));
	    return butt;
	}
	
	protected JPanel createDownloadButtons() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	    Box all = Box.createHorizontalBox();
	    {
	    	all.setBorder(BorderFactory.createTitledBorder(null, "All", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		    all.add(downloadButton("Remove", dremoveAll));
		    all.setToolTipText("All downloads");
	    }
	    Box finished = Box.createHorizontalBox();
	    {
	    	finished.setBorder(BorderFactory.createTitledBorder(null, "Finished", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		    finished.add(downloadButton("Clean up", dremoveFinished));
		    finished.setToolTipText("Successfully completed downloads");
	    }
	    Box failed = Box.createHorizontalBox();
	    {
	    	failed.setBorder(BorderFactory.createTitledBorder(null, "Failed", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		    failed.add(downloadButton("Clear", dremoveFailed));
		    failed.add(Box.createHorizontalBox());
		    failed.add(downloadButton("Retry", dretryFailed));
		    failed.setToolTipText("Cancelled and errored downloads");
	    }
	    
	    panel.add(Box.createHorizontalGlue());
	    panel.add(all);
	    panel.add(finished);
	    panel.add(failed);
	    panel.add(Box.createHorizontalGlue());
	    return panel;
	}
	
	public JTable createQueue() {
	    queue = new QueuePlaylist();
	    EventTableModel tableModel = new EventTableModel(QueuePlaylist.queue, new GlazedSongTableFormat());
        qtable = new JTable(tableModel);
	    EventSelectionModel selection = new EventSelectionModel(QueuePlaylist.queue);
	    queue.selectionModel = selection;
	    qtable.setSelectionModel(selection);
	    qtable.setShowHorizontalLines(false);
	    qtable.setGridColor(new Color(200,200,200));
	    for (int i = 0; i < qtable.getColumnCount(); i++) {
            TableColumn column = qtable.getColumnModel().getColumn(i);
            column.setCellRenderer(browserRender);
            switch (i){
                case 0:
                    column.setPreferredWidth(25);
                    column.setMaxWidth(25);
                    break;
                case 1:
                    column.setPreferredWidth(90);
                    break;
                case 2:
                    column.setPreferredWidth(80);
                    break;
                case 3:
                    column.setPreferredWidth(150);
                    column.setCellEditor(new SeekEditor());
                    break;
                case 4:
                    column.setPreferredWidth(40);
                    column.setMinWidth(40);
                    column.setMaxWidth(40);
                    //column.setCellRenderer(new BrowserTimeRenderer());
                    break;
                case 5:
                    column.setPreferredWidth(50);
                    column.setMaxWidth(50);
                    column.setMinWidth(45);
                    //column.setCellRenderer(new FileSizeRenderer());
                    break;
            }
	    }

		qtable.addMouseListener(new PopupListener() {
		    public void showPopup(MouseEvent e) {
		        JPopupMenu popup = new JPopupMenu();
		        popup.setToolTipText(null);
		        JMenuItem play = new JMenuItem(""+plPlay.getValue(ACTION_DESC));
		        play.addActionListener(plPlay);
		        popup.add(play);
		        
		        JMenuItem remove = new JMenuItem(""+plRemoveSongs.getValue(ACTION_DESC));
		        remove.addActionListener(plRemoveSongs);
		        popup.add(remove);
		        
		        JMenuItem dl = new JMenuItem(""+download.getValue(ACTION_DESC));
		        dl.addActionListener(download);
		        popup.add(dl);
		        
		        		        
				popup.show(e.getComponent(), e.getX(), e.getY());
		    }
		});
		
		qtable.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        EventList el = getVisibleEventList();
		        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			        plPlay.actionPerformed(blankAction);
				}
			    if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 1
						&& qtable.getColumnName(qtable.columnAtPoint(e.getPoint())) == "Title"
						&& el.get(qtable.rowAtPoint(e.getPoint())) == playingSong) {
					showElapsed = (showElapsed ? false : true);
					updateTables.actionPerformed(null);
				}
		    }
		});
		
		qtable.setFocusTraversalKeysEnabled(false);
	    qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
        qtable.getActionMap().put("Ctrl-Tab", switchTabs);
        qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0, false), "D");
        qtable.getActionMap().put("D", download);
        qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0, false), "Enter");
        qtable.getActionMap().put("Enter", plPlay);
        qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0, false), "Right");
        qtable.getActionMap().put("Right", playNext);
        qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0, false), "Left");
        qtable.getActionMap().put("Left", playPrevious);
        qtable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0, false), "Delete");
        qtable.getActionMap().put("Delete", plRemoveSongs);
        qtable.addFocusListener(focus);
		return qtable;
	}
	
	public JTable createDownloader() {
	    // create the download table
//		dls = new DownloadTableModel();
		dltable = new JTable();
		dltable.setModel(gopher.tableModel);
		dltable.setSelectionModel(gopher.selectionModel);
		dltable.setShowHorizontalLines(false);
		dltable.setGridColor(new Color(200, 200, 200));
		newDownloadRenderer dlrender = new newDownloadRenderer();
		for (int i = 0; i < 4; i++) {
			TableColumn column = dltable.getColumnModel().getColumn(i);
			column.setCellRenderer(dlrender);
			if (i == 0) {
				column.setPreferredWidth(25);
				column.setMaxWidth(40);
			} else if (i == 1) {
				column.setPreferredWidth(180);
				//column.setMaxWidth(200);
			} else if (i == 2) {
			    column.setPreferredWidth(50);
			    column.setMaxWidth(60);
			} else if (i == 3){
				column.setPreferredWidth(200);
			} else
			    column.setMaxWidth(80);
		}

		dltable.addMouseListener(new PopupListener() {
		    public void showPopup(MouseEvent e) {
		        JPopupMenu dPopup = new JPopupMenu();
				JMenuItem dremoveme = new JMenuItem(dremove.getValue(ACTION_DESC).toString());
				dremoveme.addActionListener(dremove);
				dPopup.add(dremoveme);
				
				JMenuItem dum = new JMenuItem(dremoveFinished.getValue(ACTION_DESC).toString());
				dum.addActionListener(dremoveFinished);
				dPopup.add(dum);
		        
				JMenuItem retry = new JMenuItem(ddeleteRetry.getValue(ACTION_DESC).toString());
				retry.addActionListener(ddeleteRetry);
				dPopup.add(retry);
				
				dPopup.show(e.getComponent(), e.getX(), e.getY());
		    }
		});
		
		dltable.setFocusTraversalKeysEnabled(false);
	    dltable.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                        KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
        dltable.getActionMap().put("Ctrl-Tab", switchTabs);
        dltable.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D,
                        0, false), "D");
        dltable.getActionMap().put("D", download);
        dltable.addFocusListener(focus);
		return dltable;
	}

	public JTable createPlaylist() {
		// create the playlist table
		pltable = new DraggableTable();
		pltable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pltable.setDragEnabled(false);
		pltable.setColumnSelectionAllowed(false);
		pltable.setRowSelectionAllowed(true);
		pltable.setShowHorizontalLines(false);
		pltable.setGridColor(new Color(200, 200, 200));
		pltable.setFocusTraversalKeysEnabled(false);

		//InputMap inpt = pltable.getInputMap();
		//    inpt.setParent(tabs.getInputMap());
		pltable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(
						KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK,
								false), "Ctrl-Tab");
		pltable.getActionMap().put("Ctrl-Tab", switchTabs);
		pltable.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "Enter");
//		pltable.getActionMap().put("Enter", playlistPlay);
		pltable.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "Delete");
		pltable.getActionMap().put("Delete", removee);
		pltable.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "Space");
		pltable.getActionMap().put("Space", pausePlay);
		pltable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false),
				"D");
		pltable.getActionMap().put("D", download);
		pltable.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "Left");
		pltable.getActionMap().put("Left", plPlayPrevious);
		pltable.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "Right");
		pltable.getActionMap().put("Right", plPlayNext);
		//pltable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
		// KeyEvent.CTRL_DOWN_MASK, false), "Ctrl-Tab");
		//    table.getActionMap().put("Ctrl-Tab", switchTabs);

		for (int i = 0; i < 4; i++) {
			TableColumn column = pltable.getColumnModel().getColumn(i);
			column.setCellRenderer(browserRender);
			switch (i) {
				case 0:
					column.setMaxWidth(40);
					column.setPreferredWidth(20);
					break;
				case 1:
					column.setPreferredWidth(100);
					//column.setMaxWidth(400);
					break;
				case 2:
					column.setPreferredWidth(300);
					//column.setMaxWidth(500);
					column.setCellEditor(new SeekEditor());
					break;
				case 3:
					column.setPreferredWidth(45);
					column.setMaxWidth(45);
					//column.setCellRenderer(new PlaylistTimeRenderer());
					break;
			}
		}

//		System.out.println("<<< Mouse Listeners:");
		MouseListener[] listeners         = pltable.getMouseListeners();
		for (int k = 0; k < listeners.length; k++) {
//			System.out.println("\t" + listeners[k].toString());
			if (listeners[k].toString().indexOf("UI") != -1 && listeners[k].toString().indexOf("DragGesture") == -1) {
				pltable.removeMouseListener(listeners[k]);
			}
		}
//		System.out.println("<<< Mouse Motion Listeners:");
		MouseMotionListener[] mlisteners  = pltable.getMouseMotionListeners();
		for (int k = 0; k < mlisteners.length; k++) {
//			System.out.println("\t" + mlisteners[k].toString());
			if (mlisteners[k].toString().indexOf("UI") != -1 && mlisteners[k].toString().indexOf("DragGesture") == -1) {
				pltable.removeMouseMotionListener(mlisteners[k]);
			}
		}
//		System.out.println("After Delete:");
//		System.out.println("<<< Mouse Listeners:");
		listeners = pltable.getMouseListeners();
		for (int k = 0; k < listeners.length; k++) {
//			System.out.println("\t" + listeners[k].toString());
		}
//		System.out.println("<<< Mouse Motion Listeners:");
		mlisteners = pltable.getMouseMotionListeners();
		for (int k = 0; k < mlisteners.length; k++) {
//			System.out.println("\t" + mlisteners[k].toString());
		}
		
		pltable.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point pt = e.getPoint();
				if (pltable.isEditing()
//						&& pltable.rowAtPoint(pt) == pls.indexOf(playingSong)
						&& pltable.getColumnName(pltable.columnAtPoint(pt)) == "Title") {
					Component editorComponent = pltable.getEditorComponent();
					Point p = e.getPoint();
					Point p2 = SwingUtilities.convertPoint(pltable, p, editorComponent);
					//Component dispatchComponent =
					// SwingUtilities.getDeepestComponentAt(editorComponent,
					//                                                     p2.x, p2.y);
					MouseEvent e2 = SwingUtilities.convertMouseEvent((Component) pltable,
							e, editorComponent);
					editorComponent.dispatchEvent(e2);
				}
			}
		});

		pltable.addMouseListener(new MouseAdapter() {
			protected boolean	selectedOnPress;

			public void mousePressed(MouseEvent e) {
				pltable.requestFocus();
				int row = pltable.rowAtPoint(e.getPoint());
				ListSelectionModel ls = pltable.getSelectionModel();
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if (pltable.isRowSelected(row)) {
					if (e.isShiftDown()) {
						pltable.changeSelection(row, 0, e.isControlDown(), e.isShiftDown());
						selectedOnPress = true;
						return;
					} else {
						selectedOnPress = false;
						ls.setAnchorSelectionIndex(row);
					}
				} else {
					if (e.isControlDown() && e.isShiftDown()) {
						System.out.println("Ctrl Shift Click!");
						pltable.addRowSelectionInterval(ls.getAnchorSelectionIndex(), row);
					}
					if ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK) {
						System.out.println("Shift Click!");
					}
					if ((e.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) == InputEvent.CTRL_DOWN_MASK) {
						System.out.println("Ctrl Click!");
					}
					pltable.changeSelection(row, 0, e.isControlDown(), e.isShiftDown());
					selectedOnPress = true;
				}

			}

			public void mouseReleased(MouseEvent e) {
				Point pt = e.getPoint();
				if (pltable.isEditing()
//						&& pltable.rowAtPoint(pt) == pls.indexOf(playingSong)
						&& pltable.getColumnName(pltable.columnAtPoint(pt)) == "Title") {
					pltable.getEditorComponent().dispatchEvent(e);
				}
				if (e.getClickCount() == 2) {
//					Song a = pls.getSongAt(pltable.getSelectedRow());
//					playSong(a);
				}
				if (e.getClickCount() == 1) {
					int row = pltable.rowAtPoint(e.getPoint());
					ListSelectionModel ls = pltable.getSelectionModel();

					if (selectedOnPress) {
						return;
					} else {
						pltable.changeSelection(row, 0, e.isControlDown(), e.isShiftDown());
					}
				}
			}

			public void mouseClicked(MouseEvent e) {
//				Point pt = e.getPoint();
//				if (e.getClickCount() == 1
//						&& pltable.getColumnName(pltable.columnAtPoint(pt)) == "Title"
//						&& pltable.rowAtPoint(pt) == pls.indexOf(playingSong)) {
//					showElapsed = (showElapsed ? false : true);
//					pls.fireTableCellUpdated(pltable.rowAtPoint(pt), pltable
//							.columnAtPoint(pt));
//				}
			}
		});

		JPopupMenu pPopup = new JPopupMenu();
		JMenuItem removeme = new JMenuItem("Remove Song(s)");
		removeme.addActionListener(removee);
		pPopup.add(removeme);
		JMenuItem play = new JMenuItem("Play Song(s)");
//		play.addActionListener(playlistPlay);
		pPopup.add(play);
		JMenuItem downloadthis = new JMenuItem("Download Song(s)");
		downloadthis.addActionListener(download);
		pPopup.add(downloadthis);
		pltable.addMouseListener(new PopupListener(pPopup));

//		JScrollPane scrollPane = new JScrollPane(pltable);

		// Ctrl-tab won't work without these:
		pltable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				revNewKeys);
		pltable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newKeys);

		return pltable;
	}

	public void createDisplay() {
		// create the display bar:
		dspanel = new JPanel();
		dspanel.setLayout(new BoxLayout(dspanel, BoxLayout.X_AXIS));
		dspanel.setPreferredSize(new Dimension(10, 50));
		dspanel.setMaximumSize(new Dimension(9000, 60));

		dspanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK, false), "C-m");
        dspanel.getActionMap().put("C-m", toggleMini);
		
		// create the central display:
		center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		dspmodel = new DisplayTableModel(this);

		displayTable2 = new JTable();
		displayTable2.setModel(dspmodel);
		TableColumn column = displayTable2.getColumnModel().getColumn(0);
		column.setCellRenderer(new TopDisplayRenderer());
		displayTable2.setDefaultEditor(Object.class, null);
		displayTable2.getColumnModel().setColumnMargin(0);
		displayTable2.setColumnSelectionAllowed(false);
		displayTable2.setRowSelectionAllowed(false);
		displayTable2.setCellSelectionEnabled(false);
		displayTable2.setShowGrid(false);
		displayTable2.setRowHeight(20);
		displayTable2.setMaximumSize(new Dimension(5000, 20));
		displayTable2.setMinimumSize(new Dimension(100, 20));
		displayTable2.setPreferredSize(new Dimension(500, 20));
		Font f = displayTable2.getFont();
		displayTable2.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 3));
		displayTable2.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (displayValue == 2) {
						displayValue = 0;
					} else {
						displayValue++;
					}

					displayTable2.repaint();
				} else if (e.getButton() == MouseEvent.BUTTON3){
					locatePlayingSong(true);
				}
			}
		});
		center.add(displayTable2);

		displayTable = new JTable();
		displayTable.setModel(dspmodel);
		column = displayTable.getColumnModel().getColumn(0);
		column.setCellEditor(new DisplayEditor());
		column.setCellRenderer(new DisplayRenderer());
		displayTable.getColumnModel().setColumnMargin(0);
		displayTable.setColumnSelectionAllowed(false);
		displayTable.setRowSelectionAllowed(false);
		displayTable.setShowGrid(false);
		displayTable.setMaximumSize(new Dimension(5000, 20));
		displayTable.setMinimumSize(new Dimension(100, 20));
		displayTable.setPreferredSize(new Dimension(500, 20));
		displayTable.setRowHeight(20);
		displayTable.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        if (e.getButton() == MouseEvent.BUTTON3) {
		            locatePlayingSong(true);
		        }
		    }
		    
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
					showElapsed = (showElapsed ? false : true);
					updateTables.actionPerformed(null);
					e.consume();
				}
			}
		});
		center.add(displayTable);
		center.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// create the buttons:
		buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		Dimension size = new Dimension(splitPane.getDividerLocation(), 50);
		buttons.setPreferredSize(size);
		buttons.setMinimumSize(size);
		buttons.setMaximumSize(size);
		JPanel revPanel = new JPanel();
		revPanel.setLayout(new BoxLayout(revPanel, BoxLayout.Y_AXIS));
		JPanel fwdPanel = new JPanel();
		fwdPanel.setLayout(new BoxLayout(fwdPanel, BoxLayout.Y_AXIS));
		MouseAdapter pp = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					pausePlay.actionPerformed(blankAction);
				}
			}
		};

		playButton = createIcon("play");
		playButton.addMouseListener(pp);
		playButton.setToolTipText("Play song");
		pauseButton = createIcon("pause");
		pauseButton.addMouseListener(pp);
		pauseButton.setToolTipText("Pause song");
		JLabel rev = createIcon("left");
		rev.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					queue.playPrevious();
				}
			}
		});
		rev.setToolTipText("Previous song");
		JLabel dl = createIcon("dl");
		dl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					download.actionPerformed(blankAction);
				}
			}
		});
		dl.setToolTipText("Download to file");
		revPanel.add(rev);
		revPanel.add(dl);
		JLabel fwd = createIcon("right");
		fwd.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//playNext.actionPerformed(blankAction);
				if (e.getButton() == MouseEvent.BUTTON1) {
					queue.playNext();
				}
			}
		});
		fwd.setToolTipText("Next song");
		JLabel pl = createIcon("pl");
		pl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					enqueueSongs.actionPerformed(blankAction);
				}
			}
		});
		pl.setToolTipText("Add to playlist");
		fwdPanel.add(fwd);
		fwdPanel.add(pl);

		buttons.add(Box.createGlue());
		buttons.add(revPanel);
		buttons.add(playButton);
		buttons.add(fwdPanel);
		buttons.add(Box.createGlue());
		dspanel.add(buttons);

		// right display labels:
		fpanel = new JPanel();
		fpanel.setLayout(new GridLayout(2, 2));
		// shuffle label:
		JLabel sl = new JLabel("<HTML><B>Shuffle : </B>");
		sl.setHorizontalAlignment(JLabel.RIGHT);
		fpanel.add(sl);
		shuffleLabel = createLabel("", changeShuffle, "");
		shuffleLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
		shuffleLabel.setHorizontalAlignment(JLabel.LEFT);
		fpanel.add(shuffleLabel);

		// volume progress bar:
		volbar = new JProgressBar(0, 100);
		volbar.setStringPainted(true);
		volbar.setString("");
		volbar.setForeground(new Color(120, 120, 160));
		volbar.setPreferredSize(new Dimension(50, 12));
		volbar.setMaximumSize(new Dimension(50, 12));
		volbar.setMinimumSize(new Dimension(50, 12));
		volbar.setAlignmentY(Component.CENTER_ALIGNMENT);
		MouseInputAdapter mouseVol = new MouseInputAdapter() {
			public int	WIDTH	= 50;

			public int setValue(MouseEvent e) {
				JProgressBar slider = (JProgressBar) e.getComponent();
				int i = WIDTH;
				double percent = ((double) e.getX() / (double) i);
				if (percent > 1) {
					percent = 1;
				}
				if (percent < 0) {
					percent = 0;
				}

				int val = (int) (percent * slider.getMaximum());
				slider.setValue(val);
				slider.repaint();
				GITProperties.playerVolume = val;
				try {
					player.setVolume(percent);
				} catch (Exception f) {
					f.printStackTrace();
				}
				return val;
			}

			public void mousePressed(MouseEvent e) {
				setValue(e);
			}

			public void mouseDragged(MouseEvent e) {
				setValue(e);
			}

			public void mouseReleased(MouseEvent e) {
			}
		};
		volbar.addMouseListener(mouseVol);
		volbar.addMouseMotionListener(mouseVol);
		volbar.setValue(GITProperties.playerVolume);
		JLabel vl = new JLabel("<HTML><B>Volume : </B>");
		vl.setHorizontalAlignment(JLabel.RIGHT);
		fpanel.add(vl);
		fpanel.add(volbar);
		//fpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		fpanel.setMinimumSize(fpanel.getPreferredSize());
		fpanel.setMaximumSize(fpanel.getPreferredSize());

		// left display labels:
		gpanel = new JPanel();
		gpanel.setLayout(new GridLayout(2,4));

		// mini label:
		ml = new JLabel("<HTML><B>Ctrl-M : </B>");
		ml.setHorizontalAlignment(JLabel.RIGHT);
		gpanel.add(ml);
		miniLabel = createLabel("Toggle Mini", toggleMini, "");
		miniLabel.setToolTipText("Toggle between full size and mini modes");
		miniLabel.setAlignmentX(Box.RIGHT_ALIGNMENT);
		miniLabel.setHorizontalAlignment(JLabel.LEFT);
		gpanel.add(miniLabel);

		// help label:
		JLabel hl = new JLabel("<HTML><B>F1 : </B>");
		hl.setHorizontalAlignment(JLabel.RIGHT);
		gpanel.add(hl);
		JLabel helpLabel = createLabel("Show Help", toggleHelp, "");
		helpLabel.setAlignmentX(Box.RIGHT_ALIGNMENT);
		helpLabel.setHorizontalAlignment(JLabel.LEFT);
		gpanel.add(helpLabel);

		//gpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		gpanel.setMaximumSize(gpanel.getPreferredSize());
		gpanel.setMinimumSize(gpanel.getPreferredSize());

		// add stuff to display panel:
		Dimension d = new Dimension(5, 0);
		dspanel.add(Box.createGlue());
		dspanel.add(gpanel);
		dspanel.add(Box.createRigidArea(d));

		dspanel.add(center);
		box = Box.createRigidArea(d);
		dspanel.add(box);
		dspanel.add(fpanel);
		dspanel.add(Box.createGlue());

	}

	public void setFocusToSearch() {
		Runnable doSearchThing = new Runnable() {
			public void run() {
				getVisibleCard().search_field.requestFocus();
			}
		};
		SwingUtilities.invokeLater(doSearchThing);
	}

	public void setFocusToTable() {
		Runnable doFocusThing = new Runnable() {
			public void run() {
				if (miniPlay) {
					dspanel.requestFocus();
				}
				getVisibleCard().table.requestFocus();
			}
		};
		SwingUtilities.invokeLater(doFocusThing);
	}

	public void createHelpPanel() {
	    bottomPanel = new JPanel();
	    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
	    bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	    bottomPanel.setMinimumSize(new Dimension(0, 100));
	    bottomPanel.setMaximumSize(new Dimension(200000, 130));
	    bottomPanel.setPreferredSize(new Dimension(200, 130));
	    
	    // CREATE THE INFO TEMPLATE:
	    JPanel help = new JPanel();
	    help.setLayout(new BorderLayout());
	    help.setBorder(BorderFactory.createTitledBorder("Help"));
	    String text = "";
	    
	    
	    helpLabel = new JLabel(text);
        help.add(helpLabel);
	    help.setPreferredSize(new Dimension(400, 75));
	    
	    // CREATE THE KEYS TEMPLATE:
	    JPanel localKeys = new JPanel(new GridLayout(0, 8));
	    localKeys.setBorder(BorderFactory.createTitledBorder("Current Commands"));
//	    localKeys.setPreferredSize(new Dimension(400, 50));
	    
	    
//	    bottomPanel.add(consolePanel);
	    bottomPanel.add(help);
	    bottomPanel.add(localKeys);
	}
	
	public ImageIcon getFileFormatIcon(Song s) {
	    if (fileFormatIcons == null)
	        fileFormatIcons = new HashMap();
	    
	    if (fileFormatIcons.get(s.getFormat()) == null) {
	        // create the icon and store it in the hash.
	        System.out.println("Creating file format icon for image type: "+s.getFormat());
	        BufferedImage bi = new BufferedImage(80,80,BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2 = (Graphics2D)bi.createGraphics();
	        g2.setColor(Color.WHITE);
	        g2.fillRect(0,0,bi.getHeight(),bi.getWidth());
	        Font f = g2.getFont().deriveFont(Font.BOLD,14);
	        boolean tooBig = false;
	        Rectangle2D bounds = null;
	        float textHeight = 0;
	        float textSink = 0;
	        while (!tooBig) {
	            System.out.println("size: "+f.getSize2D());
	            FontMetrics fm = g2.getFontMetrics(f);
	            LineMetrics lm = fm.getLineMetrics(s.getFormat(),g2);
	            bounds = f.getStringBounds(s.getFormat(),g2.getFontRenderContext());
//	            bounds = fm.getStringBounds(s.getFormat(),g2);
	            float width = (float)bounds.getWidth();
	            float height = (float)bounds.getHeight();
	            if (width > bi.getWidth() || height > bi.getHeight()) {
	                tooBig = true;
	                f = f.deriveFont((float)(f.getSize2D()));
	                lm = f.getLineMetrics(s.getFormat(),g2.getFontRenderContext());
	                textHeight = lm.getAscent() + lm.getDescent();
	                textSink = lm.getDescent();	                
//	                bounds = f.getStringBounds(s.getFormat(),g2.getFontRenderContext());
	                break;
	            }
	            f = f.deriveFont(f.getSize2D()+2);
	        }
	        g2.setFont(f);
	        g2.setColor(new Color(200,200,200,75));
	        TextLayout tl = new TextLayout(s.getFormat(),f,g2.getFontRenderContext());
	        bounds = tl.getBounds();
//	        int i= tl.getCharacterCount();
//	        Shape shape = tl.getBlackBoxBounds(0,i);
//	        bounds = shape.getBounds2D();
	        float x = (float)((double)bi.getWidth() - bounds.getWidth()) / 2;
	        float y = (float)((double)bi.getHeight() + textHeight)/2;
//	        tl.draw(g2, x,y);
	        g2.drawString(s.getFormat(),x,y - textSink);
//	        g2.drawOval((int)x,(int)y,4,4);
//	        g2.drawOval((int)x,(int)(y-bounds.getHeight()),4,4);
	        ImageIcon icon = new ImageIcon(bi);
	        fileFormatIcons.put(s.getFormat(),icon);
	        return icon;
	    } else {
	        return (ImageIcon)fileFormatIcons.get(s.getFormat());
	    }
	}
	
	public void updateSongInfoPanel(final GlazedGITSongJPanel p ) {
	    new SwingWorker() {
            public Object construct() {
                EventList el = p.visible;
                int row = p.table.getSelectionModel().getLeadSelectionIndex();
                if (row < 0) row = 0;
                if (el.size() == 0)
                    return null;
                Song s = (Song)el.get(row);
                SongInfoPanel sip = p.info_view;
                sip.title.setText(s.name);
                sip.artist.setText(s.artist);
                sip.album.setText(s.album);
                sip.genre.setText(s.genre);
                sip.bitrate.setText(String.valueOf(s.bitrate));
                sip.length.setText(GITUtils.getFormattedTime(s.time));
                sip.size.setText(GITUtils.getFormattedSize(s.size));
                sip.track.setText(String.valueOf(s.track));
                
                if (s instanceof LocalSong) {
                    LocalSong ls = (LocalSong)s;
                    if (ls.getPhoto() != null && ls.getPhoto().getImageLoadStatus() != MediaTracker.ERRORED) {
                        p.info_view.artwork.setText("");
                        p.info_view.artwork.setIcon(ls.getPhoto());
                        return null;
                    } else {
                        GITLibraryHost.findPhotoInID3(ls);
                        if (ls.getPhoto() != null && ls.getPhoto().getImageLoadStatus() != MediaTracker.ERRORED) {
                            p.info_view.artwork.setText("");
                            p.info_view.artwork.setIcon(ls.getPhoto());
                            return null;
                        }
                    }
                    sip.right_panel.remove(0);
                    JLabel exploreMe = createLabel("Show File",
                            songExploreAction,
                            ls.getPath());
                    exploreMe.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                    exploreMe.setIcon(magnifier);
                    sip.right_panel.add(exploreMe,0);
                }
                p.info_view.artwork.setIcon(getFileFormatIcon(s));
                return null;
            }
        }.start();
	}
	
	public void updateHelpPanel(JComponent f) {
	    
	    String text;
	    switch(views.getSelectedIndex()) {
    		case 0:
    		    text = "<html><b>Browser:</b>" +
	    	    " This is the main view of GIT.  Click on the name of a host to your left, " +
	    	    "and it will show up in the browser; click on the puzzle icon to show or hide " +
	    	    "the host.  Right-clicking usually brings up a context menu.  Try right-clicking " +
	    	    "on the Song Display Bar at the bottom to locate the currently playing song!";
    		    break;
    		case 1:
    		    text = "<html><b>Queue:</b>" +
    		    " This is quere you can quickly queue up some songs to play... it's kind of like "+
    		    "a playlist, it's just always there.  If Shuffle mode is set to OFF, then once the "+
    		    "end of the queue is reached, songs from the Browser will continue to play.";
    		    break;
		    case 2:
		        text = "<html><b>Downloads:</b>" + 
		        " This is where your downloads show up.  Right-click on a song to bring up a context menu.";
		        break;
	        case 3:
	            text = "<html><b>Settings:</b>" +
	            " This is where you change the preferences for GIT.  Preferences are automatically" +
	            "saved and loaded when GIT starts up and closes.";
	            break;
	    	default:
	    	    text = "Welcome to Get It Together.";
	    	    break;
        }
	    helpLabel.setText(text);
	    
	    JPanel p = (JPanel)bottomPanel.getComponent(1);
	    p.removeAll();
	    
	    InputMap inp = f.getInputMap();
		ActionMap act = f.getActionMap();
		
		KeyStroke[] keys = inp.keys();
		
		if (keys == null)
		{
		    p.add(new JLabel("<HTML><B>None.</B>"));
		    bottomPanel.revalidate();
		    return;
	}

		for (int i=0; i < keys.length; i++) {
		    String keyCommand  = KeyStrokeUtils.keyStroke2String(keys[i]);
		    while (inp.get(keys[i]) == null)
		        inp = inp.getParent();
	    
		    Action a = act.get(inp.get(keys[i]));
		    
		    JLabel cmdLabel = new JLabel("<HTML><B>" + keyCommand + " : </B>");
		    cmdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		    String str = "--nothing--";
		    if (a != null)  {
//	        try { // Getting rid of overhead of throwing a useless exception
	            Object o = a.getValue(ACTION_DESC);
	            if (o != null) {
	    		        str = o.toString();
 
	            }
	    }
//	        } catch (Exception ex) {}
	    /*if (str == null) // can't get here since str is initialized above...
	        str = "----"; */
		    JLabel actionLabel = createLabel(str, a, "");
		    actionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		    p.add(cmdLabel);
		    p.add(actionLabel);
		    
	}
		bottomPanel.revalidate();
		bottomPanel.repaint();
	}
	
	public void createActions() {
	// Note to self: Actions should not have built-in SwingWorkers.  That should be dealt
	//	with by the calling method, or I may figure out a way to dynamically
	//	instantiate SwingWorkers...

		toggleMini = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!miniPlay) {
				    getVisibleCard().table.clearSelection();
				    GITUtils.callAlwaysOnTop(frame, GITProperties.alwaysOnTop);
					GITProperties.bigPos = frame.getLocationOnScreen();
//					System.out.println(e.getSource());
					int tab = main.getHeight();
//					int tab = tabs.getHeight();
					main.setVisible(false);
					GITProperties.bigSize = frame.getSize();
					for (int i = 0; i < dspanel.getComponentCount(); i++) {
						dspanel.getComponent(i).setVisible(false);
					}
					fpanel.setLayout(new GridLayout(3, 2));
					fpanel.add(ml, 0);
					fpanel.add(miniLabel, 1);
					fpanel.validate();
					fpanel.setMaximumSize(fpanel.getPreferredSize());
					fpanel.setMinimumSize(fpanel.getPreferredSize());
					fpanel.setVisible(true);
					buttons.setVisible(true);
					buttons.setPreferredSize(new Dimension(110, 50));
					buttons.setMaximumSize(new Dimension(110, 50));
					buttons.setMinimumSize(new Dimension(110, 50));
					buttons.setSize(new Dimension(110, 50));
					center.setVisible(true);
					center.setPreferredSize(new Dimension(150, 50));
					box.setVisible(true);
					frame.validate();
					dspanel.validate();
					Insets g = frame.getInsets();
					int height = dspanel.getMinimumSize().height + g.top + g.bottom;
					if (GITProperties.miniWidth > 0) {
						frame.setSize(GITProperties.miniWidth, height);
					} else {
						frame.setSize(400, height);
					}
					if (GITProperties.miniPos == null) {
						Point p = frame.getLocationOnScreen();
						frame.setLocation(p.x, p.y + tab);
						GITProperties.miniPos = frame.getLocation();
					} else {
						frame.setLocation(GITProperties.miniPos);
					}
					miniPlay = true;
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {
					        dspanel.requestFocus();
					    }
					});
				} else {
					GITUtils.callAlwaysOnTop(frame, false);
					GITProperties.miniPos = frame.getLocationOnScreen();
					fpanel.setLayout(new GridLayout(2, 2));
					fpanel.remove(ml);
					fpanel.remove(miniLabel);
					fpanel.validate();
					fpanel.setMaximumSize(fpanel.getPreferredSize());
					gpanel.add(ml, 0);
					gpanel.add(miniLabel, 1);
					gpanel.validate();
					frame.setLocation(GITProperties.bigPos);
					main.setVisible(true);
					for (int i = 0; i < dspanel.getComponentCount(); i++) {
						dspanel.getComponent(i).setVisible(true);
					}
					center.setPreferredSize(new Dimension(450, 50));
					fixDisplay();
					GITProperties.miniWidth = frame.getWidth();
					frame.setSize(GITProperties.bigSize);
					//						GITProperties.bigSize = null;
					miniPlay = false;
				}
			}
		};
		toggleMini
				.putValue(ACTION_DESC, "Change shuffle mode");

		changeShuffle = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (GITProperties.shuffleValue == RANDOM_PLAYLIST
						|| GITProperties.shuffleValue < RANDOM_OFF) {
					GITProperties.shuffleValue = RANDOM_OFF;
				} else {
					GITProperties.shuffleValue++;
				}
				shuffleLabel.setText(shuffleStrings[GITProperties.shuffleValue]);
				shuffleLabel.setToolTipText(shuffleToolTips[GITProperties.shuffleValue]);
			}
		};
		changeShuffle.putValue(ACTION_DESC,
				"Change shuffle mode");

		chooseDirectory = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				int result = fileChooser.showOpenDialog(frame);
				switch (result) {
					case JFileChooser.APPROVE_OPTION:
						File file = fileChooser.getSelectedFile();
						GITProperties.dlDir = file.getAbsolutePath();
//						dirField.setText(file.getAbsolutePath());
						break;
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.ERROR_OPTION:
						JOptionPane.showMessageDialog(frame, "Error choosing File.");
						break;
				}
			}
		};
		chooseDirectory.putValue(ACTION_DESC,
				"Choose download location");

		browseHost =
			new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
				    nodeClicked(getSelectedNode());
				}
			};
		browseHost.putValue(ACTION_DESC, "Show/hide host");

		toggleHelp = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (hide_help == false) {
					frame.setSize(frame.getWidth(), frame.getHeight() 
					        - bottomPanel.getSize().height);
				    bottomPanel.setVisible(false);
					hide_help = true;
				} else if (hide_help == true) {
				    
				    frame.setSize(frame.getWidth(), frame.getHeight()
				            + bottomPanel.getPreferredSize().height);
				    bottomPanel.setVisible(true);
				    hide_help = false;
				}
			}
		};
		toggleHelp.putValue(ACTION_DESC, "Toggle help");

		playNext = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				queue.playNext();
			}
		};
		playNext.putValue(ACTION_DESC, "Play next");

		playPrevious = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    queue.playPrevious();
			}
		};
		playPrevious.putValue(ACTION_DESC, "Play previous");
		
		switchTabs = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println(e.getSource());
			    int row = views.getSelectedIndex();
				if (row == views.getComponentCount() - 1) {
					row = -1;
				}
				views.setSelectedIndex(row + 1);
				setFocusToTable.actionPerformed(null);
//				System.out.println("Switch tabs!");
			}
		};
		switchTabs.putValue(ACTION_DESC, "Tab Cycle");
		
		revSwitchTabs = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int row = views.getSelectedIndex();
				if (row == 0) {
					views.setSelectedIndex(views.getComponentCount());
				}
				views.setSelectedIndex(row - 1);
				setFocusToTable();

			}
		};
		revSwitchTabs.putValue(ACTION_DESC, "Tab Cycle");

		enqueueSongs = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    EventList songs = getCurrentlySelectedSongs();
			    EventList copiedSongs = new BasicEventList();
			    Iterator i = songs.iterator();
			    while (i.hasNext()) {
			        Song orig = (Song)i.next();
			        try {
                        Song copy = (Song)orig.clone();
                        copiedSongs.add(copy);
                    } catch (CloneNotSupportedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
			    }
			    queue.enqueueSongs(copiedSongs);
			}
		};
		enqueueSongs.putValue(ACTION_DESC, "Enqueue song(s)");

		plRemoveSongs = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        EventSelectionModel model = (EventSelectionModel)qtable.getSelectionModel();
		        EventList queue = QueuePlaylist.queue;
		        int ind = model.getLeadSelectionIndex();
		        ind = model.getMinSelectionIndex();
		        queue.getReadWriteLock().writeLock().lock();
		        EventList selected = model.getSelected();
		        int size = selected.size();
		        queue.removeAll(model.getSelected());
		        queue.getReadWriteLock().writeLock().unlock();
		        if (ind >= queue.size())
		            ind = queue.size() - 1;
		        if (!queue.isEmpty())
		            model.setSelectionInterval(ind,ind);
		    }
		};
		plRemoveSongs.putValue(ACTION_DESC, "Remove song(s)");
		
		plPlay = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        EventList songs = queue.selectionModel.getSelected();
		        if (songs.size() == 0) {
		            queue.playSong((Song)QueuePlaylist.queue.get(0));
		        }
		        queue.playSong((Song)songs.get(0));
		    }
		};
		plPlay.putValue(ACTION_DESC, "Play selected song");
		
		pausePlay = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//					if (p != null) {
				switch (player.getStatus()) {
					case AbstractPlayer.PLAYING:
						pause();
						break;
					case AbstractPlayer.PAUSED:
						resume();
						break;
					case AbstractPlayer.STOPPED:
						clearPlay.actionPerformed(null);
						break;
					default:
						System.err.println("player must be loading or something...");
					
				}
			}
		};
		pausePlay.putValue(ACTION_DESC, "Pause / resume");

		clearPlay = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        queue.clearQueueAndPlay(getCurrentlySelectedSongs());
		    }
		};
		clearPlay.putValue(ACTION_DESC, "Play song(s)");

		reconnectHost = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        GITNode node = getSelectedNode();
		        if (node instanceof HostNode) {
		            HostNode hnode = (HostNode)node;
		            disconnectHostNode(hnode);
		            try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
		            connectHostNode(hnode);
		        }
		    }
		};
		reconnectHost.putValue(ACTION_DESC, "Reconnect Host");
		
		disconnectHost = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        disconnectSelectedHost();
		    }
		};
		disconnectHost.putValue(ACTION_DESC, "Disconnect Host");
				
		connectHost = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        connectHostNode((HostNode)getSelectedNode());
		    }
		};
		connectHost.putValue(ACTION_DESC, "Connect Host");
		
		hostButton = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        final GITNode node = getSelectedNode();
		        if (node == null)
		            return;
		        new SwingWorker()
		        {
		            public Object construct() {
		                if (node.getType() == GITNode.DAAP || node.getType() == GITNode.LOCAL)
		                {
		                    Host host = (Host)node.getUserObject();
		                    if (host.getStatus() >= Host.STATUS_CONNECTED)
		                    {
		                        disconnectHostNode((HostNode)node);
		                    }
		                    else
		                    {
		                        nodeClicked(node);
		                    }
		                }
		                return new Integer(0);
		            }
		        }.start();
		    }
		};
		hostButton.putValue(ACTION_DESC, "Browse Share");

		acceptSearch = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    
			}
		};
		acceptSearch.putValue(ACTION_DESC, "Accept search");

		cancelSearch = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    //TODO: integrate with glazed lists (or nix it!)
			    
			}
		};
		cancelSearch.putValue(ACTION_DESC, "Cancel search");

		openSearch = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				getVisibleCard().search_field.requestFocus();
			}
		};
		openSearch.putValue(ACTION_DESC, "Search");

		download = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
	      Collection c = getCurrentlySelectedSongs();
          if (c.size() == 0 && playingSong != null)
              try {
                  	gopher.addDownload(playingSong.duplicate());
              } catch(CloneNotSupportedException f) {
//                  System.out.println("Clone not supported!");
                  f.printStackTrace();
              }
          else {
              ArrayList a = new ArrayList();
              a.addAll(c);
	          for (int i=0; i<a.size(); i++) {
				try {
				    gopher.addDownload(((Song)a.get(i)).duplicate());
				} catch (CloneNotSupportedException f) {
//					System.out.println("Clone not supported!");
					f.printStackTrace();
				}
	          }
  			}
			}
		};
		download.putValue(ACTION_DESC, "Download songs");

		dremoveFailed = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        gopher.removeFailed();
		    }
		};
		dremoveFailed.putValue(ACTION_DESC,
		        "Remove failed downloads");
		
		dretryFailed = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        gopher.retryFailed();
		    }
		};
		dretryFailed.putValue(ACTION_DESC,
		        "Retry failed downloads");
		
		dremoveFinished = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					gopher.removeFinished();
				}
			};
		dremoveFinished.putValue(ACTION_DESC,
				"Clean up finished downloads");
		
		dremoveAll = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					gopher.removeAll();
				}
			};
			dremoveAll.putValue(ACTION_DESC,
					"Remove all downloads");
			
		dremoveDuplicates = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					gopher.removeDuplicates();
				}
		};
		dremoveDuplicates.putValue(ACTION_DESC,
			"Remove all duplicate downloads");
			
		dremoveCancelled= new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gopher.removeCancelled();
			}
		};
		dremoveCancelled.putValue(ACTION_DESC,
				"Clean up cancelled downloads");
		
		
		dremoveError= new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gopher.removeError();
			}
		};
		dremoveError.putValue(ACTION_DESC,
				"Clean up failed downloads");

		dretryError= new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					gopher.retryError();
				}
			};
		dretryError.putValue(ACTION_DESC,
					"Retry failed downloads");
			
		dretryCancelled= new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    gopher.retryCancelled();
			}
		};
		dretryCancelled.putValue(ACTION_DESC,
				"Restart cancelled downloads");
		
		dremove = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			    Collection c = gopher.selectionModel.getSelected();
			    gopher.removeDownloads(c);
			}
		};
		dremove.putValue(ACTION_DESC,
				"Remove selected downloads");

		ddeleteRetry = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        Collection c = gopher.selectionModel.getSelected();
		        gopher.deleteAndRetry(c);
		    }
		};
		ddeleteRetry.putValue(ACTION_DESC, "Delete file and retry selected");
		
		removee = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Remove selected songs from playlist.
			}
		};
		removee.putValue(ACTION_DESC, "Remove from playlist");

		hideOthers = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Host h = getSelectedHost();
                GITNode parent = (GITNode)getSelectedNode().getParent();
                Enumeration enm = parent.children();
                while (enm.hasMoreElements()) {
                    GITNode node = (GITNode)enm.nextElement();
                    Host cur = (Host)node.getUserObject();
                    if (cur.getStatus() >= Host.STATUS_CONNECTED && cur.isVisible() && !(cur == h))
                        nodeClicked(node);
                }
            }
		};
		hideOthers.putValue(ACTION_DESC, "Hide all other hosts");
		
		connectAll = new AbstractAction() {
		    
            public void actionPerformed(ActionEvent e) {
                // Temporarily turn off connection error messages:
                GITProperties.showConnectionErrorPanels = false;
                GITNode node = getSelectedNode();
    		    Enumeration enm = node.children();
    		    while (enm.hasMoreElements()) {
    		        HostNode hnode = (HostNode)enm.nextElement();
    		        Host cur = (Host)hnode.getUserObject();
    		        if (cur.getStatus() < Host.STATUS_CONNECTED)
    		        {
    		            connectHostNode(hnode);
    		        }
    		        try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
    		    }
    		    // turn messages back on:
    		    GITProperties.showConnectionErrorPanels = true;
            }	};
		connectAll.putValue(ACTION_DESC, "Connect to all hosts");
		
		disconnectAll = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        GITNode node = getSelectedNode();
    		    Enumeration enm = node.children();
    		    while (enm.hasMoreElements()) {
    		        HostNode hnode = (HostNode)enm.nextElement();
    		        Host cur = (Host)hnode.getUserObject();
    		        if (cur.getStatus() >= Host.STATUS_CONNECTED)
    		            disconnectHostNode(hnode);
	    		    try {
	                    Thread.sleep(500);
	                } catch (InterruptedException e1) {
	                    e1.printStackTrace();
	                }
    		    }
		    }
		};
		disconnectAll.putValue(ACTION_DESC, "Disconnect from all hosts");
		
		addNewHost = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        addNewHost();
		    }
		};
		addNewHost.putValue(ACTION_DESC, "Add a new Host");
		
		updateTables = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        // UPDATE THE DISPLAY:
		        dspmodel.fireTableRowsUpdated(0,0);
		        
		        // FIND THE CURRENTLY VISIBLE TABLE:
		        JTable table = getVisibleTable();
                EventTableModel model = (EventTableModel)table.getModel();
                EventList list = getVisibleEventList();
//                list.getReadWriteLock().readLock().lock();
                
                // SEARCH THROUGH THE VISIBLE SONGS TO SEE IF WE HAVE THE PLAYINGSONG:
                Rectangle visible = table.getVisibleRect();
                Point top = visible.getLocation();
                Point bottom = new Point(visible.x+visible.width, visible.y+visible.height);
                int begin = table.rowAtPoint(top);
                int end = table.rowAtPoint(bottom);
                if (end == -1) { // HANDLE WHEN THE TABLE IS SHORTER THAN THE VISIBLE AREA:
                    end = table.getRowCount() - 1;
                }
                if (begin >= 0)
                {
                    list.getReadWriteLock().writeLock().lock();
                    int ind;
                    for (ind=begin; ind <= end; ind++) {
                        if (list.get(ind) == playingSong)
                            break;
                    }
                    list.getReadWriteLock().writeLock().unlock();
                    model.fireTableRowsUpdated(ind, ind);
                }
		    }
		};
		updateTables.putValue(ACTION_DESC, "Update table & display");
		
		setFocusToTable = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		                getFocusableComponentForTab().requestFocusInWindow();
		            }
		        });
		    }
		};
		setFocusToTable.putValue(ACTION_DESC, "Focus to table");
		
		removeLibrary = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new SwingWorker() {
                    public Object construct() {
                        GITNode node = getSelectedNode();
                        disconnectSelectedHost();
                        local.remove(node);
                        GITProperties.removeLibrary((LocalHost)node.getUserObject());
                        return new Integer(0);
                    }
                }.start();
            }
        };
        removeLibrary.putValue(ACTION_DESC, "Remove Library");
		
        removeSongs = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable t = getVisibleTable();
                final EventSelectionModel em = (EventSelectionModel)t.getSelectionModel();
                if (em.isSelectionEmpty())
                    return;
                EventList el = em.getSelected();
                
                ArrayList temp = new ArrayList(50);
                el.getReadWriteLock().writeLock().lock();
                temp.addAll(el);
                el.getReadWriteLock().writeLock().unlock();
                Song s = null;
                LocalSong ls = null;
                int removed = 0;
                for (int i=0; i < el.size(); i++) {
                    s = (Song)el.get(i);
                    if (s instanceof LocalSong) {
                        ls = (LocalSong)s;
                        if (!((LocalHost)ls.getHost()).isWritable()) {
                            removed++;
                        }
                    }
                }
                if (removed == temp.size()) {
                    JOptionPane.showMessageDialog(GetItTogether.instance.frame,
                            "<html><center><font size='+4'><strong>Song Removal</strong></font></center>" +
                            "<ul style=compact><li>None of the selected songs were from a writable host!</li></ul>",
                            "Song Removal",
                            JOptionPane.PLAIN_MESSAGE
                    );
                    return;
                }
                int result = JOptionPane.showConfirmDialog(GetItTogether.instance.frame,
                        "<html><center><font size='+4'><strong>Song Removal</strong></font></center>" +
                        (removed > 0 ? "<strong>("+removed+" songs</strong> were from a read-only host and will <strong>not be affected</strong>)</li><br>" : "")+
                        "<ul style=compact><li>Are you <strong>sure</strong> you want to REMOVE the remaining <strong>"+(el.size()-removed)+" songs</strong> from your library??</li></ul>",
                        "Song Removal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                switch (result) {
                    case JOptionPane.YES_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                    default:
                        return;
                }
                
                int min = em.getMinSelectionIndex();
                // Ok now, remove the currently selected songs!
                temp.clear();
                temp.addAll(el);
                HashSet hosts = new HashSet();
                for (int i=0; i < temp.size(); i++) {
                    s = (Song)temp.get(i);
                    hosts.add(s.getHost());
                }
                Iterator it = hosts.iterator();
                while (it.hasNext()) {
                    Host h = (Host)it.next();
                    h.getSongs().getReadWriteLock().writeLock().lock();
                }
                for (int i=0; i < temp.size(); i++) {
                    s = (Song)temp.get(i);
                    s.getHost().getSongs().remove(s);
                }
                it = hosts.iterator();
                while (it.hasNext()) {
                    Host h = (Host)it.next();
                    h.getSongs().getReadWriteLock().writeLock().unlock();
                }
                if (min >= 0)
                    em.setSelectionInterval(min,min);
            }
        };
        removeSongs.putValue(ACTION_DESC, "Remove song(s) from library");
        
        songInfoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                final GlazedGITSongJPanel p = getVisibleCard();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateSongInfoPanel(p);
                        p.info_view.setVisible(!p.info_view.isVisible());
                        p.split_pane.resetToPreferredSizes();
                        
                        // change the triangle icon.
                        p.info_button.setIcon((p.info_view.isVisible() ? down : up));
                    }
                });
                
            }
        };
        songInfoAction.putValue(ACTION_DESC, "Show Song Info panel");
        
        songExploreAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JLabel l = (JLabel)e.getSource();
                System.out.println(e.getActionCommand());
                File exploreFile = new File(e.getActionCommand());
                File exploreDir = exploreFile.getParentFile();
                if (exploreFile == null && exploreDir == null)
                    return;
                String cmd = "";
                if (GITUtils.isWindowsXP()) {
                    cmd = "explorer /select,\""+exploreFile.getAbsolutePath()+"\"";
                }
                else if (GITUtils.isMacOSX())
                    cmd = "open " + exploreDir.getAbsolutePath();
                else
                    return;
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }                
            }
        };
        songExploreAction.putValue(ACTION_DESC, "Show song in "+(GITUtils.isMacOSX() ? "Finder" : "Explorer"));
        
//        addPodcast = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                String url = JOptionPane.showInputDialog(frame,
//                        "Enter the URL to your desired podcast",
//                        "Add a podcast",
//                        JOptionPane.PLAIN_MESSAGE
//                        );
//                PodcastHost ph = (PodcastHost)podcasts.getUserObject();
//                ph.addPodcast(url);
//            }
//        };
//        addPodcast.putValue(ACTION_DESC, "Add a podcast");
        
        addSongstoGITLibrary = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                GITLibraryHost h = (GITLibraryHost)e.getSource();
            }
        };
        
	}

	protected void killWaitingIcon() {
	    if (waiting != null) {
            waiting.getImage().flush();
            waiting = null;
        }
	    if (arrows != null) {
	        arrows.getImage().flush();
	        arrows = null;
	    }
	}

	protected void resume() {
	  	switchButtons(pauseButton);
			try {
				player.resume();
			} catch (PlayerException e) {
				e.printStackTrace();
				return;
			}
			pause = false;

	}

	public JPanel getHelpPanel() {
	    JPanel help = new JPanel();
	    help.setLayout(new BorderLayout());
	    help.setBorder(BorderFactory.createTitledBorder("Help"));
	    String text = "Hello!";
	    
	    switch(views.getSelectedIndex()) {
	    	default:
	    	    text = "<html><b>Browser:</b><br>" +
	    	    "This is the main view of GIT.  Click on the name of a host to your left, " +
	    	    "and it will show up in the browser; click on the puzzle icon to show or hide " +
	    	    "the host.  Right-clicking usually brings up a context menu.  Try right-clicking " +
	    	    "on the Song Display Bar at the bottom to locate the currently playing song!";
	    	    break;
	    
	    }
	    JLabel helpLabel = new JLabel(text);
	    help.add(helpLabel);
	    return help;
	}
	
	public JLabel createLabel(String text, final Action action, final String optional_message) {
		JLabel a = new JLabel("" + text + "");
		a.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		a.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
//				AbstractAction a = (AbstractAction)GetItTogether.class.getField(action).get(g);
//				a.actionPerformed(blankAction);
//			    } catch (Exception ex) {ex.printStackTrace();}
			    ActionEvent ae = new ActionEvent(e.getSource(),0,optional_message);
			    action.actionPerformed(ae);
			}

			public void mouseEntered(MouseEvent e) {
				JLabel a = (JLabel) e.getComponent();
				a.setForeground(new Color(50, 50, 200));
			}

			public void mouseExited(MouseEvent e) {
				JLabel a = (JLabel) e.getComponent();
				a.setForeground(Color.BLACK);
			}
		});
		a.setHorizontalAlignment(JLabel.CENTER);
		return a;
	}

	public static JLabel createIcon(String base) {
		// System.out.println(base + "_up.png");
		java.net.URL imageURL = GetItTogether.class.getResource("/images/" + base
				+ "_up.png");
		ImageIcon i1 = new ImageIcon(imageURL);
		// System.out.println(base + "_over.png");
		imageURL = GetItTogether.class.getResource("/images/" + base + "_over.png");
		ImageIcon i2 = new ImageIcon(imageURL);
		// System.out.println(base + "_down.png");
		imageURL = GetItTogether.class.getResource("/images/" + base + "_down.png");
		ImageIcon i3 = new ImageIcon(imageURL);
		IconButton a = new IconButton(i1, i2, i3);
		a.setOpaque(false);
		a.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					IconButton b = (IconButton) e.getComponent();
					b.setIcon(b.mouseDown);
				}
			}

			public void mouseEntered(MouseEvent e) {
				IconButton b = (IconButton) e.getComponent();
				if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
					b.setIcon(b.mouseDown);
					return;
				}
				b.setIcon(b.mouseOver);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					IconButton b = (IconButton) e.getComponent();
					b.setIcon(b.mouseUp);
				}
			}

			public void mouseExited(MouseEvent e) {
				IconButton b = (IconButton) e.getComponent();
				b.setIcon(b.mouseUp);
			}

		});
		return a;
	}

	public String createHTML(Song s, int time) {
		String html = "<HTML>";
		html = html.concat("<FONT size=4>");
		html = html.concat("<STRONG>" + s.artist + "</STRONG> - " + s.name);
		String sec_zero = "";
		String sec_left_zero = "";
		int millis = time + resumeOffset;
		int millis_left = s.time - (time + resumeOffset);
		int seconds = millis / 1000;
		int seconds_left = millis_left / 1000;
		int minutes = seconds / 60;
		int minutes_left = seconds_left / 60;
		seconds = seconds % 60;
		seconds_left = seconds_left % 60;
		if (seconds < 10) {
			sec_zero = "0";
		}
		if (seconds_left < 10) {
			sec_left_zero = "0";
		}
		if (!showElapsed) {
			html = html.concat("<P><FONT alignment=\"CENTER\">" + minutes + ":"
					+ sec_zero + seconds + "</FONT>");
		} else {
			html = html.concat("<P><FONT alignment=\"CENTER\">" + minutes_left + ":"
					+ sec_left_zero + seconds_left + "</FONT>");
		}
		return html;
	}

	//	ItemListener interface method for the splash window option
	public void itemStateChanged(ItemEvent e) {
		GITProperties.showSplash = (e.getStateChange() != ItemEvent.SELECTED);
		GITPropertiesPanel.instance.showSplashBox.setSelected(GITProperties.showSplash);
		GITPropertiesPanel.instance.showSplashBox.repaint();
		System.out.println("showSplash = " + GITProperties.showSplash);
	}

	public class newDownloadRenderer extends DefaultTableCellRenderer {

	    public newDownloadRenderer() {
	        super();
	    }
	    
	    public Component getTableCellRendererComponent(JTable table, Object value,
	            boolean is_selected, boolean has_focus, int row, int col) {
	        
	        JLabel lb = (JLabel)super.getTableCellRendererComponent(table, value, is_selected, has_focus, row, col);
	        SongDownload d = (SongDownload)value;
	        if (is_selected) {
				lb.setBackground(new Color(200, 200, 240));
				lb.setForeground(Color.BLACK);
			} else if (row % 2 == 0) {
				lb.setBackground(table.getBackground());
				lb.setForeground(table.getForeground());
			} else {
				lb.setBackground(new Color(232, 232, 255));
				lb.setForeground(table.getForeground());
			}
	        switch (col) {
	        	case 0:
	        	{
	        	    lb.setText(String.valueOf(row));
	        	    lb.setHorizontalAlignment(SwingConstants.RIGHT);
	        	    break;
	        	}
	        	case 1:
	        	{
	        	    
	        	    lb.setText(d.getSong().getArtist() + " - " + d.getSong().getName());
	        	    lb.setHorizontalAlignment(SwingConstants.LEFT);
	        	    break;
	        	}
	        	case 2:
	        	{
	        	    double a = Math.round(d.getSong().getSize() / 104857.6);
					a = a / 10;
					lb.setText(Double.toString(a) + " Mb");
					lb.setHorizontalAlignment(SwingConstants.LEFT);
					break;
	        	}
	        	case 3:
	        	{
	        	    lb.setHorizontalAlignment(SwingConstants.CENTER);
	        	    switch (d.getStatus()) {
	        	    	case SongDownload.STATUS_DOWNLOADING:
	        	    	    JProgressBar progress = new JProgressBar(0, d.getSong().getSize());
		    				progress.setUI(new MyProgressBarUI());
		    				progress.setStringPainted(true);
		    				progress.setFont(table.getFont());
		    				progress.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		    				progress.setBorderPainted(false);
		    				progress.setBackground(lb.getBackground());
		    				progress.setForeground(new Color(120, 120, 160));
		    				progress.setValue(d.getProgress());
		    				long percent = Math.round(progress.getPercentComplete() * 100);
		    				progress.setString("Downloading" + " - " + percent + "%");
		    				return progress;
	    				case SongDownload.STATUS_CANCELLED:
	    				    lb.setText("Failed: Download Cancelled!");
	    					break;
		    			case SongDownload.STATUS_WAITING:
		    			    lb.setText("Waiting...");
		    				break;
		    			case SongDownload.STATUS_PREPARING:
		    			    lb.setText("Preparing...");
		    				break;
		    			case SongDownload.STATUS_ERROR:
		    			    lb.setText("Failed: "+d.error_msg);
		    				break;
		    			case SongDownload.STATUS_FINISHED:
		    			    lb.setText("Finished: No problems.");
		    				break;
		    			case SongDownload.STATUS_ALREADY_EXISTS:
		    			    lb.setText("Finished: This file already exists; no need to download.");
		    				break;
		    			case SongDownload.STATUS_DUPLICATE:
		    			    lb.setText("Finished: This song is already in the local database.");
		    				break;
	        	    }
	        	    break;
	        	}
	        }
	        if (d.getStatus() >= SongDownload.STATUS_FINISHED || d.getStatus() == SongDownload.STATUS_CANCELLED)
	            lb.setEnabled(false);
	        else
	            lb.setEnabled(true);
	        return lb;
	    }
	}
	
	public class DisplayRenderer extends DefaultTableCellRenderer {

		public DisplayRenderer() {
			super();
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean is_selected, boolean has_focus, int row, int col) {
			JLabel component = (JLabel) super.getTableCellRendererComponent(table,
					value, is_selected, has_focus, row, col);
			MyTableModel t = (MyTableModel) table.getModel();
			JProgressBar progress = new JProgressBar();
			progress.setStringPainted(true);
			progress.setFont(displayTable2.getFont());
			progress.setBorderPainted(false);
			progress.setBackground(component.getBackground());
			progress.setForeground(new Color(120, 120, 160));
			if (loading_song)
			{
			    progress.setString("loading...");
			    progress.setValue(0);
			    return progress;
			} else if (player.getType() == AbstractPlayer.EXTERNALPLAYER) {
			    switch (player.getStatus()) {
			    case AbstractPlayer.STOPPED:
			        progress.setString("Get It Together");
			    break;
			    case AbstractPlayer.PLAYING:
			        progress.setString("playing in external player");
			    break;
			    }
			    return progress;
			} else if (player.getStatus() == AbstractPlayer.ERROR)
			{
			    progress.setString("error loading song!");
			    return progress;
			} else if (playingSong == null) {
			    progress.setString("Get It Together");
			    return progress;
			}
			progress.setMinimum(0);
			progress.setMaximum(playingSong.getTime());
			int millis = player.getProgress() + resumeOffset;
			if (showElapsed)
			    progress.setString("- "+PlayerUtils.millisToTime(playingSong.time - millis));
			else
			    progress.setString(PlayerUtils.millisToTime(millis));
			progress.setValue(millis);
			
			return progress;
		}
	}

	public class TopDisplayRenderer extends DefaultTableCellRenderer {
				public Component getTableCellRendererComponent(JTable table, Object value,
				boolean is_selected, boolean has_focus, int row, int col) {
			Song s = (Song) value;
			String str = "";
			String tt = "";
			switch (displayValue) {
				case DISPLAY_TITLE:
					str = s.name;
					tt = "Title: " + str;
					break;
				case DISPLAY_ARTIST:
					str = s.artist;
					tt = "Artist: " + str;
					break;
				case DISPLAY_ALBUM:
					str = s.album;
					tt = "Album: " + str;
					break;
			}
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, str,
					is_selected, has_focus, row, col);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			if (playingSong == null) {
			    label.setText("Welcome to");
			    return label;
			} else if (player.getType() == AbstractPlayer.EXTERNALPLAYER && player.getStatus() == AbstractPlayer.STOPPED) {
			    label.setText("Welcome to");
			    return label;
			}
			if (str.length() > 0) {
				StringPairList pairs = new StringPairList();
				pairs.addPair("Title", s.name);
				pairs.addPair("Artist", s.artist);
				pairs.addPair("Album", s.album);
				label.setToolTipText(GITUtils.createPropertyLabel(pairs));
			}
			return (Component) label;
		}
	}

	public class MyProgressBarUI extends BasicProgressBarUI {
		public Point getStringPlacement(Graphics g, String progressString, int x,
				int y, int width, int height) {
			Point p = super
					.getStringPlacement(g, progressString, x, y, width, height);
			return new Point(x, p.y);
		}

		public Color getSelectionBackground() {
			return Color.BLACK;
		}
		public Color getSelectionForeground() {
			return Color.WHITE;
		}

	}

	public class DownloadTable extends JTable implements DropTargetListener {
		int									currentRow	= -1;
		boolean							clearEnd		= false;
		public Rectangle2D	lastCueLine	= new Rectangle2D.Float();

		/** Constructor for the DownloadTable object */
		public DownloadTable() {
			super();
			DropTarget dropTarget = new DropTarget(this, this);
			setDragEnabled(true);
		}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragEnter(DropTargetDragEvent e) {
		//e.rejectDrag();
		//dltable.setCursor(DragSource.DefaultCopyNoDrop);
		}

		/**
		 * Description of the Method
		 * 
		 * @param dte
		 *          Description of the Parameter
		 */
		public void dragExit(DropTargetEvent dte) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragOver(DropTargetDragEvent e) {
			int row = dltable.rowAtPoint(e.getLocation());
			if (row > dls.num) {
				e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
				//dltable.setCursor(DragSource.DefaultCopyNoDrop);
			} else {
				e.rejectDrag();
				//dltable.setCursor(DragSource.DefaultCopyDrop);
			}
			if (row != currentRow) {
				JViewport viewport = (JViewport) dltable.getParent();
				Rectangle scroll = dltable.getCellRect(row + 1, 0, true);
				Rectangle scroll2 = dltable.getCellRect(row - 1, 0, true);
				Point pt = viewport.getViewPosition();
				scroll.setLocation(scroll.x - pt.x, scroll.y - pt.y);
				scroll2.setLocation(scroll2.x - pt.x, scroll2.y - pt.y);
				if (!new Rectangle(viewport.getExtentSize()).contains(scroll)) {
					//System.out.println("scroll down");
					dltable.scrollRectToVisible(dltable.getCellRect(row + 1, 0, true));
				} else if (!new Rectangle(viewport.getExtentSize()).contains(scroll2)) {
					//System.out.println("scroll up");
					dltable.scrollRectToVisible(dltable.getCellRect(row - 1, 0, true));
				}
				currentRow = row;
				// Get the drop target's bounding rectangle
				//if (currentRow > 0)
				row++;
				Graphics2D g2 = (Graphics2D) dltable.getGraphics();
				// clear the last drawn line
				dltable.paintImmediately(lastCueLine.getBounds());
				if (clearEnd) {
					// clear the end line
					JComponent panel = (JComponent) dltable.getParent();
					panel.paintImmediately(lastCueLine.getBounds());
					clearEnd = false;
				}
				if (row <= dls.num) {
					return;
				}
				Color cueColor = Color.BLACK;
				if (currentRow >= dltable.getRowCount() || currentRow == -1) {
					return;
				}
				// Cue line bounds (2 pixels beneath the drop target)
				Rectangle2D cueLine = new Rectangle2D.Float();
				Rectangle rect = dltable.getCellRect(row, -1, true);
				cueLine.setRect(0, rect.getY(), dltable.getWidth(), 2);
				if (currentRow == dltable.getRowCount() - 1) {
					//System.out.println("last one");
					g2 = (Graphics2D) dltable.getParent().getGraphics();
					rect = dltable.getCellRect(row - 1, -1, true);
					SwingUtilities.convertPoint(dltable, rect.x, rect.y, dltable
							.getParent());
					cueLine.setRect(0, rect.getY() + dltable.getRowHeight(row - 1),
							dltable.getWidth(), 2);
					clearEnd = true;
				}

				g2.setColor(cueColor); // The cue line color
				g2.fill(cueLine); // Draw the cue line
				lastCueLine = cueLine;

			}
		}

		/**
		 * Description of the Method
		 * 
		 * @param dtde
		 *          Description of the Parameter
		 */
		public void dropActionChanged(DropTargetDragEvent dtde) {}

		/**
		 * Description of the Method
		 * 
		 * @param dtde
		 *          Description of the Parameter
		 */
		public void drop(DropTargetDropEvent dtde) {
		// do stuff
		}
	}

	public class SearchNavigationFilter extends NavigationFilter {
		/**
		 * Sets the dot attribute of the SearchNavigationFilter object
		 * 
		 * @param fb
		 *          The new dot value
		 * @param dot
		 *          The new dot value
		 * @param b
		 *          The new dot value
		 */
		public void setDot(NavigationFilter.FilterBypass fb, int dot,
				Position.Bias b) {
			super.setDot(fb, (dot < 25 ? 25 : dot), Bias.Forward);
		}

		/**
		 * Description of the Method
		 * 
		 * @param fb
		 *          Description of the Parameter
		 * @param dot
		 *          Description of the Parameter
		 * @param b
		 *          Description of the Parameter
		 */
		public void moveDot(NavigationFilter.FilterBypass fb, int dot,
				Position.Bias b) {
			super.moveDot(fb, (dot < 25 ? 25 : dot), Bias.Forward);
		}
	}

	public class MenuListener implements ActionListener {
		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) (e.getSource());
		}
	}

	public class DisplayEditor extends SeekEditor {
		boolean	doSeek;

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int col) {
			doSeek = false;
			final MyTableModel t = ((MyTableModel) table.getModel());
			final int _row = row;
			Song s = playingSong;
			slider = (JProgressBar) table.getCellRenderer(row, col)
					.getTableCellRendererComponent(table, value, isSelected, true, row,
							col);
			slider.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					stopCellEditing();
				}
			});
			slider.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					doSeek = true;
					int i = slider.getWidth();
					double percent = ((double) e.getX() / (double) i);
					int newval = (int) (percent * slider.getMaximum());
					slider.setValue(newval);
					Color color = slider.getBackground();
					slider.setBackground(color);
					// display the would-be time in the slider.
					int time = (int) (slider.getPercentComplete() * playingSong.time);
					if (!showElapsed) {
						slider.setString(PlayerUtils.millisToTime(time));
					} else {
						slider.setString("- "
								+ PlayerUtils.millisToTime(t.getSongAt(_row).getTime() - time));
					}
					e.consume();
				}

				public void mouseMoved(MouseEvent e) {
					e.consume();
				}
			});
			return slider;
		}

		public Object getCellEditorValue() {
			if (doSeek) {
				resumeOffset = (int)(slider.getPercentComplete() * (double)playingSong.getTime());
				seekPlay(playingSong, slider.getPercentComplete());
			}
			return "Hello";
		}
	}

	public class SeekEditor extends AbstractCellEditor implements TableCellEditor {
		JProgressBar	slider;
		boolean				doSeek;
		boolean				isDisplay	= false;

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int col) {
		    System.out.println("editing!");
		    final int _row = row;
		    EventList el = getVisibleEventList();
			Song s = playingSong;
			if (player.getStatus() == AbstractPlayer.ERROR || (Song)getVisibleEventList().get(row) != s)
			    return null;
			JProgressBar render_bar = (JProgressBar) table.getCellRenderer(row, col)
					.getTableCellRendererComponent(table, value, isSelected, true, row,
							col);
			slider = render_bar;
			if (table.equals(displayTable)) {
				isDisplay = true;
			}
			doSeek = false;
			slider.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (isDisplay) {
						slider.dispatchEvent(new MouseEvent(e.getComponent(),
								MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, e
										.getX(), e.getY(), 1, false));
					}
				}

				public void mouseReleased(MouseEvent e) {
					if (isDisplay) {
						doSeek = true;
					}
					stopCellEditing();
				}

			});
			slider.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					doSeek = true;
					int i = slider.getWidth();
					double percent = ((double) e.getX() / (double) i);
					int newval = (int) (percent * slider.getMaximum());
					slider.setValue(newval);
					Color color = slider.getBackground();
					if (!isDisplay) {
						slider.setUI(new MyProgressBarUI());
					}
					slider.setBackground(color);
					// display the would-be time in the slider.
					int time = (int) (slider.getPercentComplete() * playingSong.time);
					if (!showElapsed) {
						slider.setString(playingSong.getName() + " - "
								+ PlayerUtils.millisToTime(time));
					} else {
						slider.setString(playingSong.getName() + " - "
								+ PlayerUtils.millisToTime(playingSong.getTime() - time));
					}
					e.consume();
				}

				public void mouseMoved(MouseEvent e) {
					e.consume();
				}
			});
			return slider;
		}

		public Object getCellEditorValue() {
		    if (doSeek) {
				resumeOffset = (int)(slider.getPercentComplete() * (double)playingSong.getTime());
				seekPlay(playingSong,slider.getPercentComplete());
			}
			return "Hello";
		}
	}

	public class BrowserRenderer extends DefaultTableCellRenderer {
		
	    ImageIcon error = new ImageIcon(GetItTogether.class
				.getResource("/images/err.png"));
	    
		/** Constructor for the BrowserRenderer object */
		public BrowserRenderer() {
			super();
		}

		Song s;
		
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean is_selected, boolean has_focus, int row, int col) {
			s = (Song)value;
		    
		    switch (table.convertColumnIndexToModel(col)) {
		    case 0:
		        value = new Integer(s.track);
		        break;
		    case 1:
		        value = s.artist;
		        break;
		    case 2:
		        value = s.album;
		        break;
		    case 3:
		        value = s.name;
		        break;
		    case 4:
		        int a = s.time;
		        value = GITUtils.getFormattedTime(s.time);
		        break;
		    case 5:
		        value = GITUtils.getFormattedSize(s.size);
				break;
		    default:
		        value = "hello!";
		    break;
		    }
		    
		    JLabel component = (JLabel) super.getTableCellRendererComponent(table,
					value, is_selected, has_focus, row, col);

		    switch (table.convertColumnIndexToModel(col)) {
		    case 0:
		    case 4:
		    case 5:
		        component.setHorizontalAlignment(SwingConstants.RIGHT);
		        break;
		    default:
		        component.setHorizontalAlignment(SwingConstants.LEFT);
		    }
		    
//		    JLabel component = new JLabel();
//		    EventList el = getVisibleEventList();
//			if (el.size() <= row)
//			    return component;
//			System.out.println(value);
//			Song s = (Song)el.get(row);
			
//			Song s = (Song)value;
			
			if (is_selected) {
				component.setBackground(new Color(200, 200, 240));
				component.setForeground(Color.BLACK);
			} else if (row % 2 == 0) {
				component.setBackground(table.getBackground());
				component.setForeground(table.getForeground());
			} else {
				component.setBackground(new Color(232, 232, 255));
				component.setForeground(table.getForeground());
			}
			
			if (!player.supportsSong(s) || s.status != Song.STATUS_OK)
			    component.setEnabled(false);
			else
			    component.setEnabled(true);
//			if (table.convertColumnIndexToModel(col) == 0) {
//			    return new JLabel("Hello!");
//			}
//			if (table.getColumnName(col) == "Length") {
//				int a = s.time;
//				String sec_zero = "";
//				int seconds = a / 1000;
//				int minutes = seconds / 60;
//				int seconds_left = seconds % 60;
//				if (seconds_left < 10) {
//					sec_zero = "0";
//				}
//				component.setText("" + minutes + ":" + sec_zero + seconds_left);
//			}
//			if (table.getColumnName(col) == "Size") {
//				double a = Math.round(s.size / 104857.6);
//				a = a / 10;
//				component.setText(Double.toString(a) + " Mb");
//			}
//			if (table.getColumnName(col) == "Tr.") {
//				StringPairList pairs = new StringPairList();
//				pairs.addPair("Host", s.host.getName());
//				pairs.addPair("Name", s.name);
//			    pairs.addPair("ID #", String.valueOf(s.id));
//			    pairs.addPair("Format", s.format);
//			    component.setToolTipText(GITUtils.createPropertyLabel(pairs));
//			} else {
//				component.setToolTipText(null);
//			}
//			if (value instanceof Integer) {
//				component.setHorizontalAlignment(SwingConstants.RIGHT);
//			} else {
//				component.setHorizontalAlignment(SwingConstants.LEFT);
//			}
			if (s == playingSong) {
				if (player.getStatus() == AbstractPlayer.PAUSED) {
				    if (is_selected)
				        component.setBackground(PAUSED_SELECTED);
				    else
				        component.setBackground(PAUSED_UNSELECTED);
				} else if (player.getStatus() == AbstractPlayer.PLAYING) {
				    if (is_selected)
				        component.setBackground(PLAYING_SELECTED);
				    else
				        component.setBackground(PLAYING_UNSELECTED);
				}
			// update the progress bar
				if (table.convertColumnIndexToModel(col) == 3) {
				    JProgressBar progress = new JProgressBar(0, s.getTime());
				    if (player.getStatus() == AbstractPlayer.ERROR) {
				        return component;
				    }
				    progress.setUI(new MyProgressBarUI());
				    progress.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				    progress.setBorderPainted(false);
				    //progress.setForeground(new Color(180, 200, 180));
				    progress.setBackground(component.getBackground());
				    progress.setForeground(new Color(120, 120, 160));
				    progress.setFont(component.getFont());
				    progress.setAlignmentX(JComponent.LEFT_ALIGNMENT);
				    progress.setString(s.getName());
				    progress.setStringPainted(true);
				    int millis = player.getProgress() + resumeOffset;
				    if (loading_song)
				        millis = 0;
				    progress.setValue(millis);
				    String ext = "";
				    if (showElapsed)
				        ext = PlayerUtils.millisToTime(s.time - millis);
				    else
				        ext = PlayerUtils.millisToTime(millis);
				    progress.setString(component.getText() + " - " + ext);
				    return progress;
				}
			}
		    if ((s.status != Song.STATUS_OK) && col == 0)
		        component.setIcon(error);
		    else
		        component.setIcon(null);
			return component;
		}
	}

	public class HostRenderer extends DefaultListCellRenderer {
		ImageIcon					myIcon;
		ImageIcon					visibleIcon;
		final float				dash1[]	= { 10.0f };
		final BasicStroke	dashed	= new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
																	BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

		public HostRenderer() {
			super();
			// System.out.println("ellipsis.gif");
			URL imageURL = GetItTogether.class.getResource("/images/ellipsis.gif");
			java.awt.Image image = getToolkit().getImage(imageURL);
			myIcon = new ImageIcon(image);
			// System.out.println("eye.png");
			URL imageURL2 = GetItTogether.class.getResource("/images/eye.png");
			visibleIcon = new ImageIcon(imageURL2);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean is_selected, boolean hasFocus) {
			//URL imageURL = GetItTogether.class.getResource("right_over.png");
			//final ImageIcon myIcon = new ImageIcon(imageURL);
			Host host = (Host) value;
			super.getListCellRendererComponent(list, value, index, is_selected,
					hasFocus);
			if (host.getName().equals("spacer")) {
				JLabel label = new JLabel(" ") {
					public void paint(Graphics g) {
						super.paint(g);
						float[] f1 = { getWidth() / 8 };
						((Graphics2D) g).setStroke(new BasicStroke(1.5f,
								BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, f1, 0.0f));
						g.setColor(Color.GRAY);
						g.drawLine(10, getHeight() / 2, getWidth() - 10, getHeight() / 2);
					}
				};
				Dimension d = label.getPreferredSize();
				d.height = 6;
				label.setPreferredSize(d);
				label.setEnabled(false);
				return label;
			}
			if (host.getSongs().size() > 0) {
				Font currentFont = getFont();
				setFont(new Font(currentFont.getName(), Font.BOLD, currentFont
						.getSize()));
			} else {
				Font currentFont = getFont();
				setFont(new Font(currentFont.getName(), Font.PLAIN, currentFont
						.getSize()));
			}
			setToolTipText(host.getToolTipText());
			if (is_selected) {
				setBackground(new Color(200, 200, 240));
				setForeground(Color.BLACK);
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (host.isVisible()) {
				setIcon(visibleIcon);
				setHorizontalTextPosition(SwingConstants.LEFT);
			}
			/*
			 * if (host.getStatus() == Host.STATUS_CONNECTING) { setIconTextGap(2);
			 * System.out.println("setting connection icon");
			 * myIcon.setImageObserver(servicesLS); setIcon(myIcon);
			 * setHorizontalTextPosition(SwingConstants.LEFT); }
			 */
			return this;
		}

	}

	public class HostTreeRenderer extends DefaultTreeCellRenderer {
		private ImageIcon[] daap = new ImageIcon[3];
		private ImageIcon[] local = new ImageIcon[3];
		private ImageIcon[] settings = new ImageIcon[3];
		private ImageIcon[] downloads = new ImageIcon[3];
		private ImageIcon[] playlist = new ImageIcon[3];
		private ImageIcon[] ipod = new ImageIcon[3];
		private ImageIcon[] apple = new ImageIcon[3]; 
		
		private ImageIcon	visible;
		private ImageIcon 	password;
		private ImageIcon 	error;
		private ImageIcon speaker;
		private Host host;
		private Playlist p;

		{
		    daap[0] = new ImageIcon(GetItTogether.class.getResource("/images/tree_host_blank.png"));
			daap[1] = new ImageIcon(GetItTogether.class.getResource("/images/tree_host_dim.png"));
			daap[2] = new ImageIcon(GetItTogether.class.getResource("/images/tree_host_filled.png"));
			local[0] = new ImageIcon(GetItTogether.class.getResource("/images/tree_local_blank.png"));
			local[1] = new ImageIcon(GetItTogether.class.getResource("/images/tree_local_dim.png"));
			local[2] = new ImageIcon(GetItTogether.class.getResource("/images/tree_local_filled.png"));
			settings[0] = new ImageIcon(GetItTogether.class.getResource("/images/tree_settings_blank.png"));
			settings[1] = new ImageIcon(GetItTogether.class.getResource("/images/tree_settings_dim.png"));
			settings[2] = new ImageIcon(GetItTogether.class.getResource("/images/tree_settings_filled.png"));
			downloads[0] = new ImageIcon(GetItTogether.class.getResource("/images/tree_downloads_blank.png"));
			downloads[1] = new ImageIcon(GetItTogether.class.getResource("/images/tree_downloads_dim.png"));
			downloads[2] = new ImageIcon(GetItTogether.class.getResource("/images/tree_downloads_filled.png"));
			playlist[0] = new ImageIcon(GetItTogether.class.getResource("/images/tree_pl_blank.png"));
			playlist[1] = new ImageIcon(GetItTogether.class.getResource("/images/tree_pl_dim.png"));
			playlist[2] = new ImageIcon(GetItTogether.class.getResource("/images/tree_pl_filled.png"));
			ipod[0] = new ImageIcon(GetItTogether.class.getResource("/images/ipod_blank.png"));
			ipod[1] = new ImageIcon(GetItTogether.class.getResource("/images/ipod_dim.png"));
			ipod[2] = new ImageIcon(GetItTogether.class.getResource("/images/ipod_filled.png"));
			apple[0] = new ImageIcon(GetItTogether.class.getResource("/images/apple_blank.png"));
			apple[1] = new ImageIcon(GetItTogether.class.getResource("/images/apple_dim.png"));
			apple[2] = new ImageIcon(GetItTogether.class.getResource("/images/apple_filled.png"));
		}
		
		public HostTreeRenderer() {
			super();
			
			
			visible = new ImageIcon(GetItTogether.class.getResource("/images/eye.png"));
			password = new ImageIcon(GetItTogether.class.getResource("/images/lock.png"));
			error = new ImageIcon(GetItTogether.class.getResource("/images/err.png"));
			speaker = new ImageIcon(GetItTogether.class.getResource("/images/speaker.png"));
			waiting = new ImageIcon(GetItTogether.class.getResource("/images/ellipsis.gif"));
			
			setBackgroundNonSelectionColor(getBackground());
			setBackgroundSelectionColor(new Color(200, 200, 240));
			setTextNonSelectionColor(getForeground());
			setTextSelectionColor(getForeground());
		}
		
		public void setAllIcons(ImageIcon icon) {
//		    setLeafIcon(icon);
//		    setClosedIcon(icon);
//		    setOpenIcon(icon);
		    setIcon(icon);
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//		    Component oldComponent = super.getTreeCellRendererComponent(tree, value, sel, expanded,
//		            leaf, row, hasFocus);
		    String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
		    setText(stringValue);
		    this.hasFocus = hasFocus;
		    selected = sel;
		    
//		    Font font = getFont();
			GITNode node = (GITNode) value;
			if (node == null) {
			    System.err.println("error in the host tree renderer!");
			    return this;
			}
			setToolTipText(node.getToolTipText());
			ImageIcon icon[] = null;
		    host = null;
			switch (node.getType()) {
				case GITNode.DAAP_ROOT:
				    setAllIcons(daap[2]);
					break;
				case GITNode.PODCAST:
					icon = daap;
				host = (PodcastHost)node.getUserObject();
				if (host.getStatus() >= Host.STATUS_CONNECTED) {
				    if (host.isVisible())
				        setAllIcons(icon[2]);
				    else
				        setAllIcons(icon[1]);
				} else
				    setAllIcons(icon[0]);
				return this;
				case GITNode.DAAP:
				    icon = daap;
				    host = (DaapHost)node.getUserObject();
				    if (host.getStatus() >= Host.STATUS_CONNECTED) {
				        if (host.isVisible())
				            setAllIcons(icon[2]);
				        else
				            setAllIcons(icon[1]);
				    } else
				        setAllIcons(icon[0]);
				    return this;
//					// We can make all the GIT servers bold (heh, heh...)
//					if (host != null && ((DaapHost)host).getServerType() == DaapHost.GIT_SERVER)
//					    setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
//					else
//					    setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
				case GITNode.LOCAL:
					LocalHost lhost = (LocalHost)node.getUserObject();
					if (lhost.getLibraryType() == LocalHost.IPOD)
					    icon = ipod;
					else if (lhost.getLibraryType() == LocalHost.ITUNES_XML)
					    icon = apple;
					else
					    icon = local;
					if (lhost.getStatus() >= Host.STATUS_CONNECTED) {
					    if (lhost.isVisible())
					        setAllIcons(icon[2]);
					    else
					        setAllIcons(icon[1]);
					} else
					    setAllIcons(icon[0]);
					host = lhost;
					return this;
				case GITNode.LOCAL_ROOT:
				    setAllIcons(local[2]);
					break;
				
				case GITNode.PLAYLIST_ROOT:
				    setAllIcons(playlist[2]);
					break;
				case GITNode.PLAYLIST:
				    p = (Playlist)node.getUserObject();
				    if (playlist == null)
					    break;
					else if (p.getStatus() >= Playlist.STATUS_INITIALIZED)
					{
				        setAllIcons(playlist[2]);
					    break;
					}
					else
					    setAllIcons(playlist[0]);
					break;
				case GITNode.SETTINGS:
				    setAllIcons(settings[2]);
					break;
				case GITNode.DOWNLOADER:
				    setAllIcons(downloads[2]);
					break;
				case GITNode.SPACE:
					setAllIcons(null);
					return new JLabel("");
				default:
					break;
			}
			return this;
		}

		public void setWaiting() {
		    if (waiting == null) {
		        waiting = new ImageIcon(GetItTogether.class.getResource("/images/ellipsis.gif"));
		    }
		    setAllIcons(waiting);
		    waiting.setImageObserver(tree);
		}
		
		public Color getPlayingHostColor(boolean selected) {
		    if (player.getStatus() == AbstractPlayer.PAUSED) {
			    if (selected)
			        return PAUSED_SELECTED;
			    else
			        return PAUSED_UNSELECTED;
			} else if (player.getStatus() == AbstractPlayer.PLAYING){
			    if (selected)
			        return PLAYING_SELECTED;
			    else
			        return PLAYING_UNSELECTED;
			} else
			    return tree.getBackground();
		}
		
		public void paint(Graphics g) {
		    super.paint(g);
			
		}
		
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    if (daap != null && host != null) {
		        Graphics2D g2 = (Graphics2D) g;
		        if (host instanceof DaapHost)
		        {
		            if (((DaapHost)host).isPasswordProtected())
		            {
		                int wid = 6;
		                int hei = 7;
		                g2.drawImage(password.getImage(), 16 - wid, 16 - hei, wid, hei, this);
		            }
		        }
		        if (host.getStatus() == Host.STATUS_NOT_AVAILABLE)
		        {
		            int wid = 6;
		            int hei = 13;
		            g2.drawImage(error.getImage(), 11 - wid, 16 - hei, wid, hei, this);
		        }
		    }
		}
	}
	
	public class DraggableTable extends JTable implements DragGestureListener,
			DragSourceListener, DropTargetListener {
		private DragSource dragSource = null;
        private DragSourceContext dragsourcecontext = null;
        public int dragStart, currentRow = -1;
        public boolean clearEnd = false;
        public int[] selection;
        public Rectangle2D lastCueLine = new Rectangle2D.Float();

		/** Constructor for the DraggableTable object */
		public DraggableTable() {
			super();
			dragSource = DragSource.getDefaultDragSource();
			dragSource.createDefaultDragGestureRecognizer(this,
					DnDConstants.ACTION_COPY_OR_MOVE, this);
			DropTarget dropTarget = new DropTarget(this, this);
		}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragGestureRecognized(DragGestureEvent e) {

			dragStart = pltable.rowAtPoint(e.getDragOrigin());
//			if (dragStart == pls.indexOf(playingSong)
//					&& pltable.getColumnName(pltable.columnAtPoint(e.getDragOrigin())) == "Title") {
//
//				pltable.setEditingRow(dragStart);
//				pltable.setEditingColumn(pltable.columnAtPoint(e.getDragOrigin()));
//				pltable.editCellAt(dragStart, pltable.columnAtPoint(e.getDragOrigin()));
//				pltable.getSelectionModel().setValueIsAdjusting(true);
//
//				return;
//			}
//			ArrayList a = new ArrayList();
//			int[] rows = pltable.getSelectedRows();
//			for (int i = 0; i < rows.length; i++) {
//				a.add(pls.getSongAt(rows[i]));
//			}
//			Transferable transfer = new TransferableSongs(a, rows);
//			Cursor cursor = DragSource.DefaultMoveNoDrop;
			//pltable.setCursor(DragSource.DefaultMoveNoDrop);
//			dragSource.startDrag(e, cursor, transfer, this);
		}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragDropEnd(DragSourceDropEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragEnter(DragSourceDropEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragEnter(DragSourceDragEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragEnter(DropTargetDragEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragOver(DragSourceDropEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragOver(DragSourceDragEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragOver(DropTargetDragEvent e) {
			e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
			int row = pltable.rowAtPoint(e.getLocation());
			if (row != currentRow) {
				JViewport viewport = (JViewport) pltable.getParent();
				Rectangle scroll = pltable.getCellRect(row + 1, 0, true);
				Rectangle scroll2 = pltable.getCellRect(row - 1, 0, true);
				Point pt = viewport.getViewPosition();
				scroll.setLocation(scroll.x - pt.x, scroll.y - pt.y);
				scroll2.setLocation(scroll2.x - pt.x, scroll2.y - pt.y);
				if (!new Rectangle(viewport.getExtentSize()).contains(scroll)) {
					//System.out.println("scroll down");
					pltable.scrollRectToVisible(pltable.getCellRect(row + 1, 0, true));
				} else if (!new Rectangle(viewport.getExtentSize()).contains(scroll2)) {
					//System.out.println("scroll up");
					pltable.scrollRectToVisible(pltable.getCellRect(row - 1, 0, true));
				}
				currentRow = row;
				// Get the drop target's bounding rectangle
				if (currentRow > dragStart) {
					row++;
				}
				Graphics2D g2 = (Graphics2D) pltable.getGraphics();
				pltable.paintImmediately(lastCueLine.getBounds());
				if (clearEnd) {
					JComponent panel = (JComponent) pltable.getParent();
					panel.paintImmediately(lastCueLine.getBounds());
					clearEnd = false;
				}
				Color cueColor = Color.BLACK;
				if (currentRow >= pltable.getRowCount() || currentRow == dragStart
						|| currentRow == -1) {
					return;
				}
				// Cue line bounds (2 pixels beneath the drop target)
				Rectangle2D cueLine = new Rectangle2D.Float();
				Rectangle rect = pltable.getCellRect(row, -1, true);
				cueLine.setRect(0, rect.getY(), pltable.getWidth(), 2);
				if (currentRow == pltable.getRowCount() - 1) {
					//System.out.println("last one");
					g2 = (Graphics2D) pltable.getParent().getGraphics();
					rect = pltable.getCellRect(row - 1, -1, true);
					SwingUtilities.convertPoint(pltable, rect.x, rect.y, pltable
							.getParent());
					cueLine.setRect(0, rect.getY() + pltable.getRowHeight(row - 1),
							pltable.getWidth(), 2);
					clearEnd = true;
				}

				g2.setColor(cueColor); // The cue line color
				g2.fill(cueLine); // Draw the cue line
				lastCueLine = cueLine;

			}
		}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dropActionChanged(DragSourceDropEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dropActionChanged(DragSourceDragEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dropActionChanged(DropTargetDragEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragExit(DragSourceEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void dragExit(DropTargetEvent e) {}

		/**
		 * Description of the Method
		 * 
		 * @param e
		 *          Description of the Parameter
		 */
		public void drop(DropTargetDropEvent e) {
			//pltable.setCursor(Cursor.getDefaultCursor());
			try {
				Transferable tr = e.getTransferable();
				if (!tr.isDataFlavorSupported(TransferableSongs.SONGS_FLAVOR)) {
					e.rejectDrop();
				} else {
					e.acceptDrop(e.getDropAction());
				}
				ArrayList songs = new ArrayList();
				int[] rows = (int[]) tr.getTransferData(TransferableSongs.SONGS_FLAVOR);
				Point loc = e.getLocation();
				int row = pltable.rowAtPoint(loc); // insertion row
				int sel_start = row;
				int sel_end = 0;
				int offset = 0;
				boolean greater = false; // boolean true when last removal point was
																 // greater than last insertion point
				for (int i = 0; i < rows.length; i++) {
					if (row == -1) {
						row = pltable.getRowCount() - 1;
					}
					if (i > 0) {
						if ((sel_start <= row && sel_start > rows[i - 1])
								|| (sel_start >= row && sel_start < rows[i - 1])) {
							sel_start = sel_start + (greater ? 1 : -1);
						}
						for (int j = 1; j < rows.length; j++) {
							if ((rows[j] <= row && rows[j] > rows[i - 1])
									|| (rows[j] >= row && rows[j] < rows[i - 1])) {
								// the j'th deletion point is between the current insertion
								// point and the last deletion point
								rows[j] = rows[j] + (greater ? 1 : -1);
							}
						}
					}
//					if (rows[i] < row) {
//						pls.playlist.add(row, pls.playlist.remove(rows[i]));
//						greater = false;
//					}
//					if (rows[i] == row) {
//						pls.playlist.add(row, pls.playlist.remove(rows[i]));
//						greater = false;
//					}
//					if (rows[i] > row) {
//						pls.playlist.add(row, pls.playlist.remove(rows[i]));
//						row++;
//						greater = true;
//					}
//					if (i == rows.length - 1) {
//						sel_end = (rows[i] > row ? row - 1 : row);
//					}
				}
//				pls.fireTableDataChanged();
				if (sel_start < pltable.getRowCount() && sel_start >= 0) {
					pltable.setRowSelectionInterval(sel_start, sel_start + rows.length
							- 1);
				}
			} catch (IOException io) {
				e.rejectDrop();
			} catch (UnsupportedFlavorException ufe) {
				e.rejectDrop();
			}
		}		
	}

	public class GITHandler extends Handler {
	    
	    public void publish(LogRecord rec) {
	    }

        public void close() throws SecurityException {
            // TODO Auto-generated method stub
            
        }

        public void flush() {
            // TODO Auto-generated method stub
            
        }
	    
	}

	public class ServerTableFormat implements TableFormat {
	    public int getColumnCount() {
	        return 3;
	    }
	    
	    public String getColumnName(int col) {
	        switch (col) {
	        	case 0:
	        	    return "IP Address";
        	    case 1:
        	        return "Connection Type";
    	        case 2:
    	            return "Other Info";
	            default:
	                return "unknown";
	        }
	    }
	    
	    public Object getColumnValue(Object baseObject, int col) {
//	        DaapConnectionBIO connection = (DaapConnectionBIO)baseObject;
//	        DaapSession session = connection.getSession(false);
	        switch (col) {
	        	case 0:
	        	    return "";
        	    case 1:
	        	    return "Daap Connection";
    	    	case 2:
    	    	    return "";
	    	    default:
	    	        return "bad column value";
	        }
	    }
	}
	
	public class TableTimer extends TimerTask {
	    public void run() {
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                updateTables.actionPerformed(null);
	            }
	        });
	    }
	}

}