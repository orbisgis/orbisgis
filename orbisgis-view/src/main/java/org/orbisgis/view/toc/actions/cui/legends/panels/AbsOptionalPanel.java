package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.LegendStructure;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Root class for optional unique symbol panels, namely area and border.
 *
 * @author Adam Gouge
 */
public abstract class AbsOptionalPanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(AbsOptionalPanel.class);

    protected final boolean isOptional;
    protected JCheckBox enableCheckBox;

    /**
     * Constructor
     *
     * @param legend     Legend
     * @param preview    Preview
     * @param title      Title
     * @param isOptional Whether the enable checkbox should be created.
     */
    public AbsOptionalPanel(LegendStructure legend,
                            CanvasSE preview,
                            String title,
                            boolean isOptional) {
        super(legend, preview, title);
        this.isOptional = isOptional;
        if (isOptional) {
            initEnableCheckBox();
        }
    }

    /**
     * Initialize the "Enable" checkbox.
     */
    private void initEnableCheckBox() {
        enableCheckBox = new JCheckBox(I18N.tr("Enable"));
        enableCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickOptionalCheckBox();
            }
        });
        enableCheckBox.setSelected(true);
    }

    /**
     * Action taken when the optional checkbox is (de)selected.
     */
    protected abstract void onClickOptionalCheckBox();

    /**
     * Enable or disable all fields (used when the checkbox is clicked).
     *
     * @param enable True if the fields are to be enabled.
     */
    protected abstract void setFieldsState(boolean enable);
}
