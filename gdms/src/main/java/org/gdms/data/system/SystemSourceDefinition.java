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
package org.gdms.data.system;

import java.io.File;

import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.file.FileDataSourceAdapter;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.SystemDefinitionType;

public class SystemSourceDefinition extends FileSourceDefinition {

        public SystemSourceDefinition(SystemSource systemSource) {
                super(systemSource.getFile(), DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException {
                if (!file.exists()) {
                        throw new DataSourceCreationException(file + " "
                                + I18N.getString("gdms.datasource.error.noexits"));
                }
                (getDriver()).setDataSourceFactory(getDataSourceFactory());

                FileDataSourceAdapter ds = new FileDataSourceAdapter(
                        getSource(tableName), file, (FileDriver) getDriver(), false);
                return ds;
        }

        @Override
        public int getType() {
                return SourceManager.SYSTEM_TABLE;
        }

        @Override
        public DefinitionType getDefinition() {
                SystemDefinitionType ret = new SystemDefinitionType();
                ret.setPath(file.getAbsolutePath());
                return ret;
        }

        public static DataSourceDefinition createFromXML(
                SystemDefinitionType definitionType) {
                return new SystemSourceDefinition(new SystemSource(new File(
                        definitionType.getPath())));
        }
}
