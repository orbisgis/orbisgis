/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
