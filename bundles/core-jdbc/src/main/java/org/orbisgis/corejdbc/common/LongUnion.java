package org.orbisgis.corejdbc.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;

/**
 * This class aggregates consecutive long.
 *
 * The goal is to reduce memory usage, ordering by ascending index,
 * the trade-off is the additional CPU cycle for insertion and deletion.
 * Values are returned sorted and without duplicates.
 *
 * The behaviour of this class is exactly the same as a {@link SortedSet}
 *
 * Sample :
 *
 * {@code
 *      //Writing this
 *      for(long i : new LongUnion(0,99)) {
 *      }
 *      //Is equivalent to writing
 *      for(long i=0; i<=99; i++) {
 *      }
 * }
 *
 * This class is not thread safe
 *
 * @author Nicolas Fortin
 */
public class LongUnion implements NumberUnion<Long> {
    private static final long serialVersionUID = 1L;

    // long intervals ex: 0,15,50,60 for 0 to 15 and 50 to 60    
    protected List<Long> intervals;

    /**
     * Constructor with an initial value
     * @param value First value
     */
    public LongUnion(long value) {
        this();
        intervals.add(value);
        intervals.add(value);
    }

    /**
     * Constructor with all values of the range [valueBegin-valueEnd].
     *
     * @param valueBegin Included begin of range
     * @param valueEnd Included end of range
     * @throws IllegalArgumentException if valueEnd < valueBegin
     */
    public LongUnion(long valueBegin, long valueEnd) {
        this();
        if (valueEnd < valueBegin) {
            throw new IllegalArgumentException("Begin value must be inferior or equal to the end value.");
        }
        intervals.add(valueBegin);
        intervals.add(valueEnd);
    }
    /**
     * Default constructor
     */
    public LongUnion() {
        intervals = new ArrayList<Long>();
    }

    /**
     * Creator with an initial value
     * @param externalArray Array of int
     */
    public LongUnion(long[] externalArray) {
        this();
        for(Long val : externalArray) {
            internalAdd(val);
        }
    }
    /**
     * Copy constructor
     * @param externalSet
     */
    public LongUnion(LongUnion externalSet) {
        this.intervals = new ArrayList<Long>(externalSet.intervals);
    }

    /**
     * Copy constructor with a generic collection
     * @param externalCollection
     */
    public LongUnion(Collection<Long> externalCollection) {
        this();
        if(externalCollection instanceof LongUnion) {
            copyExternalLongUnion((LongUnion)externalCollection);
        } else {
            for(Long value : externalCollection) {
                internalAdd(value);
            }
        }
    }

