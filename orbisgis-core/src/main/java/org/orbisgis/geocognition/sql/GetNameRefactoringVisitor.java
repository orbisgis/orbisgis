package org.orbisgis.geocognition.sql;

import org.orbisgis.javaManager.autocompletion.NodeUtils;
import org.orbisgis.javaManager.parser.ASTMethodDeclarator;
import org.orbisgis.javaManager.parser.ASTReturnStatement;
import org.orbisgis.javaManager.parser.Token;

public class GetNameRefactoringVisitor extends AbstractRefactoringVisitor {

	private boolean nextReturn;

	public GetNameRefactoringVisitor(String text, String newPart) {
		super(text, newPart);
	}

	public Object visit(ASTMethodDeclarator node, Object data) {
		if (node.first_token.image.equals("getName")) {
			if (node.jjtGetChild(0).jjtGetNumChildren() == 0) {
				nextReturn = true;
				super.visit(node, null);
			}
		}
		return null;
	}
	
	@Override
	public Object visit(ASTReturnStatement node, Object data) {
		if (nextReturn) {
			Token first_token = node.first_token;
			start = NodeUtils.getPosition(text, first_token.beginLine,
					first_token.beginColumn);
			Token last_token = node.last_token;
			end = NodeUtils.getPosition(text, last_token.endLine,
					last_token.endColumn);
			nextReturn = false;
		}
		return super.visit(node, data);
	}
}
