package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.*;
import java.beans.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class DashArrayField extends JTextField {

    private static final I18n I18N = I18nFactory.getI18n(DashArrayField.class);
    private static final Logger LOGGER = Logger.getLogger(DashArrayField.class);

    public DashArrayField(final ConstantColorAndDashesPSLegend legend,
                          CanvasSE preview) {
        super();
        setText(legend.getDashArray());
        setHorizontalAlignment(JFormattedTextField.RIGHT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setDashArray(((JTextField) e.getSource()).getText());
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField jtf = (JTextField) e.getSource();
                String tmp = jtf.getText();
                legend.setDashArray(tmp);
                if (!tmp.equals(legend.getDashArray())) {
                    LOGGER.warn(I18N.tr("Could not validate your input."));
                    jtf.setText(legend.getDashArray());
                }
            }
        });
        addFocusListener(
                EventHandler.create(FocusListener.class, preview, "imageChanged"));
    }
}
