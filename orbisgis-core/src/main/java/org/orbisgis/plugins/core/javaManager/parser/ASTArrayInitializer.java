/* Generated By:JJTree: Do not edit this line. ASTArrayInitializer.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.plugins.core.javaManager.parser;

public class ASTArrayInitializer extends SimpleNode {
	public ASTArrayInitializer(int id) {
		super(id);
	}

	public ASTArrayInitializer(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=157b3243d278d6ab9ebc8c2e9f92a155 (do not edit this
 * line)
 */
