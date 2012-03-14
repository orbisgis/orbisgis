package org.orbisgis.core.crs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gdms.data.SQLDataSourceFactory;

public final class ProjectionConfigPanel extends JDialog implements ActionListener {

        private ProjectionTable projectionTab;
        private JPanel buttonPanel;
        private JButton yesButton = null;
        private JButton noButton = null;

        public ProjectionConfigPanel(SQLDataSourceFactory dsf, JFrame frame, boolean modal) {
                super(frame, modal);
                getContentPane().add(getProjectionPanel(dsf), BorderLayout.CENTER);
                getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
                setAlwaysOnTop(true);
                setSize(300, 500);
                setLocationRelativeTo(frame);
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
                if (projectionTab == null) {
                        projectionTab = new ProjectionTable(dsf);
                }
                return projectionTab;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("OK".equals(command)) {
                        //TODO apply projection
                        
                        //Save the CRS history
                        projectionTab.saveCRSHistory();
                } else if ("CANCEL".equals(command)) {
                        setVisible(false);
                }
        }

        public static void main(String[] args) {
                new ProjectionConfigPanel(new SQLDataSourceFactory(), null, true).setVisible(true);

        }
}
