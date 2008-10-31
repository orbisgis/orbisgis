package org.orbisgis.geocognition.actions;

import org.orbisgis.geocognition.sql.Code;

public class ActionCode extends Code {
	
	private String group = null;
	private String menuId = null;
	private String text = "Action";

	public ActionCode(String code) {
		super(code);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
