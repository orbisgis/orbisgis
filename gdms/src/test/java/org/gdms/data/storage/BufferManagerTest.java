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

import org.junit.Test;
import java.io.File;
import java.util.Arrays;
import org.orbisgis.utils.ByteUtils;

import static org.junit.Assert.*;

/**
 *
 * @author Antoine Gourlay
 */
public class BufferManagerTest {

        @Test
        public void testBufferManagerSize() throws Exception {
                BufferManager bf = new BufferManager(0);
                // smaller size: 50 MB
                assertEquals(bf.getBufferSize(), 50 * 1024 * 1024);

                bf = new BufferManager(52 * 1024 * 1024);
                assertEquals(bf.getBufferSize(), 52 * 1024 * 1024);
        }

        @Test
        public void testBufferManagerReadWrite() throws Exception {
                File f = File.createTempFile("blocktestx", null);
                f.delete();

                BlockProvider b = new FileBlockProvider(45, f);
                BufferManager bf = new BufferManager(0);

                bf.registerBlockProvider(b);
                Block bl = b.newBlock();

                bl.setContent(ByteUtils.longToBytes(123456789));
                bf.saveBlock(bl);

                BufferManager bf2 = new BufferManager(0);

                bf2.registerBlockProvider(b);

                Block bl2 = bf2.getBlock(bl.getId());

                f.delete();
                assertEquals(ByteUtils.bytesToLong(Arrays.copyOfRange(bl2.getContent(), 0, 8)), 123456789);
        }
}
