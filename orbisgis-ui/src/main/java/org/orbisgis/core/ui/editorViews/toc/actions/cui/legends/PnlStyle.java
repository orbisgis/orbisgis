/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.*;
import java.awt.event.FocusListener;
import java.beans.EventHandler;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.utils.I18N;

/**
 *
 * @author alexis
 */
public class PnlStyle extends JPanel implements ISELegendPanel {

        private Style style;
        private JTextField txtName;
        private String id;

        /**
         * Gets the {@code Style} that has been used to create this panel.
         * @return
         */
        public Style getStyle() {
                return style;
        }

        /**
         * Sets the {@code Style} that has been used to create this panel.
         * @param style
         */
        public void setStyle(Style style) {
                this.style = style;
        }

        @Override
        public Component getComponent() {
                removeAll();
		FlowLayout fl = new FlowLayout();
                JPanel panel = new JPanel();
		fl.setVgap(0);
		this.setLayout(fl);
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                //We display the title
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel("Title : "), gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                txtName = new JTextField(style.getName(),10);
                txtName.addFocusListener(EventHandler.create(FocusListener.class, this, "setTitle","source.text","focusLost"));
                panel.add(txtName, gbc);
		this.add(panel);
		this.setPreferredSize(new Dimension(200, 100));
		this.setBorder(BorderFactory.createTitledBorder(
			I18N.getString("Style configuration")));
                return this;
        }

        @Override
        public void initialize(LegendContext lc) {
        }

        @Override
        public ISELegendPanel newInstance() {
                return new PnlStyle();
        }

        @Override
        public String getId() {
                return id;
        }

        @Override
        public void setId(String newId) {
                id = newId;
        }

        @Override
        public String validateInput() {
                return null;
        }

        public void setTitle(String title){
                style.setName(title);
        }

}
