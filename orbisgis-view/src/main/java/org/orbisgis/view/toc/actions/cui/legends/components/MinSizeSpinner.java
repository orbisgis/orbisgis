package org.orbisgis.view.toc.actions.cui.legends.components;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.IInterpolationLegend;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Spinner for the minimum symbol size in proportional legends.
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
                ChangeListener.class, this.getModel(), "maximum", "source.value"
        ));
    }

    @Override
    protected double getSpinStep() {
        return LARGE_STEP;
    }
}
