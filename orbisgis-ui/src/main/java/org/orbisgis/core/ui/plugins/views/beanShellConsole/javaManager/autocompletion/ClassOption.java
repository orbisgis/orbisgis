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

public class ClassOption extends AbstractPrefixedOption implements Option {

	private String completionWord;
	private ImportOption[] imports;
	private String className;
	private String qName;
	private boolean isInterface;

	public ClassOption(String prefix, String qName, boolean isInterface) {
		super(prefix);
		this.isInterface = isInterface;
		ImportsVisitor iv = CompletionUtils.getImportsVisitor();
		this.qName = qName;
		className = CompletionUtils.getClassSimpleName(qName);
		if (iv.isImported(qName)) {
			completionWord = className;
			imports = new ImportOption[0];
		} else if (iv.getImportedClassNames().contains(className)) {
			completionWord = qName;
			imports = new ImportOption[0];
		} else {
			completionWord = className;
			imports = new ImportOption[] { new ImportOption(qName) };
		}
	}

	public String getAsString() {
		return "class-" + getCompletionWord();
	}

	public String getSortString() {
		return "bb" + toString();
	}

	@Override
	public String toString() {
		return className + " - " + CompletionUtils.getClassPackage(qName);
	}

	@Override
	protected String getCompletionWord() {
		return completionWord;
	}

	@Override
	public ImportOption[] getImports() {
		return imports;
	}

	public boolean isInterface() {
		return isInterface;
	}
}
