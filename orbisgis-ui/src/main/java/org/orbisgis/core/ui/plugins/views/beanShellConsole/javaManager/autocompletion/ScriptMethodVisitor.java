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

import org.apache.log4j.Logger;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTFormalParameter;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTMethodDeclarator;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTType;

public class ScriptMethodVisitor extends AbstractVisitor {

	private static final String EXCEPTION_MSG = "Code completion exception";

	private static Logger logger = Logger.getLogger(ScriptMethodVisitor.class);

	private String methodName;
	private ArrayList<Class<?>> types = new ArrayList<Class<?>>();
	private ArrayList<Method> methods = new ArrayList<Method>();

	@Override
	public Object visit(ASTMethodDeclarator node, Object data) {
		methodName = node.first_token.image;
		super.visit(node, data);
		methods.add(new Method(methodName, types.toArray(new Class<?>[types.size()])));
		types.clear();
		methodName = null;

		return super.visit(node, null);
	}

	@Override
	public Object visit(ASTFormalParameter node, Object data) {
		if (methodName != null) {
			ASTType type = (ASTType) node.jjtGetChild(0);
			try {
				types.add(CompletionUtils.getType(type));
			} catch (ClassNotFoundException e) {
				logger.warn(EXCEPTION_MSG, e);
			}
		}
		return super.visit(node, data);
	}

	public class Method {
		private String name;
		private Class<?>[] args;

		public Method(String name, Class<?>[] args) {
			super();
			this.name = name;
			this.args = args;
		}

		public String getName() {
			return name;
		}

		public Class<?>[] getArgs() {
			return args;
		}

	}

	public Method[] getMethods(String prefix) {
		ArrayList<Method> ret = new ArrayList<Method>();
		for (Method method : methods) {
			if (method.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
				ret.add(method);
			}
		}

		return ret.toArray(new Method[ret.size()]);
	}
}
