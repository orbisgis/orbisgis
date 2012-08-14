/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import org.orbisgis.utils.I18N;

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
                lblProgress.setFont(new Font(lblProgress.getFont().getFontName(), lblProgress.getFont().getStyle(), 10));// change font size
                lblProgress.setText(Integer.toString(overallProgress) + "%");
                changeSubTask();
                ImageIcon icon = OrbisGISIcon.REMOVE;
                JButton btn = new J3DButton();
                btn.setIcon(icon);
                btn.setToolTipText(I18N.getString("orbisgis.org.orbisgis.core.progressBar.stop"));
                btn.setPreferredSize(new Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4));
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
                lblSubTask.setFont(new Font(lblProgress.getFont().getFontName(), lblProgress.getFont().getStyle(), 10));// change font size
                lblTask.setFont(new Font(lblProgress.getFont().getFontName(), lblProgress.getFont().getStyle(), 10));// change font size
                lblProgress.setFont(new Font(lblProgress.getFont().getFontName(), lblProgress.getFont().getStyle(), 10));// change font size
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
                        String text = null;
                        if (currentTaskName.length() > 20) {
                                text = currentTaskName.substring(0, 20);
                        } else {
                                text = currentTaskName;
                        }
                        if (job.getCurrentProgress() == 0) {
                                lblSubTask.setText(text + "...");
                        } else {
                                lblSubTask.setText(text + "... (" + job.getCurrentProgress() + "%)");
                        }
                } else if (job.isStarted()) {
                        lblSubTask.setText(I18N.getString("orbisgis.org.orbisgis.core.progressBar.processing") + "..");
                } else {
                        lblSubTask.setText(I18N.getString("orbisgis.org.orbisgis.core.progressBar.waiting") + "..");
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
