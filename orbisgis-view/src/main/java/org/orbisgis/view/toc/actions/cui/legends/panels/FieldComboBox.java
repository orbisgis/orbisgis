package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.gdms.data.DataSource;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.components.WideComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 25/07/13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class FieldComboBox extends WideComboBox {

    private static final Logger LOGGER = Logger.getLogger("gui." + FieldComboBox.class);

    protected DataSource ds;
    private MappedLegend legend;

    public FieldComboBox(DataSource ds, final MappedLegend legend) {
        super();
        this.ds = ds;
        this.legend = legend;
        init();
    }

    /**
     * Creates and fill the combobox that will be used to compute the
     * analysis.
     *
     * @return A ComboBox linked to the underlying MappedLegend that
     *         configures the analysis field.
     */
    private void init() {
        if (ds != null) {
            addFields();
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                setSelectedItem(field);
            }
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateField(legend, (String) ((WideComboBox) e.getSource())
                            .getSelectedItem());
                }
            });
            updateField(legend, (String) getSelectedItem());
        } else {
            LOGGER.error("Cannot construct the field combo box because the " +
                    "DataSource is null.");
        }
    }

    /**
     * Add the fields.
     */
    protected abstract void addFields();

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param name The new field.
     */
    private void updateField(MappedLegend legend, String name) {
        legend.setLookupFieldName(name);
    }
}
