package org.orbisgis.action;

import javax.swing.JMenu;

public class JActionMenu extends JMenu implements IMenuActionControl {

	private String id;

	public JActionMenu(String id, String text) {
		super(text);
		this.id = id;
	}

	@Override
	public String getGroup() {
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setActionAdapter(IActionAdapter actionAdapter) {
	}

}
