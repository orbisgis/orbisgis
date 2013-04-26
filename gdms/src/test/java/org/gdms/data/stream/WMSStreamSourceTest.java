/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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

package org.gdms.data.stream;

import org.junit.Test;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Unit test of stream source
 * @author Nicolas Fortin
 */
public class WMSStreamSourceTest {
    @Test
    public void uriSerialisationTest() throws Exception {
        URI uri = URI.create("http://services.orbisgis.org/wms/wms?REQUEST=GetMap&SERVICE=WMS&VERSION=1.3.0" +
                "&LAYERS=cantons_dep44&CRS=EPSG:27572" +
                "&BBOX=259555.01152073737,2218274.7695852537,342561.9239631337,2287024.7695852537&WIDTH=524&HEIGHT=434" +
                "&FORMAT=image/png&STYLES=");
        WMSStreamSource streamSource = new WMSStreamSource(uri);
        URI uri2 = streamSource.toURI();
        WMSStreamSource streamSource2 = new WMSStreamSource(uri2);
        assertEquals(streamSource.getCRS(),streamSource2.getCRS());
    }
    @Test
    public void uriTest() throws UnsupportedEncodingException {
        URI uri = URI.create("http://services.orbisgis.org/wms/wms?REQUEST=GetMap&SERVICE=WMS&VERSION=1.3.0" +
                "&LAYERS=cantons_dep44&CRS=EPSG:27572" +
                "&BBOX=259555.01152073737,2218274.7695852537,342561.9239631337,2287024.7695852537&WIDTH=524&HEIGHT=434" +
                "&FORMAT=image/png&STYLES=");
        WMSStreamSource streamSource = new WMSStreamSource(uri);
        assertEquals("services.orbisgis.org",streamSource.getHost());
        assertEquals("/wms/wms",streamSource.getPath());
        assertEquals("http",streamSource.getScheme());
        assertEquals("EPSG:27572",streamSource.getCRS());
        assertEquals("EPSG:27572",streamSource.getSRS());

        // If the version is not set and CRS is given
        uri = URI.create("http://services.orbisgis.org/wms/wms?SERVICE=WMS&CRS=EPSG:27572&FORMAT=image/png&STYLES=");
        streamSource = new WMSStreamSource(uri);
        assertEquals("1.3.0",streamSource.getVersion());
        assertEquals("EPSG:27572",streamSource.getCRS());

        // If the version is not set and SRS is given
        uri = URI.create("http://services.orbisgis.org/wms/wms?SERVICE=WMS&SRS=EPSG:27572&FORMAT=image/png&STYLES=");
        streamSource = new WMSStreamSource(uri);
        assertEquals("1.1.1",streamSource.getVersion());
        assertEquals("EPSG:27572",streamSource.getSRS());

        // Port test
        assertEquals(80,streamSource.getPort());
    }

    @Test
    public void testOtherParametersTest() throws Exception {
        //http://188.64.1.61/cgi-bin/mapserv?map=/usr/map/osm-mapserver_i.map&REQUEST=GetCapabilities&SERVICE=WMS
        URI uri = URI.create("http://188.64.1.61/cgi-bin/mapserv?map=/usr/map/osm-mapserver_i.map&"
                + "REQUEST=GetMap&SERVICE=WMS&VERSION=1.3.0" +
                "&LAYERS=cantons_dep44&CRS=EPSG:27572" +
                "&BBOX=259555.01152073737,2218274.7695852537,342561.9239631337,2287024.7695852537&WIDTH=524&HEIGHT=434" +
                "&FORMAT=image/png&STYLES=");
        WMSStreamSource streamSource = new WMSStreamSource(uri);
        Map<String, String> queryMap = streamSource.getQueryMap();
        assertTrue(queryMap.containsKey(WMSStreamSource.LAYER_PARAMETER));
        assertTrue(queryMap.containsKey(WMSStreamSource.CRS_PARAMETER));
        assertTrue(queryMap.containsKey(WMSStreamSource.OUTPUTFORMAT_PARAMETER));
        assertTrue(queryMap.containsKey(WMSStreamSource.SERVICE_PARAMETER));
        assertTrue(queryMap.containsKey(WMSStreamSource.VERSION_PARAMETER));
        Map<String,String> others = streamSource.getOthersQueryMap();
        assertTrue(others.containsKey("map"));
    }

    @Test
    public void testForgottenParametersTest() throws Exception {
        //http://188.64.1.61/cgi-bin/mapserv?map=/usr/map/osm-mapserver_i.map&REQUEST=GetCapabilities&SERVICE=WMS
        URI uri = URI.create("http://188.64.1.61/cgi-bin/mapserv?map=/usr/map/osm-mapserver_i.map"
                + "&REQUEST=GetMap"
                + "&SERVICE=WMS"
                + "&VERSION=1.3.0"
                + "&LAYERS=cantons_dep44"
                + "&CRS=EPSG:27572"
                + "&BBOX=259555.01152073737,2218274.7695852537,342561.9239631337,2287024.7695852537"
                + "&WIDTH=524"
                + "&HEIGHT=434"
                + "&FORMAT=image/png"
                + "&STYLES="
                + "&TRANSPARENT=true"
                + "&TIME=12-12-2013"
                + "&BGCOLOR=0x123456"
                + "&ELEVATION=CRS:88"
                + "EXCEPTIONS=xml");
        WMSStreamSource streamSource = new WMSStreamSource(uri);
        Map<String, String> queryMap = streamSource.getQueryMap();
        Map<String,String> others = streamSource.getOthersQueryMap();
        for(String s : WMSStreamSource.IGNORED_WMS_KEYS){
            assertFalse(queryMap.containsKey(s));
            assertFalse(others.containsKey(s));
        }
    }

}
