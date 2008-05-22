package org.gdms.data.values;

import java.io.IOException;

public interface ByteProvider {

	/**
	 * Gets all the bytes
	 *
	 * @return
	 * @throws IOException
	 *             If the bytes couldn't be read
	 */
	byte[] getBytes() throws IOException;
}
