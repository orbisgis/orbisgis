package org.orbisgis.core.javaManager.autocompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.orbisgis.core.javaManager.parser.ASTImportDeclaration;
import org.orbisgis.core.javaManager.parser.ASTPackageDeclaration;
import org.orbisgis.core.javaManager.parser.ASTTypeDeclaration;
import org.orbisgis.core.javaManager.parser.Token;

public class ImportsVisitor extends AbstractVisitor {

	private HashMap<String, String> classNameFullName = new HashMap<String, String>();
	private int importsInit = Integer.MAX_VALUE;
	private int importsEnd = Integer.MIN_VALUE;

	@Override
	public Object visit(ASTPackageDeclaration node, Object data) {
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTTypeDeclaration node, Object data) {
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTImportDeclaration n, Object arg) {
		NodeUtils nodeUtils = CompletionUtils.getNodeUtils();
		if (importsInit == Integer.MAX_VALUE) {
			Token ft = n.first_token;
			importsInit = nodeUtils.getPosition(ft.beginLine, ft.beginColumn);
		}
		Token lt = n.last_token;
		importsEnd = nodeUtils.getPosition(lt.endLine, lt.endColumn + 1);

		String imp = nodeUtils.getText(n);
		imp = imp.substring(imp.indexOf("import") + 6).trim();
		imp = imp.substring(0, imp.length() - 1);
		String[] parts = imp.split("\\Q.\\E");
		classNameFullName.put(parts[parts.length - 1], imp);

		return super.visit(n, arg);
	}

	public String getClassTypeName(String className) {
		return classNameFullName.get(className);
	}

	public int getImportsInitPosition() {
		return importsInit;
	}

	public int getImportsEndPosition() {
		return importsEnd;
	}

	public String getAddImport(String name) {
		ArrayList<String> imps = new ArrayList<String>();
		Iterator<String> it = classNameFullName.values().iterator();
		while (it.hasNext()) {
			String imp = it.next();
			if (!imps.contains(imp)) {
				imps.add(imp);
			}
		}

		if (!imps.contains(name)) {
			imps.add(name);
		}

		// sort entries
		String[] retImps = imps.toArray(new String[0]);
		Arrays.sort(retImps);

		String ret = "";
		for (String retImp : retImps) {
			ret += "import " + retImp + ";\n";
		}
		ret = ret.substring(0, ret.length() - 1);

		return ret;
	}

	public boolean isImported(String className) {
		return className.startsWith("java.lang")
				|| classNameFullName.values().contains(className);
	}

	public Collection<String> getImportedClassNames() {
		return classNameFullName.keySet();
	}

	public Collection<String> getImportedClassFullNames() {
		return classNameFullName.values();
	}
}
