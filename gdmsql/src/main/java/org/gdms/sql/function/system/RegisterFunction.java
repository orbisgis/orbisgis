package org.gdms.sql.function.system;

import org.apache.log4j.Logger;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.ReadAccess;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.progress.ProgressMonitor;

public final class RegisterFunction extends AbstractExecutorFunction {

        private static final Logger LOG = Logger.getLogger(RegisterFunction.class);

        @Override
        public void evaluate(SQLDataSourceFactory dsf, ReadAccess[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                String className = values[0].getAsString();

                try {
                        Class<?> javaClass = Class.forName(className);
                        Class<?> fun = Class.forName("org.gdms.sql.function.Function");
                        if (!fun.isAssignableFrom(javaClass.getSuperclass())) {
                                throw new FunctionException("It's not a gdms SQL function");
                        }

                        FunctionManager.addFunction((Class<? extends Function>) javaClass);
                        
                } catch (ClassNotFoundException e) {
                        throw new FunctionException("Class not found" + e);
                }
        }

        @Override
        public String getDescription() {

                return "A simple way to register a function.";
        }

        @Override
        public String getName() {
                return "FunctionRegister";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT FunctionRegister('org.gdms.myFunction')";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(ScalarArgument.STRING)
                        };
        }
}
