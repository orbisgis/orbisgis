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
package org.orbisgis.corejdbc.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This thread guaranty that the connection,ResultSet is released when no longer used.
 * @author Nicolas Fortin
 */
public class ResultSetHolder implements Runnable,AutoCloseable {
    public static final int WAITING_FOR_RESULTSET = 1;
    private static final int SLEEP_TIME = 1;
    private static final int RESULT_SET_TIMEOUT = 60000;
    public enum STATUS { NEVER_STARTED, STARTED , READY, REFRESH,CLOSING, CLOSED, EXCEPTION}
    private Exception ex;
    private ResultSet resultSet;
    private DataSource dataSource;

    private final AtomicReference<STATUS> status = new AtomicReference<>(STATUS.NEVER_STARTED);
    private AtomicLong lastUsage;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetHolder.class);
    private int openCount = 0;
    private Statement cancelStatement;
    private ResultSetProvider resultSetProvider;
    private Thread resultSetThread;
    private long averageTimeMs;

    public ResultSetHolder(DataSource dataSource, ResultSetProvider resultSetProvider) {
        this.dataSource = dataSource;
        this.resultSetProvider = resultSetProvider;
    }

    public void setCancelStatement(Statement cancelStatement) {
        this.cancelStatement = cancelStatement;
    }

    @Override
    public void run() {
        lastUsage = new AtomicLong(System.currentTimeMillis());
        threadChangeStatus(STATUS.STARTED);
        try (Connection connection = dataSource.getConnection()) {
            // PostGreSQL use cursor only if auto commit is false
            averageTimeMs =  System.currentTimeMillis();
            do {
                try (ResultSet activeResultSet = resultSetProvider.getResultSet(connection)) {
                    resultSet = activeResultSet;
                    threadChangeStatus(STATUS.READY);
                    while (status.get() != STATUS.REFRESH &&
                            (lastUsage.get() + RESULT_SET_TIMEOUT > averageTimeMs  || openCount != 0)) {
                        Thread.sleep(SLEEP_TIME);
                        averageTimeMs += SLEEP_TIME;
                    }
                }
            } while (status.get() == STATUS.REFRESH);
        } catch(InterruptedException ex) {
            // Ignore
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            this.ex = ex;
            threadChangeStatus(STATUS.EXCEPTION);
        } finally {
            if (status.get() != STATUS.EXCEPTION) {
                threadChangeStatus(STATUS.CLOSED);
            }
        }
    }

    private void threadChangeStatus(STATUS newStatus) {
        if(resultSetThread.equals(Thread.currentThread())) {
            status.set(newStatus);
        } else {
            throw new IllegalStateException();
        }
    }

    public void refresh() throws SQLException {
        // Wait for ready status
        try(Resource res = getResource()) {
            status.set(STATUS.REFRESH);
        }
    }

    public boolean isRunning() {
        return !(resultSetThread == null || !resultSetThread.isAlive() ||
                status.get() == ResultSetHolder.STATUS.CLOSED);
    }

    @Override
    public void close() throws SQLException {
        lastUsage.set(0);
        openCount = 0;
        status.set(STATUS.CLOSING);
        resultSetThread.interrupt();
    }

    public void delayedClose(int milliSec) {
        lastUsage.set(averageTimeMs - RESULT_SET_TIMEOUT + milliSec);
        openCount = 0;
    }

    /**
     * {@link java.sql.Statement#cancel()}
     * @throws SQLException
     */
    public void cancel() throws SQLException {
        Statement cancelObj = cancelStatement;
        if(cancelObj != null && !cancelObj.isClosed()) {
            cancelObj.cancel();
        }
        resultSetThread.interrupt();
    }

    /**
     * @return ResultSet status
     */
    public AtomicReference<STATUS> getStatus() {
        return status;
    }

    public Resource getResource() throws SQLException {
        // Wait execution of request
        while(status.get() != STATUS.READY) {
            // Reactivate result set if necessary
            if(!isRunning()) {
                // Wait for other thread full termination
                while(resultSetThread != null && resultSetThread.isAlive()) {
                    try {
                        Thread.sleep(WAITING_FOR_RESULTSET);
                    } catch (InterruptedException e) {
                        throw new SQLException(e);
                    }
                }
                resultSetThread = new Thread(this, "ResultSet");
                resultSetThread.start();
            }
            if(status.get() == STATUS.EXCEPTION) {
                if(ex instanceof SQLException) {
                    throw (SQLException)ex;
                } else {
                    throw new SQLException(ex);
                }
            }
            try {
                Thread.sleep(WAITING_FOR_RESULTSET);
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
        }
        openCount++;
        return new Resource(this, resultSet);
    }
    /**
     * Even if the timer should close the result set, the connection is not closed
     */
    public void onResourceClosed() {
        openCount = Math.max(0, openCount-1);
    }
}