/* Generated By:JJTree: Do not edit this line. ASTSQLBetweenClause.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLBetweenClause extends SimpleNode {
  public ASTSQLBetweenClause(int id) {
    super(id);
  }

  public ASTSQLBetweenClause(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=b576d7d94a8cbaa1ad294ad63f08b180 (do not edit this line) */
