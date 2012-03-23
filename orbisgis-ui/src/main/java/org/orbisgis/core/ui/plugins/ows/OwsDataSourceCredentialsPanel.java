/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;

/**
 *
 * @author cleglaun
 */
public class OwsDataSourceCredentialsPanel extends AbstractUIPanel {
    
    private final DataSourceFactory dsf = new DataSourceFactory();
    private static final Dimension LABELS_DIMENSION = new Dimension(100, 20);
    private JPanel panel;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private String validateInputMessage;
    private OwsDataSourceCredentialsRequiredListener credentialsListener;
    private final int currentSourceNumber;
    private final int nbSourcesToCheck;
    
    public OwsDataSourceCredentialsPanel(final DBSource db, int currentSourceNumber, 
            int nbSourcesToCheck) {
        this.validateInputMessage = "";
        this.currentSourceNumber = currentSourceNumber;
        this.nbSourcesToCheck = nbSourcesToCheck;
        
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        
        final JTextField txtHost = new JTextField(15) {
            {
                setText(db.getHost());
                setEnabled(false);
            }
        };
        
        final JTextField txtDbName = new JTextField(15) {
            {
                setText(db.getDbName());
                setEnabled(false);
            }
        };
        
        final JTextField txtTable = new JTextField(15) {
            {
                setText(db.getTableName());
                setEnabled(false);
            }
        };
        
        final JLabel lblHost = new JLabel(Names.LABEL_OWS_HOST + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        final JLabel lblDbName = new JLabel(Names.LABEL_OWS_DB + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        final JLabel lblTable = new JLabel(Names.LABEL_OWS_TABLE + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        final JLabel lblUsername = new JLabel(Names.LABEL_OWS_USERNAME + ": ") {

            {
                setPreferredSize(LABELS_DIMENSION);
            }
            
        };
        final JLabel lblPassword = new JLabel(Names.LABEL_OWS_PASSWORD + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };

        
        final JPanel pnlHost = new JPanel() {
            {
                add(lblHost);
                add(txtHost);
            }
        };
        
        final JPanel pnlDbName = new JPanel() {
            {
                add(lblDbName);
                add(txtDbName);
            }
        };
        
        final JPanel pnlTable = new JPanel() {
            {
                add(lblTable);
                add(txtTable);
            }
        };
        
        final JPanel pnlUsername = new JPanel() {
            {
                add(lblUsername);
                add(txtUsername);
            }
        };
        
        final JPanel pnlPassword = new JPanel() {
            {
                add(lblPassword);
                add(txtPassword);
            }
        };
        
        final JButton cmdOk = new JButton(Names.BUTTON_OWS_VALIDATE) {
            {
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        validateInputMessage = "";
                        boolean currentDataSourceCredentialsAreOk = true;
                        
                        db.setUser(txtUsername.getText());
                        db.setPassword(new String(txtPassword.getPassword()));
                        
                        try {
                            // TODO: Checks if the credentials are correct
                            
                            DataSource ds = dsf.getDataSource(db);
                            ds.open();
                            ds.close();
                        } catch (DataSourceCreationException ex) {
                            Logger.getLogger(OwsDataSourceCredentialsPanel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DriverException ex) {
                            currentDataSourceCredentialsAreOk = false;
                            validateInputMessage = Names.LABEL_USERNAME_OR_PASSWORD_INVALID;
                            Logger.getLogger(OwsDataSourceCredentialsPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        if (currentDataSourceCredentialsAreOk) {
                            OwsDataSourceCredentialsPanel.this.credentialsListener.credentialsOk(db);
                        }
                    }
                });
            }
        };
        
        panel = new JPanel(new GridLayout(6, 1)) {
            {
                add(pnlHost);
                add(pnlDbName);
                add(pnlTable);
                add(pnlUsername);
                add(pnlPassword);
                add(cmdOk);
            }
        };

    }

    public void setCredentialsListener(OwsDataSourceCredentialsRequiredListener credentialsListener) {
        this.credentialsListener = credentialsListener;
    }
    

    @Override
    public String getTitle() {
        return Names.LABEL_ENTER_DATASOURCE_CREDENTIALS + " (" + currentSourceNumber + 
                " / " + nbSourcesToCheck + ")";
    }

    @Override
    public String validateInput() {
        return this.validateInputMessage;
    }

    @Override
    public Component getComponent() {
        return panel;
    }
    
}
