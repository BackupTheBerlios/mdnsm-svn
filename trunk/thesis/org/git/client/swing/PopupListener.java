/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class PopupListener extends MouseAdapter {
	JPopupMenu	popup;

	/**
	 * Constructor for the PopupListener object
	 * 
	 * @param p
	 *          Description of the Parameter
	 */
	public PopupListener(JPopupMenu p) {
		popup = p;
	}

	public PopupListener() {
	    popup = null;
	}
	
	/**
	 * Description of the Method
	 * 
	 * @param e
	 *          Description of the Parameter
	 */
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *          Description of the Parameter
	 */
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *          Description of the Parameter
	 */
	public void maybeShowPopup(MouseEvent e) {
		boolean selection_contains_point = false;
		if (e.isPopupTrigger()) {
			JTable table = (JTable) e.getComponent();
			int thisRow = table.rowAtPoint(e.getPoint());
			int minSel = table.getSelectionModel().getMinSelectionIndex();
			int maxSel = table.getSelectionModel().getMaxSelectionIndex();
			if (thisRow >= minSel && thisRow <= maxSel)
			    selection_contains_point = true;
			if (!selection_contains_point) {
				table.setRowSelectionInterval(table.rowAtPoint(e.getPoint()), table
						.rowAtPoint(e.getPoint()));
			}
			showPopup(e);
		}
	}
	
	public void showPopup(MouseEvent e) {
	    if (popup != null)
	        popup.show(e.getComponent(), e.getX(), e.getY());
	}
}
