/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.git.client.Song;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlazedGITSongJPanel extends JPanel implements ListEventListener{

    public EventList source;
    public EventList visible;
    public JLabel search_label;
    public JTextField search_field;
    public JTable table;
    public JLabel totals_label;
    public FilterList vis_filter;
    public VisibleHostMatcherEditor vis_matcher;
    
    public JSplitPane split_pane;
    public SongInfoPanel info_view;
    public JLabel info_button;
    public JLabel info_label;
    public JPanel info_label2;
    
    public GlazedGITSongJPanel() {
        super();
    }

    public void listChanged(ListEvent le) {
        while (le.hasNext()) {
            le.next();
        }
        updateTotals();
    }
    
    public void updateTotals() {
		int songs        = 0;
		long tme         = 0;
		double sze       = 0;
		if (source == null) {
		    totals_label.setText("0 songs, 0.0 years, 0.0 GB");
		    return;
		}
		visible.getReadWriteLock().readLock().lock();
		for (int i = 0; i < visible.size(); i++) {
			Song s  = (Song) visible.get(i);
			songs++;
			tme = tme + s.time;
			sze = sze + s.size;
		}
		visible.getReadWriteLock().readLock().unlock();
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
		totals_label.setText(totals);
	}
    
}
