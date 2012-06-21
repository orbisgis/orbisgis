/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.multiInputPanel.InputType;
import org.orbisgis.core.sif.multiInputPanel.IntType;
import org.orbisgis.utils.I18N;

public class WorkspacePanel extends JPanel implements ItemListener {

	private IntType timer;

	private static final int MAX_DIGITS = 5;
	private static final String TIMER_LABEL = I18N
			.getString("orbisgis.org.orbisgis.configuration.workspace.timerMinutes");
	private static final String TIMER_GROUP = I18N
			.getString("orbisgis.ui.menu.file.text.saveWorkspace");
	private static final String TIMER_CHECK = I18N
			.getString("orbisgis.org.core.enableSaving");

	private JCheckBox authCheck;

	public WorkspacePanel(String timerValue) {
		timer = new IntType(MAX_DIGITS);
		timer.setValue(timerValue);
	}

	protected void init() {
		authCheck = new JCheckBox();
		authCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (authCheck.isSelected())
					timer.setEditable(true);
				else {
					timer.setEditable(false);
					timer.setValue(null);
				}
			}
		});

		String[] timerLabels = { TIMER_LABEL };
		InputType[] timerInputs = { timer };
		JPanel timerPanel = new ConfigUnitPanel(TIMER_GROUP, authCheck,
				TIMER_CHECK, timerLabels, timerInputs);

		add(new CarriageReturn());
		add(timerPanel);

		if (timer.getValue() != null) {
			authCheck.setSelected(!timer.getValue().equals("") ? true : false);
			timer.setEditable(!timer.getValue().equals("") ? true : false);
		} else {
			authCheck.setSelected(false);
			timer.setEditable(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
	}

	public JCheckBox getAuthCheck() {
		return authCheck;
	}

	public void setAuthCheck(JCheckBox authCheck) {
		this.authCheck = authCheck;
	}

	public IntType getTimer() {
		return timer;
	}

	public void setTimer(IntType timer) {
		this.timer = timer;
	}
}
