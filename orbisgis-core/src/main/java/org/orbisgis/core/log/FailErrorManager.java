package org.orbisgis.core.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Throw error on error and warning log messages
 */

public class FailErrorManager extends AppenderSkeleton  {

        private boolean ignoreWarnings;
        private boolean ignoreErrors;

        public void setIgnoreWarnings(boolean ignore) {
                this.ignoreWarnings = ignore;
        }

        public void setIgnoreErrors(boolean b) {
                this.ignoreErrors = b;
        }

        @Override
        protected void append(LoggingEvent le) {
                if((le.getLevel() == Level.ERROR && !ignoreErrors) ||
                        (le.getLevel() == Level.WARN && !ignoreWarnings) ) {
                throw new RuntimeException(le.getThrowableInformation().getThrowable());
                }
        }

        public void close() {
        }

        public boolean requiresLayout() {
                return false;
        }

}
