package org.orbisgis.core.renderer.se;

/**
 *
 * @author maxence
 */
public abstract class FilterOperator {

	public class CmpOp extends FilterOperator{

		//CmpRealOp
		//CmpString
		@Override
		public String toWhereClause() {
			return "";
		}

	}
	

	public abstract String toWhereClause();
}
