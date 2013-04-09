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
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLScript;
import org.gdms.sql.engine.SQLStatement;
import org.gdms.sql.engine.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

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
        private DataSet sample;
        private boolean displayed;
        private int width;
        private int height;
        private BufferedImage bi = null;
        public final static int WIDTH = 126;
        public final static int HEIGHT = 70;

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
        this.setOpaque(true);
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
            if(sample == null){
                setBasicDataSource();
            }
            try {
                g2.setBackground(Color.WHITE);
                g2.fillRect(0, 0, width, height);
                if(sample instanceof DataSource){
                    DataSource ds = (DataSource) sample;
                    if(!ds.isOpen()){
                        ds.open();
                    }
                }
                if(sample instanceof DiskBufferDriver){
                    DiskBufferDriver dbd = (DiskBufferDriver) sample;
                    dbd.open();
                }
                s.draw(g2, sample, 0, false, mt, geom, null);
                if(sample instanceof DataSource){
                    ((DataSource) sample).close();
                }
                if(sample instanceof DiskBufferDriver){
                    DiskBufferDriver dbd = (DiskBufferDriver) sample;
                    dbd.close();
                }
            } catch (DriverException de){
            } catch (ParameterException de){
            } catch (IOException de){
            } catch (IllegalArgumentException ie){
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
         * @param dis
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
         * Sets the associated {@code DataSource} so that it contains only a
         * basic geometry in a field named the_geom.
         */
        public void setBasicDataSource(){
                DataSourceFactory dsf = Services.getService(DataManager.class).getDataSourceFactory();
                try {
                        SQLScript s = Engine.loadScript(CanvasSE.class.getResourceAsStream("GeometryOnly.bsql"));
                        s.setValueParameter("geomText", ValueFactory.createValue(getSampleGeometry().toString()));
                        s.setDataSourceFactory(dsf);
                        SQLStatement st = s.getStatements()[0];
                        st.prepare();
                        sample = st.execute();
                        st.cleanUp();
                } catch (SemanticException ex) {
                        LOGGER.warn(ex.getMessage(), ex);
                } catch (DriverException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                } catch (IOException ex) {
                        LOGGER.warn(ex.getMessage(), ex);
                }
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
            StringBuilder sb = new StringBuilder(80+15*input.size());
            sb.append("SELECT @{the_geom} :: GEOMETRY as the_geom");
            Set<Map.Entry<String,Object>> es =input.entrySet();
            for(Map.Entry<String, Object> ent : es){
                    sb.append(", @{");
                    sb.append(ent.getKey());
                    sb.append("} as ");
                    sb.append(ent.getKey());
            }
            sb.append(";");
            try {
                SQLScript sqlScript = Engine.parseScript(sb.toString(), DataSourceFactory.getDefaultProperties());
                SQLStatement stat = sqlScript.getStatements()[0];
                stat.setValueParameter("the_geom", ValueFactory.createValue(getSampleGeometry()));
                for(Map.Entry<String, Object> ent : es){
                    Value value = null;
                    Object val = ent.getValue();
                    if(val instanceof String || val instanceof StringParameter){
                        value = ValueFactory.createValue(val.toString());
                    } else if(val instanceof  Double) {
                        value = ValueFactory.createValue((Double) val);
                    } else if(val instanceof RealParameter) {
                        value = ValueFactory.createValue(((RealParameter) val).getValue(null));
                    }

                    stat.setValueParameter(ent.getKey(), value);
                }

                DataManager dataManager = Services.getService(DataManager.class);
                DataSourceFactory dsf = dataManager.getDataSourceFactory();
                stat.setDataSourceFactory(dsf);
                stat.prepare();
                sample = stat.execute();
                stat.cleanUp();
            } catch (DriverException ex) {
                    LOGGER.error("", ex);
            } catch (ParseException ex) {
                    LOGGER.error("", ex);
            } catch (ParameterException ex) {
                    LOGGER.error("", ex);
            }
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