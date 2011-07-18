/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
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
import java.util.Arrays;
import junit.framework.TestCase;
import org.orbisgis.utils.ByteUtils;

/**
 *
 * @author Antoine Gourlay
 */
public class BufferManagerTest extends TestCase {
    
    public BufferManagerTest(String testName) {
        super(testName);
    }

    public void testBufferManagerSize() throws Exception {
            BufferManager bf = new BufferManager(0);
            // smaller size: 50 MB
            assertTrue(bf.getBufferSize() == 50 * 1024 * 1024);

            bf = new BufferManager(52 * 1024 * 1024);
            assertTrue(bf.getBufferSize() == 52 * 1024 * 1024);
    }

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
            
            assertTrue(ByteUtils.bytesToLong(Arrays.copyOfRange(bl2.getContent(), 0, 8)) == 123456789);
    }
    
}
