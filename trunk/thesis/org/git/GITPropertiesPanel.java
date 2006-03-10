/*
 * Created on Sep 5, 2004
 * Created by wooo as part of git
 */
package org.git;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.git.client.swing.GetItTogether;
import org.git.player.AbstractPlayer;
import org.git.player.PlayerUtils;
import org.git.server.Server;

/**
 * @author wooo
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GITPropertiesPanel extends JPanel implements ActionListener {
    private JLabel dlDirLabel;
    private JLabel showSplashLabel;
    private JLabel organizeDLLabel;
    private JLabel sharePWLabel;
    private JPanel dlDirPanel;
    private JPanel showSplashPanel;
    private JPanel organizeDLPanel;
    private JTextField dlDirTextField;
    private JButton dlDirButton;
    private JPanel tmpDirPanel;
    private JLabel tmpDirLabel;
    private JTextField tmpDirTextField;
    private JButton tmpDirButton;
    private JPanel playerTypePanel;
    private JLabel playerTypeLabel;
    private JComboBox playerTypeCmboBox;
    private JPanel maxSharingPanel;
    private JLabel maxSharingLabel;
    private JTextField maxSharingTextField;
    private JPanel sharePWPanel;
    private JTextField sharePWTextField;
    private JPanel server;
    private JPanel externalTextField;
    private JLabel externalLabel;
    public JCheckBox showSplashBox;
    public static GITPropertiesPanel instance;
    
    
    
//    private static final int PREFERRED_WIDTH = 300;
//    private static final int PREFERRED_HEIGHT = 200;
    private JPanel pw;
    private JPanel pw_field;
    public JCheckBox tooManyUsers;
    private JPanel external;
    private JCheckBox hideUnsupported;
    private JComboBox player;
    /**
     * Stub to show the panel for testing...
     * @param args
     */
    public static void main(String[] args) {
        JFrame test = new JFrame("GITPropertiesPanel");
        test.getContentPane().add(new GITPropertiesPanel());
        test.pack();
        test.show();
    }

    /**
     * Default constructor, simply initializes panel
     */
    public GITPropertiesPanel() {
        instance = this;
        createComponents();
    }

    protected JPanel createGITSpinner(String label, final String field, int min, int max) {
        int initial = 0;
        
        try {
            initial = GITProperties.class.getField(field).getInt(null);
        } catch (Exception e) {}
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(initial, min, max, 1));
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel model = ((JSpinner)e.getSource()).getModel();
                int num = ((Integer)model.getValue()).intValue();
                try {
                    GITProperties.class.getField(field).setInt(GITProperties.class, num);
                } catch (Exception ex) {}
            }

        });
        spinner.setMaximumSize(spinner.getPreferredSize());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(label));
        panel.add(spinner);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel; 
    }
    
    protected JPanel createGITTextField(String label, final String field, String tooltip, int width) {
        final JTextField t = new JTextField();
        t.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub
                changeProperty();
            }

            public void removeUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub
                changeProperty();
            }
            
            public void changeProperty() {
                try {
                    GITProperties.class.getField(field).set(new String(), t.getText());
                } catch (Exception e) {}
            }
            
        });
        t.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    GITProperties.class.getField(field).set(new String(), t.getText());
                    System.out.println("GITProperties."+field+" = "+GITProperties.class.getField(field).get(""));
                } catch (Exception ex) {}
            }
        });
        t.setPreferredSize(new Dimension(width,24));
        t.setMaximumSize(new Dimension(width,24));
        JLabel labella = new JLabel(label);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(labella);
        p.add(t);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setToolTipText(tooltip);
        try {
            String s = "";
            s = (String)GITProperties.class.getDeclaredField(field).get(null);
            t.setText(s);
        } catch (Exception ex) {}
        return p;
    }
    
    protected JCheckBox createGITCheckBox(String label, final String field) {
        final JCheckBox j = new JCheckBox();
        if (field != null && field.length() > 0) {
	        j.setAction(new AbstractAction() {
	            public void actionPerformed(ActionEvent e) {
	                try {
	                GITProperties.class.getField(field).setBoolean(GITProperties.class, j.isSelected());
	                } catch (Exception ex) {}
	            }
	        });
        }
        j.setAlignmentX(Component.LEFT_ALIGNMENT);
        j.setText(label);
        try {
            j.setSelected(GITProperties.class.getDeclaredField(field).getBoolean(GITProperties.class));
        } catch (Exception e) {}
        return j;
    }
    
    private void createComponents() {
        
        String tooltip = new String("");
        
        setMinimumSize(new Dimension(100, 100));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Directories panel
        JPanel dp = new JPanel();
        dp.setLayout(new BoxLayout(dp, BoxLayout.Y_AXIS));
        dp.setBorder(BorderFactory.createTitledBorder(null, "Directories", TitledBorder.LEADING, TitledBorder.TOP));
        
        // Download directory panel
        dlDirPanel = new JPanel();
        dlDirLabel = new JLabel();
        dlDirTextField = new JTextField();
        dlDirButton = new JButton();
        tmpDirPanel = new JPanel();
        tmpDirLabel = new JLabel();
        tmpDirTextField = new JTextField();
        tmpDirButton = new JButton();
        dlDirPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dlDirLabel.setText("Download Directory");
        dlDirPanel.add(dlDirLabel);
        dlDirButton.setText("Browse");
        dlDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dlDirButtonPressed(evt);
            }
        });
        dlDirTextField.setText(GITProperties.dlDir);
        dlDirTextField.setMinimumSize(new Dimension(200, 20));
        dlDirTextField.setPreferredSize(new Dimension(200, 20));
        // FIXME do we want them to be able to change it manually?
        dlDirTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                dlDirChanged(evt);
            }
        });
        dlDirPanel.add(dlDirTextField);
        dlDirPanel.add(dlDirButton);
        dp.add(dlDirPanel);
        
        // Temp directory panel
        tmpDirPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tmpDirLabel.setText("Temp Directory");
        tmpDirPanel.add(tmpDirLabel);
        tmpDirButton.setText("Browse");
        tmpDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tmpDirButtonPressed(evt);
            }
        });
        tmpDirTextField.setText(GITProperties.tempDir);
        tmpDirTextField.setMinimumSize(new Dimension(200, 20));
        tmpDirTextField.setPreferredSize(new Dimension(200, 20));
        // FIXME do we want them to be able to change it manually?
        tmpDirTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                tmpDirChanged(evt);
            }
        });
        tmpDirPanel.add(tmpDirTextField);
        tmpDirPanel.add(tmpDirButton);
        dp.add(tmpDirPanel);
        dp.setAlignmentX(Component.LEFT_ALIGNMENT);
