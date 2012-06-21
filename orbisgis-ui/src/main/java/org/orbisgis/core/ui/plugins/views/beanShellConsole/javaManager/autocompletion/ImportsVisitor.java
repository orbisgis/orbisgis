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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTImportDeclaration;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTPackageDeclaration;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTTypeDeclaration;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.Token;

public class ImportsVisitor extends AbstractVisitor {

	private HashMap<String, String> classNameFullName = new HashMap<String, String>();
	private int importsInit = Integer.MAX_VALUE;
	private int importsEnd = Integer.MIN_VALUE;

	@Override
	public Object visit(ASTPackageDeclaration node, Object data) {
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTTypeDeclaration node, Object data) {
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTImportDeclaration n, Object arg) {
		NodeUtils nodeUtils = CompletionUtils.getNodeUtils();
		if (importsInit == Integer.MAX_VALUE) {
			Token ft = n.first_token;
			importsInit = nodeUtils.getPosition(ft.beginLine, ft.beginColumn);
		}
		Token lt = n.last_token;
		importsEnd = nodeUtils.getPosition(lt.endLine, lt.endColumn + 1);

		String imp = nodeUtils.getText(n);
		imp = imp.substring(imp.indexOf("import") + 6).trim();
		imp = imp.substring(0, imp.length() - 1);
		String[] parts = imp.split("\\Q.\\E");
		classNameFullName.put(parts[parts.length - 1], imp);

		return super.visit(n, arg);
	}

	public String getClassTypeName(String className) {
		return classNameFullName.get(className);
	}

	public int getImportsInitPosition() {
		return importsInit;
	}

	public int getImportsEndPosition() {
		return importsEnd;
	}

	public String getAddImport(String name) {
		ArrayList<String> imps = new ArrayList<String>();
		Iterator<String> it = classNameFullName.values().iterator();
		while (it.hasNext()) {
			String imp = it.next();
			if (!imps.contains(imp)) {
				imps.add(imp);
			}
		}

		if (!imps.contains(name)) {
			imps.add(name);
		}

		// sort entries
		String[] retImps = imps.toArray(new String[imps.size()]);
		Arrays.sort(retImps);

		String ret = "";
		for (String retImp : retImps) {
			ret += "import " + retImp + ";\n";
		}
		ret = ret.substring(0, ret.length() - 1);

		return ret;
	}

	public boolean isImported(String className) {
		return className.startsWith("java.lang")
				|| classNameFullName.values().contains(className);
	}

	public Collection<String> getImportedClassNames() {
		return classNameFullName.keySet();
	}

	public Collection<String> getImportedClassFullNames() {
		return classNameFullName.values();
	}
}
