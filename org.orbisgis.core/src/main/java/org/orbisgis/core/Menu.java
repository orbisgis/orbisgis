package org.orbisgis.core;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

public class Menu {

	private String parent;

	private String id;

	private String text;

	private ArrayList<Menu> childs = new ArrayList<Menu>();

	private String icon;

	private JToggleButton button;

	public Menu(String parent, String id, String text, String icon) {
		super();
		this.parent = parent;
		this.id = id;
		this.text = text;
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public JMenuItem getJMenuItem(ActionListener al) {
		JMenuItem ret;
		if (childs.size() > 0) {
			ret = new JMenu(text);
			for (int i = 0; i < childs.size(); i++) {
				ret.add(childs.get(i).getJMenuItem(al));
			}
		} else {
			if (button != null) {
				ret = new SyncRadioButtonMenuItem(text, button);
			} else {
				ret = new JMenuItem(text);
			}
			ret.addActionListener(al);
			ret.setActionCommand(id);
		}
		if (icon != null) {
			ret.setIcon(new ImageIcon(getClass().getResource(icon)));
		}

		return ret;
	}

	public void addChild(Menu menu) {
		childs.add(menu);
	}

	public Menu[] getChilds() {
		return childs.toArray(new Menu[0]);
	}

	public void setRelatedToggleButton(JToggleButton btn) {
		this.button = btn;
	}

}
