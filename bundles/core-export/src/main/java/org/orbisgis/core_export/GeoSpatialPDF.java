/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.core_export;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfLayer;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfRectangle;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfStructureElement;
import com.itextpdf.text.pdf.PdfStructureTreeRoot;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.Envelope;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to export a layer or a group of layer in a geospatial pdf format
 * 
 * @author Erwan Bocher
 */
public class GeoSpatialPDF {

    private final ILayer rootLayer;
    private final float width;
    private final float height;
    protected static final Logger LOGGER = LoggerFactory.getLogger(GeoSpatialPDF.class);

    public GeoSpatialPDF(ILayer rootLayer, float width, float height) {
        this.rootLayer = rootLayer;
        this.width = width;
        this.height = height;
    }

    /**
     * Create the PDF document and a the geospatial tags
     *
     * @param out
     * @param mt
     * @param pm
     * @throws IOException
     */
    public void createPDF(OutputStream out, MapTransform mt, ProgressMonitor pm) throws IOException {
        Document document = new Document(new Rectangle(width, height));
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setTagged();
            writer.setUserProperties(true);
            document.open();

            PdfContentByte cb = writer.getDirectContent();

            int numLayers = rootLayer.getLayerCount();
            for (int i = numLayers - 1; i >= 0; i--) {
                ILayer layer = rootLayer.getLayer(i);
                processSubLayer(layer, mt, writer, cb, pm, null);
            }
            georefPdf(writer, mt);

        } catch (DocumentException ex) {
            throw new IOException("Cannot create the pdf", ex);
        }
        document.close();
    }

    /**
     * Process all layers in the layermodel and build the corresponding pdf tree
     * layer
     *
     * @param layer
     */
    private void processSubLayer(ILayer layer, MapTransform mt, PdfWriter writer, PdfContentByte cb, ProgressMonitor pm, PdfLayer mainLayer) {
        if (layer.acceptsChilds() && layer.getLayerCount() > 0) {
            PdfLayer groupLayer = new PdfLayer(layer.getName(), writer);
            //To manage group of layers
            for (ILayer subLayer : layer.getChildren()) {
                processSubLayer(subLayer, mt, writer, cb, pm, groupLayer);
            }

        } else {
            if (layer.isVisible() && layer.getEnvelope().intersects(mt.getAdjustedExtent())) {
                PdfLayer mapLayer = new PdfLayer(layer.getName(), writer);
                if (mainLayer != null) {
                    mainLayer.addChild(mapLayer);
                }
                PdfTemplate layerTemplate = cb.createTemplate(width, height);
                Graphics2D g2dLayer = layerTemplate.createGraphics(width,
                        height);
                cb.beginLayer(mapLayer);
                PdfRenderer renderer2 = new PdfRenderer(layerTemplate, width, height);
                renderer2.draw(mt, g2dLayer, (int) width, (int) height, layer, pm);
                cb.addTemplate(layerTemplate, 0, 0);
                g2dLayer.dispose();
                cb.endLayer();
            }
        }
    }

    /**
     * This method is used to georeference the pdf.
     * Note : The CRS is not yet supported.
     *
     * @param writer
     * @param mt
     * @throws IOException
     * @throws DocumentException
     */
    public void georefPdf(PdfWriter writer, MapTransform mt) throws IOException, DocumentException {

        PdfStructureTreeRoot tree = writer.getStructureTreeRoot();

        //the part of the document where maps are displayed
        float mapWidth = width;
        float mapHeight = height;
        float mapLX = 0;
        float mapLY = 0;

        //ViewPort Dictionary
        PdfDictionary viewPortDict = new PdfDictionary();
        viewPortDict.put(PdfName.TYPE, new PdfName("Viewport"));
        viewPortDict.put(PdfName.BBOX, new PdfRectangle(mapLX, mapLY, mapLX + mapWidth, mapLY + mapHeight));
        viewPortDict.put(PdfName.NAME, new PdfString("Layers"));

        //Measure dictionary
        PdfDictionary measureDict = new PdfDictionary();
        measureDict.put(PdfName.TYPE, new PdfName("Measure"));
        measureDict.put(PdfName.SUBTYPE, new PdfName("GEO"));

        //Bounds
        PdfArray bounds = new PdfArray();
        bounds.add(new PdfNumber(0));
        bounds.add(new PdfNumber(0));
        bounds.add(new PdfNumber(1));
        bounds.add(new PdfNumber(0));
        bounds.add(new PdfNumber(1));
        bounds.add(new PdfNumber(1));
        bounds.add(new PdfNumber(0));
        bounds.add(new PdfNumber(1));

        measureDict.put(new PdfName("Bounds"), bounds);


        //GPTS
        Envelope adjustedBbox = mt.getAdjustedExtent();

        if (!adjustedBbox.isNull()) {

            //ly lx ly ux uy ux uy lx
            PdfArray gptsTable = new PdfArray();
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMinY()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMinX()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMinY()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMaxX()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMaxY()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMaxX()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMaxY()).toString()));
            gptsTable.add(new PdfNumber(((Double) adjustedBbox.getMinX()).toString()));

            measureDict.put(new PdfName("GPTS"), gptsTable);

            //The CRS will be added when the mapcontext will support it.

//            //GCS Geospatial Coordinate system
//            PdfDictionary gcsDict = new PdfDictionary();
//            if (context.getBbox().getCrs() != null) {
//
//                if (context.getBbox().getCrs().getType() != null) {
//                    gcsDict.put(PdfName.TYPE, new PdfName(context.getBbox().getCrs().getType()));
//                } else {
//                    LOGGER.warn("No type of crs : the pdf cannot be georeferenced");
//                    return;
//                }
//
//                if (context.getBbox().getCrs().getEpsg() != 0) {
//                    gcsDict.put(new PdfName("EPSG"), new PdfNumber(context.getBbox().getCrs().getEpsg()));
//                } else {
//                    LOGGER.warn("No epsg : the pdf cannot be georeferenced");
//                    return;
//                }
//
//
//            } else {
//                LOGGER.warn("No crs :  the pdf cannot be georeferenced");
//
//            }
//
//            measureDict.put(new PdfName("GCS"), gcsDict);
        } else {
            LOGGER.warn("Envelope of bbox null : the pdf cannot be georeferenced");

        }

        //PDU : array of units
        PdfArray pdu = new PdfArray();
        pdu.add(new PdfName("KM"));
        pdu.add(new PdfName("SQKM"));
        pdu.add(new PdfName("DEG"));

        measureDict.put(new PdfName("PDU"), pdu);

        //LPTS
        PdfArray lptsTable = new PdfArray();
        lptsTable.add(new PdfNumber(0));
        lptsTable.add(new PdfNumber(0));
        lptsTable.add(new PdfNumber(1));
        lptsTable.add(new PdfNumber(0));
        lptsTable.add(new PdfNumber(1));
        lptsTable.add(new PdfNumber(1));
        lptsTable.add(new PdfNumber(0));
        lptsTable.add(new PdfNumber(1));

        measureDict.put(new PdfName("LPTS"), lptsTable);

        viewPortDict.put(new PdfName("Measure"), measureDict);


        PdfStructureElement top = new PdfStructureElement(tree, new PdfName("VP"));
        top.putAll(viewPortDict);
    }
}
