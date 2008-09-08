package org.orbisgis.javaManager.autocompletion;

import java.util.ArrayList;

import org.orbisgis.javaManager.parser.ASTFormalParameter;
import org.orbisgis.javaManager.parser.ASTMethodDeclaration;
import org.orbisgis.javaManager.parser.ASTType;
import org.orbisgis.javaManager.parser.ASTVariableDeclaratorId;

public class MethodParameterVisitor extends AbstractVisitor {

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
				String varType = CompletionUtils.getNodeUtils().getText(type);
				Variable variable = new Variable(varName, varType);
				paramsBlock.add(variable);
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

	public String getArgType(String argName) {
		for (int i = 0; i < paramsBlock.size(); i++) {
			Variable variable = paramsBlock.get(i);
			if (variable.name.equals(argName)) {
				return variable.type;
			}
		}
		return null;
	}

	private class Variable {
		public String name;
		public String type;

		public Variable(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

	}
}
