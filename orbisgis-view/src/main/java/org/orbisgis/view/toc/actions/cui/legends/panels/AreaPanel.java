package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 30/07/13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class AreaPanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(AreaPanel.class);

    private final boolean isAreaOptional;

    private JCheckBox areaCheckBox;
    private ColorLabel colorLabel;
    private LineOpacitySpinner fillOpacitySpinner;

    public AreaPanel(IUniqueSymbolArea legend,
                     CanvasSE preview,
                     String title,
                     boolean isAreaOptional) {
        super(legend, preview, title);
        this.isAreaOptional = isAreaOptional;
        init();
        addComponents();
    }

    @Override
    protected IUniqueSymbolArea getLegend() {
        return (IUniqueSymbolArea) legend;
    }

    private void init() {
        this.colorLabel = new ColorLabel(preview, getLegend().getFillLegend());
        if (isAreaOptional) {
            areaCheckBox = new JCheckBox(I18N.tr("Enable"));
            areaCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onClickAreaCheckBox();
                }
            });
            areaCheckBox.setSelected(true);
        }
        this.fillOpacitySpinner = new LineOpacitySpinner(
                getLegend().getFillLegend(), preview);
    }

    @Override
    public void addComponents() {
        if (isAreaOptional) {
            add(areaCheckBox, "align l");
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Color
        add(colorLabel);
        // Opacity
        add(new JLabel(I18N.tr(AbstractFieldPanel.OPACITY)));
        add(fillOpacitySpinner, "growx");
    }

    /**
     * If {@code isLineOptional()}, a {@code JCheckBox} will be added in the
     * UI to let the user enable or disable the fill configuration. In fact,
     * clicking on it will recursively enable or disable the containers
     * contained in the configuration panel.
     */
    private void onClickAreaCheckBox() {
        if (areaCheckBox.isSelected()) {
            // TODO: Answer why this works with opacity.
            getLegend().setFillLegend(colorLabel.getFill());
            setAreaFieldsState(true);
        } else {
            getLegend().setFillLegend(new NullSolidFillLegend());
            setAreaFieldsState(false);
        }
        preview.imageChanged();
    }

    private void setAreaFieldsState(boolean state) {
        ComponentUtil.setFieldState(state, colorLabel);
        ComponentUtil.setFieldState(state, fillOpacitySpinner);
    }
}
