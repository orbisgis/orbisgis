package org.orbisgis.core.map.projection;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.cts.util.CRSUtil;
import fr.cts.util.CRSUtil;

public class ProjectionConfigPanel extends JDialog implements ActionListener {

	private JPanel projectionPanel;
	private JTabbedPane projectionTabbedPane;
	private ProjectionTab projectionTab;
	private WKTTab wktTab;
	private JPanel buttonPanel;
	private JButton yesButton = null;
	private JButton noButton = null;
	private boolean answer = false;

	public ProjectionConfigPanel(JFrame frame, boolean modal) {
		super(frame, modal);
		getContentPane().add(getProjectionPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		setUndecorated(true);
		setAlwaysOnTop(true);
		setSize(300, 200);
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	public boolean getAnswer() {
		return answer;
	}

	public JPanel getButtonPanel() {
		buttonPanel = new JPanel();
		yesButton = new JButton("Yes");
		yesButton.addActionListener(this);
		buttonPanel.add(yesButton);
		noButton = new JButton("No");
		noButton.addActionListener(this);
		buttonPanel.add(noButton);

		return buttonPanel;
	}

	public JPanel getProjectionPanel() {
		projectionPanel = new JPanel();
		projectionTabbedPane = new JTabbedPane();
		projectionTab = new ProjectionTab();
		wktTab = new WKTTab("");
		projectionTabbedPane.addTab("Projections list", null, projectionTab,
				null);
		projectionTabbedPane.addTab("WKT projection", null, wktTab, null);
		projectionTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		projectionPanel.setLayout(new GridLayout(1, 1));
		projectionPanel.add(projectionTabbedPane, BorderLayout.CENTER);
		addTabbedPaneListeners();

		return projectionPanel;
	}

	private void addTabbedPaneListeners() {
		projectionTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (sourceTabbedPane.getTitleAt(index).equals("WKT projection")) {
					String sridASText = projectionTab.getSRS();
					wktTab
							.setWKT(CRSUtil.getCRSFromEPSG(sridASText)
									.toString());
				}

			}

		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (yesButton == e.getSource()) {
			System.err.println("User chose yes.");
			answer = true;
			setVisible(false);
		} else if (noButton == e.getSource()) {
			System.err.println("User chose no.");
			answer = false;
			setVisible(false);
			dispose();
		}

	}

	public void setSelectedSRS(String srs) {

	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();

		frame.add(new ProjectionConfigPanel(frame, false));

		frame.show();
	}

}
