package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.gdms.data.values.Value;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.renderer.legend.carto.ClassifiedLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;

public abstract class ClassifiedLegendTableModel extends AbstractTableModel
		implements TableModel {

	private ClassifiedLegend classifiedLegend = LegendFactory.createUniqueValueLegend();
	private boolean ordered = false;
	private Integer[] valueIndex;
	private boolean showRestOfValues;

	public void deleteRows(int[] rows) {
		Arrays.sort(rows);
		for (int i = rows.length - 1; i >= 0; i--) {
			int row = rows[i];
			if (ordered) {
				row = getValueIndex(row);
			}
			if (row == classifiedLegend.getClassificationCount()) {
				JOptionPane.showMessageDialog(null,
						"Cannot delete 'Rest of values'", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				classifiedLegend.removeClassification(row);
			}
		}
		invalidateOrder();
		fireTableStructureChanged();
	}

	public int getRowCount() {
		int ret = classifiedLegend.getClassificationCount();
		if (showRestOfValues) {
			ret++;
		}
		return ret;
	}

	public void setOrdered(boolean selected) {
		ordered = selected;
		invalidateOrder();
	}

	protected int getSortedIndex(int index) {
		if (ordered) {
			index = getValueIndex(index);
		}

		return index;
	}

	protected void invalidateOrder() {
		valueIndex = null;
		fireTableDataChanged();
	}

	private int getValueIndex(int index) {
		if (valueIndex == null) {
			TreeSet<Integer> values = new TreeSet<Integer>(
					new Comparator<Integer>() {

						public int compare(Integer i1, Integer i2) {
							Value v1 = getOrderValue(i1.intValue());
							Value v2 = getOrderValue(i2.intValue());
							try {
								if (v1.isNull())
									return -1;
								if (v2.isNull())
									return 1;
								if (v1.less(v2).getAsBoolean()) {
									return -1;
								} else if (v2.less(v1).getAsBoolean()) {
									return 1;
								}
							} catch (IncompatibleTypesException e) {
								throw new RuntimeException(e);
							}

							return -1;
						}

					});

			for (int i = 0; i < classifiedLegend.getClassificationCount(); i++) {
				values.add(new Integer(i));
			}

			valueIndex = values.toArray(new Integer[0]);
		}

		return valueIndex[index];
	}

	protected abstract Value getOrderValue(int intValue);

	public void setLegend(ClassifiedLegend legend) {
		invalidateOrder();
		fireTableDataChanged();
		this.classifiedLegend = legend;
	}

	public void setShowRestOfValues(boolean showRestOfValues) {
		this.showRestOfValues = showRestOfValues;
		if (!showRestOfValues) {
			classifiedLegend.setDefaultSymbol(null);
			classifiedLegend.setDefaultLabel(null);
		}
		fireTableDataChanged();
	}

}
