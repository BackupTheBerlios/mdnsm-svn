/*
 * Created on Jun 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SongInfoPanel extends JPanel {

    protected JLabel title;
    protected JLabel artist;
    protected JLabel album;
    protected JLabel artwork;
    
    protected JLabel bitrate;
    protected JLabel track;
    protected JLabel length;
    protected JLabel host;
    protected JLabel size;
    protected JLabel browse;
    protected JLabel genre;
    
    public JPanel right_panel;
    public JPanel left_panel;
    
    public SongInfoPanel() {
        super();
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        
        
        JLabel label;
        Field[] fields = this.getClass().getDeclaredFields();
        String str;
        for (int i=0; i < fields.length; i++) {
            if (fields[i].getType().equals(JLabel.class)) {
                str = fields[i].getName();
                str = str.substring(0,1).toUpperCase() + str.substring(1,str.length());
                final String s = str;
                label = new JLabel("testing") {
                    public void setText(String string) {
                        super.setText("<html><b>"+s + ": </b>"+string);
                    }
                };
                label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                try {
                    fields[i].set(this,label);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        artwork = new JLabel();
        artwork.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        artwork.setMinimumSize(new Dimension(80,80));
        add(artwork);
        
        left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel,BoxLayout.Y_AXIS));
        left_panel.add(title);
        left_panel.add(artist);
        left_panel.add(album);
        add(left_panel);
        
        add(Box.createHorizontalGlue());
        
        right_panel = new JPanel();
        right_panel.setLayout(new GridLayout(0,2));
        right_panel.add(browse);
        right_panel.add(size);
        right_panel.add(length);
        right_panel.add(bitrate);
        right_panel.add(track);
        right_panel.add(genre);
        right_panel.add(host);
        add(right_panel);
    }
    
}
