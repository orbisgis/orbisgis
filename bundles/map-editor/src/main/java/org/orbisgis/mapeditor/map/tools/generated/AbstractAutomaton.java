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

package org.orbisgis.mapeditor.map.tools.generated;

import org.apache.log4j.Logger;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.tool.Automaton;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.Point;

/**
 * Common properties between Automaton implementations
 * @author Nicolas Fortin
 */
public abstract class AbstractAutomaton implements Automaton {
    protected final I18n i18n = I18nFactory.getI18n(AbstractAutomaton.class);
    protected final Logger logger = Logger.getLogger(AbstractAutomaton.class);
    private static final Point DEFAULT_CURSOR_OFFSET = new Point(8,8);
    protected Status status = Status.STANDBY;
    protected MapContext mc;
    protected ToolManager tm;

    @Override
    public void init(MapContext mc, ToolManager tm) throws TransitionException,
            FinishedAutomatonException {
        logger.info("status: " + status);
        this.mc = mc;
        this.tm = tm;
        status = Status.STANDBY;
        transitionTo_Standby(mc, tm);
        if (isFinished(status)) {
            throw new FinishedAutomatonException();
        }
    }

    @Override
    public ImageIcon getCursor() {
        return null; // Cross cursor
    }

    /**
     * @return Automaton status
     */
    public final Status getStatus() {
        return status;
    }

    /**
     * Update the current state of status
     * @param status New status state
     */
    protected final void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Is the current Status is the final one.
     * @param status Automaton status
     * @return True if this is the final status.
     */
    public abstract boolean isFinished(Status status);

    /**
     * Stand By status is set when the Automaton is ready for user input, called after {@link #init(MapContext, ToolManager)}
     * @param mc MapContext instance
     * @param tm ToolManager instance
     * @throws FinishedAutomatonException
     * @throws TransitionException
     */
    protected abstract void transitionTo_Standby(MapContext mc, ToolManager tm)
            throws FinishedAutomatonException, TransitionException;

    @Override
    public java.awt.Point getHotSpotOffset() {
        return DEFAULT_CURSOR_OFFSET;
    }

    @Override
    public String getMessage() {
        return ""; //No message if not defined by the automaton
    }
}
