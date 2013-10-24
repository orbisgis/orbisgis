package org.orbisgis.view.main.frames;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Manage MessageOverlay by capturing Logger events
 * @author Nicolas Fortin
 */
public class OverlayLoggerTarget extends AppenderSkeleton {
    private Logger logger;

    private MessageOverlay messageOverlay;

    /**
     * Constructor.
     * @param messageOverlay instance of messageOverlay
     */
    public OverlayLoggerTarget(MessageOverlay messageOverlay) {
        this.messageOverlay = messageOverlay;
    }

    /**
     * Add log appender.
     */
    public void initLogger(Logger logger) {
        this.logger = logger;
        logger.addAppender(this);
    }

    /**
     * Remove log appender
     */
    public void disposeLogger() {
        if(logger != null) {
            logger.removeAppender(this);
        }
    }

    @Override
    protected void append(LoggingEvent le) {
        if(le.getMessage()!=null) {
            MessageOverlay.MESSAGE_TYPE messageType;
            switch(le.getLevel().toInt()) {
                case Level.WARN_INT:
                case Level.ERROR_INT:
                case Level.FATAL_INT:
                    messageType = MessageOverlay.MESSAGE_TYPE.ERROR;
                    break;
                default:
                    messageType = MessageOverlay.MESSAGE_TYPE.INFO;
            }
            messageOverlay.setMessage(le.getRenderedMessage(), messageType);
            messageOverlay.start();
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
