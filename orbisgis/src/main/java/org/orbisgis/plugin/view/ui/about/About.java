package org.orbisgis.plugin.view.ui.about;


import javax.swing.JPanel;
import javax.swing.JFrame;



import java.awt.Rectangle;

import javax.swing.JEditorPane;



public class About extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JEditorPane jEditorPane = null;

	/**
	 * This is the default constructor
	 */
	public About() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(475, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("About GeoView2D ");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJEditorPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setBounds(new Rectangle(12, 11, 439, 147));
			jEditorPane.setText(Messages.getString("About"));
		}
		return jEditorPane;
	}

}  
