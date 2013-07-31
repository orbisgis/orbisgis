package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * Second value text field for {@link org.orbisgis.legend.IInterpolationLegend}s.
 * Updates the preview.
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 */
public class SecondValueTextField extends JFormattedTextField {

    private static final I18n I18N = I18nFactory.getI18n(SecondValueTextField.class);
    private static final Logger LOGGER = Logger.getLogger(SecondValueTextField.class);

    public SecondValueTextField(final IInterpolationLegend legend,
                                CanvasSE preview) {
        super(new DecimalFormat());
        try {
            setValue(legend.getSecondValue());
        } catch (ParameterException e) {
            LOGGER.error(I18N.tr("Can't retrieve the minimum value of"
                    + " the symbol"), e);
        }
        addPropertyChangeListener("value",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        legend.setSecondValue(Double.valueOf(getValue().toString()));
                    }
                });
        addPropertyChangeListener(
                "value",
                EventHandler.create(PropertyChangeListener.class, preview, "imageChanged"));
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
}