//        dp.setMinimumSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        add(dp);
        
        // Startup panels
        JPanel su = new JPanel();
        su.setLayout(new BoxLayout(su, BoxLayout.Y_AXIS));
        su.setBorder(BorderFactory.createTitledBorder(null, "Startup", TitledBorder.LEADING, TitledBorder.TOP));
        showSplashBox = createGITCheckBox("Show splash on startup", "showSplash");
        su.add(showSplashBox);
        su.add(createGITCheckBox("Enable Daap auto-connect", "autoConnect"));
        su.setAlignmentX(Component.LEFT_ALIGNMENT);
//        su.setMinimumSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        add(su);
        
        // PLAYER PANEL:
        JPanel pl = new JPanel();
        pl.setLayout(new BoxLayout(pl, BoxLayout.Y_AXIS));
        Vector items = new Vector();
        items.add("Java");
        if (GITUtils.hasQuicktimeForJava()) {
            items.add("Quicktime");
        }
        items.add("External");
        player = new JComboBox(items);
        player.addActionListener(this);
        player.setMaximumSize(new Dimension(120, 20));
        
        player.setAlignmentX(Component.LEFT_ALIGNMENT);
        pl.setBorder(BorderFactory.createTitledBorder(null, "Player", TitledBorder.LEADING, TitledBorder.TOP));
        pl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pl.add(player);
        
        external = new JPanel();
        external.setLayout(new BoxLayout(external, BoxLayout.Y_AXIS));
        external.setAlignmentX(Component.LEFT_ALIGNMENT);
        external.add(createGITTextField("External command: ", "externalProg", "Choose the command to execute with the ExternalPlayer.",260));
        external.add(new JLabel("<html>Browse or enter the command to run your favorite music program.<br>" +
        		"We will add the song URL at the end."));
        external.hide();
        
        JPanel external_pad = new JPanel();
        external_pad.setLayout(new BoxLayout(external_pad, BoxLayout.X_AXIS));
        external_pad.setAlignmentX(Component.LEFT_ALIGNMENT);        
        external_pad.add(Box.createHorizontalStrut(30));
        external_pad.add(external);
        
        pl.add(external_pad);
        add(pl);
        player.setSelectedIndex(GITProperties.playerType);
        
        // GUI panels
        JPanel gui = new JPanel();
        gui.setLayout(new BoxLayout(gui, BoxLayout.Y_AXIS));
        hideUnsupported = createGITCheckBox("Hide music not supported by current player", "");
        hideUnsupported.addActionListener(this);
        gui.add(hideUnsupported);
        gui.add(createGITCheckBox("Update search results every keystroke", "searchEveryKey"));
        gui.add(createGITCheckBox("Auto-scroll on song change", "locatePlayingSong"));
        gui.add(createGITCheckBox("Always-on-top in mini-mode (only works with Java 1.5)", "alwaysOnTop"));
        tooManyUsers = createGITCheckBox("Show \"Too Many Users\" Popup window", "showTooManyUsersPanel");
        gui.add(tooManyUsers);
        gui.setBorder(BorderFactory.createTitledBorder(null, "GUI", TitledBorder.LEADING, TitledBorder.TOP));
        gui.setAlignmentX(Component.LEFT_ALIGNMENT);
