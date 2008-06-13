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
package org.gdms.data.command;

import junit.framework.TestCase;

import org.gdms.data.edition.Command;
import org.gdms.data.edition.CommandStack;
import org.gdms.driver.DriverException;

public class CommandStackTests extends TestCase {
	public void testNormal() throws Exception {
		CommandStack cs = new CommandStack();
		cs.setLimit(3);
		cs.setUseLimit(true);

		assertTrue(!cs.canRedo());
		assertTrue(!cs.canUndo());

		cs.put(new C());
		assertTrue(!cs.canRedo());
		assertTrue(cs.canUndo());

		cs.put(new C());
		cs.put(new C());
		assertTrue(!cs.canRedo());
		assertTrue(cs.canUndo());

		cs.undo();
		assertTrue(cs.canRedo());
		assertTrue(cs.canUndo());
		cs.undo();
		assertTrue(cs.canRedo());
		assertTrue(cs.canUndo());
		cs.undo();
		assertTrue(cs.canRedo());
		assertTrue(!cs.canUndo());
	}

	public void testLimit() throws Exception {
		CommandStack cs = new CommandStack();
		cs.setLimit(2);
		cs.setUseLimit(true);

		cs.put(new C(1));
		cs.put(new C(2));
		cs.put(new C(3));
		assertTrue(cs.undo().equals(new C(3)));
		assertTrue(cs.undo().equals(new C(2)));
		assertTrue(cs.canRedo());
		assertTrue(!cs.canUndo());
	}

	public void testPutUndoPut() throws Exception {
		CommandStack cs = new CommandStack();
		cs.setUseLimit(false);

		cs.put(new C(1));
		cs.put(new C(2));
		cs.put(new C(3));
		cs.put(new C(4));
		assertTrue(cs.undo().equals(new C(4)));
		assertTrue(cs.undo().equals(new C(3)));
		assertTrue(cs.undo().equals(new C(2)));
		assertTrue(cs.redo().equals(new C(2)));
		assertTrue(cs.canRedo());
		cs.put(new C(3));
		assertTrue(!cs.canRedo());
		assertTrue(cs.canUndo());
	}

	public class C implements Command {

		private int id;

		public C() {
		}

		public C(int id) {
			this.id = id;
		}

		public void redo() throws DriverException {

		}

		public void undo() throws DriverException {

		}

		@Override
		public boolean equals(Object obj) {
			C c = (C) obj;
			return this.id == c.id;
		}

	}

}
