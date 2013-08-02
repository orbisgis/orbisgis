package org.orbisgis.view.toc.actions.cui.legends.components;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Spinner for the maximum symbol size in proportional legends.
 *
 * @author Adam Gouge
 */
public class MaxSizeSpinner extends AbsPreviewSpinner {

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

    @Override
    protected double getSpinStep() {
        return LARGE_STEP;
    }
}
