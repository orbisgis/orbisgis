package org.orbisgis.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.sif.SIFDialog;
import org.orbisgis.sif.SQLUIPanel;
import org.orbisgis.sif.UIFactory;

public class ErrorPanel extends JPanel implements SQLUIPanel {

	private JTextArea txtError;
	private JCheckBox chkSend;
	private String title;

	public ErrorPanel() {
		this.setLayout(new BorderLayout());
		txtError = new JTextArea();
		txtError.setBorder(null);
		txtError.setOpaque(false);
		txtError.setEditable(false);
		this.add(txtError, BorderLayout.CENTER);
		chkSend = new JCheckBox("Send the log in '"
				+ Services.getService(ApplicationInfo.class).getLogFile()
				+ "' to help OrbisGIS team to solve this problem");
		this.add(chkSend, BorderLayout.SOUTH);
	}

	public void show(String title, String message) {
		this.title = title;
		this.txtError.setText(message);
		SIFDialog dlg = UIFactory.getSimpleDialog(this, false);
		dlg.setModal(true);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		dlg.setMaximumSize(new Dimension(size.width / (int) 1.1, size.height
				/ (int) 1.1));
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
	}

	@Override
	public String[] getErrorMessages() {
		return null;
	}

	@Override
	public String[] getFieldNames() {
		return new String[] { "chk" };
	}

	@Override
	public int[] getFieldTypes() {
		return new int[] { STRING };
	}

	@Override
	public String getId() {
		return "org.orbisgis.ui.error";
	}

	@Override
	public String[] getValidationExpressions() {
		return null;
	}

	@Override
	public String[] getValues() {
		return new String[] { Boolean.toString(chkSend.isSelected()) };
	}

	@Override
	public void setValue(String fieldName, String fieldValue) {
		chkSend.setSelected(Boolean.parseBoolean(fieldValue));
	}

	@Override
	public boolean showFavorites() {
		return false;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getInfoText() {
		return "";
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		return null;
	}

	public boolean sendLog() {
		return chkSend.isSelected();
	}
}
