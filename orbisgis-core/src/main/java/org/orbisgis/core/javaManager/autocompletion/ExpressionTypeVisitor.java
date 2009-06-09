package org.orbisgis.core.javaManager.autocompletion;

import org.apache.log4j.Logger;
import org.orbisgis.core.javaManager.parser.ASTAllocationExpression;
import org.orbisgis.core.javaManager.parser.ASTArrayDimsAndInits;
import org.orbisgis.core.javaManager.parser.ASTCastExpression;
import org.orbisgis.core.javaManager.parser.ASTClassOrInterfaceType;
import org.orbisgis.core.javaManager.parser.ASTConditionalExpression;
import org.orbisgis.core.javaManager.parser.ASTPrimaryExpression;
import org.orbisgis.core.javaManager.parser.ASTPrimaryPrefix;
import org.orbisgis.core.javaManager.parser.ASTPrimarySuffix;
import org.orbisgis.core.javaManager.parser.ASTPrimitiveType;
import org.orbisgis.core.javaManager.parser.ASTType;
import org.orbisgis.core.javaManager.parser.Node;
import org.orbisgis.core.javaManager.parser.SimpleNode;

public class ExpressionTypeVisitor extends AbstractVisitor {
	private static final String EXCEPTION_MSG = "Code completion exception";
	private static Logger logger = Logger.getLogger(ExpressionTypeVisitor.class);

	private Class<? extends Object> type;

	@Override
	public Object visit(ASTAllocationExpression node, Object data) {
		if (CompletionUtils.getNodeUtils().isAtCursor(node)) {
			Node allocatedType = node.jjtGetChild(0);
			if (allocatedType instanceof ASTPrimitiveType) {
				type = CompletionUtils.getPrimitiveType((ASTPrimitiveType) allocatedType, node
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
