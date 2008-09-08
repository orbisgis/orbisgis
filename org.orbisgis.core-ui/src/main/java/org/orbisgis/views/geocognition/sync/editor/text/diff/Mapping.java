package org.orbisgis.views.geocognition.sync.editor.text.diff;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mapping {

	private List<Difference> differences;
	private ArrayList<Point> map;

	// The ranges of lines with the differences
	private ArrayList<Range> originalHighlight, newHighlight;

	/**
	 * Creates a new map of lines between two files
	 * 
	 * @param originalFile
	 *            one file to map lines
	 * @param newFile
	 *            the other file to map lines
	 */
	public Mapping(String[] originalFile, String[] newFile) {
		Diff diff = new Diff(originalFile, newFile);
		differences = diff.diff();
		mapLines(differences, originalFile.length);
		highlight();
	}

	/**
	 * Map the lines between the two files
	 * 
	 * @param diffs
	 *            the differences between the files
	 * @param nLines
	 *            the number of lines of the original file
	 */
	private void mapLines(List<Difference> diffs, int nLines) {
		map = new ArrayList<Point>();

		Iterator<Difference> iterator = diffs.iterator();

		// Indexes are initialized to -1 and are always updated before add them
		// to the map. This means mapping (0, 0) is first included in the map
		int newIndex = -1;
		int originalIndex = -1;

		while (iterator.hasNext()) {
			Difference d = iterator.next();

			// Map 1 to 1 up to the next difference
			while (originalIndex < d.getDeletedStart()) {
				originalIndex++;
				newIndex++;
				map.add(new Point(originalIndex, newIndex));
			}

			if (d.getType() == Difference.ADD) {
				// If the difference is an addition, map the last mapped
				// line in the original file with all the lines in the addition
				while (newIndex <= d.getAddedEnd()) {
					newIndex++;
					map.add(new Point(originalIndex, newIndex));
				}
			} else if (d.getType() == Difference.DELETE) {
				// If the difference is a deletion, map the last mapped
				// line in the new file with all the lines in the deletion
				while (originalIndex <= d.getDeletedEnd()) {
					originalIndex++;
					map.add(new Point(originalIndex, newIndex));
				}
			} else {
				// Get the size of both chunks of the difference (+1 because the
				// indexes of the difference are both inclusive)
				int originalChunk = d.getDeletedEnd() - d.getDeletedStart() + 1;
				int newChunk = d.getAddedEnd() - d.getAddedStart() + 1;

				// y = m · x
				Double m, y;

				if (originalChunk > newChunk) {
					// Interpolation: x = originalIndex, y = newIndex
					newIndex++;
					m = new Double(newChunk / ((float) originalChunk + 1));
					y = new Double(newIndex);
					while (originalIndex <= d.getDeletedEnd()) {
						originalIndex++;
						newIndex = y.intValue();
						map.add(new Point(originalIndex, newIndex));
						y += m;
					}
				} else {
					// Interpolation: x = newIndex, y = originalIndex
					originalIndex++;
					m = new Double(originalChunk / ((float) newChunk + 1));
					y = new Double(originalIndex);
					while (newIndex <= d.getAddedEnd()) {
						newIndex++;
						originalIndex = y.intValue();
						map.add(new Point(originalIndex, newIndex));
						y += m;
					}
				}

			}
		}

		// Map lines 1 to 1 up to the end of the file
		while (originalIndex < nLines) {
			originalIndex++;
			newIndex++;
			map.add(new Point(originalIndex, newIndex));
		}
	}

	/**
	 * Computes the ranges of the lines with differences in both files
	 */
	private void highlight() {
		originalHighlight = new ArrayList<Range>();
		for (int i = 0; i < differences.size(); i++) {
			Difference d = differences.get(i);
			originalHighlight.add(d.getDeletion());
		}

		newHighlight = new ArrayList<Range>();
		for (int i = 0; i < differences.size(); i++) {
			Difference d = differences.get(i);
			newHighlight.add(d.getAddition());
		}
	}

	/**
	 * Gets the differences between the files
	 * 
	 * @return the differences
	 */
	public List<Difference> getDifferences() {
		return differences;
	}

	/**
	 * Gets the ranges of the lines with differences in the original file
	 * 
	 * @return the ranges of the lines with differences in the original file
	 */
	public ArrayList<Range> getOriginalHighlight() {
		return originalHighlight;
	}

	/**
	 * Gets the ranges of the lines with differences in the new file
	 * 
	 * @return the ranges of the lines with differences in the new file
	 */
	public ArrayList<Range> getNewHighlight() {
		return newHighlight;
	}

	/**
	 * Gets the mapping of the file lines
	 * 
	 * @return the map of the file lines
	 */
	public ArrayList<Point> getMap() {
		return map;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < map.size(); i++) {
			s += map.get(i).x + " - " + map.get(i).y + "\n";
		}
		return s;
	}

}
