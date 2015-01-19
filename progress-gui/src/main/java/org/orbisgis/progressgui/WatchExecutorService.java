/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
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
@Component(immediate = true)
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

    private static Class<?> getAppContext() throws ClassNotFoundException {
        return WatchExecutorService.class.getClassLoader().loadClass("sun.awt.AppContext");
    }

    @Activate
    public void activate() {
        try {
            // Register This as default ThreadPool for all SwingWorker
            getAppContext().getDeclaredMethod("put").invoke(null, SwingWorker.class, this);
        } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex) {
            // Ignore
        }
    }

    @Deactivate
    public void deactivate() {
        try {
            getAppContext().getDeclaredMethod("put").invoke(null, SwingWorker.class, null);
        } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex) {
            // Ignore
        }
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

