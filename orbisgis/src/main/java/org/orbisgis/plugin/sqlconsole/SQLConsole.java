package org.orbisgis.plugin.sqlconsole;

import javax.swing.JTabbedPane;

import org.orbisgis.plugin.sqlconsole.ui.SQLConsolePanel;





public class SQLConsole extends JTabbedPane{

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

		this.addTab("SQL Console", null, new SQLConsolePanel(), null);
	}
}
