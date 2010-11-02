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
 * Copyright (C) 2010 Erwan BOCHER
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
package org.orbisgis.core.background;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.components.button.J3DButton;
import org.orbisgis.core.ui.components.job.GradientProgressBarUI;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class ProgressBar extends JPanel {

	private Job job;
	private JProgressBar overallProgressBar;
	private JLabel lblProgress;
	private JLabel lblSubTask;
	private JLabel lblTask;
	private ProgressListener progressListener;

	public ProgressBar(Job job) {
		this.job = job;
		progressListener = new ProgressListener() {

			public void subTaskStarted(Job job) {
				changeSubTask();
			}

			public void subTaskFinished(Job job) {
				changeSubTask();
			}

			public void progressChanged(Job job) {
				changeProgress();
			}

		};
		job.addProgressListener(progressListener);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(224, 224, 224));
		this.add(getLabelPanel(), BorderLayout.NORTH);
		overallProgressBar = new JProgressBar(0, 100);
		overallProgressBar.setUI(new GradientProgressBarUI());
		overallProgressBar.setBorderPainted(false);
		overallProgressBar.setBackground(Color.white);
		overallProgressBar.setForeground(Color.BLUE.darker());
		int overallProgress = job.getOverallProgress();
		this.add(overallProgressBar, BorderLayout.CENTER);
		overallProgressBar.setValue(overallProgress);
		lblProgress.setText(Integer.toString(overallProgress) + "%");
		changeSubTask();
		ImageIcon icon = OrbisGISIcon.REMOVE;
		JButton btn = new J3DButton();
		btn.setIcon(icon);
		btn.setToolTipText("Stop the process");
		btn.setPreferredSize(new Dimension(icon.getIconWidth() + 4, icon
				.getIconHeight() + 4));
		this.add(btn, BorderLayout.EAST);
		btn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ProgressBar.this.job.cancel();
			}

		});

		if (job.isCancelled()) {
			lblProgress.setEnabled(false);
			lblSubTask.setEnabled(false);
			lblTask.setEnabled(false);
			overallProgressBar.setEnabled(false);
			btn.setEnabled(false);
		}
	}

	private JPanel getLabelPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new CRFlowLayout());
		lblTask = new JLabel(job.getTaskName() + ": ");
		ret.add(lblTask);
		lblProgress = new JLabel("0%");
		ret.add(lblProgress);
		ret.add(new CarriageReturn());
		lblSubTask = new JLabel("");
		Font taskFont = lblTask.getFont();
		lblSubTask.setFont(taskFont.deriveFont(taskFont.getSize() - 2));
		ret.add(lblSubTask);
		return ret;
	}

	public void setJob(Job job) {
		this.job.removeProgressListener(progressListener);
		this.job = job;
		this.job.addProgressListener(progressListener);
		changeSubTask();
		changeProgress();

	}

	private void changeSubTask() {
		String currentTaskName = job.getCurrentTaskName();
		if (currentTaskName != null) {
			lblSubTask.setText(currentTaskName + "(" + job.getCurrentProgress()
					+ "%)");
		} else if (job.isStarted()) {
			lblSubTask.setText("processing...");
		} else {
			lblSubTask.setText("waiting...");
		}
	}

	private void changeProgress() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				int overallProgress = ProgressBar.this.job.getOverallProgress();
				int currentProgress = job.getCurrentProgress();
				int progress = overallProgress == 0 ? currentProgress
						: overallProgress;
				overallProgressBar.setValue(progress);
				lblProgress.setText(Integer.toString(progress) + "%");
				changeSubTask();
				overallProgressBar.setIndeterminate(progress == 0 ? true
						: false);
			}

		});
	}

}
