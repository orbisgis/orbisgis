package org.gdms.sql.customQuery.system;

import java.io.File;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

/**
 *
 * @author Antoine Gourlay
 */
public class ExportCall implements CustomQuery {

        @Override
        public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values, IProgressMonitor pm) throws ExecutionException {
                final SourceManager sourceManager = dsf.getSourceManager();

                if (values.length == 2) {
                        String name = values[0].getAsString();
                        String file = values[1].getAsString();

                        DataSource ds = null;
                        try {
                                ds = dsf.getDataSource(name);
                        } catch (NoSuchTableException ex) {
                                throw new ExecutionException(ex);
                        } catch (DataSourceCreationException ex) {
                                throw new ExecutionException(ex);
                        }
                        String destName = sourceManager.nameAndRegister(new File(file));
                        try {
                                ds.open();
                                dsf.saveContents(destName, ds, pm);
                                ds.close();
                        } catch (DriverException ex) {
                                throw new ExecutionException(ex);
                        } finally {
                                sourceManager.remove(destName);
                        }
                } else if (values.length == 9 || values.length == 10) {

                        final String fromName = values[0].toString();
                        DataSource ds = null;
                        try {
                                ds = dsf.getDataSource(fromName);
                        } catch (NoSuchTableException ex) {
                                throw new ExecutionException(ex);
                        } catch (DataSourceCreationException ex) {
                                throw new ExecutionException(ex);
                        }

                        final String vendor = values[1].toString();
                        final String host = values[2].toString();
                        final int port = values[3].getAsInt();
                        final String dbName = values[4].toString();
                        final String user = values[5].toString();
                        final String password = values[6].toString();
                        String schemaName = null;
                        String tableName = null;
                        String name = null;
                        if (values.length == 9) {
                                tableName = values[7].toString();
                                name = values[8].toString();
                        }
                        if (values.length == 10) {
                                schemaName = values[7].toString();
                                tableName = values[8].toString();
                                name = values[9].toString();
                        }

                        String destName = sourceManager.nameAndRegister(new DBTableSourceDefinition(
                                new DBSource(host, port, dbName,
                                user, password, schemaName, tableName, "jdbc:" + vendor)));
                        try {
                                dsf.saveContents(destName, ds);
                        } catch (DriverException ex) {
                                throw new ExecutionException(ex);
                        } finally {
                                sourceManager.remove(destName);
                        }
                } else {
                        throw new ExecutionException("Wrong number of arguments. See function description");
                }

                return null;
        }

        @Override
        public String getName() {
                return "Export";
        }

        @Override
        public String getDescription() {
                return "Exports an existing table to the specified file and format.";
        }

        @Override
        public String getSqlOrder() {
                return "1) SELECT Export('myTable', '/home/myuser/myFile.shp')\n"
                        + "2) SELECT Export('myTable', vendor', 'host', port, "
                        + "dbName, user, password, tableName, dsEntryName);\n"
                        + "3) select Export('myTable', vendor', 'host', port, "
                        + "dbName, user, password, schema, tableName, dsEntryName);\n";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return null;
        }

        @Override
        public TableDefinition[] getTablesDefinitions() {
                return new TableDefinition[]{};
        }

        @Override
        public Arguments[] getFunctionArguments() {
                return new Arguments[]{
                                new Arguments(Argument.STRING, Argument.STRING),
                                new Arguments(Argument.STRING, Argument.STRING,
                                Argument.STRING, Argument.INT, Argument.STRING,
                                Argument.STRING, Argument.STRING, Argument.STRING,
                                Argument.STRING),
                                new Arguments(Argument.STRING, Argument.STRING,
                                Argument.STRING, Argument.INT, Argument.STRING,
                                Argument.STRING, Argument.STRING, Argument.STRING,
                                Argument.STRING, Argument.STRING)
                        };
        }
}
