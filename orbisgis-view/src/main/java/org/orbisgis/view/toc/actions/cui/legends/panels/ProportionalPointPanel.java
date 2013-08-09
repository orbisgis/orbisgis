package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.components.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * Settings panel for Proportional Point.
 *
 * @author Adam Gouge
 */
public class ProportionalPointPanel extends AbsPanel {

    private static final I18n I18N = I18nFactory.getI18n(ProportionalPointPanel.class);

    private DataSource dataSource;
    private int geometryType;

    private PPointFieldsComboBox pPointFieldsComboBox;
    private SymbolUOMComboBox symbolUOMComboBox;
    private WKNComboBox wknComboBox;
    private MaxSizeSpinner maxSizeSpinner;
    private MinSizeSpinner minSizeSpinner;

    private OnVertexOnCentroidPanel onVertexOnCentroidPanel;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param dataSource   DataSource
     * @param geometryType The type of geometry linked to this legend
     */
    public ProportionalPointPanel(ProportionalPoint legend,
                                  CanvasSE preview,
                                  String title,
                                  DataSource dataSource,
                                  int geometryType) {
        super(legend, preview, title);
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
        pPointFieldsComboBox = PPointFieldsComboBox.
                createInstance(dataSource, getLegend(), preview);
        symbolUOMComboBox = new SymbolUOMComboBox(getLegend(), preview);
        wknComboBox = new WKNComboBox(getLegend(), preview);
        try {
            maxSizeSpinner = new MaxSizeSpinner(getLegend(), preview);
            minSizeSpinner = new MinSizeSpinner(getLegend(), maxSizeSpinner);
            maxSizeSpinner.setMinSizeSpinner(minSizeSpinner);
        } catch (ParameterException e) {
            e.printStackTrace();
        }
        if (geometryType != SimpleGeometryType.POINT) {
            onVertexOnCentroidPanel =
                    new OnVertexOnCentroidPanel(getLegend(), preview);
        }
    }

    @Override
    protected void addComponents() {
        // Field
        add(new JLabel(I18N.tr(FIELD)));
        add(pPointFieldsComboBox, COMBO_BOX_CONSTRAINTS);
        // Unit of measure - symbol size
        add(new JLabel(I18N.tr(SYMBOL_SIZE_UNIT)));
        add(symbolUOMComboBox, COMBO_BOX_CONSTRAINTS);
        // Symbol
        add(new JLabel(I18N.tr(SYMBOL)));
        add(wknComboBox, COMBO_BOX_CONSTRAINTS);
        // Max size
        add(new JLabel(I18N.tr("Max. size")));
        add(maxSizeSpinner, "growx");
        // Min size
        add(new JLabel(I18N.tr("Min. size")));
        add(minSizeSpinner, "growx");
        // If geometryType != POINT, we must let the user choose if he
        // wants to draw symbols on centroid or on vertices.
        if (geometryType != SimpleGeometryType.POINT) {
            add(new JLabel(I18N.tr(PLACE_SYMBOL_ON)), "span 1 2");
            add(onVertexOnCentroidPanel, "span 1 2");
        }
    }
}