//        gui.setMinimumSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        add(gui);
        
        // Downloads panels
        JPanel dl = new JPanel();
        dl.setLayout(new BoxLayout(dl, BoxLayout.Y_AXIS));
        dl.add(createGITCheckBox("Organize downloaded files into folders (Artist/Album/Song)", "organizeDLdSongs"));
        dl.add(createGITCheckBox("Don't download DRM-protected songs", "hideDRMProtected"));
        dl.add(createGITCheckBox("Try to avoid duplicate songs", "tryToAvoidDups"));
        dl.setBorder(BorderFactory.createTitledBorder(null, "Downloads", TitledBorder.LEADING, TitledBorder.TOP));
        dl.setAlignmentX(Component.LEFT_ALIGNMENT);
//        dl.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        add(dl);
        
        // SERVER panels
        JPanel server_outer = new JPanel();
        	server_outer.setLayout(new BoxLayout(server_outer, BoxLayout.Y_AXIS));
        	server_outer.setBorder(BorderFactory.createTitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP));
            server_outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        server_outer.add(createServerCheckBox("Share my music", "shareEnabled"));
        server = new JPanel();
        	server.setAlignmentX(Component.LEFT_ALIGNMENT);
        	server.setLayout(new BoxLayout(server, BoxLayout.Y_AXIS));
        JPanel server_pad = new JPanel();
        	server_pad.setLayout(new BoxLayout(server_pad, BoxLayout.X_AXIS));
        	server_pad.setAlignmentX(Component.LEFT_ALIGNMENT);
        server_pad.add(Box.createHorizontalStrut(30));
        server_pad.add(server);
        
        tooltip = "Set the name of your server.";
        server.add(createGITTextField("Name:  ", "shareName", tooltip,120));
        server.add(Box.createVerticalStrut(5));
        pw = new JPanel();
        	pw.setLayout(new BoxLayout(pw, BoxLayout.X_AXIS));
	        pw.add(createPasswordCheckBox("", "sharePasswordRequired"));
	        tooltip = "Set the password for your server.";
	        pw_field = createGITTextField("Password:  ", "sharePassword", tooltip,100);
	        pw.add(pw_field);
	        pw.setAlignmentX(Component.LEFT_ALIGNMENT);
        server.add(pw);
        server.add(Box.createVerticalStrut(5));
        server.add(createGITSpinner("Port:  ", "sharePort", 1, 9999));
        server.add(Box.createVerticalStrut(5));
        server.add(createGITSpinner("User limit:  ", "shareLimit", 0, 10));
        server.add(Box.createVerticalStrut(5));
        server.add(createServerUpdateButton());
        tooltip = "<HTML>Sets the IP address of your special friend.  When <br>the server is enabled, this \"special friend\" is <br>allowed to connect from the internet.";
        server.add(createGITTextField("Special Friend IP:  ", "specialFriend", tooltip,110));
        
        server_outer.add(server_pad);
        add(server_outer);
        
        try {
            enableAllChildren(server, GITProperties.class.getDeclaredField("shareEnabled").getBoolean(GITProperties.class));
            enableAllChildren(pw_field, GITProperties.class.getDeclaredField("sharePasswordRequired").getBoolean(GITProperties.class));
        } catch (Exception e) {}
    }
    
    protected JComponent createServerUpdateButton() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        JButton dl = new JButton();
        dl.setText("Update server");
        dl.setMargin(new Insets(0, 1, 0, 1));
        dl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Server.instance().update();
            }
        });
        dl.setToolTipText("Updates the server's information, reflecting any changes you have made.");
        panel.add(dl);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    protected JCheckBox createPasswordCheckBox(final String label, final String field) {
        final JCheckBox j = new JCheckBox();
        j.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                GITProperties.class.getField(field).setBoolean(GITProperties.class, j.isSelected());
                // if unchecked, stop server and disable all other options
                if (!j.isSelected())
                {
                    enableAllChildren(pw_field, false);
                }
                else
                {
                    enableAllChildren(pw_field, true);
                }
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
        j.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            j.setSelected(GITProperties.class.getDeclaredField(field).getBoolean(GITProperties.class));
        } catch (Exception e) {}
        return j;
    }
    
    protected JCheckBox createServerCheckBox(final String label, final String field) {
        final JCheckBox j = new JCheckBox();
        j.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                GITProperties.class.getField(field).setBoolean(GITProperties.class, j.isSelected());
                // if unchecked, stop server and disable all other options
                if (!j.isSelected())
                {
                    enableAllChildren(server, false);
                    Server.instance().stop();
                }
                else
                {
                    enableAllChildren(server, true);
                    enableAllChildren(pw_field, GITProperties.sharePasswordRequired);
                    Server.instance().start();
                }
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
        j.setAlignmentX(Component.LEFT_ALIGNMENT);
        j.setText(label);
        try {
            j.setSelected(GITProperties.class.getDeclaredField(field).getBoolean(GITProperties.class));
        } catch (Exception e) {}
        return j;
    }
    
    public void enableAllChildren(JComponent c, boolean enable) {
        Component[] comps = c.getComponents();
        for (int i=0; i < comps.length; i++) {
            comps[i].setEnabled(enable);
            enableAllChildren((JComponent)comps[i], enable);
        }
    }
    
    protected void tmpDirChanged(FocusEvent myEvt) {
        String validatedPath = validatePath(this.tmpDirTextField.getText());
        if (validatedPath != null) {
            GITProperties.tempDir = validatedPath;
        } else {
            this.tmpDirTextField.setText(GITProperties.tempDir);
        }
    }

    protected void tmpDirButtonPressed(ActionEvent myEvt) {
        // TODO Auto-generated method stub
        // FIXME show filechooser to get a selected dir and make sure we can
        // write to it!
        GITProperties.tempDir = chooseDirectory(tmpDirTextField.getText());
        this.tmpDirTextField.setText(GITProperties.tempDir);
    }

    private String chooseDirectory(String startDir) {
        String result = startDir;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectedFile(new File(startDir));
        int chosen = fileChooser.showOpenDialog(this);
        switch (chosen) {
        case JFileChooser.APPROVE_OPTION:
            File file = fileChooser.getSelectedFile();
            result = file.getAbsolutePath();
            if (file.exists() && file.isDirectory()) {
            } else {
                JOptionPane
                        .showMessageDialog(
                                this,
                                "\""
                                        + result
                                        + "\" is not a valid directory!  Option not changed.");
                result = this.dlDirTextField.getText();
            }
            break;
        case JFileChooser.CANCEL_OPTION:
            break;
        case JFileChooser.ERROR_OPTION:
            JOptionPane.showMessageDialog(this, "Error choosing File.");
            break;
        }
        return result;
    }

    protected void dlDirChanged(FocusEvent evt) {
        String validatedPath = validatePath(this.dlDirTextField.getText());
        if (validatedPath != null) {
            GITProperties.dlDir = validatedPath;
        } else {
            this.dlDirTextField.setText(GITProperties.dlDir);
        }
    }

    private String validatePath(String myText) {
        if (myText.length() == 0)
            myText = "./";
        File file = new File(myText);
        String path = null;
        if (file.exists() && file.isDirectory() /*&& file.canWrite()*/) {
            path = file.getAbsolutePath();
        } else {
            JOptionPane.showMessageDialog(this, "\"" + myText
                    + "\" is not a valid directory!  Option not changed.");
        }
        return path;
    }

    protected void dlDirButtonPressed(ActionEvent evt) {
        // TODO Auto-generated method stub
        // FIXME show filechooser to get a selected dir and make sure we can
        // write to it!
        GITProperties.dlDir = chooseDirectory(dlDirTextField.getText());
        this.dlDirTextField.setText(GITProperties.dlDir);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hideUnsupported) {
            GITProperties.hideUnsupportedSongs = hideUnsupported.isSelected();
            System.out.println("changed!");
                GetItTogether.instance.localSongsMatcher.playerChanged();
                GetItTogether.instance.daapSongsMatcher.playerChanged();
        } else if (e.getSource() == player) {
            JComboBox box = (JComboBox)e.getSource();
            String s = (String)box.getSelectedItem();
            int oldType = GITProperties.playerType;
            if (s.equals("Java")) {
                GITProperties.playerType = PlayerUtils.JAVAPLAYER;
            }
            if (s.equals("Quicktime")) {
                GITProperties.playerType = PlayerUtils.QTPLAYER;
            }
            if (s.equals("External")) {
                GITProperties.playerType = PlayerUtils.EXTERNALPLAYER;
                PlayerUtils.loadNewPlayer(GITProperties.playerType);
                GetItTogether.instance.localSongsMatcher.playerChanged();
                GetItTogether.instance.daapSongsMatcher.playerChanged();
                external.show();
                revalidate();
            } else {
                external.hide();
                revalidate();
            }
            if (oldType != GITProperties.playerType)
                PlayerUtils.loadNewPlayer(GITProperties.playerType);
                System.out.println("New player: "+GITProperties.playerType);
        }
    }
}