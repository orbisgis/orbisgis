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
package org.orbisgis.view.map.mapsManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentListener;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import org.apache.log4j.Logger;

/**
 *
 * @author Nicolas Fortin
 */
public class MapsManager extends JDialog {
        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = Logger.getLogger(MapsManager.class);
        private JPanel parentPanel=null;
        private JPanel contentPane;
        private JTree tree;
        private JFrame parentFrame;

        public MapsManager(JFrame frame) {
                super(frame);
                parentFrame = frame;
                contentPane = new JPanel(new BorderLayout());
                contentPane.setOpaque(true);
                tree = new JTree(new String[]{"Kate","Lisa","Cindy"});
                contentPane.add(
                        new JScrollPane(tree,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
                        BorderLayout.CENTER);
                setUndecorated(true);
                setContentPane(contentPane);      
                contentPane.setBorder(BorderFactory.createEtchedBorder());          
                pack();
        }
        
        /**
         * Find the correct location for this panel
         */
        private void resetLocation() {
                if(parentPanel!=null) {
                        Point parentLocation = parentPanel.getLocationOnScreen();
                        Point mmLocation =
                                new Point(parentLocation.x+
                                parentPanel.getWidth()-
                                tree.getPreferredSize().width
                                ,
                                parentLocation.y
                                );
                        setLocation(mmLocation);
                        LOGGER.debug("resetLocation :"+tree.getPreferredSize().height+" "+parentPanel.getHeight());
                }
        }
        
        private void resetSize() {
                Dimension newSize = new Dimension(-1, Math.min(tree.getPreferredSize().height, parentPanel.getHeight()));
                LOGGER.debug("Setting size to "+newSize.height);
                setSize(newSize);   
                pack();
        }

        public void setParentPanel(JPanel parentPanel) {
                this.parentPanel = parentPanel;
                addWindowListener(EventHandler.create(WindowListener.class,this,"windowOpened",null,"windowOpened"));          
                ComponentListener resizeListener = EventHandler.create(ComponentListener.class,this,"onParentResize",null,"componentResized");
                ComponentListener moveListener =EventHandler.create(ComponentListener.class,this,"onParentMove",null,"componentMoved");                
                parentPanel.addComponentListener(resizeListener);
                parentPanel.addComponentListener(moveListener);
                parentFrame.addComponentListener(resizeListener);
                parentFrame.addComponentListener(moveListener);
                resetSize();
        }
        
        public void windowOpened() {
                resetLocation();
        }
        
        public void onParentResize() {
                resetSize();
        }
        
        public void onParentMove() {
                resetLocation();                
        }
}
