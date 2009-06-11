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
package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.driver.DriverException;

/**
 * This class stores commands executed against the current edition theme. It
 * stores a limited number of commands throwing away the first command that were
 * stored in the stack when this limit is reached
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class CommandStack {
	private ArrayList<Command> commands = new ArrayList<Command>();

	private int position = 0;

	private boolean useLimit = true;

	private int limit = 40;

	public void put(Command c) throws DriverException {
		c.redo();
		commands.add(position, c);
		position++;
		for (int i = position; i < commands.size();) {
			commands.remove(i);
		}

		if (useLimit && (position > limit)) {
			position--;
			commands.remove(0);
		}
	}

	public Command redo() throws DriverException {
		Command ret = commands.get(position);
		ret.redo();
		position++;

		return ret;
	}

	public Command undo() throws DriverException {
		position--;
		Command ret = commands.get(position);
		ret.undo();

		return ret;
	}

	public boolean canUndo() {
		return position != 0;
	}

	public boolean canRedo() {
		return position < commands.size();
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isUseLimit() {
		return useLimit;
	}

	public void setUseLimit(boolean limited) {
		this.useLimit = limited;
	}

	public void clear() {
		position = 0;
		commands = new ArrayList<Command>();
	}
}
