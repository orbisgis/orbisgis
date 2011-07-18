/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : #name, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.data.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import junit.framework.TestCase;
import org.gdms.driver.ReadWriteBufferManager;
import org.orbisgis.utils.ByteUtils;

/**
 *
 * @author Antoine Gourlay
 */
public class BlockSystemTest extends TestCase {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateNewFile() throws Exception {
            File f = File.createTempFile("blocktest", null);
            f.delete();

            BlockProvider b = new FileBlockProvider(45, f);
            b.close();

            assertTrue(f.exists());

            RandomAccessFile raf = new RandomAccessFile(f, "r");
            ReadWriteBufferManager m = new ReadWriteBufferManager(raf.getChannel(), 1000);

            m.position(0);
            // version number
            assertTrue(m.getInt() == 1);

            // default block size
            assertTrue(m.getInt() == FileBlockProvider.DEFAULT_BLOCK_SIZE);

            // last block id
            long count = m.getLong();
            assertTrue(count == -1);
    }

    public void testReadWriteBlock() throws Exception {
            File f = File.createTempFile("blocktest", null);
            f.delete();

            BlockProvider b = new FileBlockProvider(45, f);

            // add 1 block and write into it

            Block bl = b.newBlock();
            long id = bl.getId().getBlockId();

            bl.setContent(ByteUtils.longToBytes(123456789));
            b.writeBlock(bl);
            b.close();

            b = new FileBlockProvider(45, f);
            bl = b.readBlock(id);
            assertTrue(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)) == 123456789);

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
            assertTrue(count == 1);

            raf.close();

            // open bock blocks and read content
            b = new FileBlockProvider(45, f);
            bl = b.readBlock(0);
            assertTrue(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)) == 123456789);

            bl = b.readBlock(1);
            assertTrue(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)) == 987654321);

            // truncate to 1 block
            b.truncateToBlock(0);
            assertTrue(b.getNumberOfBlocks() == 1);
            b.close();

            b = new FileBlockProvider(45, f);
            assertTrue(b.getNumberOfBlocks() == 1);

            bl = b.readBlock(0);
            assertTrue(ByteUtils.bytesToLong(Arrays.copyOfRange(bl.getContent(), 0, 8)) == 123456789);

            b.close();
    }

    public void testOtherBlockSize() throws Exception {
            File f = File.createTempFile("blocktest", null);
            f.delete();

            BlockProvider b = new FileBlockProvider(45, f, 2 * FileBlockProvider.DEFAULT_BLOCK_SIZE);
            b.close();

            RandomAccessFile raf = new RandomAccessFile(f, "r");
            ReadWriteBufferManager m = new ReadWriteBufferManager(raf.getChannel(), 1000);

            m.position(4);
            // default block size
            assertTrue(m.getInt() == 2 * FileBlockProvider.DEFAULT_BLOCK_SIZE);

            raf.close();
    }

}