    /**
     * Copy constructor with a generic collection
     * @param valueIterator Value to insert in this set
     */
    public LongUnion(Iterator<Long> valueIterator) {
        this();
        while(valueIterator.hasNext()) {
            internalAdd(valueIterator.next());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LongUnion)) {
            return false;
        }
        final LongUnion other = (LongUnion) obj;
        // Intervals is never Null
        return this.intervals.equals(other.intervals);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.intervals != null ? this.intervals.hashCode() : 0);
        return hash;
    }
    private void copyExternalLongUnion(LongUnion externalSet) {
        intervals.addAll(externalSet.intervals);
    }

    /**
     * Does this container has intervals
     *
     * @return True if this container is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return intervals.isEmpty();
    }

    @Override
    public String toString() {
        if(intervals.isEmpty()) {
            return "[]";
        }
        StringBuilder ret = new StringBuilder();
        Iterator<Long> it = intervals.iterator();
        while(it.hasNext()) {
            ret.append(" [");
            ret.append(it.next());
            ret.append("-");
            ret.append(it.next()+1);
            ret.append("[");
        }
        return ret.toString();
    }
    /**
     * Remove the provided item from the Set
     * @param value
     * @return
     */
    protected final boolean internalRemove(long value) {
        int index = Collections.binarySearch(intervals, value);
        if(index>=0) {
            if(index > 0 && intervals.get(index - 1).equals(value)) {
                intervals.remove(index-1);
                intervals.remove(index-1);
            } else if(index + 1 < intervals.size() && intervals.get(index + 1).equals(value)) {
                intervals.remove(index);
                intervals.remove(index);
            } else {
                if (index % 2 == 0) {
                    intervals.set(index,value+1);
                } else {
                    intervals.set(index,value-1);
                }
            }
            return true;
        } else {
            index = -index - 1; //retrieve the insertion point
            if (index % 2 == 0) {
                //Not in the collection
                return false;
            } else {
                //Split in two ranges
                Long endValue = intervals.get(index);
                intervals.set(index, value-1);
                intervals.add(index+1,value+1);
                intervals.add(index+2,endValue);
                return true;
            }
        }
    }

    @Override
    public boolean remove(Object o) {
        Long value = (Long) o;
        return internalRemove(value);
    }

    /**
     * Add the value in this Set
     * @param value New item
     * @return True if the value is successfully inserted
     */
    protected final boolean internalAdd(long value) {
        // Iterate over the value range array and find contiguous value
        //Find the insertion polong in ranges
        int index = Collections.binarySearch(intervals, value);
        if (index >= 0) {
            return false;
        }
        index = -index - 1; //retrieve the insertion point
        // intervals[index] > value
        if (index % 2 == 0) {
            //If index corresponding to begin of a range
            boolean mergeFirst = index > 0 && intervals.get(index - 1).equals(value - 1);
            boolean mergeSecond = index < intervals.size() && intervals.get(index).equals(value + 1);
            if (mergeFirst && mergeSecond) {
                //Merge two ranges and update the end of the first range
                Long endNextRange = intervals.get(index + 1);
                intervals.remove(index);
                intervals.remove(index);
                intervals.set(index - 1, endNextRange);
                return true;
            } else if (mergeFirst) {
                //Replace the value (merge to the previous range)
                intervals.set(index - 1, value);
                return true;
            } else if (mergeSecond) {
                //Replace the value (merge to the next range)
                intervals.set(index, value);
                return true;
            }
        } else {
            //If index corresponding to the end of a range
            //the provided value is in a range
            return false;
        }
        //New range
        intervals.add(index, new Long(value));
        intervals.add(index,  new Long(value));
        return true;
    }

    @Override
    public boolean add(Long value) {
        return internalAdd(value);
    }

    @Override
    public Iterator<Long> iterator() {
        return listIterator();
    }

    /**
     * Return the internal container
     *
     * @return intervals ex: 0,0,50,60 for [0] and [50-60]
     */
    public List<Long> getValueRanges() {
        return Collections.unmodifiableList(intervals);
    }

    @Override
    public Comparator<? super Long> comparator() {
        return new LongComparator();
    }

    @Override
    public SortedSet<Long> subSet(Long e, Long e1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SortedSet<Long> headSet(Long e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SortedSet<Long> tailSet(Long e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long first() {
        return intervals.get(0);
    }

    @Override
    public Long last() {
        return intervals.get(intervals.size()-1);
    }

    @Override
    public int size() {
        Iterator<Long> it = intervals.iterator();
        int count=0;
        while(it.hasNext()) {
            count+=-it.next()+(it.next()+1);
        }
        return count;
    }

    @Override
    public boolean contains(Object o) {
        Long value = (Long)o;
        long index = Collections.binarySearch(intervals,value );
        if(index>=0) {
            return true;
        } else {
            //retrieve the insertion point
            index = -index - 1;
            //value < than an end range
            return index % 2 != 0;
        }

    }

    @Override
    public Object[] toArray() {
        long arraySize = size();
        Object[] values = new Object[size()];
        Iterator<Long> it = iterator();
        for(int i = 0; i < arraySize; i++) {
            values[i]=it.next();
        }
        return values;
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends Long> clctn) {
        boolean isAllAdded=true;
        for (Iterator<? extends Long> it = clctn.iterator(); it.hasNext();) {
            Long value = it.next();
            if(!add(value)) {
                isAllAdded = false;
            }
        }
        return isAllAdded;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        intervals.clear();
    }

    /**
     * @return Two direction iterator
     */
    public ListIterator<Long> listIterator() {
        return new ValueListIterator(intervals.listIterator());
    }

    private static class ValueListIterator implements ListIterator<Long>  {
        protected long current = -1;
        protected long itEnd = 0;
        // Interval list iterator
        private ListIterator<Long> it;
        private long itBegin = -1;
        private boolean lastNext = true;

        public ValueListIterator(ListIterator<Long> listIterator) {
            this.it = listIterator;
        }

        @Override
        public boolean hasNext() {
            return current + 1 < itEnd || it.hasNext();
        }

        @Override
        public Long next() {
            lastNext = true;
            if (current + 1 < itEnd) {
                return ++current;
            } else {
                current = it.next();
                itBegin = current;
                itEnd = it.next() + 1;
                return current;
            }
        }

        @Override
        public boolean hasPrevious() {
            return current > itBegin || it.hasPrevious();
        }

        @Override
        public Long previous() {
            if(current > itBegin && !lastNext) {
                return --current;
            } else {
                if(lastNext) {
                    lastNext = false;
                    it.previous();
                    it.previous();
                    return current;
                } else {
                    itEnd = it.previous() + 1;
                    itBegin = it.previous();
                    current = itEnd - 1;
                }
                return current;
            }
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void set(Long Long) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(Long Long) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("It won't be supported because it's not mandatory.");
        }
    }

    /**
     * This class converts an interval iterator into a serial iterator.
     * [0,2,5,7] becomes [0,1,2,5,6,7]
     */
    private static class LongComparator implements Comparator<Long> {

        @Override
        public int compare(Long t, Long t1) {
            return (t<t1 ? -1 : (t.equals(t1) ? 0 : 1));
        }

    }
}