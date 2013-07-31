package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.gdms.data.DataSource;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueLineSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * Settings panel for Proportional Line.
 *
 * @author Adam Gouge
 */
public class ProportionalLinePanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(ProportionalLinePanel.class);

    private DataSource dataSource;

    private NumericalFieldsComboBox numericalFieldsComboBox;
    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private SecondValueTextField secondValueTextField;
    private FirstValueTextField firstValueTextField;

    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    public ProportionalLinePanel(ProportionalLine legend,
                                 CanvasSE preview,
                                 DataSource dataSource) {
        super(legend, preview, I18N.tr(PnlUniqueLineSE.LINE_SETTINGS), false);
        this.dataSource = dataSource;
        init();
        addComponents();
    }

    @Override
    protected ProportionalLine getLegend() {
        return (ProportionalLine) legend;
    }

    @Override
    protected void init() {
        ProportionalStrokeLegend strokeLegend = getLegend().getStrokeLegend();
        ConstantSolidFill fillAnalysis = (ConstantSolidFill) strokeLegend.getFillAnalysis();

        numericalFieldsComboBox =
                new NumericalFieldsComboBox(dataSource, getLegend());
        colorLabel = new ColorLabel(fillAnalysis, preview);
        lineUOMComboBox = new LineUOMComboBox(getLegend(), preview);
        secondValueTextField = new SecondValueTextField(getLegend(), preview);
        firstValueTextField = new FirstValueTextField(getLegend());
        lineOpacitySpinner = new LineOpacitySpinner(fillAnalysis, preview);
        dashArrayField = new DashArrayField(strokeLegend, preview);
    }

    @Override
    protected void addComponents() {
        // Field
        add(new JLabel(I18N.tr(AbstractFieldPanel.FIELD)));
        add(numericalFieldsComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        // Color
        add(new JLabel(I18N.tr("Color")));
        add(colorLabel);
        // Unit of Measure - line width
        add(new JLabel(I18N.tr(AbstractFieldPanel.LINE_WIDTH_UNIT)));
        add(lineUOMComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        // Max width
        add(new JLabel(I18N.tr("Max width")));
        add(secondValueTextField, "growx");
        // Min width
        add(new JLabel(I18N.tr("Min width")));
        add(firstValueTextField, "growx");
        // Opacity
        add(new JLabel(I18N.tr(AbstractFieldPanel.OPACITY)));
        add(lineOpacitySpinner, "growx");
        // Dash array
        add(new JLabel(I18N.tr(AbstractFieldPanel.DASH_ARRAY)));
        add(dashArrayField, "growx");
    }

    @Override
    protected void onClickOptionalCheckBox() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setFieldsState(boolean enable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
