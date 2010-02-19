package org.orbisgis.core.javaManager.autocompletion;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.core.javaManager.parser.ASTFormalParameter;
import org.orbisgis.core.javaManager.parser.ASTMethodDeclaration;
import org.orbisgis.core.javaManager.parser.ASTType;
import org.orbisgis.core.javaManager.parser.ASTVariableDeclaratorId;

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
