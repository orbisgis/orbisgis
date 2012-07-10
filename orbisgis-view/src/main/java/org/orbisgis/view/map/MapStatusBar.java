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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.geom.Point2D;
import javax.swing.*;
import org.jproj.CRSFactory;
import org.jproj.CoordinateReferenceSystem;
import org.orbisgis.view.icons.OrbisGISIcon;
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
        //Scale
        private JLabel scaleLabel;
        private JTextField scaleField;
        //CRS
        private JLabel projectionLabel;
        //Coordinates
        private JLabel mouseCoordinatesLabel;
        private Point2D mouseCoordinates = new Point2D.Double();
        //Layout parameters
        private final static int OUTER_BAR_BORDER = 1;
        private final static int HORIZONTAL_EMPTY_BORDER = 4;
        private final static int SCALE_FIELD_COLUMNS = 6;

        public MapStatusBar() {
                super(new BorderLayout());
                horizontalBar = new JPanel();
                horizontalBar.setLayout(new BoxLayout(horizontalBar, BoxLayout.X_AXIS));
                setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),BorderFactory.createEmptyBorder(OUTER_BAR_BORDER, OUTER_BAR_BORDER, OUTER_BAR_BORDER, OUTER_BAR_BORDER)));
                add(horizontalBar,BorderLayout.EAST);                
                ////////
                //Add bar components
                //Coordinates
                mouseCoordinatesLabel = new JLabel();
                addComponent(mouseCoordinatesLabel);
                // Projection
                projectionLabel = new JLabel();
                addComponent(projectionLabel);
                JButton changeProjection = new JButton(OrbisGISIcon.getIcon("world"));
                changeProjection.setToolTipText(I18N.tr("Change coordinate reference system"));
                changeProjection.setMargin(new Insets(0, 0, 0, 0));
                changeProjection.setBorderPainted(false);
                //changeProjection.setContentAreaFilled(false);
                addComponent(changeProjection,false);
                // Scale
                scaleLabel = new JLabel(I18N.tr("Scale :"));
                scaleField = new JTextField();
                scaleField.setEditable(false);
                scaleField.setColumns(SCALE_FIELD_COLUMNS);
                addComponent(scaleLabel);
                addComponent(scaleField,false);
                //Set initial value
                setScaleDenominator(1);
                setProjection(new CRSFactory().createFromName("EPSG:4326"));
                setCursorCoordinates(new Point2D.Double());
        }
        /**
         * Set the new Projection of the Map
         * @param projection 
         */
        public final void setProjection(CoordinateReferenceSystem projection) {
                
                projectionLabel.setText(I18N.tr("Projection : {0}",projection));
        }

        /**
         * Set the mouse coordinate in the Coordinate reference system
         * @param coordinate 
         */
        public final void setCursorCoordinates(Point2D cursorCoordinate) {
                if(!mouseCoordinates.equals(cursorCoordinate)) {
                        mouseCoordinates=cursorCoordinate;
                        mouseCoordinatesLabel.setText(I18N.tr("Coordinate {0};{1}",cursorCoordinate.getX(),cursorCoordinate.getY()));
                }
        }
        /**
         * Set the value of scaleDenominator
         *
         * @param scaleDenominator new value of scaleDenominator
         */
        public final void setScaleDenominator(double scaleDenominator) {
                //scaleLabel.setText();
                scaleField.setText(I18N.tr("1:{0}",Math.round(scaleDenominator)));
        }

        /**
         * Append a component on the right of the status bar
         * @param component 
         */
        private void addComponent(JComponent component) {
                addComponent(component,true);
        }
        /**
         * Append a component on the right of the status bar
         * @param component 
         * @param addSeparator Add a separator at the left of the component
         */
        private void addComponent(JComponent component,boolean addSeparator) {
                if(addSeparator && horizontalBar.getComponentCount()!=0) {
                        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
                        horizontalBar.add(Box.createHorizontalStrut(HORIZONTAL_EMPTY_BORDER));
                        horizontalBar.add(separator);
                        horizontalBar.add(Box.createHorizontalStrut(HORIZONTAL_EMPTY_BORDER));
                }
                horizontalBar.add(component);
        }
        
        

        
        
}
