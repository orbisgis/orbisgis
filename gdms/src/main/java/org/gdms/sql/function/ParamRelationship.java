package org.gdms.sql.function;

public class ParamRelationship {

	public static final int SPATIAL_OVERLAP = 1;

	private int paramNumber1;

	private int paramNumber2;

	private int relationshipType;

	public ParamRelationship(int paramNumber1, int paramNumber2,
			int relationshipType) {
		super();
		this.paramNumber1 = paramNumber1;
		this.paramNumber2 = paramNumber2;
		this.relationshipType = relationshipType;
	}

	public int getParamNumber1() {
		return paramNumber1;
	}

	public int getParamNumber2() {
		return paramNumber2;
	}

	public int getRelationshipType() {
		return relationshipType;
	}
}
