/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mainframe.impl;

import org.orbisgis.sif.components.MessageOverlay;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

/**
 * Can be registered in {@link org.osgi.service.log.LogReaderService}. Log output to main frame overlay
 * @author Nicolas Fortin
 */
public class LogListenerOverlay extends MessageOverlay implements LogListener {

    @Override
    public void logged(LogEntry entry) {
        if(entry.getLevel() <= LogService.LOG_INFO &&
                entry.getMessage() != null) {
            MessageOverlay.MESSAGE_TYPE messageType;
            switch(entry.getLevel()) {
                case LogService.LOG_WARNING:
                case LogService.LOG_ERROR:
                    messageType = MessageOverlay.MESSAGE_TYPE.ERROR;
                    break;
                default:
                    messageType = MessageOverlay.MESSAGE_TYPE.INFO;
            }
            setMessage(entry.getMessage(), messageType);
            start();
        }
    }
}
