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
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTMethodDeclaration;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTType;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTVariableDeclaratorId;

public class MethodParameterVisitor extends AbstractVisitor {

	private static final String EXCEPTION_MSG = "Code completion exception";

	private static Logger logger = Logger
			.getLogger(MethodParameterVisitor.class);

	private ArrayList<Variable> paramsBlock = new ArrayList<Variable>();

	private boolean cursorInside = false;

	@Override
	public Object visit(ASTMethodDeclaration n, Object arg) {
		if (CompletionUtils.getNodeUtils().isCursorInside(n)) {
			paramsBlock.clear();
			cursorInside = true;
			super.visit(n, arg);
			cursorInside = false;
			return null;
		} else {
			return super.visit(n, arg);
		}
	}

	@Override
	public Object visit(ASTFormalParameter n, Object data) {
		if (cursorInside) {
			ASTType type = (ASTType) n.jjtGetChild(0);
			for (int i = 1; i < n.jjtGetNumChildren(); i++) {
				ASTVariableDeclaratorId var = (ASTVariableDeclaratorId) n
						.jjtGetChild(i);
				String varName = CompletionUtils.getNodeUtils().getText(var);
				try {
					Class<?> varType = CompletionUtils.getType(type);
					Variable variable = new Variable(varName, varType);
					paramsBlock.add(variable);
				} catch (ClassNotFoundException e) {
					logger.warn(EXCEPTION_MSG, e);
				}
			}
		}
		return super.visit(n, data);
	}

	public String[] getArgNames() {
		String[] ret = new String[paramsBlock.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = paramsBlock.get(i).name;
		}
		return ret;
	}

	public Class<?> getArgType(String argName) {
		for (int i = 0; i < paramsBlock.size(); i++) {
			Variable variable = paramsBlock.get(i);
			if (variable.name.equals(argName)) {
				return variable.type;
			}
		}
		return null;
	}

}
