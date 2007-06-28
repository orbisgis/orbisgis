package org.gdms.geotoolsAdapter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.data.FeatureReader;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureList;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.filter.Filter;
import org.geotools.filter.SortBy;
import org.geotools.util.ProgressListener;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FeatureCollectionAdapter implements FeatureCollection {

	private SpatialDataSourceDecorator ds;

	public FeatureCollectionAdapter(SpatialDataSourceDecorator ds) {
		this.ds = ds;
	}

	public void accepts(FeatureVisitor visitor, ProgressListener progress)
			throws IOException {
		throw new Error();
	}

	public void addListener(CollectionListener listener)
			throws NullPointerException {
		// TODO
	}

	public void close(FeatureIterator close) {
		throw new Error();
	}

	public void close(Iterator close) {
		// TODO
	}

	public FeatureIterator features() {
		return new FeatureIteratorAdapter(ds);
	}

	public FeatureType getFeatureType() {
		throw new Error();

	}

	public FeatureType getSchema() {
		// TODO
		return new FeatureTypeAdapter(ds);
	}

	public void removeListener(CollectionListener listener)
			throws NullPointerException {
		throw new Error();
	}

	public FeatureList sort(SortBy order) {
		throw new Error();
	}

	public FeatureCollection subCollection(Filter filter) {
		throw new Error();
	}

	public Iterator iterator() {
		// TODO
		return new IteratorAdapter(ds);
	}

	public void purge() {
		throw new Error();
	}

	public boolean add(Object o) {
		throw new Error();
	}

	public boolean addAll(Collection c) {
		throw new Error();

	}

	public void clear() {
		throw new Error();

	}

	public boolean contains(Object o) {
		throw new Error();

	}

	public boolean containsAll(Collection c) {
		throw new Error();

	}

	public boolean isEmpty() {
		throw new Error();

	}

	public boolean remove(Object o) {
		throw new Error();

	}

	public boolean removeAll(Collection c) {
		throw new Error();

	}

	public boolean retainAll(Collection c) {
		throw new Error();

	}

	public int size() {
		try {
			return (int) ds.getRowCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Object[] toArray() {
		throw new Error();

	}

	public Object[] toArray(Object[] a) {
		throw new Error();

	}

	public FeatureCollection collection() throws IOException {
		throw new Error();

	}

	public Envelope getBounds() {
		throw new Error();

	}

	public int getCount() throws IOException {
		throw new Error();

	}

	public FeatureReader reader() throws IOException {
		throw new Error();

	}

	public Object getAttribute(String xPath) {
		throw new Error();

	}

	public Object getAttribute(int index) {
		throw new Error();

	}

	public Object[] getAttributes(Object[] attributes) {
		throw new Error();

	}

	public Geometry getDefaultGeometry() {
		throw new Error();

	}

	public String getID() {
		throw new Error();

	}

	public int getNumberOfAttributes() {
		throw new Error();

	}

	public FeatureCollection getParent() {
		throw new Error();

	}

	public void setAttribute(int position, Object val)
			throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
		throw new Error();

	}

	public void setAttribute(String xPath, Object attribute)
			throws IllegalAttributeException {
		throw new Error();

	}

	public void setDefaultGeometry(Geometry geometry)
			throws IllegalAttributeException {
		throw new Error();

	}

	public void setParent(FeatureCollection collection) {
		throw new Error();
	}

	public DataSource getDataSource() {
		return ds;
	}
}