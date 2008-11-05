package org.orbisgis.editors.table.actions;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

public class SelectEqual implements ITableCellAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void execute(final DataSource dataSource, final Selection selection,
			final int rowIndex, final int columnIndex) {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				try {
					ArrayList<Integer> newSel = new ArrayList<Integer>();
					Value ref = dataSource.getFieldValue(rowIndex, columnIndex);
					for (int i = 0; i < dataSource.getRowCount(); i++) {
						if (dataSource.getFieldValue(i, columnIndex)
								.equals(ref).getAsBoolean()) {
							newSel.add(i);
						}
					}
					int[] sel = new int[newSel.size()];
					for (int i = 0; i < sel.length; i++) {
						sel[i] = newSel.get(i);
					}

					selection.setSelection(sel);
				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot read source", e);
				}
			}

			@Override
			public String getTaskName() {
				return "finding matches";
			}
		});
	}

}
