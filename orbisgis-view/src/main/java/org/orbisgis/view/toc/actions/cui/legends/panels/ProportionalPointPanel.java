package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.gdms.data.DataSource;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * Settings panel for Proportional Point.
 *
 * @author Adam Gouge
 */
public class ProportionalPointPanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(ProportionalPointPanel.class);

    private DataSource dataSource;
    private int geometryType;

    private NumericalFieldsComboBox numericalFieldsComboBox;
    private SymbolUOMComboBox symbolUOMComboBox;
    private WKNComboBox wknComboBox;
    private SecondValueTextField secondValueTextField;
    private FirstValueTextField firstValueTextField;

    private OnVertexOnCentroidPanel onVertexOnCentroidPanel;

    public ProportionalPointPanel(ProportionalPoint legend,
                                  CanvasSE preview,
                                  String title,
                                  boolean isOptional,
                                  DataSource dataSource,
                                  int geometryType) {
        super(legend, preview, title, isOptional);
        this.dataSource = dataSource;
        this.geometryType = geometryType;
        init();
        addComponents();
    }

    @Override
    public ProportionalPoint getLegend() {
        return (ProportionalPoint) legend;
    }

    @Override
    protected void init() {
        numericalFieldsComboBox =
                new NumericalFieldsComboBox(dataSource, getLegend());
        symbolUOMComboBox = new SymbolUOMComboBox(getLegend(), preview);
        wknComboBox = new WKNComboBox(getLegend(), preview);
        secondValueTextField = new SecondValueTextField(getLegend(), preview);
        firstValueTextField = new FirstValueTextField(getLegend());
        if (geometryType != SimpleGeometryType.POINT) {
            onVertexOnCentroidPanel =
                    new OnVertexOnCentroidPanel(getLegend(), preview);
        }
    }

    @Override
    protected void addComponents() {
        // Field
        add(new JLabel(I18N.tr(AbstractFieldPanel.FIELD)));
        add(numericalFieldsComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        // Unit of measure - symbol size
        add(new JLabel(I18N.tr(AbstractFieldPanel.SYMBOL_SIZE_UNIT)));
        add(symbolUOMComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        // Symbol
        add(new JLabel(I18N.tr(AbstractFieldPanel.SYMBOL)));
        add(wknComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        // Max size
        add(new JLabel(I18N.tr("Max. size")));
        add(secondValueTextField, "growx");
        // Min size
        add(new JLabel(I18N.tr("Min. size")));
        add(firstValueTextField, "growx");
        // If geometryType != POINT, we must let the user choose if he
        // wants to draw symbols on centroid or on vertices.
        if (geometryType != SimpleGeometryType.POINT) {
            add(new JLabel(I18N.tr(AbstractFieldPanel.PLACE_SYMBOL_ON)), "span 1 2");
            add(onVertexOnCentroidPanel, "span 1 2");
        }
    }

    @Override
    protected void onClickOptionalCheckBox() {
    }

    @Override
    protected void setFieldsState(boolean enable) {
    }
}
