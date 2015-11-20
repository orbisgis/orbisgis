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

/**
 * This thread guaranty that the connection,ResultSet is released when no longer used.
 * @author Nicolas Fortin
 */
public class ResultSetHolder implements Runnable,AutoCloseable {
    public static final int WAITING_FOR_RESULTSET = 5;
    private static final int SLEEP_TIME = 50;
    private static final int RESULT_SET_TIMEOUT = 60000;
    public enum STATUS { NEVER_STARTED, STARTED , READY, CLOSING, CLOSED, EXCEPTION}
    private Exception ex;
    private ResultSet resultSet;
    private DataSource dataSource;
    private STATUS status = STATUS.NEVER_STARTED;
    private long lastUsage = System.currentTimeMillis();
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetHolder.class);
    private int openCount = 0;
    private Statement cancelStatement;
    private ResultSetProvider resultSetProvider;
    private Thread resultSetThread;

    public ResultSetHolder(DataSource dataSource, ResultSetProvider resultSetProvider) {
        this.dataSource = dataSource;
        this.resultSetProvider = resultSetProvider;
    }

    public void setCancelStatement(Statement cancelStatement) {
        this.cancelStatement = cancelStatement;
    }

    @Override
    public void run() {
        lastUsage = System.currentTimeMillis();
        status = STATUS.STARTED;
        try (Connection connection = dataSource.getConnection()) {
            // PostGreSQL use cursor only if auto commit is false
            try (ResultSet activeResultSet = resultSetProvider.getResultSet(connection)) {
                resultSet = activeResultSet;
                status = STATUS.READY;
                while (lastUsage + RESULT_SET_TIMEOUT > System.currentTimeMillis() || openCount != 0) {
                    Thread.sleep(SLEEP_TIME);
                }
            }
        } catch(InterruptedException ex) {
            // Ignore
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            this.ex = ex;
            status = STATUS.EXCEPTION;
        } finally {
            if (status != STATUS.EXCEPTION) {
                status = STATUS.CLOSED;
            }
        }
    }

    @Override
    public void close() throws SQLException {
        lastUsage = 0;
        openCount = 0;
        status = STATUS.CLOSING;
        resultSetThread.interrupt();
    }

    public void delayedClose(int milliSec) {
        lastUsage = System.currentTimeMillis() - RESULT_SET_TIMEOUT + milliSec;
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
    public STATUS getStatus() {
        return status;
    }

    public Resource getResource() throws SQLException {
        // Wait execution of request
        while(getStatus() != STATUS.READY) {
            // Reactivate result set if necessary
            if(resultSetThread == null || !resultSetThread.isAlive() ||
                    getStatus() == ResultSetHolder.STATUS.CLOSED ||
                    getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
                resultSetThread = new Thread(this, "ResultSet");
                resultSetThread.start();
            }
            if(status == STATUS.EXCEPTION) {
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
        lastUsage = System.currentTimeMillis();
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