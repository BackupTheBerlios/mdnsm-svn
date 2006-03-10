/*
 * Created on Sep 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

import org.git.GITProperties;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GITSongTable extends JPanel {
    
    protected SongTableModel model;
    protected TableSorter sorter;
    protected JTextField search_field;
    protected JTable table;
    protected JLabel totalsLabel;
    protected String last_search;
    protected JLabel search_label;
    
    public GITSongTable(String s) {
        super();
        setName(s);
        initialize();
        addSearch();
        addTable();
        addTotals();
    }
	
    public GITSongTable(String s, boolean removeSearch) {
        super();
        setName(s);
        initialize();
        if (!removeSearch)
            addSearch();
        addTable();
        addTotals();
    }
    
    public void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(300, 200));
    }
    
    public void addSearch() {
        search_field = new JTextField();
        search_field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                //    search(e);
            }
            
            public void insertUpdate(DocumentEvent e) {
                search(e);
            }
            
            public void removeUpdate(DocumentEvent e) {
                
                search(e);
            }
            
            public void search(DocumentEvent e) {
                if (GITProperties.searchEveryKey)
                {
                    System.out.println("searching every key...");
                    String s = "";
                    try {
                        s = search_field.getDocument().getText(0,
                                search_field.getDocument().getLength());
                    } catch (BadLocationException g) {
                        System.out.println("bad location");
                    };
                    try {
                        model.search(s);
                        model.fireTableDataChanged();
                        sorter.fireTableDataChanged();
                        totalsLabel.setText(model.getTotals());
                    } catch (Exception f) {
                        f.printStackTrace();
                    }
                }
            }
        });
        
        search_field.setMaximumSize(new Dimension(1000, 21));
        search_field.setMinimumSize(new Dimension(100, 21));
        search_field.setPreferredSize(new Dimension(100, 21));
        
        search_label = new JLabel("Search:");
        search_label.setHorizontalTextPosition(JLabel.TRAILING);
        search_label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(search_label);
        searchPanel.add(search_field);
        
        add(searchPanel);
    }

    public void addTotals() {

        totalsLabel = new JLabel();
		totalsLabel.setText(model.getTotals());
		totalsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel totalsPanel = new JPanel();
		totalsPanel.add(totalsLabel);
//		totalsPanel.setMaximumSize(totalsPanel.getPreferredSize());
		
		add(totalsLabel);
    }
    
    public void addTable() {
        model = new SongTableModel();
        sorter = new TableSorter(model);
        table = new JTable();
        sorter.setTableHeader(table.getTableHeader());
        table.setModel(sorter);
        table.setFocusTraversalKeysEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setShowHorizontalLines(false);
        table.setGridColor(new Color(180, 180, 180));
        table.getColumnModel().setColumnMargin(1);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setDoubleBuffered(false);
		for (int i = 0; i <= 5; i++) {
		    TableColumn column = table.getColumnModel().getColumn(i);
		    //			column.setCellRenderer(browserRender);
		    switch (i) {
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
		        //					column.setCellEditor(new SeekEditor());
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
			    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					ArrayList a = new ArrayList();
					int[] ind = table.getSelectedRows();
					for (int i = 0; i < ind.length; i++) {
						a.add(sorter.getSongAt(ind[i]));
					}
//					clearAndPlaySongs(a);
					for (int i = 0; i < ind.length; i++) {
						table.addRowSelectionInterval(ind[i], ind[i]);
					}
				}
			    if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 1
						&& table.getColumnName(table.columnAtPoint(e.getPoint())) == "Title") {
//						&& table.rowAtPoint(e.getPoint()) == sorter.indexOf(playingSong)) {
//					showElapsed = (showElapsed ? false : true);
					sorter.fireTableCellUpdated(table.rowAtPoint(e.getPoint()), 3);
//					dspmodel.fireTableRowsUpdated(0, 0);
				}
			}

			public void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu tPopup = new JPopupMenu();
					JMenuItem play = new JMenuItem("Play Song(s)");
//					play.addActionListener(clearPlay);
					tPopup.add(play);
					JMenuItem queue = new JMenuItem("Queue Song(s)");
//					queue.addActionListener(queueNoPlay);
					tPopup.add(queue);
					JMenuItem downloader = new JMenuItem("Download Song(s)");
//					downloader.addActionListener(download);
					tPopup.add(downloader);
					JMenuItem showOnly = new JMenuItem();
					final int row = table.rowAtPoint(e.getPoint());
					final int col = table.columnAtPoint(e.getPoint());
					showOnly.setText("Show only this " + table.getColumnName(col));
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
		
		JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroller);
        
    }
    
    public void searchText(String text) {
		try {
//			search_field.setText(text);
			model.search(text);
			model.fireTableDataChanged();
			sorter.fireTableDataChanged();
			totalsLabel.setText(model.getTotals());
//			last_search = text;
		} catch (Exception f) {
			f.printStackTrace();
		}

	}    
}
