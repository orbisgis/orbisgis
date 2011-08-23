package org.gdms.driver.dxf;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.DataSet;
import org.gdms.source.SourceManager;

public final class DXFDriver implements FileDriver, DataSet {

        private DataSet result;
        private static final Logger LOG = Logger.getLogger(DXFDriver.class);
        private Schema schema;
        private File file;

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                result = null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"dxf"};
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                try {
                        DxfFile dxfFile = DxfFile.createFromFile(file);
                        result = dxfFile.read().getTable("main");
                        System.gc();
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public String getTypeDescription() {
                return "DXF format";
        }

        @Override
        public String getTypeName() {
                return "DXF";
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "DXF driver";
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return new TypeDefinition[0];
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                return result.getFieldValue(rowIndex, fieldId);
        }

        @Override
        public long getRowCount() throws DriverException {
                return result.getRowCount();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return result.getScope(dimension);
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }

        @Override
        public void setFile(File file) throws DriverException {
                this.file = file;
                schema = new DefaultSchema("DXF" + file.getAbsolutePath().hashCode());
                DxfFile.initializeDXF_SCHEMA();
                schema.addTable("main", DxfFile.DXF_SCHEMA);
        }

        @Override
        public boolean isOpen() {
                return result != null;
        }
}
