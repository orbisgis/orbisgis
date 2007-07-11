package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AddSqlQuery extends JDialog {

	private JPanel upperPanel = null;

	private JPanel lowerPanel = null;

	private JTextField name = null;

	private JTextField query = null;

	private JButton ok = null;

	private JButton cancel = null;

	private boolean OK = false;

	private AssistantActionListener acl = null;

	private MyKeyListener kl = null;

	AddSqlQuery(JFrame jFrame) {
		super(jFrame, "Add a SQL query", true);
		setLayout(new CRFlowLayout());
		acl = new AssistantActionListener();
		kl = new MyKeyListener();

		/** *** UPPERPANEL **** */
		upperPanel = new JPanel();
		upperPanel.setLayout(new GridLayout(2, 4, 10, 10));
		upperPanel.add(new JLabel("Name"));
		name = new JTextField();
		name.addKeyListener(kl);
		upperPanel.add(name);
		upperPanel.add(new JLabel("Query"));
		query = new JTextField();
		query.addKeyListener(kl);
		upperPanel.add(query);

		/** *** LOWER PANEL **** */
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new FlowLayout());

		ok = new JButton("OK");
		ok.setActionCommand("OK");
		ok.addActionListener(acl);
		ok.setEnabled(false);
		lowerPanel.add(ok);

		cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(acl);
		lowerPanel.add(cancel);

		add(upperPanel);
		add(new CarriageReturn());
		add(lowerPanel);
		pack();
		setVisible(true);
	}

	public String getName() {
		return name.getText();
	}

	public String getQuery() {
		return query.getText();
	}

	/**
	 * Tells what action did the user
	 * 
	 * @return true is the user pressed OK
	 */
	public boolean userSayOk() {
		return OK;
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

	public class MyKeyListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		public void keyTyped(KeyEvent e) {
			if (!name.getText().isEmpty() && !query.getText().isEmpty()) {
				ok.setEnabled(true);
			} else ok.setEnabled(false);
		}

	}

}
