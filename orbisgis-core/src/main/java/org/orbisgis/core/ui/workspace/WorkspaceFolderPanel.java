package org.orbisgis.core.ui.workspace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.chainsaw.Main;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;

public class WorkspaceFolderPanel extends JPanel implements UIPanel {

	private static final String OUT_FILE_ID = "test";
	private JPanel panel;
	private OpenFilePanel outfilePanel;
	private JButton btFolder;

	public WorkspaceFolderPanel() {

		checkWorkSpace();
		panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 20));
		JLabel label = new JLabel("Workspace : ");
		 label.setAlignmentX(Component.LEFT_ALIGNMENT);
		final JComboBox combobox = new JComboBox(
				new String[] { "List de path" });
		combobox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		combobox.setPreferredSize(new Dimension(100, 20));
		btFolder = new JButton("Choose a folder");
		btFolder.setPreferredSize(new Dimension(30, 20));
		btFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				outfilePanel = new OpenFilePanel(OUT_FILE_ID,
						"Select a workspace folder");
				JFileChooser ret = outfilePanel.getFileChooser();
				ret.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				ret.setMultiSelectionEnabled(false);
				ret.setSelectedFile(new File("/"));
				outfilePanel.getFileChooser();
				if (UIFactory.showDialog(outfilePanel)) {
					String savedFile = outfilePanel.getSelectedFile()
							.getAbsolutePath();

					combobox.insertItemAt(savedFile, 0);
					combobox.setSelectedIndex(0);

				}

			}
		});
		panel.add(label);
		panel.add(combobox);
		panel.add(btFolder);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		this.add(panel);

	}

	private void checkWorkSpace() {
		String currentWorkspaceFolder = null;
		File defaultWorkspaceFolder = new File(
				System.getProperty("user.home"),Services.getService(ApplicationInfo.class).getName());
		
		/*if(workspaceFolder == null)
			currentWorkspaceFolder = defaultWorkspaceFolder.getAbsolutePath();
		else
			workspaceFolder.getAbsolutePath();*/
		
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

		if (UIFactory.showDialog(new WorkspaceFolderPanel())) {
			System.out.println("Ok");
		}
	}

}
