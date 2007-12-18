package org.sif;

import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.gdms.data.DataSourceFactory;

public class UIFactory {

	private static HashMap<String, String> inputs = new HashMap<String, String>();
	static File baseDir = new File(System.getProperty("user.home")
			+ File.separator + ".sif");
	private static URL defaultIconURL;
	private static String okMessage;
	static final DataSourceFactory dsf = new DataSourceFactory();

	public static SIFDialog getSimpleDialog(UIPanel panel) {
		return getSimpleDialog(panel, null);
	}

	public static SIFDialog getSimpleDialog(UIPanel panel, Window owner) {
		SIFDialog dlg = new SIFDialog(owner);
		SimplePanel simplePanel = new SimplePanel(dlg, panel);
		dlg.setComponent(simplePanel, inputs);
		return dlg;
	}

	public static DynamicUIPanel getDynamicUIPanel(String title, URL icon,
			String[] names) {
		return getDynamicUIPanel(null, title, icon, names, new int[0],
				new String[0], new String[0]);
	}

	public static DynamicUIPanel getDynamicUIPanel(String title, URL icon,
			String[] names, int[] types, String[] expressions,
			String[] errorMsgs) {
		return getDynamicUIPanel(null, title, icon, names, types, expressions,
				errorMsgs);
	}

	public static DynamicUIPanel getDynamicUIPanel(String id, String title,
			URL icon, String[] names) {
		return new DynamicUIPanel(id, title, icon, names, new int[0],
				new String[0], new String[0]);
	}

	public static DynamicUIPanel getDynamicUIPanel(String id, String title,
			URL icon, String[] names, int[] types, String[] expressions,
			String[] errorMsgs) {
		return new DynamicUIPanel(id, title, icon, names, types, expressions,
				errorMsgs);
	}

	public static SIFWizard getWizard(UIPanel[] panels) {
		return getWizard(panels, null);
	}

	private static SIFWizard getWizard(UIPanel[] panels, Frame owner) {
		SIFWizard dlg = new SIFWizard(owner);
		SimplePanel[] simplePanels = new SimplePanel[panels.length];
		for (int i = 0; i < simplePanels.length; i++) {
			simplePanels[i] = new SimplePanel(dlg, panels[i]);
		}
		dlg.setComponent(simplePanels, inputs);
		return dlg;
	}

	public static boolean showDialog(UIPanel[] panels) {
		AbstractOutsideFrame dlg;
		if (panels.length == 0) {
			throw new IllegalArgumentException(
					"At least a panel has to be specified");
		} else if (panels.length == 1) {
			dlg = getSimpleDialog(panels[0]);
		} else {
			dlg = getWizard(panels);
		}
		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);

		return dlg.isAccepted();
	}

	public static boolean showDialog(UIPanel panel) {
		return showDialog(new UIPanel[] { panel });
	}

	public static void setInputFor(String id, String inputName) {
		inputs.put(id, inputName);
	}

	public static void setPersistencyDirectory(File baseDir) {
		if (!baseDir.exists()) {
			throw new IllegalArgumentException(baseDir + " doesn't exist");
		}
		UIFactory.baseDir = baseDir;
	}

	public static void setTempDirectory(File tempDir) {
		if (!tempDir.exists()) {
			throw new IllegalArgumentException(tempDir + " doesn't exist");
		}
		dsf.setTempDir(tempDir.getAbsolutePath());
	}

	public static URL getDefaultIcon() {
		return defaultIconURL;
	}

	public static void setDefaultIcon(URL iconURL) {
		UIFactory.defaultIconURL = iconURL;
	}

	public static String getDefaultOkMessage() {
		return okMessage;
	}

	public static void setDefaultOkMessage(String msg) {
		okMessage = msg;
	}
}
