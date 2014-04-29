/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.List;

import org.gdms.driver.DriverException;

/**
 * This class stores commands executed against the current edition theme. It
 * stores a limited number of commands throwing away the first command that were
 * stored in the stack when this limit is reached
 * 
 * @author Fernando Gonzalez Cortes
 */
public final class CommandStack {
	private List<Command> commands = new ArrayList<Command>();

	private int position = 0;

	private boolean useLimit = true;

	private int limit;

        public CommandStack(int limit) {
                this.limit = limit;
        }

        /**
         *Do the <code>Command</code> c and add it to the Stack
         * @param c the command to do
         * @throws DriverException if there is a problem during the command execution
         */

	public void put(Command c) throws DriverException {
		c.redo();
		commands.add(position, c);
		position++;
                //Empty the end of the stack, if the command is not added at the end of it
		for (int i = position; i < commands.size();) {
			commands.remove(i);
		}
                //If the stack is greater than the limit, delete the oldest command.
		if (useLimit && (position > limit)) {
			position--;
			commands.remove(0);
		}
	}

        /**
         * Redo the last undone command.
         * @return the command that has been redone
         * @throws DriverException if there is a problem during the command execution
         */

	public Command redo() throws DriverException {
		Command ret = commands.get(position);
		ret.redo();
		position++;

		return ret;
	}

        /**
         * Undo the last ndone command.
         * @return the command that has been undone
         * @throws DriverException if there is a problem during the command execution
         */

	public Command undo() throws DriverException {
		position--;
		Command ret = commands.get(position);
		ret.undo();

		return ret;
	}
        /**
         *
         * @return true if something can be undone (position is not the beginning of the stack)
         */
	public boolean canUndo() {
		return position != 0;
	}
        /**
         *
         * @return true if something can be redone (position is not the end of the stack)
         */
	public boolean canRedo() {
		return position < commands.size();
	}
        /**
         *
         * @return the limit
         */
	public int getLimit() {
		return limit;
	}
        /**
         *
         * @param limit the new limit
         */
	public void setLimit(int limit) {
		this.limit = limit;
	}
        /**
         *
         * @return true if a limit is used for thhe stack
         */
	public boolean isUseLimit() {
		return useLimit;
	}
        /**
         *
         * @param limited : if true, a limit will be used, not if false.
         */
	public void setUseLimit(boolean limited) {
		this.useLimit = limited;
	}
        /**
         * Empties the stack
         */
	public void clear() {
                for (int i = 0; i < commands.size(); i++) {
                        commands.get(i).clear();
                }
		position = 0;
		commands = new ArrayList<Command>();
	}
}
