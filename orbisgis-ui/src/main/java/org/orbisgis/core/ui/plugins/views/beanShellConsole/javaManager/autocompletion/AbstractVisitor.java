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

import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.*;

public class AbstractVisitor implements JavaParserVisitor {

	private void visitChildren(SimpleNode simpleNode) {
		for (int i = 0; i < simpleNode.jjtGetNumChildren(); i++) {
			Node child = simpleNode.jjtGetChild(i);
			child.jjtAccept(this, null);
		}
	}

	public Object visit(SimpleNode node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTCompilationUnit node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPackageDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTImportDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTModifiers node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTExtendsList node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTImplementsList node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTEnumDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTEnumBody node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTEnumConstant node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeParameters node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeParameter node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeBound node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTClassOrInterfaceBody node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTFieldDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTVariableDeclarator node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTVariableDeclaratorId node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTVariableInitializer node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTArrayInitializer node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMethodDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMethodDeclarator node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTFormalParameters node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTFormalParameter node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTConstructorDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTExplicitConstructorInvocation node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTInitializer node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTType node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTReferenceType node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTClassOrInterfaceType node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeArguments node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTypeArgument node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTWildcardBounds node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPrimitiveType node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTResultType node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTName node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTNameList node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAssignmentOperator node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTConditionalExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTConditionalOrExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTConditionalAndExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTInclusiveOrExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTExclusiveOrExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAndExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTEqualityExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTInstanceOfExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTRelationalExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTShiftExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAdditiveExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMultiplicativeExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTUnaryExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPreIncrementExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPreDecrementExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTCastLookahead node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPostfixExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTCastExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPrimaryExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMemberSelector node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPrimaryPrefix node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTPrimarySuffix node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTLiteral node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTBooleanLiteral node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTNullLiteral node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTArguments node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTArgumentList node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAllocationExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTArrayDimsAndInits node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAssertStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTLabeledStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTBlock node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTBlockStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTLocalVariableDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTEmptyStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTStatementExpression node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTSwitchStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTSwitchLabel node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTIfStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTWhileStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTDoStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTForStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTForInit node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTStatementExpressionList node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTForUpdate node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTBreakStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTContinueStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTReturnStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTThrowStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTSynchronizedStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTTryStatement node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTRUNSIGNEDSHIFT node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTRSIGNEDSHIFT node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAnnotation node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTNormalAnnotation node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMarkerAnnotation node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTSingleMemberAnnotation node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMemberValuePairs node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMemberValuePair node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMemberValue node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTMemberValueArrayInitializer node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAnnotationTypeBody node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTAnnotationTypeMemberDeclaration node, Object data) {
		visitChildren(node);
		return null;
	}

	public Object visit(ASTDefaultValue node, Object data) {
		visitChildren(node);
		return null;
	}

	@Override
	public Object visit(ASTScript node, Object data) {
		visitChildren(node);
		return null;
	}

	@Override
	public Object visit(ASTScriptMethod node, Object data) {
		visitChildren(node);
		return null;
	}

	@Override
	public Object visit(ASTerror_skipto node, Object data) {
		visitChildren(node);
		return null;
	}

}
