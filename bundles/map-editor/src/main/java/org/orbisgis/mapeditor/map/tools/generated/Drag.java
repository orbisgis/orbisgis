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
package org.orbisgis.mapeditor.map.tools.generated;

import java.awt.Graphics;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.NoSuchTransitionException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * Common states of Drag operations.
 */
public abstract class Drag extends AbstractAutomaton {

    @Override
	public String[] getTransitionLabels() {
		return new String[0];
	}

    @Override
	public Code[] getTransitionCodes() {
		return new Code[0];
	}

    @Override
	public void transition(Code code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
                Status preStatus;
                switch(status){
                        case STANDBY:
                                if (Code.PRESS == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.MOUSE_DOWN;
                                                transitionTo_MouseDown(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case MOUSE_DOWN:
                                if (Code.RELEASE == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.MOUSE_RELEASED;
                                                transitionTo_MouseReleased(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case MOUSE_RELEASED:
                                if (Code.FINISHED == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.STANDBY;
                                                transitionTo_Standby(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        default:
                                throw new NoSuchTransitionException(code.toString());
                }
	}

    @Override
	public boolean isFinished(Status status) {
                switch(status){
                        case STANDBY:
                        case MOUSE_DOWN:
                        case MOUSE_RELEASED:
                                return false;
                        default:
                                throw new RuntimeException("Invalid status: " + status);
                }
	}

    @Override
	public void draw(Graphics g) throws DrawingException {
                switch(status){
                        case STANDBY:
                                drawIn_Standby(g, mc, tm);
                                break;
                        case MOUSE_DOWN:
                                drawIn_MouseDown(g, mc, tm);
                                break;
                        case MOUSE_RELEASED:
                                drawIn_MouseReleased(g, mc, tm);
                                break;
                }

	}

    @Override
	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;


	public abstract void drawIn_Standby(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_MouseDown(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_MouseDown(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_MouseReleased(MapContext vc,
			ToolManager tm) throws FinishedAutomatonException,
			TransitionException;

	public abstract void drawIn_MouseReleased(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

    @Override
    public String getMessage() {
            switch(status){
                    case STANDBY:
                            return i18n.tr("Select start point");
                    case MOUSE_DOWN:
                            return i18n.tr("Drag to destination point");
                    case MOUSE_RELEASED:
                            return "";
                    default:
                            throw new RuntimeException("Invalid status: " + status);
            }
	}

    @Override
	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {
	}

    @Override
	public java.awt.Point getHotSpotOffset() {
		return new java.awt.Point(8, 8);
	}
}
