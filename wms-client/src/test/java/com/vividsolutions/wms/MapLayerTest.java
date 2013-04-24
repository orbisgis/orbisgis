/**
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
package com.vividsolutions.wms;

import com.vividsolutions.jts.geom.Envelope;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.xerces.parsers.DOMParser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class MapLayerTest {
    private MapLayer parent;
    private MapLayer c2;
    private MapLayer c1;

    @Before
    public void setUp(){
        String s = "EPSG:2154";
        String wgs = "EPSG:4326";
        Collection<String> srs = new ArrayList<String>();
        srs.add(s);
        srs.add(wgs);
        BoundingBox bb = new BoundingBox(s, new Envelope(40,50,40,50));
        BoundingBox bw = new BoundingBox(wgs, new Envelope(1,5,4,6));
        HashMap<String, BoundingBox> hm = new HashMap<String,BoundingBox>();
        hm.put(s,bb);
        hm.put(wgs,bw);
        c1 = new MapLayer("c1", "c1", srs, new ArrayList<MapLayer>(),bw, hm);
        s = "EPSG:2154";
        wgs = "EPSG:4326";
        srs = new ArrayList<String>();
        srs.add(s);
        srs.add(wgs);
        bb = new BoundingBox(s, new Envelope(60,70,60,70));
        bw = new BoundingBox(wgs, new Envelope(2,6,5,7));
        hm = new HashMap<String,BoundingBox>();
        hm.put(s,bb);
        hm.put(wgs,bw);
        c2 = new MapLayer("c2", "c2", srs, new ArrayList<MapLayer>(),bw, hm);
        List<MapLayer> children = new ArrayList<MapLayer>(2);
        children.add(c1);
        children.add(c2);
        bw = new BoundingBox(wgs, new Envelope(2,6,5,7));
        parent = new MapLayer("parent","parent",srs,children,bw, new HashMap<String, BoundingBox>());
    }

    @Test
    public void testGetBoundingBoxStatic(){
        Envelope env = MapLayer.getBoundingBox("EPSG:2154", parent, new Envelope());
        assertTrue(env.equals(new Envelope(40,70,40,70)));
        String s = "EPSG:2154";
        String wgs = "EPSG:4326";
        Collection<String> srs = new ArrayList<String>();
        List<MapLayer> children = new ArrayList<MapLayer>(2);
        children.add(c1);
        children.add(c2);
        BoundingBox bb = new BoundingBox(s, new Envelope(10,20,10,20));
        BoundingBox bw = new BoundingBox(wgs, new Envelope(2,6,5,7));
        HashMap<String, BoundingBox> hm = new HashMap<String,BoundingBox>();
        hm.put(s,bb);
        hm.put(wgs,bw);
        parent = new MapLayer("parent","parent",srs,children,bw, hm);
        env = MapLayer.getBoundingBox("EPSG:2154", parent, new Envelope());
        assertTrue(env.equals(new Envelope(10,20,10,20)));
    }

    @Test
    public void testGetAllBoundingBox(){
        List<BoundingBox> bbs = c1.getAllBoundingBoxList();
        assertTrue(bbs.size()==2);
        Envelope exp1 = new Envelope(40,50,40,50);
        Envelope exp2 = new Envelope(1,5,4,6);
        Envelope real0 = bbs.get(0).getEnvelope();
        Envelope real1 = bbs.get(1).getEnvelope();
        assertTrue(exp1.equals(real0) || exp2.equals(real0));
        assertTrue(exp1.equals(real1) || exp2.equals(real1));
        assertFalse(real1.equals(real0));
    }

    @Test
    public void testGetAllBoundingBoxFromParent(){
        String s = "EPSG:2154";
        String wgs = "EPSG:4326";
        Collection<String> srs = new ArrayList<String>();
        srs.add(s);
        srs.add(wgs);
        HashMap<String, BoundingBox> hm = new HashMap<String,BoundingBox>();
        MapLayer ml = new MapLayer("c1", "c1", srs, new ArrayList<MapLayer>(), null, hm);
        List<MapLayer> children = new ArrayList<MapLayer>(2);
        children.add(ml);
        BoundingBox bb = new BoundingBox(s, new Envelope(10,20,10,20));
        BoundingBox bw = new BoundingBox(wgs, new Envelope(2,6,5,7));
        hm = new HashMap<String,BoundingBox>();
        hm.put(s,bb);
        hm.put(wgs,bw);
        MapLayer par = new MapLayer("parent","parent",srs,children,bw, hm);
        List<BoundingBox> bbs = ml.getAllBoundingBoxList();
        Envelope exp1 = new Envelope(10,20,10,20);
        Envelope exp2 = new Envelope(2,6,5,7);
        Envelope real0 = bbs.get(0).getEnvelope();
        Envelope real1 = bbs.get(1).getEnvelope();
        assertTrue(exp1.equals(real0) || exp2.equals(real0));
        assertTrue(exp1.equals(real1) || exp2.equals(real1));
        assertFalse(real1.equals(real0));

    }

    @Test
    public void testCRSFromCapabilities() throws Exception {
        File f = new File("src/test/resources/com/vividsolutions/wms/capabilities_1_3_0.xml");
        FileInputStream fis = new FileInputStream(f);
        DOMParser domParser = new DOMParser();
        domParser.setFeature("http://xml.org/sax/features/validation", false);
        domParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        domParser.parse(new InputSource(fis));
        Document doc = domParser.getDocument();
        ParserWMS1_3 parser = new ParserWMS1_3();
        WMService service = new WMService("http://dummy.org/wms?");
        Capabilities cap = parser.parseCapabilities(service, doc);
        MapLayer ml = cap.getTopLayer();
        assertTrue(ml.getName().equals("top"));
        assertTrue(ml.getTitle().equals("TopLayer"));
        assertTrue(ml.getAllBoundingBoxList().size()==2);
    }

}
