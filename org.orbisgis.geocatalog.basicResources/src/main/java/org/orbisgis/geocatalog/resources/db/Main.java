package org.orbisgis.geocatalog.resources.db;

import org.sif.UIFactory;
import org.sif.UIPanel;

public class Main {
	public static void main(String[] args) {
		if (UIFactory.showDialog(prepareUIPanel())) {
		}
	}

	private static UIPanel[] prepareUIPanel() {
		final FirstUIPanel firstPanel = new FirstUIPanel();
		return new UIPanel[] { firstPanel, new SecondUIPanel(firstPanel) };
	}
}