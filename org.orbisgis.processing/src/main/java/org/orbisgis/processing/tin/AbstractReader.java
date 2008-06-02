package org.orbisgis.processing.tin;

import java.util.Scanner;

import org.gdms.driver.DriverException;

abstract class AbstractReader {
	abstract Scanner getIn();

	private String nextThatIsNotAComment() throws DriverException {
		while (getIn().hasNext()) {
			final String tmp = getIn().next();
			if (tmp.startsWith("#")) {
				getIn().nextLine();
			} else {
				return tmp;
			}
		}
		throw new DriverException("NodeReader: format failure - i miss a token");
	}

	int nextInteger() throws DriverException {
		return new Integer(nextThatIsNotAComment());
	}

	double nextDouble() throws DriverException {
		return new Double(nextThatIsNotAComment());
	}

	void close() {
		if (null != getIn()) {
			getIn().close();
		}
	}
}