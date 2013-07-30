package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.SymbolizerLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolLine;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class LinePanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(LinePanel.class);

    private ConstantPenStrokeLegend penStrokeMemory;

    private final boolean displayUom;
    private final boolean isLineOptional;
    private final boolean penStrokeIsConstant;

    private JCheckBox lineCheckBox;
    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private LineWidthSpinner lineWidthSpinner;
    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    public LinePanel(IUniqueSymbolLine legend,
                     CanvasSE preview,
                     String title,
                     ConstantPenStrokeLegend penStrokeMemory,
                     boolean displayUom,
                     boolean isLineOptional) {
        super(legend, preview);
        setBorder(BorderFactory.createTitledBorder(title));
        this.penStrokeMemory = penStrokeMemory;
        this.displayUom = displayUom;
        this.isLineOptional = isLineOptional;
        this.penStrokeIsConstant =
                getLegend().getPenStroke() instanceof ConstantPenStrokeLegend;
        init(penStrokeMemory, displayUom);
        addComponents();
    }

    /**
     * Gets the legend.
     *
     * @return The legend.
     */
    private IUniqueSymbolLine getLegend() {
        return (IUniqueSymbolLine) legend;
    }

    /**
     * Initialize the elements.
     *
     * @param penStrokeMemory
     * @param displayUom      Whether the stroke UOM should be displayed
     */
    private void init(ConstantPenStrokeLegend penStrokeMemory, boolean displayUom) {
        if (preview == null && legend != null) {
            initPreview();
        }
        ConstantPenStroke chosenPenStroke =
                penStrokeIsConstant ? getLegend().getPenStroke() : penStrokeMemory;
        this.colorLabel = new ColorLabel(preview, chosenPenStroke.getFillLegend());
        if (displayUom) {
            this.lineUOMComboBox =
                    new LineUOMComboBox((SymbolizerLegend) legend, preview);
        }
        this.lineWidthSpinner =
                new LineWidthSpinner(chosenPenStroke, preview);
        this.lineOpacitySpinner =
                new LineOpacitySpinner(chosenPenStroke.getFillLegend(), preview);
        this.dashArrayField =
                new DashArrayField((ConstantColorAndDashesPSLegend) chosenPenStroke, preview);
    }

    /**
     * Add the components to the UI.
     */
    private void addComponents() {
        if (isLineOptional) {
            lineCheckBox = new JCheckBox(I18N.tr("Enable"));
            lineCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onClickLineCheckBox();
                }
            });
            add(lineCheckBox, "align l");
            // We must check the CheckBox according to leg, not to legend.
            // legend is here mainly to let us fill safely all our
            // parameters.
            lineCheckBox.setSelected(penStrokeIsConstant);
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Line color
        add(colorLabel);

        // Unit of measure - line width
        if (displayUom) {
            JLabel uom = new JLabel(I18N.tr(AbstractFieldPanel.LINE_WIDTH_UNIT));
            add(uom);
            add(lineUOMComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        }
        // Line width
        add(new JLabel(I18N.tr(AbstractFieldPanel.WIDTH)));
        add(lineWidthSpinner, "growx");
        // Line opacity
        add(new JLabel(I18N.tr(AbstractFieldPanel.OPACITY)));
        add(lineOpacitySpinner, "growx");
        // Dash array
        add(new JLabel(I18N.tr(AbstractFieldPanel.DASH_ARRAY)));

        add(dashArrayField, "growx");
        if (isLineOptional) {
            setLineFieldsState(penStrokeIsConstant);
        }
    }

    /**
     * Change the state of all the fields used for the line configuration.
     *
     * @param enable
     */
    private void setLineFieldsState(boolean enable) {
        ComponentUtil.setFieldState(enable, colorLabel);
        if (displayUom) {
            if (lineUOMComboBox != null) {
                ComponentUtil.setFieldState(enable, lineUOMComboBox);
            }
        }
        ComponentUtil.setFieldState(enable, lineWidthSpinner);
        ComponentUtil.setFieldState(enable, lineOpacitySpinner);
        ComponentUtil.setFieldState(enable, dashArrayField);
    }

    /**
     * If {@code isLineOptional()}, a {@code JCheckBox} will be added in the
     * UI to let the user enable or disable the line configuration. In fact,
     * clicking on it will recursively enable or disable the containers
     * contained in the configuration panel.
     */
    public void onClickLineCheckBox() {
        if (lineCheckBox.isSelected()) {
            getLegend().setPenStroke(penStrokeMemory);
            setLineFieldsState(true);
        } else {
            // We must replace the old PenStroke representation with
            // its null representation.
            getLegend().setPenStroke(new NullPenStrokeLegend());
            setLineFieldsState(false);
        }
        preview.imageChanged();
    }
}
