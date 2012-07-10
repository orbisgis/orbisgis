/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.map;

import com.vividsolutions.jts.geom.Envelope;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.orbisgis.core.layerModel.MapContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief Area at the bottom of the MapEditor
 * This is an area in the bottom of the map that contain :
 * - A scale information label
 * - A projection information label
 * - A projection selection button
 */

public class MapStatusBar extends JPanel {
        protected final static I18n I18N = I18nFactory.getI18n(MapStatusBar.class);
        private JPanel horizontalBar;
        private double scaleDenominator;
        private JLabel scaleLabel;

        /**
         * Get the value of scaleDenominator
         *
         * @return the value of scaleDenominator
         */
        public double getScaleDenominator() {
                return scaleDenominator;
        }

        /**
         * Set the value of scaleDenominator
         *
         * @param scaleDenominator new value of scaleDenominator
         */
        public final void setScaleDenominator(double scaleDenominator) {
                this.scaleDenominator = scaleDenominator;
                scaleLabel.setText(I18N.tr("Scale 1:{0}",scaleDenominator));
        }

        
        
        
        public MapStatusBar() {
                horizontalBar = new JPanel();
                horizontalBar.setLayout(new BoxLayout(horizontalBar, BoxLayout.X_AXIS));
                add(horizontalBar);
                scaleLabel = new JLabel();
                add(scaleLabel);
                setScaleDenominator(1);
        }

        
        
}
