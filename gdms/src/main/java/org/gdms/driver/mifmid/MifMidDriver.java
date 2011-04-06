package org.gdms.driver.mifmid;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.source.SourceManager;

public class MifMidDriver implements FileDriver {

        private MifMidReader mm;
        private GenericObjectDriver driver;

        @Override
        public void close() throws DriverException {
                mm.close();
                mm = null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"mif", "mid"};
        }

        @Override
        public void open(File file) throws DriverException {

                try {
                        mm = new MifMidReader(file, false);
                        driver = mm.read();
                } catch (IOException e) {
                        e.printStackTrace();
                } catch (Exception e) {
                        e.printStackTrace();
                }

        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return driver.getMetadata();
        }

        @Override
        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public String getTypeDescription() {
                return "MIF/MID format";
        }

        @Override
        public String getTypeName() {
                return "MIF";
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "MIF/MID driver";
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                return driver.getFieldValue(rowIndex, fieldId);
        }

        @Override
        public long getRowCount() throws DriverException {
                return driver.getRowCount();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return driver.getScope(dimension);
        }

        @Override
        public boolean isOpen() {
                // once .open() is called, the content of driver
                // is always accessible.
                return driver != null;
        }
}
