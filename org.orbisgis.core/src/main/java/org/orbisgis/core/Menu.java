package org.orbisgis.core;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

public class Menu implements IMenu {

	String parent;

	String id;

	String text;

	private ArrayList<IMenu> childs = new ArrayList<IMenu>();

	private String icon;

	private JToggleButton button;

	private HashMap<String, ArrayList<IMenu>> groups = new HashMap<String, ArrayList<IMenu>>();

	private String group;

	private ActionListener al;

	public Menu(String parent, String id, String group, String text,
			String icon, ActionListener al) {
		super();
		this.parent = parent;
		this.id = id;
		this.text = text;
		this.icon = icon;
		this.group = group;
		this.al = al;
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

	/**
	 * @return
	 */
	public JComponent getJMenuItem() {
		JMenuItem ret;
		if (childs.size() > 0) {
			ret = new JMenu(text);
			for (int i = 0; i < childs.size(); i++) {
				ret.add(childs.get(i).getJMenuItem());
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

		ret.setName(id);
		return ret;
	}

	public void addChild(IMenu menu) {
		childs.add(menu);

		String menuGroup = menu.getGroup();
		if (menuGroup == null) {
			menuGroup = "_default";
		}
		ArrayList<IMenu> menusInGroup = groups.get(menuGroup);
		if (menusInGroup == null) {
			menusInGroup = new ArrayList<IMenu>();
		}
		menusInGroup.add(menu);
		groups.put(menuGroup, menusInGroup);
	}

	public String getGroup() {
		return group;
	}

	public IMenu[] getChilds() {
		return childs.toArray(new IMenu[0]);
	}

	public void setRelatedToggleButton(JToggleButton btn) {
		this.button = btn;
	}

	public void groupMenus() {
		ArrayList<IMenu> newChilds = new ArrayList<IMenu>();
		Iterator<String> it = groups.keySet().iterator();
		boolean separator = false;
		while (it.hasNext()) {
			if (separator) {
				newChilds.add(new MenuSeparator());
			}
			separator = true;
			String group = it.next();
			ArrayList<IMenu> menusInGroup = groups.get(group);
			for (IMenu menu : menusInGroup) {
				newChilds.add(menu);
				menu.groupMenus();
			}
		}

		childs = newChilds;
	}

}
