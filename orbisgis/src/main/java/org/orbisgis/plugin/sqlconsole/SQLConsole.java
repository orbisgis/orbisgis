package org.orbisgis.plugin.sqlconsole;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import javax.swing.JButton;

import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.sqlconsole.ui.SQLConsolePanel;





public class SQLConsole extends JTabbedPane{
	
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanel = null;
	/**
	 * This is the default constructor
	 */
	public SQLConsole() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setToolTipText("SQL console");
		this.setName("SQL console");		

		this.addTab(null, null, new SQLConsolePanel(), null);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);		
		}
		return jPanel;
	}

	

}
