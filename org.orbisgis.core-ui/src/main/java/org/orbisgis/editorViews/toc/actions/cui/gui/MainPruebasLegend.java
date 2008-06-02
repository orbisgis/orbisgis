package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JFrame;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.values.AbstractValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.LayerListener;
import org.orbisgis.layerModel.persistence.LayerType;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.renderer.legend.UniqueValueLegend;

import com.vividsolutions.jts.geom.Envelope;

public class MainPruebasLegend {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UniqueSymbolLegend leg = LegendFactory.createUniqueSymbolLegend();
		leg.setName("Unique symbol legend");
		Symbol sym = SymbolFactory.createLineSymbol(Color.BLACK,
				new BasicStroke(new Float(2.0)));
		leg.setSymbol(sym);

		UniqueSymbolLegend leg2 = LegendFactory.createUniqueSymbolLegend();
		leg2.setName("Unique symbol legend 2");
		Symbol sym2 = SymbolFactory.createLineSymbol(Color.GREEN,
				new BasicStroke(new Float(4.0)));
		leg2.setSymbol(sym2);
		
		UniqueValueLegend leg3 = LegendFactory.createUniqueValueLegend();
		leg3.setName("Unique Value Legend");
		
		Symbol sym3 = SymbolFactory.createCirclePointSymbol(Color.black, Color.LIGHT_GRAY, 20);
		Symbol sym4 = SymbolFactory.createPolygonSymbol(new BasicStroke((float)2.0), Color.blue, Color.YELLOW);
		
		
		Value value = ValueFactory.createValue(2.0);
		Value value2 = ValueFactory.createValue(3.0);
		Value value3 = ValueFactory.createValue(4.0);
		Value value4 = ValueFactory.createValue(5.0);
		leg3.addClassification(value, sym2);
		leg3.addClassification(value2, sym);
		leg3.addClassification(value3, sym3);
		leg3.addClassification(value4, sym4);

		Legend[] legs = { leg, leg2, leg3 };
		
		ILayer layer = new ILayer(){

			public boolean acceptsChilds() {
				// TODO Auto-generated method stub
				return false;
			}

			public void addLayer(ILayer layer) throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void addLayer(ILayer layer, boolean isMoving)
					throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void addLayerListener(LayerListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void addLayerListenerRecursively(LayerListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void close() throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public Set<String> getAllLayersNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer[] getChildren() {
				// TODO Auto-generated method stub
				return null;
			}

			public CoordinateReferenceSystem getCoordinateReferenceSystem() {
				// TODO Auto-generated method stub
				return null;
			}

			public SpatialDataSourceDecorator getDataSource() {			
				return null;
			}

			public Envelope getEnvelope() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getIndex(ILayer targetLayer) {
				// TODO Auto-generated method stub
				return 0;
			}

			public ILayer getLayer(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer getLayerByName(String layerName) {
				// TODO Auto-generated method stub
				return null;
			}

			public int getLayerCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			public ILayer[] getLayerPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer[] getLayersRecursively() {
				// TODO Auto-generated method stub
				return null;
			}

			public Legend[] getLegend() throws DriverException {
				// TODO Auto-generated method stub
				return null;
			}

			public Legend[] getLegend(String fieldName)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer getParent() {
				// TODO Auto-generated method stub
				return null;
			}

			public GeoRaster getRaster() throws DriverException {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer[] getRasterLayers() throws DriverException {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getSelection() {
				// TODO Auto-generated method stub
				return null;
			}

			public LayerType getStatus() {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer[] getVectorLayers() throws DriverException {
				// TODO Auto-generated method stub
				return null;
			}

			public void insertLayer(ILayer layer, int index)
					throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void insertLayer(ILayer layer, int index, boolean isMoving)
					throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public boolean isRaster() throws DriverException {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isVectorial() throws DriverException {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isVisible() {
				// TODO Auto-generated method stub
				return false;
			}

			public void moveTo(ILayer layer, int index) throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void moveTo(ILayer layer) throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void open() throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public ILayer remove(ILayer layer, boolean isMoving)
					throws LayerException {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer remove(ILayer layer) throws LayerException {
				// TODO Auto-generated method stub
				return null;
			}

			public ILayer remove(String layerName) throws LayerException {
				// TODO Auto-generated method stub
				return null;
			}

			public void removeLayerListener(LayerListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void removeLayerListenerRecursively(LayerListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void setCoordinateReferenceSystem(
					CoordinateReferenceSystem coordinateReferenceSystem)
					throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void setLegend(Legend... legends) throws DriverException {
				// TODO Auto-generated method stub
				
			}

			public void setLegend(String fieldName, Legend... legends)
					throws IllegalArgumentException, DriverException {
				// TODO Auto-generated method stub
				
			}

			public void setName(String name) throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void setParent(ILayer parent) throws LayerException {
				// TODO Auto-generated method stub
				
			}

			public void setSelection(int[] newSelection) {
				// TODO Auto-generated method stub
				
			}

			public void setVisible(boolean isVisible) throws LayerException {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		JPanelLegendList ven = new JPanelLegendList(
				GeometryConstraint.MIXED, (ILayer)null);
		ven.setPreferredSize(new Dimension(910, 460));
		

		JFrame fra = new JFrame();
		fra.add(ven);
		fra.setSize(new Dimension(910, 460));

		fra.pack();
		fra.setVisible(true);
		fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
