package org.orbisgis.views.geocognition.sync.editor.text.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

public class Diff {
	// Source arrays
	private Object[] a, b;

	// List of differences
	protected ArrayList<Difference> diffs = new ArrayList<Difference>();

	// Pending, uncommited difference
	private Difference pending;

	// Thresholds
	private TreeMap<Integer, Integer> thresh;

	/**
	 * Constructs the Diff object for the two arrays
	 */
	Diff(Object[] a, Object[] b) {
		if (a.length < 1 || b.length < 1) {
			throw new RuntimeException("Cannot compare empty contents");
		}

		this.a = a;
		this.b = b;
		this.thresh = null;
	}

	/**
	 * Runs diff and returns the results
	 */
	ArrayList<Difference> diff() {
		int[] matches = getLongestCommonSubsequences();

		int lastA = a.length - 1;
		int lastB = b.length - 1;
		int bi = 0;
		int ai;

		for (ai = 0; ai <= matches.length - 1; ++ai) {
			int bLine = matches[ai];

			if (bLine == -1) {
				onANotB(ai, bi);
			} else {
				while (bi < bLine) {
					onBNotA(ai, bi++);
				}

				// on match
				bi++;
				if (pending != null) {
					diffs.add(pending);
					pending = null;
				}
			}
		}

		while (ai <= lastA || bi <= lastB) {
			// last A?
			if (ai == lastA + 1 && bi <= lastB) {
				while (bi <= lastB) {
					onBNotA(ai, bi++);
				}
			}

			// last B?
			if (bi == lastB + 1 && ai <= lastA) {
				while (ai <= lastA) {
					onANotB(ai++, bi);
				}
			}

			if (ai <= lastA) {
				onANotB(ai++, bi);
			}

			if (bi <= lastB) {
				onBNotA(ai, bi++);
			}
		}

		// add the last difference, if pending
		if (pending != null) {
			diffs.add(pending);
		}

		return diffs;
	}

	/**
	 * Invoked for elements in <code>a</code> and not in <code>b</code>.
	 */
	private void onANotB(int ai, int bi) {
		if (pending == null) {
			pending = new Difference(ai, ai, bi, -1);
		} else {
			pending.setDeleted(ai);
		}
	}

	/**
	 * Invoked for elements in <code>b</code> and not in <code>a</code>.
	 */
	private void onBNotA(int ai, int bi) {
		if (pending == null) {
			pending = new Difference(ai, -1, bi, bi);
		} else {
			pending.setAdded(bi);
		}
	}

	/**
	 * Returns an array of the longest common subsequences.
	 */
	private int[] getLongestCommonSubsequences() {
		int aStart = 0;
		int aEnd = a.length - 1;

		int bStart = 0;
		int bEnd = b.length - 1;

		TreeMap<Integer, Integer> matches = new TreeMap<Integer, Integer>();

		while (aStart <= aEnd && bStart <= bEnd && a[aStart].equals(b[bStart])) {
			matches.put(aStart++, bStart++);
		}

		while (aStart <= aEnd && bStart <= bEnd && a[aEnd].equals(b[bEnd])) {
			matches.put(aEnd--, bEnd--);
		}

		Map<Object, ArrayList<Integer>> bMatches = null;
		if (a[0] instanceof Comparable) {
			// this uses the Comparable interface
			bMatches = new TreeMap<Object, ArrayList<Integer>>();
		} else {
			// this just uses hashCode()
			bMatches = new HashMap<Object, ArrayList<Integer>>();
		}

		for (int bi = bStart; bi <= bEnd; ++bi) {
			Object element = b[bi];
			Object key = element;
			ArrayList<Integer> positions = bMatches.get(key);
			if (positions == null) {
				positions = new ArrayList<Integer>();
				bMatches.put(key, positions);
			}
			positions.add(bi);
		}

		thresh = new TreeMap<Integer, Integer>();
		Map<Integer, Object[]> links = new HashMap<Integer, Object[]>();

		for (int i = aStart; i <= aEnd; ++i) {
			Object aElement = a[i]; // keygen here.
			ArrayList<Integer> positions = bMatches.get(aElement);

			if (positions != null) {
				int k = 0;
				ListIterator<Integer> iterator = positions
						.listIterator(positions.size());
				while (iterator.hasPrevious()) {
					int j = iterator.previous();

					k = insert(j, k);

					if (k != -1) {
						Object value = k > 0 ? links.get(k - 1) : null;
						links.put(k, new Object[] { value, i, j });
					}
				}
			}
		}

		if (thresh.size() > 0) {
			Integer ti = thresh.lastKey();
			Object[] link = links.get(ti);
			while (link != null) {
				Integer x = (Integer) link[1];
				Integer y = (Integer) link[2];
				matches.put(x, y);
				link = (Object[]) link[0];
			}
		}

		// Populate returning array
		int size = (matches.size() == 0) ? 0 : matches.lastKey() + 1;
		int[] array = new int[size];

		for (int i = 0; i < array.length; i++) {
			if (matches.containsKey(i)) {
				array[i] = matches.get(i);
			} else {
				array[i] = -1;
			}
		}

		return array;
	}

	/**
	 * Inserts the given values into the threshold map.
	 */
	private int insert(int j, int k) {
		boolean greaterThan = thresh.get(k) != null && thresh.get(k) > j;
		boolean lessThan = thresh.get(k - 1) != null && thresh.get(k - 1) < j;
		if (k != 0 && greaterThan && lessThan) {
			thresh.put(k, j);
		} else {
			int hi = -1;

			if (k != 0) {
				hi = k;
			} else if (thresh.size() > 0) {
				hi = thresh.lastKey();
			}

			// off the end?
			if (hi == -1 || j > thresh.get(thresh.lastKey())) {
				if (thresh.size() == 0) {
					thresh.put(0, j);
				} else {
					thresh.put(thresh.lastKey() + 1, j);
				}
				k = hi + 1;
			} else {
				// binary search for insertion point:
				int lo = 0;

				while (lo <= hi) {
					int index = (hi + lo) / 2;
					int val = thresh.get(index);

					if (j == val) {
						return -1;
					} else if (j > val) {
						lo = index + 1;
					} else {
						hi = index - 1;
					}
				}

				thresh.put(lo, j);
				k = lo;
			}
		}

		return k;
	}
}
