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
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Spinner for the maximum symbol size in proportional legends. The max value
 * must always be >= the min value, so {@link #setMinSizeSpinner} must be called
 * after creating a new {@link MaxSizeSpinner} in order to ensure this.
 *
 * @author Adam Gouge
 */
public class MaxSizeSpinner extends PreviewSpinner {

    /**
     * Constructor
     *
     * @param legend  Legend
     * @param preview Preview
     * @throws ParameterException If the legend's second value cannot be
     *                            retrieved.
     */
    public MaxSizeSpinner(IInterpolationLegend legend,
                          CanvasSE preview) throws ParameterException {
        super(new SpinnerNumberModel(
                legend.getSecondValue(), 0, Double.POSITIVE_INFINITY, LARGE_STEP),
                preview);
        addChangeListener(EventHandler.create(
                ChangeListener.class, legend, "secondValue", "source.value"));
    }

    /**
     * Make sure this value is always >= the min value. This should always be
     * called just after the initialization of {@code minSizeSpinner}.
     *
     * @param minSizeSpinner Spinner from which to obtain the min value.
     */
    public void setMinSizeSpinner(MinSizeSpinner minSizeSpinner) {
        minSizeSpinner.addChangeListener(EventHandler.create(
                ChangeListener.class, this.getModel(), "minimum", "source.value"));
    }

    @Override
    protected double getSpinStep() {
        return LARGE_STEP;
    }
}
