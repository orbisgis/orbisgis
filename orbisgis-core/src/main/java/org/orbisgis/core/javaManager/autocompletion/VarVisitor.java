package org.orbisgis.core.javaManager.autocompletion;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.core.javaManager.parser.ASTBlock;
import org.orbisgis.core.javaManager.parser.ASTFieldDeclaration;
import org.orbisgis.core.javaManager.parser.ASTLocalVariableDeclaration;
import org.orbisgis.core.javaManager.parser.ASTType;
import org.orbisgis.core.javaManager.parser.ASTVariableDeclaratorId;
import org.orbisgis.core.javaManager.parser.JavaParserConstants;
import org.orbisgis.core.javaManager.parser.Node;

public class VarVisitor extends AbstractVisitor {

	private static final String EXCEPTION_MSG = "Code completion exception";

	private static Logger logger = Logger
			.getLogger(VarVisitor.class);

	private int col;
	private int line;
	private ArrayList<Variable> attributes = new ArrayList<Variable>();
	private ArrayList<Variable> globalVars = new ArrayList<Variable>();
	private ArrayList<Variable> varsBlock = new ArrayList<Variable>();
	private boolean added = false;

	public VarVisitor(int line, int col) {
		this.line = line;
		this.col = col;
	}

	@Override
	public Object visit(ASTBlock n, Object arg) {
		if (CompletionUtils.getNodeUtils().isCursorInside(n)) {
			super.visit(n, arg);
			return null;
		} else {
			return null;
		}

	}

	private boolean before(Node n) {
		int el = CompletionUtils.getNodeUtils().getEndLine(n);
		int ec = CompletionUtils.getNodeUtils().getEndColumn(n);
		if (el < line) {
			return true;
		} else if ((el == line) && (ec < col)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object visit(ASTLocalVariableDeclaration n, Object data) {
		if (before(n)) {
			ASTType type = (ASTType) n.jjtGetChild(0);
			for (int i = 1; i < n.jjtGetNumChildren(); i++) {
				ASTVariableDeclaratorId var = (ASTVariableDeclaratorId) n
						.jjtGetChild(i).jjtGetChild(0);
				String varName = CompletionUtils.getNodeUtils().getText(var);
				try {
					Class<?> varType = CompletionUtils.getType(type);
					Variable variable = new Variable(varName, varType);
					if (n.first_token.kind == JavaParserConstants.FINAL) {
						globalVars.add(variable);
					}
					varsBlock.add(variable);
				} catch (ClassNotFoundException e) {
					logger.warn(EXCEPTION_MSG, e);
				}
			}
		}
		return super.visit(n, data);
	}

	@Override
	public Object visit(ASTFieldDeclaration n, Object data) {
		ASTType type = (ASTType) n.jjtGetChild(0);
		for (int i = 1; i < n.jjtGetNumChildren(); i++) {
			ASTVariableDeclaratorId var = (ASTVariableDeclaratorId) n
					.jjtGetChild(i).jjtGetChild(0);
			String varName = CompletionUtils.getNodeUtils().getText(var);
			try {
				Class<? extends Object> varType;
				varType = CompletionUtils.getType(type);
				Variable variable = new Variable(varName, varType);
				attributes.add(variable);
			} catch (ClassNotFoundException e) {
				logger.warn(EXCEPTION_MSG, e);
			}
		}
		return super.visit(n, data);
	}

	public String[] getVarNames() {
		addBlockVars();
		ArrayList<Variable> vars = globalVars;
		return toNameArray(vars);
	}

	private String[] toNameArray(ArrayList<Variable> vars) {
		String[] ret = new String[vars.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = vars.get(i).name;
		}
		return ret;
	}

	private void addBlockVars() {
		if (!added) {
			globalVars.addAll(varsBlock);
			added = true;
		}
	}

	public Class<?> getVarType(String varName) {
		addBlockVars();
		for (int i = 0; i < globalVars.size(); i++) {
			Variable variable = globalVars.get(i);
			if (variable.name.equals(varName)) {
				return variable.type;
			}
		}
		return null;
	}

	public String[] getAttributeNames() {
		return toNameArray(attributes);
	}
}
