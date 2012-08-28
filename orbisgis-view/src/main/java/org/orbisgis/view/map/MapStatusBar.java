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
package org.orbisgis.view.map;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.EventHandler;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.jproj.CRSFactory;
import org.jproj.CoordinateReferenceSystem;
import org.orbisgis.view.components.button.CustomButton;
import org.orbisgis.view.components.statusbar.StatusBar;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Area at the bottom of the MapEditor
 * This is an area in the bottom of the map that contain :
 * - A scale information label
 * - A projection information label
 * - A projection selection button
 */

public class MapStatusBar extends StatusBar {
        private static final I18n I18N = I18nFactory.getI18n(MapStatusBar.class);
        private static final Logger LOGGER = Logger.getLogger(MapStatusBar.class);
        public static final String PROP_USER_DEFINED_SCALE_DENOMINATOR = "userDefinedScaleDenominator";
        private static final int STATUS_BAR_HEIGHT = 30;
        private ActionListener scaleInputActionListener = EventHandler.create(ActionListener.class,this,"validateInputScale");
        
        protected VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

        //Scale
        private JLabel scaleLabel;
        private JTextField scaleField;
        private double scaleValue=1; //Valid scale defined by the MapEditor
        private long userDefinedScaleDenominator=1; //Last User scale set
        //CRS
        private JLabel projectionLabel;
        //Coordinates
        private JLabel mouseCoordinatesLabel;
        private Point2D mouseCoordinates = new Point2D.Double();
        //Layout parameters
        private static final int OUTER_BAR_BORDER = 1;
        private static final int HORIZONTAL_EMPTY_BORDER = 4;

        public MapStatusBar() {
                super(OUTER_BAR_BORDER,HORIZONTAL_EMPTY_BORDER);
                setPreferredSize(new Dimension(-1,STATUS_BAR_HEIGHT));
                setMinimumSize(new Dimension(1,STATUS_BAR_HEIGHT));
                ////////
                //Add bar components
                //Coordinates
                mouseCoordinatesLabel = new JLabel();
                addComponent(mouseCoordinatesLabel);
                // Projection
                projectionLabel = new JLabel();
                addComponent(projectionLabel);
                JButton changeProjection = new CustomButton(OrbisGISIcon.getIcon("world"));
                changeProjection.setToolTipText(I18N.tr("Change coordinate reference system"));
                //changeProjection.setContentAreaFilled(false);
                addComponent(changeProjection,false);
                // Scale
                scaleLabel = new JLabel(I18N.tr("Scale :"));
                scaleField = new JTextField();
                scaleField.addActionListener(scaleInputActionListener);
                scaleField.setInputVerifier(new FormattedTextFieldVerifier());
                //scaleField.setEditable(false);
                //scaleField.setColumns(SCALE_FIELD_COLUMNS);
                addComponent(scaleLabel);
                addComponent(scaleField,false);
                //Set initial value
                setScaleDenominator(1);
                setProjection(new CRSFactory().createFromName("EPSG:4326"));
                setCursorCoordinates(new Point2D.Double());
        }

        /**
         * Called by the VK_ENTER of the textField
         */
        public void validateInputScale() {
                scaleField.getInputVerifier().verify(scaleField);           
        }

        /**
         * Set the value of userDefinedScaleDenominator
         *
         * @param userDefinedScaleDenominator new value of
         * userDefinedScaleDenominator
         * @throws java.beans.PropertyVetoException
         */
        public void setUserDefinedScaleDenominator(long userDefinedScaleDenominator) throws PropertyVetoException {
                long oldUserDefinedScaleDenominator = this.userDefinedScaleDenominator;
                try {
                        vetoableChangeSupport.fireVetoableChange(PROP_USER_DEFINED_SCALE_DENOMINATOR, oldUserDefinedScaleDenominator, userDefinedScaleDenominator);
                        fireVetoableChange(PROP_USER_DEFINED_SCALE_DENOMINATOR, oldUserDefinedScaleDenominator, userDefinedScaleDenominator);
                } catch( RuntimeException ex) {
                        //Something has converted the PropertyVetoException into a runtime exception
                        throw (PropertyVetoException)ex.getCause();
                }
                this.userDefinedScaleDenominator = userDefinedScaleDenominator;
                firePropertyChange(PROP_USER_DEFINED_SCALE_DENOMINATOR, oldUserDefinedScaleDenominator, userDefinedScaleDenominator);
        }           
        /**
         * Set the new Projection of the Map
         * @param projection 
         */
        public final void setProjection(CoordinateReferenceSystem projection) {
                String projectLabel = projection.toString();
                //projectLabel = "TODO"; //TODO read map context project                        
                projectionLabel.setText(I18N.tr("Projection : {0}",projectLabel));
        }
        /**
         * Add a VetoableChangeListener for a specific property.
         * @param listener 
         */
        public void addVetoableChangeListener(String property, VetoableChangeListener listener )
        {
                vetoableChangeSupport.addVetoableChangeListener(property, listener );
        }
        
        /**
         * Set the mouse coordinate in the Coordinate reference system
         * @param coordinate 
         */
        public final void setCursorCoordinates(Point2D cursorCoordinate) {
                if(!mouseCoordinates.equals(cursorCoordinate)) {
                        mouseCoordinates=cursorCoordinate;
                        NumberFormat f = DecimalFormat.getInstance(new Locale("en"));
                        f.setGroupingUsed(false);
                        mouseCoordinatesLabel.setText(I18N.tr("X:{0} Y:{1}",f.format(cursorCoordinate.getX()),f.format(cursorCoordinate.getY())));
                }
        }
        
        /**
         * Set the value of scaleDenominator
         *
         * @param scaleDenominator new value of scaleDenominator
         */
        public final void setScaleDenominator(double scaleDenominator) {
                scaleValue = scaleDenominator;
                scaleField.setText(I18N.tr("1:{0}",Math.round(scaleDenominator)));
                validate(); // Resize and layout controls
        }
        
        private class FormattedTextFieldVerifier extends InputVerifier {

                private void invalidateUserInput() {
                        setScaleDenominator(scaleValue);
                }
                @Override
                public boolean verify(JComponent input) {
                        if (input instanceof JTextField) {
                                LOGGER.debug("Verify scale user input..");
                                JTextField ftf = (JTextField) input;
                                String text = ftf.getText();
                                NumberFormat ft = NumberFormat.getIntegerInstance(); //Use default locale
                                String[] scaleParts = text.split(":");
                                if(scaleParts.length!=2) {
                                        //More or Less than a single ':' character
                                        LOGGER.error(I18N.tr("The format of a scale is 1:number"));
                                        invalidateUserInput();
                                } else {
                                        try {
                                                if(ft.parse(scaleParts[0]).intValue()==1) {
                                                        setUserDefinedScaleDenominator(ft.parse(scaleParts[1]).longValue());
                                                        LOGGER.debug("User scale input accepted..");
                                                } else {
                                                        throw new ParseException(I18N.tr("The only accepted value on nominator is 1"), 0);
                                                }
                                        } catch( ParseException ex) {
                                                //Do not send the exception, the user has not to see the traceback
                                                LOGGER.error(ex.getLocalizedMessage());
                                                invalidateUserInput();
                                        } catch (PropertyVetoException ex) {
                                                //Don't know why but something catch the 
                                                //Vetoed by the MapEditor
                                                LOGGER.error(ex.getLocalizedMessage());
                                                invalidateUserInput();
                                        }
                                }
                        }
                        return true;
                }

                @Override
                public boolean shouldYieldFocus(JComponent input) {
                        return verify(input);
                }
        }
}
