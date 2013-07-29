package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.beans.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class LineOpacitySpinner extends JSpinner {

    public static final double SPIN_STEP = 0.1;

    /**
     * Gets a spinner that is linked with the opacity of the {@code
     * ConstantSolidFill} given in argument.
     *
     * @param legend The stroke that will be configured with the spinner.
     */
    public LineOpacitySpinner(final ConstantSolidFill legend,
                              CanvasSE preview) {
        super(new SpinnerNumberModel(legend.getOpacity(), 0, 1, SPIN_STEP));
        addChangeListener(EventHandler.create(
                ChangeListener.class, legend, "opacity", "source.value"));
        addChangeListener(EventHandler.create(
                ChangeListener.class, preview, "imageChanged"));
    }
}
