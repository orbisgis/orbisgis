/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.WriteBufferManager;

/**
 * Command to delete a {@link org.gdms.data.edition.Field} in a datasource.
 * 
 */
public class DelFieldCommand implements Command {

        private EditionDecorator dataSource;
        private int fieldIndex;
        private DelFieldInfo info;

        /**
         * Public constructor
         * @param dataSource The datasource where the command will occur
         * @param index the index of the field
         */
        public DelFieldCommand(EditionDecorator dataSource, int index) {
                this.dataSource = dataSource;
                this.fieldIndex = index;
        }

        @Override
        public void redo() throws DriverException {
                info = dataSource.doRemoveField(fieldIndex);
        }

        @Override
        public void undo() throws DriverException {
                try {
                        dataSource.undoDeleteField(info.fieldIndex, info.field,
                                info.getFieldValues());
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void clear() {
                if (info != null) {
                        info.fieldFile.delete();
                }
        }

        /**
         * Inner class used to store data of a field before it is deleted, so the delete command can be undone.
         */
        public static class DelFieldInfo {

                /**
                 * The index of the field
                 */
                public int fieldIndex;
                /**
                 * The field
                 */
                public Field field;
                //A factory used to write datas to fieldFile
                private DataSourceFactory factory;
                //The file were datas are stored
                private File fieldFile;

                /**
                 * Public constructor. Called when a Field is deleted.
                 * @param factory   factory used to write the datas
                 * @param fieldIndex index of the field
                 * @param field field to be deleted
                 * @param fieldValues {@link org.gdms.data.values.Value} associated to the field.
                 * @throws IOException If there is an error durring the writeValues step
                 */
                public DelFieldInfo(DataSourceFactory factory, int fieldIndex,
                        Field field, Value[] fieldValues) throws IOException {
                        super();
                        this.fieldIndex = fieldIndex;
                        this.field = field;
                        this.factory = factory;
                        writeValues(fieldValues);
                }

                /**
                 * Extract the values stored in the fieldFile
                 * @return the values stored in the file
                 * @throws IOException if there is a pproblem during the file reading
                 */
                public Value[] getFieldValues() throws IOException {
                        FileInputStream fis = null;
                        DataInputStream dis = null;
                        try {
                                fis = new FileInputStream(fieldFile);
                                dis = new DataInputStream(fis);
                                byte[] buffer = new byte[(int) fis.getChannel().size()];
                                dis.readFully(buffer);
                                dis.close();
                                ValueCollection ret = (ValueCollection) ValueFactory.createValue(Type.COLLECTION, buffer);
                                return ret.getValues();
                        } finally {
                                if (dis != null) {
                                        dis.close();
                                } else if (fis != null) {
                                        fis.close();
                                }
                        }
                }

                /**
                 * write the values on disk
                 * @param fieldValues
                 * @throws IOException
                 */
                private void writeValues(Value[] fieldValues) throws IOException {
                        fieldFile = new File(factory.getTempFile());
                        FileChannel channel = new FileOutputStream(fieldFile).getChannel();
                        WriteBufferManager wb = new WriteBufferManager(channel);
                        ValueCollection v = ValueFactory.createValue(fieldValues);
                        wb.put(v.getBytes());
                        wb.flush();
                        channel.close();
                }
        }
}
