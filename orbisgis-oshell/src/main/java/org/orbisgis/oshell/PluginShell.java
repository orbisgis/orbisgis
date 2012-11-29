/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.oshell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.apache.felix.shell.ShellService;
import org.apache.log4j.Logger;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * OSGI Shell command GUI. GUI access to the Apache Felix Shell service.
 * @url http://felix.apache.org/site/apache-felix-shell.html
 * @author Nicolas Fortin
 */
public class PluginShell extends JPanel implements DockingPanel {
        private static final Logger LOGGER = Logger.getLogger("gui."+PluginShell.class);
        private static final String SHELL_SERVICE_REFERENCE = "org.apache.felix.shell.ShellService";
        private DockingPanelParameters parameters = new DockingPanelParameters();
        private static final I18n I18N = I18nFactory.getI18n(PluginShell.class);
        private final BundleContext hostBundle;
        private JTextField commandField = new JTextField();
        private JTextPane outputField = new JTextPane();
        private TextDocumentOutputStream info = new TextDocumentOutputStream(outputField, Color.BLACK);
        private TextDocumentOutputStream error = new TextDocumentOutputStream(outputField, Color.RED.darker());

        public PluginShell(final BundleContext hostBundle) {
                super(new BorderLayout());
                this.hostBundle = hostBundle;
                parameters.setName("plugin-shell");
                parameters.setTitle(I18N.tr("Plugin Shell"));
                parameters.setTitleIcon(new ImageIcon(PluginShell.class.getResource("panel_icon.png")));
                outputField.setEditable(false);
                outputField.setText(I18N.tr("Plugin shell, type \"help\" for command list.\n"));
                // Initialising components
                // The shell is composed by a logging part and a command line part
                add(new JScrollPane(outputField), BorderLayout.CENTER);
                add(commandField, BorderLayout.SOUTH);
                commandField.addActionListener(EventHandler.create(ActionListener.class,this,"onValidateCommand"));
        }
        
        /**
         * User type enter on command input
         */
        public void onValidateCommand() {
                final String command = commandField.getText();
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                executeCommand(command);
                        }
                });
                commandField.setText("");
        }
        private void executeCommand(String command) {
                // Get shell service.
                ServiceReference ref = hostBundle.getServiceReference(
                        SHELL_SERVICE_REFERENCE);
                if (ref == null)
                {
                        LOGGER.error(I18N.tr("No shell service is available."));
                        return;
                }
                ShellService shell = (ShellService) hostBundle.getService(ref);
                try {
                        // Print the command line in the output window.
                        try {
                                outputField.getDocument().insertString(outputField.getDocument().getLength(), "osgi> "+command+"\n", null);
                        } catch(BadLocationException ex) {
                                LOGGER.debug(ex.getLocalizedMessage(), ex);
                                //ignore
                        }

                        try {
                            shell.executeCommand(command,
                                    new PrintStream(info),
                                    new PrintStream(error));
                        } catch (Exception ex) {
                            LOGGER.error(ex.getLocalizedMessage(),ex);
                        } finally {
                                try {
                                        // Send messages to the window
                                        info.flush();
                                        error.flush();
                                        outputField.setCaretPosition(outputField.getDocument().getLength());
                                } catch(IOException ex) {
                                        LOGGER.error(ex.getLocalizedMessage(),ex);                                        
                                }
                        }
                } finally {
                        hostBundle.ungetService(ref);
                }
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return parameters;
        }

        @Override
        public JComponent getComponent() {
                return this;
        }

        private class TextDocumentOutputStream extends OutputStream {

                private JTextComponent textComponent;
                private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                //Current text Attribute for insertion, change whith style update        
                private AttributeSet aset;
                
                public TextDocumentOutputStream(JTextComponent textComponent, Color textColor) {
                        this.textComponent = textComponent;
                        changeAttribute(textColor);
                }

                @Override
                public void write(int i) throws IOException {
                        buffer.write(i);
                }

                @Override
                public void flush() throws IOException {
                        super.flush();
                        // Fetch lines in the byte array
                        String messages = buffer.toString();
                        if (!messages.isEmpty()) {
                                Document doc = textComponent.getDocument();
                                try {
                                        doc.insertString(doc.getLength(), messages, aset);
                                }catch(BadLocationException ex) {
                                        LOGGER.error(I18N.tr("Cannot show the log message"), ex);                                       
                                }
                        }
                        buffer.reset();
                }
                private void changeAttribute(Color color) {
                    StyleContext sc = StyleContext.getDefaultStyleContext();
                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                                    StyleConstants.Foreground, color);            
                }
        }
}
