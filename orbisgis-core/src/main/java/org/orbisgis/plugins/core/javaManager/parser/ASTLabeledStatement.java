/* Generated By:JJTree: Do not edit this line. ASTLabeledStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.plugins.core.javaManager.parser;

public class ASTLabeledStatement extends SimpleNode {
	public ASTLabeledStatement(int id) {
		super(id);
	}

	public ASTLabeledStatement(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=d2e20559551e5eda444e42855d651b04 (do not edit this
 * line)
 */
