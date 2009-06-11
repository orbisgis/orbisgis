
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


/**
 * A collection of Features, with a special method for querying the Features
 * that lie within a given Envelope.
 */
public interface FeatureCollection {
    /**
     * Returns information about this FeatureCollection
     * @return the types of the attributes of the features in this collection
     */
    FeatureSchema getFeatureSchema();

    /**
     * Returns the bounds of this collection.
     * @return the smallest Envelope enclosing all the Features in this collection
     */
    Envelope getEnvelope();

    /**
     * Returns the number of features in this collection.
     * @return the number of features in this collection
     */
    int size();

    /**
     * Returns whether this collection has no features.
     * @return whether or not the size of this collection is 0
     */
    boolean isEmpty();

    /**
     * Returns an unmodifiable List of the features in this collection
     * @return a read-only view of all the features
     */
    List getFeatures();

    /**
     * Returns an Iterator over the features
     * @return an Iterator over the features
     */
    Iterator iterator();

    /**
     * A quick search for features, using an envelope comparison.
     * @param envelope the envelope to query against
     * @return features whose envelopes intersect the given envelope
     */
    List query(Envelope envelope);

    /**
     * Adds a feature to this collection. 
     * @param feature a Feature to add to the end of this collection
     */
    void add(Feature feature);

    /**
     * Adds multiple features to this collection. To be preferred over #add for
     * adding multiple features, because in some systems (like the JUMP Workbench)
     * fewer events will be fired.
     */
    void addAll(Collection features);

    /**
     * Removes multiple features from this collection. To be preferred over #remove for
     * removing multiple features, because in some systems (like the JUMP Workbench)
     * fewer events will be fired.
     */    
    void removeAll(Collection features);

    /**
     * Removes a feature from this collection.
     * @param feature a Feature to remove from this collection
     */
    void remove(Feature feature);

    /**
     * Removes all features from this collection.
     */
    void clear();

    /**
     * Removes the features which intersect the given envelope
     * @return the removed features
     */
    Collection remove(Envelope env);
}
