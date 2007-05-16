package org.gdms.geotoolsAdapter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.gdms.data.SpatialDataSource;
import org.gdms.driver.DriverException;
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

	private SpatialDataSource ds;

	public FeatureCollectionAdapter(SpatialDataSource ds) {
		this.ds = ds;
	}

	public void accepts(FeatureVisitor visitor, ProgressListener progress)
			throws IOException {
		throw new RuntimeException();
	}

	public void addListener(CollectionListener listener)
			throws NullPointerException {
		//TODO
	}

	public void close(FeatureIterator close) {
		throw new RuntimeException();
	}

	public void close(Iterator close) {
		//TODO
	}

	public FeatureIterator features() {
		return new FeatureIteratorAdapter(ds);
	}

	public FeatureType getFeatureType() {
		throw new RuntimeException();

	}

	public FeatureType getSchema() {
		//TODO
		return new FeatureTypeAdapter(ds);
	}

	public void removeListener(CollectionListener listener)
			throws NullPointerException {
		throw new RuntimeException();
	}

	public FeatureList sort(SortBy order) {
		throw new RuntimeException();
	}

	public FeatureCollection subCollection(Filter filter) {
		throw new RuntimeException();
	}

	public Iterator iterator() {
		//TODO
		return new IteratorAdapter(ds);
	}

	public void purge() {
		throw new RuntimeException();
	}

	public boolean add(Object o) {
		throw new RuntimeException();
	}

	public boolean addAll(Collection c) {
		throw new RuntimeException();

	}

	public void clear() {
		throw new RuntimeException();

	}

	public boolean contains(Object o) {
		throw new RuntimeException();

	}

	public boolean containsAll(Collection c) {
		throw new RuntimeException();

	}

	public boolean isEmpty() {
		throw new RuntimeException();

	}

	public boolean remove(Object o) {
		throw new RuntimeException();

	}

	public boolean removeAll(Collection c) {
		throw new RuntimeException();

	}

	public boolean retainAll(Collection c) {
		throw new RuntimeException();

	}

	public int size() {
		try {
			return (int) ds.getRowCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Object[] toArray() {
		throw new RuntimeException();

	}

	public Object[] toArray(Object[] a) {
		throw new RuntimeException();

	}

	public FeatureCollection collection() throws IOException {
		throw new RuntimeException();

	}

	public Envelope getBounds() {
		throw new RuntimeException();

	}

	public int getCount() throws IOException {
		throw new RuntimeException();

	}

	public FeatureReader reader() throws IOException {
		throw new RuntimeException();

	}

	public Object getAttribute(String xPath) {
		throw new RuntimeException();

	}

	public Object getAttribute(int index) {
		throw new RuntimeException();

	}

	public Object[] getAttributes(Object[] attributes) {
		throw new RuntimeException();

	}

	public Geometry getDefaultGeometry() {
		throw new RuntimeException();

	}

	public String getID() {
		throw new RuntimeException();

	}

	public int getNumberOfAttributes() {
		throw new RuntimeException();

	}

	public FeatureCollection getParent() {
		throw new RuntimeException();

	}

	public void setAttribute(int position, Object val)
			throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
		throw new RuntimeException();

	}

	public void setAttribute(String xPath, Object attribute)
			throws IllegalAttributeException {
		throw new RuntimeException();

	}

	public void setDefaultGeometry(Geometry geometry)
			throws IllegalAttributeException {
		throw new RuntimeException();

	}

	public void setParent(FeatureCollection collection) {
		throw new RuntimeException();

	}

}
