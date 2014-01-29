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

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.IInterpolationLegend;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Spinner for the minimum symbol size in proportional legends. We pass a
 * {@link MaxSizeSpinner} to the constructor in order to make sure that the min
 * value is always <= the max value.
 *
 * @author Adam Gouge
 */
public class MinSizeSpinner extends AbsSpinner {

    /**
     * Constructor
     *
     * @param legend         Legend
     * @param maxSizeSpinner The max size spinner whose value controls how
     *                       large the min size spinner's maximum value
     * @throws ParameterException If the legend's first value cannot be
     *                            retrieved.
     */
    public MinSizeSpinner(IInterpolationLegend legend,
                          MaxSizeSpinner maxSizeSpinner) throws ParameterException {
        super(new SpinnerNumberModel(
                legend.getFirstValue(), 0, Double.POSITIVE_INFINITY, LARGE_STEP));
        addChangeListener(EventHandler.create(
                ChangeListener.class, legend, "firstValue", "source.value"));
        maxSizeSpinner.addChangeListener(EventHandler.create(
                ChangeListener.class, this.getModel(), "maximum", "source.value"));
    }

    @Override
    protected double getSpinStep() {
        return LARGE_STEP;
    }
}
