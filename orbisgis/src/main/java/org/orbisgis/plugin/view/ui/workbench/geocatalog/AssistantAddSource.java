package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class AssistantAddSource extends JDialog {

	/**
	 * This wizard will help the user to add a source It allows him to choose
	 * between flat files or databases
	 * 
	 * @author Samuel Chemla
	 */
	private static final long serialVersionUID = 1L;

	private AssistantActionListener acl = null;

	private AddSourceChoosePanel choosePanel = null;

	private JButton ok = null;

	private JButton cancel = null;

	boolean OK = false;

	public AssistantAddSource(JFrame ownerFrame) {
		super(ownerFrame, "Add a Datasource", true);
		setLayout(new CRFlowLayout());
		acl = new AssistantActionListener();

		choosePanel = new AddSourceChoosePanel();
		add(choosePanel);

		add(new CarriageReturn());

		ok = new JButton("OK");
		ok.setActionCommand("OK");
		ok.addActionListener(acl);
		add(ok);

		cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(acl);
		add(cancel);

		pack();
		setVisible(true);
	}

	/**
	 * Tells what action did the user
	 * 
	 * @return true is the user pressed OK
	 */
	public boolean userSayOk() {
		return OK;
	}

	/**
	 * Retrieves the datas provided by the user, wether it is database
	 * parameters or files
	 */
	public Object getData() {
		return choosePanel.getData();
	}

	private class AssistantActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if ("OK".equals(e.getActionCommand())) {
				OK = true;
				setVisible(false);

			} else if ("CANCEL".equals(e.getActionCommand())) {
				OK = false;
				setVisible(false);
			}
		}
	}
}