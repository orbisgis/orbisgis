package org.orbisgis;


import org.orbisgis.ProgressMonitor;

import junit.framework.TestCase;

public class ProgressMonitorTest extends TestCase {

	public void testUsage() throws Exception {
		ProgressMonitor pm = new ProgressMonitor("open file");
		pm.startTask("read header", 50);
		System.out.println(pm);
		for (int i = 0; i < 100; i++) {
			pm.progressTo(i);
			System.out.println(pm);
		}
		pm.endTask();
		System.out.println(pm);
		pm.startTask("index file", 20);
		pm.startTask("select index", 50);
		System.out.println(pm);
		pm.endTask();
		System.out.println(pm);
		pm.endTask();
		System.out.println(pm);
		pm.progressTo(80);
		System.out.println(pm);
		pm.endTask();
		System.out.println(pm);
	}

}
