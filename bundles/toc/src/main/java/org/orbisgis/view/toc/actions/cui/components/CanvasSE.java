/**
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
package org.orbisgis.view.toc.actions.cui.components;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.AreaSymbolizer;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

/**
 * This class is responsible for drawing a preview of what will be rendered on
 * the map, using a particular symbolizer.
 * @author Alexis Guéganno, others...
 */
public class CanvasSE extends JPanel {
        private static final Logger LOGGER = Logger.getLogger("gui."+CanvasSE.class);
        private Symbolizer s;
        private GeometryFactory gf;
        private Geometry geom;
        private MapTransform mt;
        private boolean displayed;
        private int width;
        private int height;
        private BufferedImage bi = null;
        public final static int WIDTH = 126;
        public final static int HEIGHT = 70;
        /** Geometry and  */
        private Map<String, Object> sample;

    /**
     * Build this as a JPanel of size WIDTH*HEIGHT.
     */
	public CanvasSE(Symbolizer sym) {
        this(sym, WIDTH, HEIGHT);
	}

    /**
     * Builds a CanvasSE with the given width and height.
     * @param sym the symbolizer used to draw in the panel
     * @param w the width of the panel
     * @param h the height of the panel
     */
    public CanvasSE(Symbolizer sym, int w, int h){
        super();
        width = w;
        height = h;
        this.setSize(width, height);
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setOpaque(false);
        s = sym;
        gf = new GeometryFactory();
        geom = getSampleGeometry();
        mt = new MapTransform();
        mt.setExtent(new Envelope(0, width, 0, height));
        displayed = true;

    }

    /**
     * Force the image and the panel to be refreshed.
     */
    public void imageChanged(){
        bi = null;
        geom = getSampleGeometry();
        repaint();
    }

    /**
     * Gets the image used to fill the JPanel.
     * @return The image that is displayed in the JPanel.
     */
    public BufferedImage getImage(){
        if(bi == null){
            BufferedImage newBi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = (Graphics2D) newBi.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            try {
                g2.setBackground(new Color(255, 255, 255, 0));
                g2.clearRect(0, 0, width, height);
                ResultSet rs = getQuery();
                s.draw(g2, rs, 0, false, mt, geom);
            } catch (SQLException | ParameterException
                   | IOException | IllegalArgumentException ie){
                    LOGGER.error(ie.getMessage());
            }
            this.bi = newBi;
        }
        return bi;
    }

	@Override
	public void paintComponent(Graphics g) {
            g.drawImage(getImage(), 0, 0, null);
	}

        /**
         * Sets if the canvas must be drawn or not.
         * @param dis new value
         */
        public void setDisplayed(boolean dis){
                displayed = dis;
        }

        /**
         * Used to know if the canvas will be displayed or not.
         * @return
         */
        public boolean isDisplayed() {
                return displayed;
        }

        /**
         * @return SQL query from sample map.
         */
        private ResultSet getQuery() throws SQLException {
            if(sample == null) {
                sample = new HashMap<>();
            }
            Object[] values = new Object[sample.size() + 1];
            String[] fieldsName = new String[sample.size() + 1];
            int fieldId = 1;
            fieldsName[0] = "THE_GEOM";
            values[0] = getSampleGeometry();
            for(Map.Entry<String,Object> entry : sample.entrySet()) {
                values[fieldId] = entry.getValue();
                fieldsName[fieldId] = entry.getKey();
            }
            return new OneLineResultSet(fieldsName, values);
        }
        /**
         * Creates a sample {@link DataSource} that will be filled using :
         * <ul><li>The geometry type of the associated symbolizer.</li>
         * <li>The map given in argument.</li></ul></p>
         * <p>The inner sample {@code DataSource} will be created by creating a
         * SQL instruction and executing it.
         *
         * @param input
         */
        public void setSampleDatasource(Map<String, Object> input){
            sample = new HashMap<>(input);
        }

        /**
         * Set the symbolizer used to draw geometries with this canvas.
         * @param sym
         */
	public void setSymbol(Symbolizer sym) {
            this.s = sym;
            geom = getSampleGeometry();
            this.imageChanged();
	}

        /**
         * Gets the {@code Symbolizer} used to draw geometries in this {@code Canvas}.
         * @return
         */
	public Symbolizer getSymbol() {
		return s;
	}

	private LineString getComplexLine() {
		int widthUnit = getWidth() / 8;
		int heightUnit = getHeight() / 8;
		return gf.createLineString(new Coordinate[] {
				new Coordinate(widthUnit, 7 * heightUnit),
				new Coordinate(3 * widthUnit, 4 * heightUnit),
				new Coordinate(4 * widthUnit, 6 * heightUnit),
				new Coordinate(7 * widthUnit, heightUnit) });
	}

	private Geometry getComplexPolygon() {
		int widthUnit = getWidth() / 8;
		int heightUnit = getHeight() / 8;
		Coordinate[] coordsP = { new Coordinate(widthUnit, heightUnit),
				new Coordinate(7 * widthUnit, heightUnit),
				new Coordinate(widthUnit, 7 * heightUnit),
				new Coordinate(widthUnit, heightUnit) };
		return gf.createPolygon(gf.createLinearRing(coordsP), null);
	}

        private Geometry getSampleGeometry() {
                if(s instanceof LineSymbolizer){
                        return getComplexLine();
                } else if(s instanceof AreaSymbolizer){
                        return getComplexPolygon();
                } else {
                        PointSymbolizer ps = (PointSymbolizer)s;
                        if(ps.isOnVertex()){
                                return getComplexPolygon();
                        } else {
                                return gf.createPoint(new Coordinate(getWidth() / 2, getHeight() / 2));
                        }
                }
        }

}