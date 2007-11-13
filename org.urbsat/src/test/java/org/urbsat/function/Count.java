package org.urbsat.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;


/**
 * @author Fernando Gonz�lez Cort�s
 */
public class Count implements Function {
    private LongValue v = ValueFactory.createValue(0L);

    /**
     * @see org.gdms.sql.function.AggregateFunction#getName()
     */
    public String getName() {
        return "count";
    }

    /**
     * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
     */
    public Value evaluate(Value[] args) throws FunctionException {
        v.setValue(v.getValue()+1);
        return v;
    }

    /**
     * @see org.gdms.sql.function.Function#isAggregate()
     */
    public boolean isAggregate() {
        return true;
    }

    /**
     * @see org.gdms.sql.function.Function#cloneFunction()
     */
    public Function cloneFunction() {
        return new Count();
    }

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType(int[] types) {
		
		return Type.INT;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
