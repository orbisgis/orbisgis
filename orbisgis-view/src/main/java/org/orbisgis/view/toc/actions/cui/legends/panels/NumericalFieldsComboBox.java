package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class NumericalFieldsComboBox extends FieldComboBox {

    private static final Logger LOGGER = Logger.getLogger(NumericalFieldsComboBox.class);

    public NumericalFieldsComboBox(DataSource ds,
                                   final AbstractCategorizedLegend legend) {
        super(ds, legend);
    }

    /**
     * Initialize a {@code JComboBox} whose values are set according to the
     * numerical fields of {@code ds}.
     *
     * @return A JComboBox.
     */
    @Override
    protected void addFields() {
        try {
            Metadata md = ds.getMetadata();
            int fc = md.getFieldCount();
            for (int i = 0; i < fc; i++) {
                if (TypeFactory.isNumerical(md.getFieldType(i).getTypeCode())) {
                    addItem(md.getFieldName(i));
                }
            }
        } catch (DriverException ex) {
            LOGGER.error(ex);
        }
    }
}
