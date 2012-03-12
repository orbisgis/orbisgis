package org.orbisgis.core.crs;

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

import org.gdms.data.SQLDataSourceFactory;
import org.jproj.CoordinateReferenceSystem;

public final class ProjectionConfigPanel extends JDialog implements ActionListener {

        private JPanel projectionPanel;
        private JTabbedPane projectionTabbedPane;
        private ProjectionTab projectionTab;
        private WKTTab wktTab;
        private JPanel buttonPanel;
        private JButton yesButton = null;
        private JButton noButton = null;
        private boolean answer = false;

        public ProjectionConfigPanel(SQLDataSourceFactory dsf, JFrame frame, boolean modal) {
                super(frame, modal);
                getContentPane().add(getProjectionPanel(dsf), BorderLayout.CENTER);
                getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
                setAlwaysOnTop(true);
                setSize(300, 400);
                setLocationRelativeTo(frame);
        }

        public boolean getAnswer() {
                return answer;
        }

        public JPanel getButtonPanel() {
                buttonPanel = new JPanel();
                yesButton = new JButton("Ok");
                yesButton.setActionCommand("OK");
                yesButton.addActionListener(this);
                buttonPanel.add(yesButton);
                noButton = new JButton("Cancel");
                noButton.setActionCommand("CANCEL");
                noButton.addActionListener(this);
                buttonPanel.add(noButton);
                return buttonPanel;
        }

        public JPanel getProjectionPanel(SQLDataSourceFactory dsf) {
                projectionPanel = new JPanel();
                projectionTabbedPane = new JTabbedPane();
                projectionTab = new ProjectionTab(dsf);
                CoordinateReferenceSystem crs = dsf.getCrsFactory().createFromName("EPSG:4326");
                //TODO towkt
                //String crsAsWKT = CRSUtil.getCRSFromEPSG("4326").toWkt();
                wktTab = new WKTTab(crs.getParameters().toString());
                projectionTabbedPane.addTab("Projections list", null, projectionTab,
                        null);
                projectionTabbedPane.addTab("WKT representation", null, wktTab, null);
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
                                        try {
                                                CoordinateReferenceSystem crs = projectionTab.getDsf().getCrsFactory().createFromName(sridASText);
                                                if (crs != null) {
                                                        wktTab.setWKT(crs.getParameterString());
                                                }
                                        } catch (Exception e1) {
                                                System.out.println(e1);
                                        }
                                }

                        }
                });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("OK".equals(command)) {
                        //TODO apply projection
                } else if ("CANCEL".equals(command)) {
                        setVisible(false);
                }
        }

        public static void main(String[] args) {
                new ProjectionConfigPanel(new SQLDataSourceFactory(), null, true).setVisible(true);

        }
}
