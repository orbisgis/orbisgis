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
			.getText("orbisgis.org.orbisgis.configuration.workspace.timerMinutes");
	private static final String TIMER_GROUP = I18N
			.getText("orbisgis.ui.menu.file.text.saveWorkspace");
	private static final String TIMER_CHECK = I18N
			.getText("orbisgis.org.core.enableSaving");

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
