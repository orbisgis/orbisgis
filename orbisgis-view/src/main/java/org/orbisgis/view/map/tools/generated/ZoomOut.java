/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.view.map.tools.generated;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.NoSuchTransitionException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


public abstract class ZoomOut implements Automaton {
        protected final static I18n I18N = I18nFactory.getI18n(ZoomOut.class);
	private static Logger logger = Logger.getLogger(ZoomOut.class.getName());

	private String status = "Standby";

	private MapContext ec;

	private ToolManager tm;

        public ImageIcon getCursor() {
            return null;
        }

        
        
	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();

		if ("Standby".equals(status)) {

		}

		if ("Done".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();

		if ("Standby".equals(status)) {

		}

		if ("Done".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

		return ret.toArray(new String[0]);
	}

	public void init(MapContext ec, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ec;
		this.tm = tm;
		status = "Standby";
		transitionTo_Standby(ec, tm);
		if (isFinished(status)) {
			throw new FinishedAutomatonException();
		}
	}

	public void transition(String code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);

		if ("Standby".equals(status)) {

			if ("point".equals(code)) {
				String preStatus = status;
				try {
					status = "Done";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Done(ec, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("Done".equals(status)) {

			if ("init".equals(code)) {
				String preStatus = status;
				try {
					status = "Standby";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Standby(ec, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("Cancel".equals(status)) {

		}

		if ("esc".equals(code)) {
			status = "Cancel";
			transitionTo_Cancel(ec, tm);
			if (isFinished(status)) {
				throw new FinishedAutomatonException();
			}
			return;
		}

		throw new NoSuchTransitionException(code);
	}

	public boolean isFinished(String status) {

		if ("Standby".equals(status)) {

			return false;

		}

		if ("Done".equals(status)) {

			return false;

		}

		if ("Cancel".equals(status)) {

			return true;

		}

		throw new RuntimeException("Invalid status: " + status);
	}

	public void draw(Graphics g) throws DrawingException {

		if ("Standby".equals(status)) {
			drawIn_Standby(g, ec, tm);
		}

		if ("Done".equals(status)) {
			drawIn_Done(g, ec, tm);
		}

		if ("Cancel".equals(status)) {
			drawIn_Cancel(g, ec, tm);
		}

	}

	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Standby(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Done(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;

	public abstract void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException;

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "ZoomOut";
	}

	public String getMessage() {

		if ("Standby".equals(status)) {
			return I18N.tr("orbisgis.core.ui.editors.map.tool.zoomout_standby");
		}

		if ("Done".equals(status)) {
			return "";
		}

		if ("Cancel".equals(status)) {
			return "";
		}

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "";
	}

	public String getTooltip() {
		return I18N.tr("orbisgis.core.ui.editors.map.tool.zoomout_tooltip");
	}

	private ImageIcon mouseCursor;

	public ImageIcon getImageIcon() {
		if (mouseCursor != null) {
			return mouseCursor;
		} else {
			return null;
		}
	}

	public void setMouseCursor(ImageIcon mouseCursor) {
		this.mouseCursor = mouseCursor;
	}

	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {

		if ("Standby".equals(status)) {

		}

		if ("Done".equals(status)) {

		}

		if ("Cancel".equals(status)) {

		}

	}

	public java.awt.Point getHotSpotOffset() {

		String spot = "5,5";
		String[] splited = spot.split(",");
		return new java.awt.Point(Integer.parseInt(splited[0]), Integer
				.parseInt(splited[1]));

	}

}
