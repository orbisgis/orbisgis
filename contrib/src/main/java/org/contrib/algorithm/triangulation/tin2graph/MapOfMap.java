package org.contrib.algorithm.triangulation.tin2graph;

import java.util.HashMap;
import java.util.Map;

public class MapOfMap {
	private Map<Integer, HashMap<Integer, Integer>> bag;

	public MapOfMap() {
		bag = new HashMap<Integer, HashMap<Integer, Integer>>();
	}

	public boolean containsKeys(final Integer key1, final Integer key2) {
		return (null != get(key1, key2));
	}

	public Integer get(final Integer key1, final Integer key2) {
		int minKey;
		int maxKey;

		if (key1 < key2) {
			minKey = key1;
			maxKey = key2;
		} else {
			minKey = key2;
			maxKey = key1;
		}

		if (bag.containsKey(minKey)) {
			return bag.get(minKey).get(maxKey);
		}
		return null;
	}

	public Integer put(final Integer key1, final Integer key2,
			final Integer value) {
		int minKey;
		int maxKey;

		if (key1 < key2) {
			minKey = key1;
			maxKey = key2;
		} else {
			minKey = key2;
			maxKey = key1;
		}

		if (bag.containsKey(minKey)) {
			return bag.get(minKey).put(maxKey, value);
		} else {
			final HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
			tmp.put(maxKey, value);
			bag.put(minKey, tmp);
			return value;
		}
	}
}