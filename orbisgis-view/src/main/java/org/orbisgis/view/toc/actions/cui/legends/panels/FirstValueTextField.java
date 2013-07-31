package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.IInterpolationLegend;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * First value text field for {@link IInterpolationLegend}s. Does not update
 * the preview.
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 */
public class FirstValueTextField extends JFormattedTextField {

    private static final I18n I18N = I18nFactory.getI18n(FirstValueTextField.class);
    private static final Logger LOGGER = Logger.getLogger(FirstValueTextField.class);

    public FirstValueTextField(final IInterpolationLegend legend) {
        super(new DecimalFormat());
        try {
            setValue(legend.getFirstValue());
        } catch (ParameterException e) {
            LOGGER.error(I18N.tr("Can't retrieve the minimum value of"
                    + " the symbol"), e);
        }
        addPropertyChangeListener("value",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        legend.setFirstValue(Double.valueOf(getValue().toString()));
                    }
                });
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
}