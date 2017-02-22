/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.commons.progress;

import org.junit.Test;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static org.junit.Assert.assertEquals;

public class ProgressMonitorTest {

    @Test
    public void testUsage() throws Exception {
        ProgressMonitor pm = new RootProgressMonitor("open file", 200);
        assertEquals(0, pm.getCurrentProgress());
        assertEquals(0, pm.getOverallProgress(),1e-12);
        assertEquals(200, pm.getEnd());
        for (int i = 0; i < 200; i++) {
            pm.endTask();
            assertEquals(i + 1, pm.getCurrentProgress());
            assertEquals((i + 1) / 200., pm.getOverallProgress(), 1e-12);
        }
        assertEquals(200, pm.getCurrentProgress());
    }

    @Test
    public void testSubTask() throws Exception {
        ProgressMonitor pm = new RootProgressMonitor("loops", 200);
        for(int i=0; i < 200; i++) {
            ProgressMonitor subProcess = pm.startTask(5);
            assertEquals(0, subProcess.getCurrentProgress());
            for(int j=0; j< 5; j++) {
                subProcess.endTask();
                assertEquals(j + 1, subProcess.getCurrentProgress());
                assertEquals((i / 200.) + ((j + 1) / (5. * 200)), subProcess.getOverallProgress(), 1e-12);
            }
            assertEquals(5, subProcess.getCurrentProgress());
        }
        assertEquals(1, pm.getOverallProgress(), 1e-12);
    }

    @Test
    public void testListeners() throws Exception {
        ProgressMonitor pm = new RootProgressMonitor("open file", 100);
        ProgressListener pl = new ProgressListener();
        CancelListener cl = new CancelListener();
        TaskListener tl = new TaskListener();
        pm.addPropertyChangeListener(ProgressMonitor.PROP_PROGRESSION, pl);
        pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cl);
        pm.addPropertyChangeListener(ProgressMonitor.PROP_TASKNAME, tl);
        for(int i=0; i < 100; i++) {
            assertEquals(i / 100., pl.lastSeenProgress, 1e-12);
            pm.endTask();
            assertEquals((i+1) / 100., pl.lastSeenProgress, 1e-12);
        }
        assertEquals(false, cl.canceled);
        pm.setCancelled(true);
        assertEquals(true, cl.canceled);
        pm.setTaskName("hello");
        assertEquals("hello", tl.taskName);
    }

    @Test
    public void regressionTest698() {
        ProgressMonitor pm = new RootProgressMonitor("open file", 0);
        // this should not throw any error (used to produce a /0 exception)
        pm.progressTo(100);
    }

    private static class ProgressListener implements PropertyChangeListener {
        double lastSeenProgress = 0;

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            lastSeenProgress = (Double)propertyChangeEvent.getNewValue();
        }
    }

    private static class CancelListener implements PropertyChangeListener {
        boolean canceled = false;

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            canceled = (Boolean)propertyChangeEvent.getNewValue();
        }
    }

    private static class TaskListener implements PropertyChangeListener {
        String taskName = "";

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            taskName = (String)propertyChangeEvent.getNewValue();
        }
    }
}
