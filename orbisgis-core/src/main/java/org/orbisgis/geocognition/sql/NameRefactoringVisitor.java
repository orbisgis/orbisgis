package org.orbisgis.geocognition.sql;

import org.orbisgis.javaManager.autocompletion.NodeUtils;
import org.orbisgis.javaManager.parser.ASTClassOrInterfaceDeclaration;
import org.orbisgis.javaManager.parser.Token;

public class NameRefactoringVisitor extends AbstractRefactoringVisitor {

	public NameRefactoringVisitor(String text, String newPart) {
		super(text, newPart);
	}

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		Token token = node.first_token.next;
		start = NodeUtils.getPosition(text, token.beginLine,
				token.beginColumn);
		end = NodeUtils.getPosition(text, token.endLine,
				token.endColumn);
		
		return null;
	}
}
