package org.orbisgis.views.geocognition.sync.editor.text.diff;

/**
 * Represents a difference, as used in <code>Diff</code>. A difference
 * consists of two pairs of starting and ending points, each pair representing
 * either the "from" or the "to" collection passed to <code>Diff</code>. If
 * an ending point is -1, then the difference was either a deletion or an
 * addition. For example, if <code>getDeletedEnd()</code> returns -1, then the
 * difference represents an addition.
 */
public class Difference {
	public static final int NONE = -1;
	public static final char ADD = 'a';
	public static final char DELETE = 'd';
	public static final char CHANGE = 'c';

	private Range deleted, added;

	/**
	 * Creates the difference for the given start and end points for the
	 * original and new.
	 */
	public Difference(int delStart, int deEnd, int addStart, int addEnd) {
		deleted = new Range(delStart, deEnd);
		added = new Range(addStart, addEnd);
	}

	/**
	 * The point at which the deletion starts, if any. A value equal to
	 * <code>NONE</code> means this is an addition.
	 */
	int getDeletedStart() {
		return deleted.getStart();
	}

	/**
	 * The point at which the deletion ends, if any. A value equal to
	 * <code>NONE</code> means this is an addition.
	 */
	int getDeletedEnd() {
		return deleted.getEnd();
	}

	/**
	 * The point at which the addition starts, if any. A value equal to
	 * <code>NONE</code> means this must be an addition.
	 */
	int getAddedStart() {
		return added.getStart();
	}

	/**
	 * The point at which the addition ends, if any. A value equal to
	 * <code>NONE</code> means this must be an addition.
	 */
	int getAddedEnd() {
		return added.getEnd();
	}

	/**
	 * Sets the point as deleted. The start and end points will be modified to
	 * include the given line.
	 */
	void setDeleted(int line) {
		deleted.setStart(Math.min(line, getDeletedStart()));
		deleted.setEnd(Math.max(line, getDeletedEnd()));
	}

	/**
	 * Sets the point as added. The start and end points will be modified to
	 * include the given line.
	 */
	void setAdded(int line) {
		added.setStart(Math.min(line, getAddedStart()));
		added.setEnd(Math.max(line, getAddedEnd()));
	}

	/**
	 * Gets the type of the difference (ADD, DELETE or CHANGE)
	 * 
	 * @return the type
	 */
	char getType() {
		char type;
		if (getDeletedEnd() == -1) {
			type = 'a';
		} else if (getAddedEnd() == -1) {
			type = 'd';
		} else {
			type = 'c';
		}

		return type;
	}

	/**
	 * Gets the range of the deletion
	 * 
	 * @return the range of the deletion
	 */
	public Range getDeletion() {
		return deleted;
	}

	/**
	 * Gets the range of the addition
	 * 
	 * @return the range of the addition
	 */
	public Range getAddition() {
		return added;
	}

	/**
	 * Compares this object to the other for equality. Both objects must be of
	 * type Difference, with the same starting and ending points.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Difference) {
			Difference other = (Difference) obj;

			return (getDeletedStart() == other.getDeletedStart()
					&& getDeletedEnd() == other.getDeletedEnd()
					&& getAddedStart() == other.getAddedStart() && getAddedEnd() == other
					.getAddedEnd());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return deleted.hashCode() + added.hashCode();
	}
	/**
	 * Returns a string representation of this difference.
	 */

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("del: [" + getDeletedStart() + ", " + getDeletedEnd() + "]");
		buf.append(" ");
		buf.append("add: [" + getAddedStart() + ", " + getAddedEnd() + "]");
		return buf.toString();
	}
}
