/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.pluginSystem.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionUtil {
	public CollectionUtil() {
	}

	public static Collection concatenate(Collection a, Collection b) {
		ArrayList result = new ArrayList();
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	public static List list(Object a, Object b) {
		ArrayList list = new ArrayList();
		list.add(a);
		list.add(b);
		return list;
	}

	/**
	 * Returns a List of Lists: all combinations of the elements of the given
	 * List.
	 * 
	 * @param maxCombinationSize
	 *            combinations larger than this value are discarded
	 */
	public static List combinations(List original, int maxCombinationSize) {
		return combinations(original, maxCombinationSize, null);
	}

	/*
	 * public static Map inverse(Map map) { Map inverse; try { inverse = (Map)
	 * map.getClass().newInstance(); } catch (InstantiationException e) {
	 * Assert.shouldNeverReachHere(e.toString());
	 * 
	 * return null; } catch (IllegalAccessException e) {
	 * Assert.shouldNeverReachHere(e.toString());
	 * 
	 * return null; } for (Iterator i = map.keySet().iterator(); i.hasNext();) {
	 * Object key = i.next(); Object value = map.get(key); inverse.put(value,
	 * key); }
	 * 
	 * return inverse; }
	 */

	/**
	 * Returns a List of Lists: all combinations of the elements of the given
	 * List.
	 * 
	 * @param maxCombinationSize
	 *            combinations larger than this value are discarded
	 * @param mandatoryItem
	 *            an item that all returned combinations must contain, or null
	 *            to leave unspecified
	 */
	public static List combinations(List original, int maxCombinationSize,
			Object mandatoryItem) {
		ArrayList combinations = new ArrayList();

		// Combinations are given by the bits of each binary number from 1 to
		// 2^N
		for (int i = 1; i <= ((int) Math.pow(2, original.size()) - 1); i++) {
			ArrayList combination = new ArrayList();
			for (int j = 0; j < original.size(); j++) {
				if ((i & (int) Math.pow(2, j)) > 0) {
					combination.add(original.get(j));
				}
			}
			if (combination.size() > maxCombinationSize) {
				continue;
			}
			if ((mandatoryItem != null) && !combination.contains(mandatoryItem)) {
				continue;
			}
			combinations.add(combination);
		}

		return combinations;
	}

	/**
	 * Returns a List of Lists: all combinations of the elements of the given
	 * List.
	 */
	public static List combinations(List original) {
		return combinations(original, original.size(), null);
	}

	public static void removeKeys(Collection keys, Map map) {
		for (Iterator i = keys.iterator(); i.hasNext();) {
			Object key = (Object) i.next();
			map.remove(key);
		}
	}

	/**
	 * The nth key corresponds to the nth value
	 */
	public static List[] keysAndCorrespondingValues(Map map) {
		ArrayList keys = new ArrayList(map.keySet());
		ArrayList values = new ArrayList();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			Object key = i.next();
			values.add(map.get(key));
		}

		return new List[] { keys, values };
	}

	public static Collection concatenate(Collection collections) {
		ArrayList concatenation = new ArrayList();
		for (Iterator i = collections.iterator(); i.hasNext();) {
			Collection collection = (Collection) i.next();
			concatenation.addAll(collection);
		}

		return concatenation;
	}

	public static Object randomElement(List list) {
		return list.get((int) Math.floor(Math.random() * list.size()));
	}

	public static SortedSet reverseSortedSet(int[] ints) {
		TreeSet sortedSet = new TreeSet(Collections.reverseOrder());
		for (int i = 0; i < ints.length; i++) {
			sortedSet.add(new Integer(ints[i]));
		}

		return sortedSet;
	}

	public static List reverse(List list) {
		Collections.reverse(list);

		return list;
	}

	/**
	 * Data is evenly discarded or duplicated to attain the new size
	 */
	/*
	 * public static Collection stretch(Collection source, Collection
	 * destination, int destinationSize) { Assert.isTrue(destination.isEmpty());
	 * 
	 * List originalList = source instanceof List ? (List) source : new
	 * ArrayList(source); for (int i = 0; i < destinationSize; i++) {
	 * destination.add(originalList.get( (int) Math.round( i *
	 * originalList.size() / (double) destinationSize))); }
	 * 
	 * return destination; }
	 */

	public static Object ifNotIn(Object o, Collection c, Object alternative) {
		return c.contains(o) ? o : alternative;
	}

	public static void setIfNull(int i, List list, String value) {
		if (i >= list.size()) {
			resize(list, i + 1);
		}
		if (list.get(i) != null) {
			return;
		}
		list.set(i, value);
	}

	public static void resize(List list, int newSize) {
		if (newSize < list.size()) {
			list.subList(newSize, list.size()).clear();
		} else {
			list.addAll(Collections.nCopies(newSize - list.size(), null));
		}
	}

	public static boolean containsReference(Object[] objects, Object o) {
		return indexOf(o, objects) > -1;
	}

	public static int indexOf(Object o, Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == o) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Brute force, for when HashSet and TreeSet won't work (e.g. #hashCode
	 * implementation isn't appropriate). The original Collection is not
	 * modified.
	 */
	public static Collection removeDuplicates(Collection original) {
		ArrayList result = new ArrayList();
		for (Iterator i = original.iterator(); i.hasNext();) {
			Object item = i.next();
			if (!result.contains(item)) {
				result.add(item);
			}
		}

		return result;
	}

	public static void addIfNotNull(Object item, Collection collection) {
		if (item != null) {
			collection.add(item);
		}
	}

	/**
	 * Modifies and returns the collection.
	 */
	public static Collection filterByClass(Collection collection, Class c) {
		for (Iterator i = collection.iterator(); i.hasNext();) {
			Object item = i.next();
			if (!c.isInstance(item)) {
				i.remove();
			}
		}

		return collection;
	}

	/*
	 * public static Map createMap(Object[] alternatingKeysAndValues) { return
	 * createMap(HashMap.class, alternatingKeysAndValues); }
	 */

	/*
	 * public static Map createMap(Class mapClass, Object[]
	 * alternatingKeysAndValues) { Map map = null; try { map = (Map)
	 * mapClass.newInstance(); } catch (Exception e) {
	 * Assert.shouldNeverReachHere(e.toString()); } for (int i = 0; i <
	 * alternatingKeysAndValues.length; i += 2) {
	 * map.put(alternatingKeysAndValues[i], alternatingKeysAndValues[i + 1]); }
	 * 
	 * return map; }
	 */

	/**
	 * The Smalltalk #collect method.
	 */
	/*
	 * public static Collection collect(Collection collection, Block block) {
	 * ArrayList result = new ArrayList(); for (Iterator i =
	 * collection.iterator(); i.hasNext();) { Object item = i.next();
	 * result.add(block.yield(item)); }
	 * 
	 * return result; }
	 *//**
	 * The Smalltalk #select method.
	 */
	/*
	 * public static Collection select(Collection collection, Block block) {
	 * ArrayList result = new ArrayList(); for (Iterator i =
	 * collection.iterator(); i.hasNext();) { Object item = i.next(); if
	 * (Boolean.TRUE.equals(block.yield(item))) { result.add(item); } ; }
	 * 
	 * return result; }
	 */

	public static Object get(Class c, Map map) {
		if (map.keySet().contains(c)) {
			return map.get(c);
		}
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			Class candidateClass = (Class) i.next();
			if (candidateClass.isAssignableFrom(c)) {
				return map.get(candidateClass);
			}
		}

		return null;
	}
}
