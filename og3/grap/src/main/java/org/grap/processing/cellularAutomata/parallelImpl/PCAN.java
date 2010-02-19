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
package org.grap.processing.cellularAutomata.parallelImpl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class PCAN extends ACAN {
	private final static int NUMBER_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();

	private CyclicBarrier barrier;

	private int iterationsCount;

	private BreakCondition breakCondition;

	/* constructor */
	public PCAN(final ICA ca) {
		super(ca);

		breakCondition = new BreakCondition(NUMBER_OF_THREADS);
		// This barrier action is useful for updating shared-state before
		// any of the parties continue.
		barrier = new CyclicBarrier(NUMBER_OF_THREADS + 1, breakCondition);
	}

	/* getters */
	public int getIterationsCount() {
		return iterationsCount;
	}

	public BreakCondition getBreakCondition() {
		return breakCondition;
	}

	/* public methods */
	public int getStableState() {
		final long startTime = System.currentTimeMillis();
		final int subDomainSize = getNbCells() / NUMBER_OF_THREADS;

		// initialize
		if (getCa() instanceof ICAShort) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? getNbCells()
						: startIdx + subDomainSize;
				new Thread(new PCANShort(this, startIdx, endIdx, i)).start();
			}
		} else if (getCa() instanceof ICAFloat) {
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				final int startIdx = i * subDomainSize;
				final int endIdx = (NUMBER_OF_THREADS == i + 1) ? getNbCells()
						: startIdx + subDomainSize;
				new Thread(new PCANFloat(this, startIdx, endIdx, i)).start();
			}
		}
		// initialize
		synchronization();
		System.err.printf("end of initialization\n");

		// get stable state
		iterationsCount = 0;
		do {
			final long startT = System.currentTimeMillis();
			synchronization();
			System.err.printf("Par. Step %d : %d ms\n", iterationsCount, System
					.currentTimeMillis()
					- startT);
			iterationsCount++;
		} while (breakCondition.doIContinue());

		System.err.printf("Total duration : %d ms\n", System
				.currentTimeMillis()
				- startTime);
		return iterationsCount;
	}

	public void synchronization() {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}