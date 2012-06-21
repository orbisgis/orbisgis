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

public class MethodOption extends AbstractPrefixedOption implements Option {

	private Class<?>[] parameters;
	protected String methodName;
	protected String completionName;

	public MethodOption(String prefix, String methodName,
			Class<?>[] parameterTypes) {
		super(prefix);
		this.methodName = methodName;
		this.completionName = methodName;
		this.parameters = parameterTypes;
	}

	public String getAsString() {
		String separator = "";
		StringBuffer ret = new StringBuffer("method-").append(methodName)
				.append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			Class<?> type = parameters[i];
			if (type.isArray()) {
				ret.append(type.getComponentType().getSimpleName())
						.append("[]");
			} else {
				ret.append(type.getSimpleName());
			}
			ret.append(" ").append("arg").append(i);
			separator = ", ";
		}

		return ret.append(")").toString();
	}

	public String getSortString() {
		return "ba" + toString();
	}

	@Override
	public String toString() {
		String separator = "";
		StringBuffer ret = new StringBuffer();
		ret.append(methodName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			Class<?> type = parameters[i];
			if (type.isArray()) {
				ret.append(type.getComponentType().getSimpleName())
						.append("[]");
			} else {
				ret.append(type.getSimpleName());
			}
			ret.append(" ").append("arg").append(i);
			separator = ", ";
		}

		return ret.append(")").toString();
	}

	public int getPosition() {
		return cursorPos;
	}

	@Override
	public String getCompletionWord() {
		String separator = "";
		StringBuffer ret = new StringBuffer();
		ret.append(completionName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			ret.append(separator);
			ret.append("arg").append(i);
			separator = ", ";
		}
		return ret.append(")").toString();
	}
}
