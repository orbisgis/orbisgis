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
package org.orbisgis.view.toc.actions.cui.components;

import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 * ComboBoxInput extension, create a jpanel which allow to choose unit
 * Uom is auto-saving
 *
 * @author maxence
 */
public final class UomInput extends ComboBoxInput {

	public static final String[] possibilities;

	static {
		possibilities = new String[Uom.values().length + 1];
		int i = 1;
		possibilities[0] = "n/a";
		for (Uom u : Uom.values()) {
			possibilities[i] = u.toString();
			i++;
		}
	}

	private UomNode node;

	public UomInput(UomNode node) {
		super(possibilities, (node.getOwnUom() != null ? node.getOwnUom().ordinal() + 1 : 0));
		this.node = node;
	}

	@Override
	protected void valueChanged(int i) {
		if (i > 0) {
			node.setUom(Uom.values()[i - 1]);
		} else {
			node.setUom(null);
		}
	}
}
