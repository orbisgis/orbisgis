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

import org.apache.log4j.Logger;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTAllocationExpression;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTArrayDimsAndInits;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTCastExpression;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTClassOrInterfaceType;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTConditionalExpression;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTPrimaryExpression;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTPrimaryPrefix;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTPrimarySuffix;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTPrimitiveType;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ASTType;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.Node;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.SimpleNode;

public class ExpressionTypeVisitor extends AbstractVisitor {
	private static final String EXCEPTION_MSG = "Code completion exception";
	private static Logger logger = Logger
			.getLogger(ExpressionTypeVisitor.class);

	private Class<? extends Object> type;

	@Override
	public Object visit(ASTAllocationExpression node, Object data) {
		if (CompletionUtils.getNodeUtils().isAtCursor(node)) {
			Node allocatedType = node.jjtGetChild(0);
			if (allocatedType instanceof ASTPrimitiveType) {
				type = CompletionUtils.getPrimitiveType(
						(ASTPrimitiveType) allocatedType, node
								.jjtGetNumChildren() == 2);
			} else if (allocatedType instanceof ASTClassOrInterfaceType) {
				boolean isArray = (node.jjtGetChild(1) instanceof ASTArrayDimsAndInits)
						|| (node.jjtGetChild(2) instanceof ASTArrayDimsAndInits);
				try {
					type = CompletionUtils.getClassOrInterfaceType(
							(ASTClassOrInterfaceType) allocatedType, isArray);
				} catch (ClassNotFoundException e) {
					logger.warn(EXCEPTION_MSG, e);
				}
			} else {
				throw new RuntimeException("bug!");
			}
		}
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTConditionalExpression node, Object data) {
		if (node.jjtGetNumChildren() == 1) {
			return super.visit(node, data);
		} else {
			return super.visit((SimpleNode) node.jjtGetChild(0), data);
		}
	}

	@Override
	public Object visit(ASTCastExpression node, Object data) {
		ASTType type = (ASTType) node.jjtGetChild(0);
		try {
			this.type = CompletionUtils.getType(type);
		} catch (ClassNotFoundException e) {
			logger.warn(EXCEPTION_MSG, e);
		}
		return null;
	}

	@Override
	public Object visit(ASTPrimaryExpression node, Object data) {
		ASTPrimaryPrefix primaryPrefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
		ASTPrimarySuffix[] suffixes = new ASTPrimarySuffix[node
				.jjtGetNumChildren() - 1];
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			suffixes[i - 1] = (ASTPrimarySuffix) node.jjtGetChild(i);
		}
		try {
			type = CompletionUtils.getType(primaryPrefix, suffixes);
			return null;
		} catch (SecurityException e) {
			logger.warn(EXCEPTION_MSG, e);
		} catch (ClassNotFoundException e) {
			logger.warn(EXCEPTION_MSG, e);
		} catch (CannotAutocompleteException e) {
			logger.warn(EXCEPTION_MSG, e);
		} catch (NoSuchFieldException e) {
			logger.warn(EXCEPTION_MSG, e);
		} catch (NoSuchMethodException e) {
			logger.warn(EXCEPTION_MSG, e);
		}
		return super.visit(node, data);
	}

	public Class<? extends Object> getType() {
		return type;
	}

}
