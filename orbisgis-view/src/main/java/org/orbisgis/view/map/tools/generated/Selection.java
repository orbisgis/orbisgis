/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.map.tools.generated;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


public abstract class Selection implements Automaton {
        protected static final I18n I18N = I18nFactory.getI18n(Selection.class);
	private static Logger logger = Logger.getLogger(Selection.class);

	private Status status = Status.STANDBY;

	private MapContext ec;

	private ToolManager tm;

        @Override
	public String[] getTransitionLabels() {
		return new String[]{};
	}

        @Override
	public Code[] getTransitionCodes() {
		return new Code[]{};
	}

        @Override
	public void init(MapContext ec, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ec;
		this.tm = tm;
		status = Status.STANDBY;
		transitionTo_Standby(ec, tm);
		if (isFinished(status)) {
			throw new FinishedAutomatonException();
		}
	}

        @Override
	public void transition(Code code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);
                Status preStatus;
                switch(status){
                        case STANDBY :
                                if (Code.POINT.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.ONE_POINT;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_OnePoint(ec, tm);
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
                                                        logger.info("status: " + status);
                                                        double[] v = tm.getValues();
                                                        for (int i = 0; i < v.length; i++) {
                                                                logger.info("value: " + v[i]);
                                                        }
                                                        transitionTo_Selection(ec, tm);
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
                                                        logger.info("status: " + status);
                                                        double[] v = tm.getValues();
                                                        for (int i = 0; i < v.length; i++) {
                                                                logger.info("value: " + v[i]);
                                                        }
                                                        transitionTo_OnePointLeft(ec, tm);
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
                                                        logger.info("status: " + status);
                                                        double[] v = tm.getValues();
                                                        for (int i = 0; i < v.length; i++) {
                                                                logger.info("value: " + v[i]);
                                                        }
                                                        transitionTo_Standby(ec, tm);
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
                                if (Code.POINT.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.TWO_POINTS;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_TwoPoints(ec, tm);
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
                                if (Code.SELECTION.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.SELECTION;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_Selection(ec, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                } else if (Code.NO_SELECTION.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.STANDBY;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_Standby(ec, tm);
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
                                if (Code.POINT.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.POINT_WITH_SELECTION;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_PointWithSelection(ec, tm);
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

                                if (Code.IN_HANDLER.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.MOVEMENT;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_Movement(ec, tm);
                                                if (isFinished(status)) {
                                                        throw new FinishedAutomatonException();
                                                }
                                        } catch (TransitionException e) {
                                                status = preStatus;
                                                throw e;
                                        }
                                } else if (Code.OUT_HANDLER.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.ONE_POINT;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_OnePoint(ec, tm);
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
                                if (Code.POINT.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.MAKE_MOVE;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_MakeMove(ec, tm);
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
                                if (Code.EMPTY.equals(code)) {
                                        preStatus = status;
                                        try {
                                                status = Status.SELECTION;
                                                logger.info("status: " + status);
                                                double[] v = tm.getValues();
                                                for (int i = 0; i < v.length; i++) {
                                                        logger.info("value: " + v[i]);
                                                }
                                                transitionTo_Selection(ec, tm);
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
                                if (Code.ESC.equals(code)) {
                                        status = Status.STANDBY;
                                        transitionTo_Standby(ec, tm);
                                        if (isFinished(status)) {
                                                throw new FinishedAutomatonException();
                                        }
                                } else {
                                        throw new NoSuchTransitionException(code.toString());
                                }

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
                                drawIn_Standby(g, ec, tm);
                                break;
                        case ONE_POINT:
                                drawIn_OnePoint(g, ec, tm);
                                break;
                        case ONE_POINT_LEFT :
                                drawIn_OnePointLeft(g, ec, tm);
                                break;
                        case TWO_POINTS :
                                drawIn_TwoPoints(g, ec, tm);
                                break;
                        case SELECTION :
                                drawIn_Selection(g, ec, tm);
                                break;
                        case POINT_WITH_SELECTION :
                                drawIn_PointWithSelection(g, ec, tm);
                                break;
                        case MOVEMENT :
                                drawIn_Movement(g, ec, tm);
                                break;
                        case MAKE_MOVE :
                                drawIn_MakeMove(g, ec, tm);
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

	protected void setStatus(Status status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status.toString();
	}

        @Override
	public String getName() {
		return Status.SELECTION.toString();
	}

	public String getMessage() {
                switch(status){
                        case STANDBY :
                                return I18N.tr("Select a geometry or draw a selection rectangle");
                        case ONE_POINT_LEFT:
                                return I18N.tr("Select the second point");
                        case SELECTION :
                                return I18N.tr("Click a handler to move it or select another geometry");
                        case MOVEMENT:
                                return I18N.tr("Place the handler in its new position");
                        case MAKE_MOVE :
                        case TWO_POINTS :
                        case POINT_WITH_SELECTION :
                        case ONE_POINT :
                                return "";
                        default :
                                throw new RuntimeException(I18N.tr("Can't find the status of this tool."));
                }
	}

	public String getConsoleCommand() {
		return "select";
	}

        @Override
	public String getTooltip() {
		return I18N.tr("Select a feature");
	}

        @Override
	public ImageIcon getCursor() {
        	return null;
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
