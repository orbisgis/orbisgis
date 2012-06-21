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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion;

public class ImportOption extends AbstractOption implements Option {

	private String qName;

	ImportOption(String qName) {
		this.qName = qName;
	}

	public String getAsString() {
		return "import " + qName;
	}

	public String getSortString() {
		return "a" + toString();
	}

	@Override
	public String toString() {
		return "import " + qName;
	}

	public int getCursorPosition() {
		return cursorPos + getTransformedText().length() - text.length();
	}

	public String getTransformedText() {
		ImportsVisitor iv = CompletionUtils.getImportsVisitor();
		String newImportSection = iv.getAddImport(qName);

		String ret;
		if (iv.getImportsInitPosition() == Integer.MAX_VALUE) {
			ret = newImportSection + "\n" + text;
		} else {
			String afterImports = "";
			if (text.length() > iv.getImportsEndPosition()) {
				afterImports = text.substring(iv.getImportsEndPosition());
			}
			ret = text.substring(0, iv.getImportsInitPosition())
					+ newImportSection + afterImports;
		}
		return ret;
	}

	@Override
	public ImportOption[] getImports() {
		return new ImportOption[0];
	}

	@Override
	public boolean setPrefix(String prefix) {
		throw new RuntimeException("Bug! This should never be called");
	}

	@Override
	public String getPrefix() {
		throw new RuntimeException("Bug! This should never be called");
	}

}
