package org.orbisgis.core.javaManager.autocompletion;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.core.javaManager.parser.ASTFormalParameter;
import org.orbisgis.core.javaManager.parser.ASTMethodDeclarator;
import org.orbisgis.core.javaManager.parser.ASTType;

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
		methods.add(new Method(methodName, types.toArray(new Class<?>[0])));
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

		return ret.toArray(new Method[0]);
	}
}
