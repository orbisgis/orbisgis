package org.orbisgis.sif;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This dialog can host a single SimplePanel. It has a OK
 * and a Cancel buttons as well as SIFDialog. It provides also a
 * Apply button that can be customized through an ActionListener.
 * @author Alexis Guéganno
 */
public class ApplyDialog extends AbstractOutsideFrame {

    protected JButton btnOk;
    protected JButton btnCancel;
    private SimplePanel simplePanel;
    protected JPanel pnlButtons;
    private JButton btnApply;

    /**
     * Builds a new ApplyDialog
     * @param owner The parent window of this dialog.
     * @param applyListener The ActionListener that must be associated to the
     *                      apply button.
     */
    public ApplyDialog(Window owner, ActionListener applyListener) {
        super(owner);
        init(applyListener);
    }

    /**
     * Initializes the main panel and the buttons. {@code applyListener}
     * will be associated to the Apply button.
     * @param applyListener The listener associated to the apply button.
     */
    private void init(ActionListener applyListener) {
        this.setLayout(new BorderLayout());

        btnOk = new JButton(I18N.tr("OK"));
        btnOk.setBorderPainted(false);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exit(true);
            }
        });
        getRootPane().setDefaultButton(btnOk);
        btnCancel = new JButton(I18N.tr("Cancel"));
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exit(false);
            }
        });
        btnApply = new JButton(I18N.tr("Apply"));
        btnApply.setBorderPainted(false);
        btnApply.addActionListener(applyListener);
        pnlButtons = new JPanel();
        pnlButtons.add(btnOk);
        pnlButtons.add(btnApply);
        pnlButtons.add(btnCancel);
        this.add(pnlButtons, BorderLayout.SOUTH);

        add(errorLabel, BorderLayout.NORTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Sets the center component og this dialog.
     * @param simplePanel The main component of the panel.
     */
    public void setComponent(SimplePanel simplePanel) {
        this.simplePanel = simplePanel;
        this.add(simplePanel, BorderLayout.CENTER);
        this.setIconImage(getSimplePanel().getIconImage());
    }

    @Override
    protected SimplePanel getSimplePanel() {
        return simplePanel;
    }
}
