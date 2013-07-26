package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class NonSpatialFieldsComboBox extends FieldComboBox {

    private static final Logger LOGGER = Logger.getLogger(NonSpatialFieldsComboBox.class);

    public NonSpatialFieldsComboBox(DataSource ds,
                                    AbstractRecodedLegend legend) {
        super(ds, legend);
    }

    /**
     * Initialize a {@code JComboBox} whose values are set according to the
     * non-spatial fields of {@code ds}.
     *
     * @return A JComboBox.
     */
    @Override
    protected void addFields() {
        try {
            Metadata md = ds.getMetadata();
            int fc = md.getFieldCount();
            for (int i = 0; i < fc; i++) {
                if (!TypeFactory.isSpatial(md.getFieldType(i).getTypeCode())) {
                    addItem(md.getFieldName(i));
                }
            }
        } catch (DriverException ex) {
            LOGGER.error(ex);
        }
    }
}
