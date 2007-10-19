package org.sif;

import java.awt.Frame;
import java.awt.Window;
import java.net.URL;

import javax.swing.JDialog;

public class UIFactory {

	public static SIFDialog getSimpleDialog(UIPanel panel) {
		return getSimpleDialog(panel, null);
	}

	public static SIFDialog getSimpleDialog(UIPanel panel, Window owner) {
		SIFDialog dlg = new SIFDialog(owner);
		SimplePanel simplePanel = new SimplePanel(dlg, panel);
		dlg.setComponent(simplePanel);
		return dlg;
	}

	public static DynamicUIPanel getDynamicUIPanel(String title, URL icon,
			String[] names) {
		return new DynamicUIPanel(title, icon, names, new int[0],
				new String[0], new String[0]);
	}

	public static DynamicUIPanel getDynamicUIPanel(String title, URL icon,
			String[] names, int[] types, String[] expressions,
			String[] errorMsgs) {
		return new DynamicUIPanel(title, icon, names, types, expressions,
				errorMsgs);
	}

	public static JDialog getWizard(UIPanel[] panels) {
		return getWizard(panels, null);
	}

	private static JDialog getWizard(UIPanel[] panels, Frame owner) {
		SIFWizard dlg = new SIFWizard(owner);
		SimplePanel[] simplePanels = new SimplePanel[panels.length];
		for (int i = 0; i < simplePanels.length; i++) {
			simplePanels[i] = new SimplePanel(dlg, panels[i]);
		}
		dlg.setComponent(simplePanels);
		return dlg;
	}
}
