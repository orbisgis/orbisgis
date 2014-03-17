/**
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

import java.awt.Graphics;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.tool.*;


public abstract class Selection extends AbstractAutomaton {

    @Override
	public String[] getTransitionLabels() {
		return new String[]{};
	}

    @Override
	public Code[] getTransitionCodes() {
		return new Code[]{};
	}

        @Override
	public void transition(Code code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
                if (Code.ESC == code) {
                    status = Status.STANDBY;
                    transitionTo_Standby(mc, tm);
                    if (isFinished(status)) {
                        throw new FinishedAutomatonException();
                    }
                }
                Status preStatus;
                switch(status){
                        case STANDBY :
                                if (Code.POINT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.ONE_POINT;
                                                transitionTo_OnePoint(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case ONE_POINT :
                                switch(code){
                                        case SELECTION :
                                                preStatus = status;
                                                try {
                                                        status = Status.SELECTION;
                                                        transitionTo_Selection(mc, tm);
                                                        if (isFinished(status)) {
                                                                throw new FinishedAutomatonException();
                                                        }
                                                } catch (TransitionException e) {
                                                        status = preStatus;
                                                        throw e;
                                                }
                                                break;
                                        case NO_SELECTION :
                                                preStatus = status;
                                                try {
                                                        status = Status.ONE_POINT_LEFT;
                                                        transitionTo_OnePointLeft(mc, tm);
                                                        if (isFinished(status)) {
                                                                throw new FinishedAutomatonException();
                                                        }
                                                } catch (TransitionException e) {
                                                        status = preStatus;
                                                        throw e;
                                                }
                                                break;
                                        case INIT :
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
                                                break;
                                }
                                break;
                        case ONE_POINT_LEFT :
                                if (Code.POINT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.TWO_POINTS;
                                                transitionTo_TwoPoints(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case TWO_POINTS :
                                if (Code.SELECTION == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.SELECTION;
                                                transitionTo_Selection(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                } else if (Code.NO_SELECTION == code) {
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
                        case SELECTION :
                                if (Code.POINT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.POINT_WITH_SELECTION;
                                                transitionTo_PointWithSelection(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case POINT_WITH_SELECTION :

                                if (Code.IN_HANDLER == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.MOVEMENT;
                                                transitionTo_Movement(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                } else if (Code.OUT_HANDLER == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.ONE_POINT;
                                                transitionTo_OnePoint(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case MOVEMENT :
                                if (Code.POINT == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.MAKE_MOVE;
                                                transitionTo_MakeMove(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        case MAKE_MOVE :
                                if (Code.EMPTY == code) {
                                        preStatus = status;
                                        try {
                                                status = Status.SELECTION;
                                                transitionTo_Selection(mc, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                }
                                break;
                        default :
                                throw new NoSuchTransitionException(code.toString());
                }

	}

	public boolean isFinished(Status status) {
                switch(status){
                        case STANDBY :
                        case ONE_POINT :
                        case ONE_POINT_LEFT :
                        case TWO_POINTS :
                        case SELECTION :
                        case POINT_WITH_SELECTION :
                        case MOVEMENT :
                        case MAKE_MOVE :
                                return false;
                        default:
                                throw new RuntimeException("Invalid status: " + status);
                }
	}

        @Override
	public void draw(Graphics g) throws DrawingException {
                switch(status){
                        case STANDBY :
                                drawIn_Standby(g, mc, tm);
                                break;
                        case ONE_POINT:
                                drawIn_OnePoint(g, mc, tm);
                                break;
                        case ONE_POINT_LEFT :
                                drawIn_OnePointLeft(g, mc, tm);
                                break;
                        case TWO_POINTS :
                                drawIn_TwoPoints(g, mc, tm);
                                break;
                        case SELECTION :
                                drawIn_Selection(g, mc, tm);
                                break;
                        case POINT_WITH_SELECTION :
                                drawIn_PointWithSelection(g, mc, tm);
                                break;
                        case MOVEMENT :
                                drawIn_Movement(g, mc, tm);
                                break;
                        case MAKE_MOVE :
                                drawIn_MakeMove(g, mc, tm);
                                break;
		}
	}

	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Standby(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_OnePoint(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_OnePoint(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_OnePointLeft(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_TwoPoints(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_TwoPoints(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_Selection(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Selection(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_PointWithSelection(MapContext vc,
			ToolManager tm) throws FinishedAutomatonException,
			TransitionException;

	public abstract void drawIn_PointWithSelection(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_Movement(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Movement(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_MakeMove(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_MakeMove(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;


	public String getMessage() {
                switch(status){
                        case STANDBY :
                                return i18n.tr("Select a geometry or draw a selection rectangle");
                        case ONE_POINT_LEFT:
                                return i18n.tr("Select the second point");
                        case SELECTION :
                                return i18n.tr("Click a handler to move it or select another geometry");
                        case MOVEMENT:
                                return i18n.tr("Place the handler in its new position");
                        case MAKE_MOVE :
                        case TWO_POINTS :
                        case POINT_WITH_SELECTION :
                        case ONE_POINT :
                                return "";
                        default :
                                throw new RuntimeException(i18n.tr("Can't find the status of this tool."));
                }
	}

    @Override
	public String getTooltip() {
		return i18n.tr("Select a feature");
	}


        @Override
	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {
	}

}
