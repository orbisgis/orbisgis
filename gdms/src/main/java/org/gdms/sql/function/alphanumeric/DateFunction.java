package org.gdms.sql.function.alphanumeric;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;



/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DateFunction implements Function {
	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		if ((args.length < 1) || (args.length > 2)) {
			throw new FunctionException(
				"use: date('date_literal'[ , date_format])");
		}

		if (!(args[0] instanceof StringValue)) {
			throw new FunctionException("date parameters must be strings");
		}

		DateFormat df;

		if (args.length == 2) {
			if ((args[1] instanceof StringValue)) {
				df = new SimpleDateFormat(((StringValue) args[1]).getValue());
			} else {
				throw new FunctionException("date parameters must be strings");
			}
		} else {
			df = DateFormat.getDateInstance();
		}

		try {
			return ValueFactory.createValue(df.parse(
					((StringValue) args[0]).getValue()));
		} catch (ParseException e) {
			throw new FunctionException("date format must match DateFormat java class requirements",
				e);
		}
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "date";
	}

    /**
     * @see org.gdms.sql.function.Function#isAggregate()
     */
    public boolean isAggregate() {
        return false;
    }

    /**
     * @see org.gdms.sql.function.Function#cloneFunction()
     */
    public Function cloneFunction() {
        return new DateFunction();
    }

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType() {
		return Value.DATE;
	}

}
