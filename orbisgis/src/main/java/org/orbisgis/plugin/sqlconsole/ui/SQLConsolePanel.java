package org.orbisgis.plugin.sqlconsole.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.plugin.sqlconsole.actions.ActionsListener;



public class SQLConsolePanel extends JPanel{

	
	public static JTextArea jTextArea;
	private JButton executeBT = null;
	private JButton eraseBT = null;
	
	
	private JButton saveQuery = null;
	private JButton openQuery = null;
	private JButton stopQueryBt = null;
	private JButton connectionBt = null;
	private JScrollPane jScrollPane = null;
	public static DefaultMutableTreeNode racine;
	 static DefaultTreeModel m_model;
	
	public static JButton jButtonNext = null;
	public static JButton jButtonPrevious = null;
	static JButton tableViewBt = null;

	ActionsListener acl = new ActionsListener();
	

	/**
	 * This is the default constructor
	 */
	public SQLConsolePanel() {
		super();	
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {		
		this.setLayout(new BorderLayout());		
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getJScrollPane(),BorderLayout.CENTER);
	}
	
	
	private JPanel getNorthPanel() {
		
		JPanel northPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		northPanel.setLayout(flowLayout);
		
		northPanel.add(getJScrollPane(), null);
		northPanel.add(getExecuteBT(), null);		
		northPanel.add(getEraseBT(), null);
		northPanel.add(getStopQueryBt(), null);
		
		northPanel.add(getJButtonPrevious(), null);
		northPanel.add(getJButtonNext(), null);
		
		northPanel.add(getOpenQuery(), null);
		northPanel.add(getSaveQuery(), null);
				
		return northPanel;	
	
	}
	
	
	
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExecuteBT() {
		if (executeBT == null) {
			executeBT = new JButton();
			executeBT.setMargin(new Insets(0,0,0,0));
			executeBT.setText("");
			executeBT.setToolTipText("Click to execute query");
			executeBT.setIcon(new ImageIcon(getClass().getResource("Execute.png")));
			executeBT.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
			executeBT.setActionCommand("EXECUTE");
			executeBT.addActionListener(acl);
			
						
		}
		return executeBT;
	}
	

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);						
			jTextArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		}
		return jTextArea;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEraseBT() {
		if (eraseBT == null) {
			eraseBT = new JButton();
			eraseBT.setMargin(new Insets(0,0,0,0));
			eraseBT.setText("");
			eraseBT.setIcon(new ImageIcon(getClass().getResource("Erase.png")));
			eraseBT.setFont(new Font("Dialog", Font.BOLD, 10));
			eraseBT.setToolTipText("Clear the query");
			eraseBT.setActionCommand("ERASE");
			eraseBT.addActionListener(acl);
		}
		return eraseBT;
	}
	
	

	/**
	 * This method initializes saveQuery	
	 * 	
	 * Elle permet d'ouvrir une interface d'ouverture de fenetre.
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveQuery() {
		if (saveQuery == null) {
			saveQuery = new JButton();
			saveQuery.setMargin(new Insets(0,0,0,0));
			saveQuery.setIcon(new ImageIcon(getClass().getResource("Save.png")));
			saveQuery.setActionCommand("SAVEQUERY");
			saveQuery.addActionListener(acl);
			
		
		}
		return saveQuery;
	}

	/**
	 * This method initializes openQuery	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenQuery() {
		if (openQuery == null) {
			openQuery = new JButton();
			openQuery.setMargin(new Insets(0,0,0,0));
			
			openQuery.setIcon(new ImageIcon(getClass().getResource("Open.png")));
			openQuery.setActionCommand("OPENSQLFILE");
			openQuery.addActionListener(acl);
		}
		return openQuery;
	}

	/**
	 * This method initializes stopQueryBt	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopQueryBt() {
		if (stopQueryBt == null) {
			stopQueryBt = new JButton();
			stopQueryBt.setMargin(new Insets(0,0,0,0));
			stopQueryBt.setFont(new Font("Dialog", Font.BOLD, 10));
			stopQueryBt.setToolTipText("Stop the query");
			stopQueryBt.setIcon(new ImageIcon(getClass().getResource("Stop.png")));
			stopQueryBt.setText("");
			stopQueryBt.setMnemonic(KeyEvent.VK_UNDEFINED);
			stopQueryBt.setEnabled(true);
		}
		return stopQueryBt;
	}

	

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(3, 37, 383, 188));
						
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	

	/**
	 * This method initializes jButtonNext	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonNext() {
		if (jButtonNext == null) {
			jButtonNext = new JButton();
			jButtonNext.setMargin(new Insets(0,0,0,0));			
			jButtonNext.setIcon(new ImageIcon(getClass().getResource("go-next.png")));
			
			jButtonNext.setFont(new Font("Dialog", Font.BOLD, 10));
			jButtonNext.setToolTipText("Next query");
			jButtonNext.setActionCommand("NEXT");
			jButtonNext.addActionListener(acl);
			
		}
		return jButtonNext;
	}

	/**
	 * This method initializes jButtonPrevious	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPrevious() {
		if (jButtonPrevious == null) {
			jButtonPrevious = new JButton();
			jButtonPrevious.setMargin(new Insets(0,0,0,0));
			
			jButtonPrevious.setIcon(new ImageIcon(getClass().getResource("go-previous.png")));
			
			jButtonPrevious.setFont(new Font("Dialog", Font.BOLD, 10));
			jButtonPrevious.setToolTipText("Previous query");
			
			jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            
	            	
	                                
	            }
	        });	
		
		
		
		}
		return jButtonPrevious;
	}

	   
	    	
	    	
	   
	
	
}
