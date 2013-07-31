/**
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
package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * Base class for "Unique Symbol" UIs.
 *
 * This class proposes some methods that will be common to all the panels built
 * for unique symbols.
 * @author Alexis Guéganno
 */
public abstract class PnlUniqueSymbolSE extends AbstractFieldPanel implements UIPanel {

        public static final double SPIN_STEP = 0.1;
        private static final Logger LOGGER = Logger.getLogger("gui."+PnlUniqueSymbolSE.class);
        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueSymbolSE.class);
        private String id;
        private CanvasSE preview;

        /**
         * Rebuild the {@code CanvasSe} instance used to display a preview of
         * the current symbol.
         */
        public void initPreview(){
                Legend leg = getLegend();
                if(leg != null){
                        preview = new CanvasSE(leg.getSymbolizer());
                        preview.imageChanged();
                }
        }

        /**
         * Gets the {@code CanvasSE} instance used to display a preview of
         * the current symbol in a bordered JPanel.
         *
         * @return Preview of the symbol in a bordered JPanel.
         */
        public JPanel getPreviewPanel(){
                return getPreviewPanel(getPreview());
        }

        /**
         * Gets the {@code CanvasSE} instance used to display a preview of
         * the current symbol in a bordered JPanel.
         *
         * @return Preview of the symbol in a bordered JPanel.
         */
        public JPanel getPreviewPanel(CanvasSE prev){
            JPanel previewPanel = new JPanel(
                    new MigLayout("wrap 1", "[" + FIXED_WIDTH + "]"));
            previewPanel.setBorder(
                    BorderFactory.createTitledBorder(I18N.tr("Preview")));
            previewPanel.add(prev, "align c");
            return previewPanel;
        }

        /**
         * Gets the {@code CanvasSE} instance used to display a preview of
         * the current symbol.
         *
         * @return Preview of the symbol.
         */
        public CanvasSE getPreview(){
            if (preview == null) {
                initPreview();
            }
            return preview;
        }

        @Override
        public String getId(){
                return id;
        }

        @Override
        public void setId(String id){
                this.id = id;
        }
}
