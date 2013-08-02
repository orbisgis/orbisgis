package org.orbisgis.view.toc.actions.cui.legends.components;

import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Root class for mouse wheel scrollable spinners which also update a preview.
 *
 * @author Adam Gouge
 */
public class AbsPreviewSpinner extends AbsSpinner {

    /**
     * Constructor
     *
     * @param model   Spinner model
     * @param preview Preview
     */
    public AbsPreviewSpinner(SpinnerNumberModel model,
                             final CanvasSE preview) {
        super(model);
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                preview.imageChanged();
            }
        });
    }
}
