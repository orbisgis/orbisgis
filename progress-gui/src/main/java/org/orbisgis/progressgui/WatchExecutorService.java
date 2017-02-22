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
package org.orbisgis.progressgui;

import org.orbisgis.progressgui.api.SwingWorkerPool;
import org.osgi.service.component.annotations.Component;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Override ThreadPool of SwingWorker in order to fill job list.
 * @author Nicolas Fortin
 */
@Component(service = {SwingWorkerPool.class, ExecutorService.class})
public class WatchExecutorService extends ThreadPoolExecutor implements SwingWorkerPool {

    EventListenerList actionListenerList = new EventListenerList();

    /**
     * number of worker threads.
     */
    private static final int MAX_WORKER_THREADS = 10;

    public WatchExecutorService() {
        super(MAX_WORKER_THREADS, MAX_WORKER_THREADS,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                new WatchThreadFactory());
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
        ActionEvent event = new ActionEvent(command, ActionEvent.ACTION_PERFORMED, "execute");
        for(ActionListener actionListener : actionListenerList.getListeners(ActionListener.class)) {
            try {
                actionListener.actionPerformed(event);
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    /**
     * @param actionListener Fired when a new SwingWorker (source) is added
     */
    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(ActionListener.class, actionListener);
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        actionListenerList.remove(ActionListener.class, actionListener);
    }

    private static class WatchThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(final Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("SwingWorker-" + thread.getName());
            thread.setDaemon(true);
            return thread;
        }
    }

    @Override
    public void cancelAll(boolean mayInterruptIfRunning) {
        for(Runnable runnable : new ArrayList<>(getQueue())) {
            if(runnable instanceof SwingWorker) {
                ((SwingWorker) runnable).cancel(mayInterruptIfRunning);
            }
        }
    }
}

