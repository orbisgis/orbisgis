package org.gdms.sql.function;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public interface ComplexFunction extends Function {

	/**
	 * This method is called by the sql engine to filter the DataSource
	 *
	 * @param args
	 *            the Values provided in the evaluate method. Some of the
	 *            elements of the Value array are set to null which means that
	 *            value cannot be used to filter the table. It's important to
	 *            notice the difference between an args element containing null
	 *            and another one containing NullValue. The first means it
	 *            cannot be used and the second means that the value can be used
	 *            but it is a null value
	 * @param fieldNames
	 *            The names of the fields of the args. If the argument is not a
	 *            direct reference to a field, the corresponding element in this
	 *            array contains a null
	 * @param from
	 *            Table to filter.
	 * @param argsFromTableToIndex
	 *            This array contains the indexes in the arguments array of the
	 *            values that belong to the table to filter. If there is no
	 *            argument belonging to the table to filter this method is not
	 *            called
	 * @return
	 * @throws DriverException
	 */
	public Iterator<PhysicalDirection> filter(Value[] args,
			String[] fieldNames, DataSource from,
			ArrayList<Integer> argsFromTableToIndex) throws DriverException;
}
