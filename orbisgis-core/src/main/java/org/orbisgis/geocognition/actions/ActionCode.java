package org.orbisgis.geocognition.actions;

import java.util.ArrayList;

import org.orbisgis.geocognition.sql.Code;

public class ActionCode extends Code {

	private String group = null;
	private String menuId = null;
	private String text = "Action";

	private ArrayList<ActionPropertyChangeListener> listeners = new ArrayList<ActionPropertyChangeListener>();

	public ActionCode(String code) {
		super(code);
	}

	public void addActionPropertyListener(ActionPropertyChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeActionPropertyListener(
			ActionPropertyChangeListener listener) {
		this.listeners.remove(listener);
	}

	private void callListeners(String propertyName, String oldValue,
			String newValue) {
		for (ActionPropertyChangeListener listener : listeners) {
			listener.propertyChanged(propertyName, newValue, oldValue);
		}
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		String oldGroup = this.group;
		this.group = group;
		callListeners("group", oldGroup, group);
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		String oldMenuId = this.menuId;
		this.menuId = menuId;
		callListeners("menuId", oldMenuId, menuId);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldText = this.text;
		this.text = text;
		callListeners("text", oldText, text);
	}

}
