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
package org.orbisgis.core.ui.views.sqlConsole.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.orbisgis.images.IconLoader;


public class ConsoleAction {
	public final static int EXECUTE = 110;
	public final static int CLEAR = 111;
	public final static int OPEN = 115;
	public final static int SAVE = 116;

	private static class InternalConsoleAction {
		ImageIcon icon;
		String toolTipText;

		InternalConsoleAction(final String icon, final String toolTipText) {
			this.icon = IconLoader.getIcon(icon);
			this.toolTipText = toolTipText;
		}
	}

	private static Map<Integer, InternalConsoleAction> mapOfActions;

	static {
		mapOfActions = new HashMap<Integer, InternalConsoleAction>(7);

		mapOfActions.put(EXECUTE, new InternalConsoleAction("execute.png",
				"Click to execute query"));
		mapOfActions.put(CLEAR, new InternalConsoleAction("erase.png",
				"Clear console"));
		mapOfActions.put(OPEN, new InternalConsoleAction("open.png",
				"Open an already saved SQL script"));
		mapOfActions.put(SAVE, new InternalConsoleAction("save.png",
				"Save current console"));
	}

	public static ImageIcon getImageIcon(final int type) {
		return mapOfActions.get(type).icon;
	}

	public static String getToolTipText(final int type) {
		return mapOfActions.get(type).toolTipText;
	}
}