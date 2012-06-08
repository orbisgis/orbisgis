/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package  org.orbisgis.sif;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import org.orbisgis.sif.AbstractUIPanel;

/**
 * An {@code UIPanel} that will let the user choose one option in a list of
 * {@code String} instances. Configured with a list of {@code String} and a
 * title.
 * @author alexis
 */
public class RadioButtonPanel extends AbstractUIPanel{

        private List<String> choices;
        private String title;
        private JPanel pane;
        private ButtonGroup bg;

        /**
         * Instanciates the panel, using the given list of {@code String} and
         * the given title.
         * @param choices
         * @param title
         */
        public RadioButtonPanel(List<String> choices, String title){
                this.choices = choices;
        }

        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String validateInput() {
                for(String s : choices){
                        if(s == null){
                                return "Must not contain null Strings";
                        }
                }
                return null;
        }

        @Override
        public Component getComponent() {
                pane = new JPanel();
                pane.setLayout(new FlowLayout());
                bg = new ButtonGroup();
                for(String s: choices){
                        JRadioButton jrb = new JRadioButton(s);
                        bg.add(jrb);
                        pane.add(jrb);
                }
                return pane;
        }

        /**
         * Get the text that has been selected by the user.
         * @return
         */
        public String getSelectedText(){
                Enumeration<AbstractButton> en = bg.getElements();
                while(en.hasMoreElements()){
                        JRadioButton jrb = (JRadioButton)en.nextElement();
                        if(jrb.isSelected()){
                                return jrb.getText();
                        }
                }
                return null;
        }
}
