package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.view.toc.actions.cui.legends.PnlAbstractTableAnalysis;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class EnableStrokeCheckBox extends JCheckBox {

    private static final I18n I18N = I18nFactory.getI18n(EnableStrokeCheckBox.class);

    private EnablesStroke legend;
    private LineUOMComboBox lineUOMComboBox;

    public EnableStrokeCheckBox(EnablesStroke legend,
                                LineUOMComboBox lineUOMComboBox) {
        super(I18N.tr(PnlAbstractTableAnalysis.ENABLE_BORDER));
        this.legend = legend;
        this.lineUOMComboBox = lineUOMComboBox;
        init();
    }

    /**
     * Gets the checkbox used to set if the border will be drawn.
     *
     * @return The enable border checkbox.
     */
    private void init() {
        setSelected(legend.isStrokeEnabled());
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setStrokeEnabled(((JCheckBox) e.getSource()).isSelected());
                lineUOMComboBox.updatePreview();
            }
        });
    }
}
