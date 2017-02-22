/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.sif.components.fstree;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.orbisgis.sif.icons.SifIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This node represent the download operation in progress.
 * The user is able to cancel this process from this node
 * @author Nicolas Fortin
 */
public class TreeNodeBusy extends AbstractTreeNodeLeaf implements TreeNodeCustomIcon {
        private static final int FRAME_DURATION = 100;
        private static final String[] frames = new String[]{"progress_1",
                "progress_2","progress_3","progress_4"};
        private Timer animationTimer;
        private int curFrame = 0;
        private boolean doAnimation = false;
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeBusy.class);

        public TreeNodeBusy() {                
                setEditable(false);
                setLabel(I18N.tr("Downloading.."));
        }        

        @Override
        public void setUserObject(Object o) {
                // Read only
        }

        /**
         * Called by the time when the frame is finished
         */
        public void onAnimationTimerEvent() {
                if (curFrame + 1 < frames.length) {
                        curFrame++;
                } else {
                        curFrame = 0;
                }
                model.nodeChanged(this);
                if(doAnimation) {
                        animationTimer.start();
                } else {                        
                        if(parent.getIndex(this)!=-1) {
                                model.removeNodeFromParent(this);
                        }                      
                }
        }
        
        /**
         * Update the animation state of this node
         * @param doAnimation 
         */
        public void setDoAnimation(boolean doAnimation) {
                if(doAnimation) {
                        if(animationTimer==null) {
                                // Create the timer
                                animationTimer =
                                        new Timer(FRAME_DURATION,
                                        EventHandler.create(ActionListener.class,
                                        this,"onAnimationTimerEvent"));
                                animationTimer.setRepeats(false);
                        }
                        // Run the timer
                        animationTimer.start();
                }
                this.doAnimation = doAnimation;
        }

        @Override
        public ImageIcon getLeafIcon() {
                return SifIcon.getIcon(frames[curFrame]);
        }

        @Override
        public ImageIcon getClosedIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ImageIcon getOpenIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }
}
