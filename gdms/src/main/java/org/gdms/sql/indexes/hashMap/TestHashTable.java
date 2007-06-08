package org.gdms.sql.indexes.hashMap;

import java.io.File;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TestHashTable {
	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		File temp = File.createTempFile("gdbms", "tmp");
		temp.deleteOnExit();

		Index idx = new DiskIndex(temp);
		idx.start();

		String[] cadenas = new String[] { "hola", "adios", "ata logo",
				"deica logo", "hasta luego", "au", "bye", "ciao", "que te den",
				"venga" };

		for (int i = 0; i < cadenas.length; i++) {
			idx.add(cadenas[i], i);
		}

		for (int i = 0; i < cadenas.length; i++) {
			PositionIterator it = idx.getPositions(cadenas[i]);
			System.out.println("cadenas[" + i + "] = " + cadenas[i]);

			while (it.hasNext()) {
				int element = (int) it.next();
				System.out.println(element);
			}
		}

		idx.stop();
	}
}
