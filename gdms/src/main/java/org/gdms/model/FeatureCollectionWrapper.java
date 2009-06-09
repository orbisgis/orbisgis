/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package org.gdms.model;

import java.util.*;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.util.Assert;

/**
 * Delegates to another instance of FeatureCollection. A useful means of
 * overriding the behaviour of a FeatureCollection dynamically, at run-time
 * (i.e. without subclassing).
 */
public abstract class FeatureCollectionWrapper implements FeatureCollection {
	private FeatureCollection fc;

	/**
	 * Constructs a FeatureCollectionWrapper that delegates to the given
	 * FeatureCollection.
	 */
	public FeatureCollectionWrapper(FeatureCollection fc) {
		this.fc = fc;
	}

	/**
	 * Returns the non-wrapper FeatureCollection wrapped by this wrapper and
	 * possibly by other wrappers in-between. Intended to get at the "real"
	 * FeatureCollection underneath several layers of FeatureCollectionWrappers.
	 * 
	 * @see #getWrappee()
	 */
	public FeatureCollection getUltimateWrappee() {
		FeatureCollection currentWrappee = fc;
		while (currentWrappee instanceof FeatureCollectionWrapper) {
			currentWrappee = ((FeatureCollectionWrapper) currentWrappee).fc;
		}
		return currentWrappee;
	}

	/**
	 * Throws an AssertionFailedException if this FeatureCollectionWrapper wraps
	 * (directly or indirectly) another FeatureCollectionWrapper having the same
	 * class (or descendant class thereof). A consistency check that is useful
	 * for some FeatureCollectionWrapper implementations.
	 */
	public void checkNotWrappingSameClass() {
		Assert
				.isTrue(!(fc instanceof FeatureCollectionWrapper && ((FeatureCollectionWrapper) fc)
						.hasWrapper(getClass())));
	}

	public Collection remove(Envelope env) {
		return fc.remove(env);
	}

	/**
	 * Returns whether this FeatureCollectionWrapper (or a
	 * FeatureCollectionWrapper that it wraps, directly or indirectly) is an
	 * instance of the given class (or one of its descendants).
	 */
	public boolean hasWrapper(Class c) {
		Assert.isTrue(FeatureCollectionWrapper.class.isAssignableFrom(c));

		if (c.isInstance(this)) {
			return true;
		}

		return fc instanceof FeatureCollectionWrapper
				&& ((FeatureCollectionWrapper) fc).hasWrapper(c);
	}

	/**
	 * Returns the FeatureCollection that this wrapper delegates to (possibly
	 * another FeatureCollectionWrapper).
	 * 
	 * @see #getUltimateWrappee()
	 */
	public FeatureCollection getWrappee() {
		return fc;
	}

	public FeatureSchema getFeatureSchema() {
		return fc.getFeatureSchema();
	}

	public Envelope getEnvelope() {
		return fc.getEnvelope();
	}

	public int size() {
		return fc.size();
	}

	public boolean isEmpty() {
		return fc.isEmpty();
	}

	public List getFeatures() {
		return fc.getFeatures();
	}

	public Iterator iterator() {
		return fc.iterator();
	}

	public List query(Envelope envelope) {
		return fc.query(envelope);
	}

	public void add(Feature feature) {
		fc.add(feature);
	}

	public void remove(Feature feature) {
		fc.remove(feature);
	}

	public void addAll(Collection features) {
		fc.addAll(features);
	}

	public void removeAll(Collection features) {
		fc.removeAll(features);
	}

	public void clear() {
		//Create a new ArrayList to avoid a ConcurrentModificationException.
		// [Jon Aquino]
		removeAll(new ArrayList(getFeatures()));
	}

	protected FeatureCollection getFeatureCollection() {
		return fc;
	}

	protected void setFeatureCollection(FeatureCollection featureCollection) {
		this.fc = featureCollection;
	}
}