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
package org.orbisgis.view.toc.actions.cui.legends.components;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Root class for spinners. Adds a {@code MouseWheelListener} so that the user
 * can update the value by means of the mouse scroll wheel.
 *
 * @author Adam Gouge
 */
public abstract class AbsSpinner extends JSpinner {

    protected static final double SMALL_STEP = 0.1;
    protected static final double LARGE_STEP = 0.5;

    /**
     * Constructor
     *
     * @param model Spinner model
     */
    public AbsSpinner(final SpinnerNumberModel model) {
        super(model);
        // Enable the mouse scroll wheel on spinners.
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // The new value is the old one minus the wheel rotation
                // times the spin step.
                Double newValue = ((Double) getValue())
                        - e.getPreciseWheelRotation() * getSpinStep();
                // Only update if we are within the given range.
                if (model.getMaximum().compareTo(newValue) >= 0
                        && model.getMinimum().compareTo(newValue) <= 0) {
                    setValue(newValue);
                }
            }
        });
    }

    /**
     * Gets the spin step for the mouse scroll wheel listener.
     *
     * @return The spin step.
     */
    protected double getSpinStep() {
        return SMALL_STEP;
    }
}
