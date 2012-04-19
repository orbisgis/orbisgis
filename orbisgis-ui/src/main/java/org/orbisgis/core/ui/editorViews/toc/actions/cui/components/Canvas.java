/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.components;

import com.vividsolutions.jts.geom.*;
import java.awt.Dimension;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JPanel;
import org.gdms.data.*;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * This class is responsible for drawing a preview of what will be rendered on
 * the map, using a particular symbolizer.
 * @author alexis, others...
 */
public class Canvas extends JPanel {

        private Symbolizer s;
        private GeometryFactory gf;
        private Geometry geom;
        private MapTransform mt;

        /**
         * Build this as a JPanel of size 126*70.
         */
	public Canvas(Symbolizer sym) {
		super();
		this.setSize(126, 70);
		this.setPreferredSize(new Dimension(126, 70));
		this.setMaximumSize(new Dimension(126, 70));
                s = sym;
                gf = new GeometryFactory();
                geom = getSampleGeometry();
                mt = new MapTransform();
                mt.setExtent(new Envelope(0, 126, 0, 70));
	}

	@Override
	public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Object old = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.fillRect(0, 0, getWidth(), getHeight());
                try {
                        s.draw(g2, new InnerDS(), 0, false, mt, geom, null);
                } catch (DriverException de){
                } catch (ParameterException de){
                } catch (IOException de){
                }
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, old);
	}

	public void setSymbol(Symbolizer sym) {
		this.s = sym;
                geom = getSampleGeometry();
		this.repaint();
	}

	public Symbolizer getSymbol() {
		return s;
	}

	private LineString getComplexLine() {
		int widthUnit = getWidth() / 4;
		int heightUnit = getHeight() / 4;
		return gf.createLineString(new Coordinate[] {
				new Coordinate(widthUnit, 3 * heightUnit),
				new Coordinate(1.5 * widthUnit, 2 * heightUnit),
				new Coordinate(2 * widthUnit, 3 * heightUnit),
				new Coordinate(3 * widthUnit, heightUnit) });
	}

	private Geometry getComplexPolygon() {
		int widthUnit = getWidth() / 4;
		int heightUnit = getHeight() / 4;
		Coordinate[] coordsP = { new Coordinate(widthUnit, heightUnit),
				new Coordinate(3 * widthUnit, heightUnit),
				new Coordinate(widthUnit, 3 * heightUnit),
				new Coordinate(widthUnit, heightUnit) };
		return gf.createPolygon(gf.createLinearRing(coordsP), null);
	}

        private Geometry getSampleGeometry() {
                if(s instanceof LineSymbolizer){
                        return getComplexLine();
                } else if(s instanceof AreaSymbolizer){
                        return getComplexPolygon();
                }
                return gf.createPoint(new Coordinate(getWidth() / 2, getHeight() / 2));
        }

        private class InnerDS extends AbstractDataSource {

                @Override
                public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                        return ValueFactory.createValue(geom);
                }

                @Override
                public long getRowCount() throws DriverException {
                        return 1;
                }

                @Override
                public Number[] getScope(int dimension) throws DriverException {
                        return null;
                }

                @Override
                public Metadata getMetadata() throws DriverException {
                        return new DefaultMetadata(new Type[]{TypeFactory.createType(Type.GEOMETRY)},
                                new String[]{"the_geom"}
                        );
                }

                @Override
                public void open() throws DriverException {
                }

                @Override
                public void close() throws DriverException {
                }

                @Override
                public DataSourceFactory getDataSourceFactory() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setDataSourceFactory(DataSourceFactory dsf) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void insertFilledRow(Value[] values) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void insertEmptyRow() throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void insertFilledRowAt(long index, Value[] values) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void insertEmptyRowAt(long index) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void deleteRow(long rowId) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void commit() throws DriverException, NonEditableDataSourceException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void saveData(DataSource ds) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void redo() throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void undo() throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean canRedo() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean canUndo() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addMetadataEditionListener(MetadataEditionListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void removeMetadataEditionListener(MetadataEditionListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addEditionListener(EditionListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void removeEditionListener(EditionListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addDataSourceListener(DataSourceListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void removeDataSourceListener(DataSourceListener listener) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setDispatchingMode(int dispatchingMode) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getDispatchingMode() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addField(String name, Type driverType) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void removeField(int index) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setFieldName(int index, String name) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Driver getDriver() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public DataSet getDriverTable() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getDriverTableName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean isModified() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean isOpen() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean isEditable() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Iterator<Integer> queryIndex(IndexQuery queryIndex) throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Commiter getCommiter() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String[] getReferencedSources() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Source getSource() {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void syncWithSource() throws DriverException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

        }
}