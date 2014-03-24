/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.orbisgis.utils.ByteUtils;

import static org.junit.Assert.*;

import org.gdms.driver.ReadWriteBufferManager;

/**
 *
 * @author Antoine Gourlay
 */
public class BlockSystemTest {

        private File f;

        @Before
        public void before() throws IOException {
                f = File.createTempFile("blocktest", null);
        }
        
        public void after() {
                if (f != null) {
                        f.delete();
                }
        }

        @Test
        public void testCreateNewFile() throws Exception {
                BlockProvider b = new FileBlockProvider(45, f);
                b.close();

                assertTrue(f.exists());

                RandomAccessFile raf = new RandomAccessFile(f, "r");
                ReadWriteBufferManager m = new ReadWriteBufferManager(raf.getChannel(), 1000);

                m.position(0);
                // version number
                assertEquals(m.getInt(), 1);

                // default block size
                assertEquals(m.getInt(), FileBlockProvider.DEFAULT_BLOCK_SIZE);

                // last block id
                long count = m.getLong();
                assertEquals(count, -1);

                f.delete();
        }

        @Test
        public void testReadWriteBlock() throws Exception {
                BlockProvider b = new FileBlockProvider(45, f);

                // add 1 block and write into it

                Block bl = b.newBlock();
                long id = bl.getId().getBlockId();

                bl.setContent(ByteUtils.longToBytes(123456789));
                b.writeBlock(bl);
                b.close();

                b = new FileBlockProvider(45, f);
                bl = b.readBlock(id);
                assertEquals(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)), 123456789);

                // add a second block and check there is now 2 blocks

                bl = b.newBlock();
                id = bl.getId().getBlockId();
                bl.setContent(ByteUtils.longToBytes(987654321));
                b.writeBlock(bl);
                b.close();

                RandomAccessFile raf = new RandomAccessFile(f, "r");
                ReadWriteBufferManager m = new ReadWriteBufferManager(raf.getChannel(), 1000);

                m.position(8);
                // block count - 1
                long count = m.getLong();
                assertEquals(count, 1);

                raf.close();

                // open bock blocks and read content
                b = new FileBlockProvider(45, f);
                bl = b.readBlock(0);
                assertEquals(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)), 123456789);

                bl = b.readBlock(1);
                assertEquals(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)), 987654321);

                // truncate to 1 block
                b.truncateToBlock(0);
                assertEquals(b.getNumberOfBlocks(), 1);
                b.close();

                b = new FileBlockProvider(45, f);
                assertEquals(b.getNumberOfBlocks(), 1);

                bl = b.readBlock(0);
                assertEquals(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)), 123456789);

                b.close();

                f.delete();
        }

        @Test
        public void testOtherBlockSize() throws Exception {
                BlockProvider b = new FileBlockProvider(45, f, 2 * FileBlockProvider.DEFAULT_BLOCK_SIZE);
                b.close();

                RandomAccessFile raf = new RandomAccessFile(f, "r");
                ReadWriteBufferManager m = new ReadWriteBufferManager(raf.getChannel(), 1000);

                m.position(4);
                // default block size
                assertEquals(m.getInt(), 2 * FileBlockProvider.DEFAULT_BLOCK_SIZE);

                raf.close();

                f.delete();
        }
}
