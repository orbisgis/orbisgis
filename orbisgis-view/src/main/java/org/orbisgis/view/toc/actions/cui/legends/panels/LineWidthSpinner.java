package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class LineWidthSpinner extends JSpinner {

    public static final double SPIN_STEP = 0.1;

    /**
     * Creates and configures a line width {@link javax.swing.JSpinner}.
     *
     * @param legend The stroke that will be configured with the spinner.
     */
    public LineWidthSpinner(final ConstantPenStroke legend,
                            CanvasSE preview) {
        super(new SpinnerNumberModel(
                legend.getLineWidth(), 0, Double.POSITIVE_INFINITY, SPIN_STEP));
        addChangeListener(EventHandler.create(
                ChangeListener.class, legend, "lineWidth", "source.value"));
        addChangeListener(EventHandler.create(
                ChangeListener.class, preview, "imageChanged"));
    }
}
