/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */ 
package org.orbisgis.view.joblist;


import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.orbisgis.core.Services;
import org.orbisgis.view.background.BackgroundListener;
import org.orbisgis.view.background.BackgroundManager;

/**
 * @brief JList model of the Job list
 */
public class JobListModel extends AbstractListModel {
        private List<JobListItem> shownJobs = new ArrayList<JobListItem>();
        private AtomicBoolean awaitingRefresh=new AtomicBoolean(false); 
        /*!< If true a swing runnable is pending to refresh the content of
          the JobListModel
         */
        
        /**
         * Attach listeners to the BackgroundManager
         */
        public void listenToBackgroundManager() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.addBackgroundListener(EventHandler.create(BackgroundListener.class,this,"onJobListChange"));
        }
        
        @Override
        public int getSize() {
                return shownJobs.size();
        }

        /**
         * JobList model need to be updated
         */
        public void onJobListChange() {
                if(!awaitingRefresh.getAndSet(true)) {
                        SwingUtilities.invokeLater(new ReadJobListOnSwingThread());
                }
        }
        /**
         * Update the job list
         * @warning called only by ReadJobListOnSwingThread
         */
        private void updateJobList() {
                
        }
        
        
        @Override
        public Object getElementAt(int i) {
                return shownJobs.get(i);
        }
        
        private class ReadJobListOnSwingThread  implements Runnable {

                @Override
                public void run() {
                        awaitingRefresh.set(false);
                        updateJobList();
                }
                
        }
}
