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

import java.lang.reflect.Method;
import java.util.HashSet;

public class InlineImplementationOption extends AbstractPrefixedOption
		implements Option {

	private String interfaceName;
	private StringBuffer str;
	private String interfaceFullName;
	private HashSet<ImportOption> imports = new HashSet<ImportOption>();

	public InlineImplementationOption(String prefix, String interfaceName,
			String fullName) throws ClassNotFoundException {
		super(prefix);
		this.interfaceName = interfaceName;
		this.interfaceFullName = fullName;
		str = new StringBuffer(getClassReference(Class
				.forName(interfaceFullName))
				+ "() {\n");
		Class<?> cl = Class.forName(fullName);
		Method[] methods = cl.getMethods();
		for (Method method : methods) {
			str.append("public ").append(
					getClassReference(method.getReturnType())).append(" ")
					.append(method.getName()).append(" (");
			Class<?>[] params = method.getParameterTypes();
			String separator = "";
			for (int i = 0; i < params.length; i++) {
				Class<?> paramClass = params[i];
				str.append(separator).append(getClassReference(paramClass))
						.append(" arg").append(i);
				separator = ", ";
			}
			str.append(") {\n}\n");
		}
		str.append("}");
	}

	private String getClassReference(Class<?> cl) throws ClassNotFoundException {
		if (cl == Void.TYPE) {
			return "void";
		} else {
			String arraySuffix = "";
			if (cl.isArray()) {
				arraySuffix = "[]";
				cl = cl.getComponentType();
			}
			ImportsVisitor iv = CompletionUtils.getImportsVisitor();
			String qName = cl.getName();
			String simpleName = CompletionUtils.getClassSimpleName(qName);
			if (cl.isPrimitive()) {
				return simpleName + arraySuffix;
			} else if (iv.isImported(qName)) {
				return simpleName + arraySuffix;
			} else if (iv.getImportedClassNames().contains(simpleName)) {
				return qName + arraySuffix;
			} else {
				imports.add(new ImportOption(qName));
				return simpleName + arraySuffix;
			}
		}
	}

	@Override
	public String getAsString() {
		return "inlineImplementation-" + interfaceName;
	}

	@Override
	public String getSortString() {
		return "c" + toString();
	}

	@Override
	protected String getCompletionWord() {
		return str.toString();
	}

	@Override
	public String toString() {
		return interfaceFullName + "- Inline implementation";
	}

	@Override
	public ImportOption[] getImports() {
		return imports.toArray(new ImportOption[imports.size()]);
	}
}
