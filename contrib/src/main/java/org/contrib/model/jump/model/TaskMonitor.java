
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

package org.contrib.model.jump.model;


/**
 * Provides a simple interface between an operation (or "task") and the
 * application in which it executes. Enables the task to report its progress,
 * and to check whether the application has requested that it be cancelled.
 */
public interface TaskMonitor {
    /**
     * Describes the status of the task.
     * @param description a description of the progress of the overall task
     */
    public void report(String description);

    /**
     * Reports the number of items processed.
     * @param itemsDone the number of items that have been processed
     * @param totalItems the total number of items being processed, or -1 if the
     * total number is not known
     * @param itemDescription a one-word description of the items, such as "features"
     */
    public void report(int itemsDone, int totalItems, String itemDescription);

    /**
     * Reports an Exception that occurred. The task may choose to carry on.
     * @param exception an Exception that occurred during the execution of the task.
     */
    public void report(Exception exception);

    /**
     * Notifies parties that the task will accept requests for cancellation
     * (though the task is not obligated to cancel immediately, or at all
     * for that matter).
     */
    public void allowCancellationRequests();

    /**
     * Checks whether a party has requested that the task be cancelled. However,
     * the task is not obligated to cancel immediately (or at all).
     * @return whether a party has requested that the task be cancelled
     */
    public boolean isCancelRequested();
}
