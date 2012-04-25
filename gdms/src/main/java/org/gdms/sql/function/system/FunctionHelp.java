package org.gdms.sql.function.system;

import org.apache.log4j.Logger;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.InitializationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class FunctionHelp extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(FunctionHelp.class);
        
        private Metadata metadata;

        public FunctionHelp() {
                DefaultMetadata defaultMetadata = new DefaultMetadata();
                try {
                        defaultMetadata.addField("name", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("sqlorder", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("description", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("type", TypeFactory.createType(Type.STRING));
                } catch (DriverException ex) {
                        throw new InitializationException(ex);
                }
                metadata = defaultMetadata;
        }

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");

                String[] functions = dsf.getFunctionManager().getFunctionNames();

                try {
                        MemoryDataSetDriver genericObjectDriver = new MemoryDataSetDriver(
                                metadata);
                        for (String function : functions) {

                                Function fct = dsf.getFunctionManager().getFunction(function);
                                String type = null;
                                if (fct.isScalar()) {
                                        type = "Scalar Function";
                                } else if (fct.isTable()) {
                                        type = "Table Function";
                                } else if (fct.isAggregate()) {
                                        type = "Aggregate Function";
                                } else if (fct.isExecutor()) {
                                        type = "Executor Function";
                                }
                                genericObjectDriver.addValues(ValueFactory.createValue(function), ValueFactory.createValue(fct.getSqlOrder()), ValueFactory.createValue(fct.getDescription()), ValueFactory.createValue(type));
                        }

                        return genericObjectDriver;
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }

        }

        @Override
        public String getDescription() {
                return "Create a table with all functions";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return metadata;
        }

        @Override
        public String getName() {
                return "FunctionHelp";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT * from FunctionHelp()";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.ANY)
                        };
        }
}
