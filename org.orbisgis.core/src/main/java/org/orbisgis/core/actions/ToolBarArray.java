package org.orbisgis.core.actions;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JToolBar;

public class ToolBarArray {
	private HashMap<String, JToolBar> idToolBar = new HashMap<String, JToolBar>();
	private ArrayList<String> orderedToolBarIds = new ArrayList<String>();

	public void put(String id, JToolBar toolBar) {
		idToolBar.put(id, new JToolBar(id));
		orderedToolBarIds.add(id);

	}

	public JToolBar get(String toolBarId) {
		return idToolBar.get(toolBarId);
	}

	public JToolBar[] getToolBars() {
		JToolBar[] ret = new JToolBar[orderedToolBarIds.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = idToolBar.get(orderedToolBarIds.get(i));
		}

		return ret;
	}

}
