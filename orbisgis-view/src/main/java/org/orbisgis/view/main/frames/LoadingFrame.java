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
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.RootProgressMonitor;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Splash screen shown while loading OrbisGIS on startup
 * @author Nicolas Fortin
 */
public class LoadingFrame extends JFrame {
        private JLabel messageLabel = new JLabel();
        private JProgressBar progressBar = new JProgressBar();
        private static final Logger LOGGER = Logger.getLogger(LoadingFrame.class);
        private ProgressMonitor pm = new RootProgressMonitor(1);

        /**
         * Constructor of LoadingFrame
         * @throws HeadlessException 
         */
        public LoadingFrame() throws HeadlessException {
                setUndecorated(true);
                JPanel mainPanel = new JPanel(new BorderLayout());
                setContentPane(mainPanel);
                mainPanel.add(new JLabel(OrbisGISIcon.getIcon("logo_orbisgis")),
                        BorderLayout.CENTER);
            JPanel bottomPanel = new JPanel(new BorderLayout());
                progressBar.setMaximum(100);
                progressBar.setIndeterminate(true);
                bottomPanel.add(progressBar, BorderLayout.WEST);
                bottomPanel.add(messageLabel, BorderLayout.CENTER);
                mainPanel.add(bottomPanel,BorderLayout.SOUTH);
                pack();
                // Try to set the frame at the center of the default screen
                try {
                        GraphicsDevice device = GraphicsEnvironment.
                                getLocalGraphicsEnvironment().getDefaultScreenDevice();                        
                        Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
                        setLocation(screenBounds.x+screenBounds.width/2-getWidth()/2,
                        screenBounds.y+screenBounds.height/2-getHeight()/2);
                } catch(Throwable ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
                pm.addPropertyChangeListener(ProgressMonitor.PROP_PROGRESSION,
                        EventHandler.create(PropertyChangeListener.class, this, "onProgressChange"));
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    EventHandler.create(PropertyChangeListener.class, this, "onCancel"));
                pm.addPropertyChangeListener(ProgressMonitor.PROP_TASKNAME,
                    EventHandler.create(PropertyChangeListener.class, this, "onTaskChange"));
        }

        /**
         * @return Progress monitor of this panel
         */
        public ProgressMonitor getProgressMonitor() {
            return pm;
        }

        /**
         * Progression of loading change.
         */
        public void onProgressChange() {
            int progress = (int)(pm.getOverallProgress() * 100);
            progressBar.setIndeterminate(progress == 0);
            progressBar.setValue(progress);
        }

        /**
         * Cancel loading
         */
        public void onCancel() {
            dispose();
        }

        /**
         * Task name change.
         */
        public void onTaskChange() {
            messageLabel.setText(pm.getCurrentTaskName());
        }
}
