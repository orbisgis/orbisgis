/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
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

package org.geoalgorithm.jcs.qa;

import java.util.*;

import org.gdms.model.Feature;
import org.gdms.model.FeatureCollection;
import org.gdms.model.FeatureDataset;
import org.gdms.model.FeatureDatasetFactory;
import org.gdms.model.FeatureUtil;
import org.gdms.model.IndexedFeatureCollection;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.*;

/**
 * Finds features in a dataset which overlap.
 */
public class InternalOverlapFinder {

	private static final String FEATURES = "features";

	private FeatureCollection inputFC;

	private FeatureCollection overlappingFC;

	private FeatureCollection overlapIndicatorFC;

	private FeatureCollection overlapSizeIndicatorFC;

	private Set overlappingFeatures = new TreeSet(
			new FeatureUtil.IDComparator());

	private List overlapIndicators = new ArrayList();// a list of Geometry's

	private List overlapSizeIndicators = new ArrayList();// a list of

	// Geometry's

	private Envelope fence = null;

	private IProgressMonitor monitor;

	private boolean isComputed = false;

	public InternalOverlapFinder(FeatureCollection inputFC,
			IProgressMonitor monitor) {
		this.inputFC = inputFC;
		this.monitor = monitor;
	}

	public void setFence(Envelope fence) {
		this.fence = fence;
	}

	public FeatureCollection getOverlappingFeatures() {
		computeOverlaps();
		return overlappingFC;
	}

	public FeatureCollection getOverlapIndicators() {
		computeOverlaps();
		return overlapIndicatorFC;
	}

	public FeatureCollection getOverlapSizeIndicators() {
		computeOverlaps();
		return overlapSizeIndicatorFC;
	}

	private FeatureCollection getSubjectFC() {
		if (fence == null)
			return inputFC;
		List fenceFeat = inputFC.query(fence);
		return new FeatureDataset(fenceFeat, inputFC.getFeatureSchema());
	}

	public void computeOverlaps() {
		monitor.startTask("Computing overlaps");
		if (isComputed)
			return;
		int featuresProcessed = 0;
		int totalFeatures = inputFC.size();
		FeatureCollection subjectFC = getSubjectFC();
		FeatureCollection indexFC = new IndexedFeatureCollection(subjectFC);
		int k = 0;
		for (Iterator i = inputFC.iterator(); i.hasNext();) {

			if (k / 100 == k / 100.0) {
				if (monitor.isCancelled()) {
					break;
				} else {
					monitor.progressTo((int) (100 * k / totalFeatures));
				}
			}

			Feature f = (Feature) i.next();
			featuresProcessed++;
			List closeFeat = indexFC.query(f.getGeometry()
					.getEnvelopeInternal());
			// monitor.report(featuresProcessed, totalFeatures, FEATURES);
			for (Iterator j = closeFeat.iterator(); j.hasNext();) {
				Feature closeF = (Feature) j.next();
				/**
				 * Since the overlaps relation is symmetric, we can avoid
				 * redundantly comparing each pair of features twice if we only
				 * compare the smaller ID to the larger. This also avoids
				 * comparing features with themselves.
				 *
				 * We can't actually use the OGC overlaps predicate, since it is
				 * false if one geometry is wholely contained in the other.
				 * Instead, we check for the interiors intersecting using
				 * relate().
				 */
				if (f.getID() < closeF.getID()) {
					IntersectionMatrix im = f.getGeometry().relate(
							closeF.getGeometry());
					boolean interiorsIntersect = im.get(Location.INTERIOR,
							Location.INTERIOR) >= 0;
					if (interiorsIntersect) {
						overlappingFeatures.add(f);
						overlappingFeatures.add(closeF);

						addIndicators(f, closeF);
					}
				}
			}
		}
		monitor.endTask();
		overlappingFC = new FeatureDataset(overlappingFeatures, inputFC
				.getFeatureSchema());
		overlapIndicatorFC = FeatureDatasetFactory
				.createFromGeometry(overlapIndicators);
		overlapSizeIndicatorFC = FeatureDatasetFactory
				.createFromGeometryWithLength(overlapSizeIndicators, "LENGTH");

		isComputed = true;
	}

	/**
	 * Computes indicator for a pair of overlapping geometries. Tries using
	 * {@link OverlapBoundaryIndicators} first; if it can't compute indicators
	 * (because of a robustness failure or a linear collapse) uses the slower
	 * but more robust {@link OverlapSegmentIndicators}
	 *
	 * @param f0
	 * @param f1
	 */
	private void addIndicators(Feature f0, Feature f1) {
		List overlapIndList;
		List overlapSizeIndList;

		OverlapBoundaryIndicators obi = new OverlapBoundaryIndicators(f0
				.getGeometry(), f1.getGeometry());
		overlapIndList = obi.getOverlapIndicators();
		overlapSizeIndList = obi.getSizeIndicators();
		if (overlapIndList.size() > 0 && overlapSizeIndList.size() > 0) {
			overlapIndicators.addAll(overlapIndList);
			overlapSizeIndicators.addAll(overlapSizeIndList);
			return;
		}

		OverlapSegmentIndicators osi = new OverlapSegmentIndicators(f0
				.getGeometry(), f1.getGeometry());
		overlapIndList = osi.getOverlapIndicators();
		overlapSizeIndList = osi.getSizeIndicators();
		// as long as there is at least one indicator computed, use the segment
		// indicators
		// (there should always be segment indicators, even if there is no size
		// indicator)
		if (overlapIndList.size() > 0 || overlapSizeIndList.size() > 0) {
			overlapIndicators.addAll(overlapIndList);
			overlapSizeIndicators.addAll(overlapSizeIndList);
			return;
		}
		// no indicators were computed - print a warning
		System.out.println("Warning - Could not compute overlap indicators");
		System.out.println(f0.getGeometry());
		System.out.println(f1.getGeometry());
	}
}
