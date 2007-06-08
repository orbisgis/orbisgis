/*
 * Created on 23-oct-2004
 */
package org.gdms.sql.indexes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonzlez Corts
 */
public abstract class DiskIndexSet {
	protected File file;

	protected FileInputStream fis;

	protected FileOutputStream fos;

	protected FileChannel outputChannel;

	protected FileChannel inputChannel;

	protected ByteBuffer buffer = ByteBuffer.allocate(8);

	protected byte[] writeBuffer = new byte[8];
}
