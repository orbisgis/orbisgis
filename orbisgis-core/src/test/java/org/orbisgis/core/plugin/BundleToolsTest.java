/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.core.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Manifest;
import junit.framework.Assert;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * Set of Unit Test on BundleTools
 * @author Nicolas Fortin
 */
public class BundleToolsTest {
    @Test
    public void parseManifestTest() throws IOException {
        List<PackageDeclaration> packages = new LinkedList<PackageDeclaration>();
        // Header is :
        // Export-Package: org.xnap.commons.i18n;version="0.9.6",org.xnap.commons.i18n.nover
        InputStream manifestStream = BundleToolsTest.class.getResourceAsStream("MANIFEST.MF");
        Manifest manifest = new Manifest(manifestStream);
        BundleTools.parseManifest(manifest,packages);
        Assert.assertEquals(3,packages.size());
        PackageDeclaration packageInfo = packages.get(0);
        Assert.assertEquals("org.xnap.commons.i18n",packageInfo.getPackageName());
        Assert.assertEquals(new Version(0,9,6),packageInfo.getVersion());
        packageInfo = packages.get(1);
        // BundleTools will use the Bundle version if the package version is not available
        Assert.assertEquals("org.xnap.commons.i18n.nover",packageInfo.getPackageName());
        Assert.assertEquals(new Version(0,9,6),packageInfo.getVersion());
        packageInfo = packages.get(2);
        Assert.assertEquals("org.xnap.commons.i18n.bla",packageInfo.getPackageName());
        Assert.assertEquals(new Version(0,5,0),packageInfo.getVersion());

    }
}
