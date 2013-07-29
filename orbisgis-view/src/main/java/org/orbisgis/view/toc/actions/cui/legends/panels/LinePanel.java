package org.orbisgis.view.toc.actions.cui.legends.panels;

import net.miginfocom.swing.MigLayout;
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
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class LinePanel extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(LinePanel.class);

    private IUniqueSymbolLine legend;
    private ConstantPenStrokeLegend penStrokeMemory;
    private CanvasSE preview;
    private final boolean displayUom;
    private final boolean isLineOptional;
    private final boolean penStrokeConstant;

    private JCheckBox lineCheckBox;
    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private LineWidthSpinner lineWidthSpinner;
    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    public LinePanel(IUniqueSymbolLine legend,
                     ConstantPenStrokeLegend penStrokeMemory,
                     CanvasSE preview,
                     boolean displayUom,
                     boolean isLineOptional,
                     String title) {
        super(new MigLayout("wrap 2", AbstractFieldPanel.COLUMN_CONSTRAINTS));
        setBorder(BorderFactory.createTitledBorder(title));
        this.legend = legend;
        this.penStrokeMemory = penStrokeMemory;
        this.preview = preview;
        this.displayUom = displayUom;
        this.isLineOptional = isLineOptional;

        ConstantPenStroke penStroke = legend.getPenStroke();
        if (preview == null && legend != null) {
            initPreview();
        }
        this.penStrokeConstant = penStroke instanceof ConstantPenStrokeLegend;
        ConstantPenStroke chosenPenStroke =
                penStrokeConstant ? penStroke : penStrokeMemory;

        this.colorLabel = new ColorLabel(preview, chosenPenStroke.getFillLegend());
        if (displayUom) {
            this.lineUOMComboBox = new LineUOMComboBox((SymbolizerLegend) legend, preview);
        }
        this.lineWidthSpinner = new LineWidthSpinner(chosenPenStroke, preview);
        this.lineOpacitySpinner = new LineOpacitySpinner(chosenPenStroke.getFillLegend(), preview);
        dashArrayField = new DashArrayField((ConstantColorAndDashesPSLegend) chosenPenStroke, preview);

        init();
    }

    /**
     * Gets a panel containing all the fields to edit a unique line.
     *
     * @return
     */
    private void init() {
        if (isLineOptional) {
            lineCheckBox = new JCheckBox(I18N.tr("Enable"));
            lineCheckBox.addActionListener(
                    EventHandler.create(ActionListener.class, this, "onClickLineCheckBox"));
            add(lineCheckBox, "align l");
            // We must check the CheckBox according to leg, not to legend.
            // legend is here mainly to let us fill safely all our
            // parameters.
            lineCheckBox.setSelected(penStrokeConstant);
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
            setLineFieldsState(penStrokeConstant);
        }
    }

    /**
     * Rebuild the {@code CanvasSE} instance used to display a preview of
     * the current symbol.
     */
    private void initPreview() {
        if (legend != null) {
            preview = new CanvasSE(legend.getSymbolizer());
            preview.imageChanged();
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
            legend.setPenStroke(penStrokeMemory);
            setLineFieldsState(true);
        } else {
            // We must replace the old PenStroke representation with
            // its null representation.
            legend.setPenStroke(new NullPenStrokeLegend());
            setLineFieldsState(false);
        }
        preview.imageChanged();
    }
}
