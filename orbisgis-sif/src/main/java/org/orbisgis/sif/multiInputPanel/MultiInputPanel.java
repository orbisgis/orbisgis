/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.orbisgis.sif.SIFMessage;
import org.orbisgis.sif.UIPanel;

/**
 *
 * MultiInputPanel is a fast and simple way to create user interface to ask user
 * input.
 *
 *
 * Example :
 *
 * MultiInputPanel mip = new MultiInputPanel( "org.myPanel", "AddValue
 * initialization", false); mip.addInput("AddValue1", "Value1 to add", "1", new
 * IntType()); mip.addInput("AddValue2", "Value2 to add", "0", new IntType());
 * mip.addValidationExpression("AddValue1 > 0 or AddValue2 <12","Invalid input
 * values : Value1 > 0 and Value2 < 12 "); mip.group("Values", new
 * String[]{"AddValue1","AddValue2"});
 *
 */
public class MultiInputPanel implements UIPanel {

        private String id;
        private URL url;
        private String title;
        private ArrayList<Input> inputs = new ArrayList<Input>();
        private ArrayList<MIPValidation> validation = new ArrayList<MIPValidation>();
        private HashMap<String, Input> nameInput = new HashMap<String, Input>();
        private InputPanel comp;
        private String infoText;

        /**
         *
         * @param title of the panel
         */
        public MultiInputPanel(String title) {
                this(null, title);
        }


        /**
         *
         * @param id unique identifier to make the content persistent.
         * @param title of the panel.
         */
        public MultiInputPanel(String id, String title ) {
                this.id = id;
                this.title = title;
        }

        /**
         *
         * @param mDValidation
         */
        public void addValidation(MIPValidation mDValidation) {
                validation.add(mDValidation);
        }

        /**
         * To add a component on the multiInputPanel
         *
         * @param name of the component. Using an identifier to get it.
         * @param text of the component that is showed.
         * @param type of the component.
         *
         * Example :
         *
         *
         * addInput("AddValue", "Value to add", new IntType());
         *
         */
        public void addInput(String name, String text, InputType type) {
                Input input = new Input(name, text, null, type);
                inputs.add(input);
                nameInput.put(name, input);
        }

        /**
         * To add a component on the multiInputPanel
         *
         * @param name of the component. Using an identifier to get it.
         * @param text of the component that is showed.
         *
         * @param initialValue for the input component
         * @param type of the component.
         *
         * Example :
         *
         * addInput("AddValue", "Value to add", "1", new IntType());
         */
        public void addInput(String name, String text, String initialValue,
                InputType type) {
                Input input = new Input(name, text, initialValue, type);
                inputs.add(input);
                nameInput.put(name, input);
        }

        /**
         *
         * @param text
         */
        public void addText(String text) {
                Input input = new Input(null, text, null, new NoInputType());
                inputs.add(input);
        }

        /**
         *
         * @param url for the icon panel.
         */
        public void setIcon(URL url) {
                this.url = url;
        }

        /**
         *
         * @param title
         */
        public void setTitle(String title) {
                this.title = title;
        }

        /**
         *
         * @param infoText
         */
        public void setInfoText(String infoText) {
                this.infoText = infoText;
        }

        @Override
        public String getInfoText() {
                return infoText;
        }

        public String[] getFieldNames() {
                ArrayList<String> ret = new ArrayList<String>();
                for (Input input : inputs) {
                        if (input.getType().isPersistent()) {
                                ret.add(input.getName());
                        }
                }

                return ret.toArray(new String[ret.size()]);
        }

        public int[] getFieldTypes() {
                String[] fieldNames = getFieldNames();
                int[] ret = new int[fieldNames.length];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = nameInput.get(fieldNames[i]).getType().getType();
                }

                return ret;
        }

        public String getId() {
                return id;
        }

        /**
         *
         * @return
         */
        @Override
        public SIFMessage validateInput() {
                for (Iterator<MIPValidation> it = validation.iterator(); it.hasNext();) {
                        MIPValidation validator = it.next();
                        SIFMessage sifMessage = validator.validate(this);
                        if (sifMessage.getMessageType() != SIFMessage.OK) {
                                return sifMessage;
                        }
                }
                return new SIFMessage();
        }

        public String[] getValues() {
                String[] fieldNames = getFieldNames();
                String[] ret = new String[fieldNames.length];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = nameInput.get(fieldNames[i]).getType().getValue();
                }
                return ret;
        }

        public void setValue(String fieldName, String fieldValue) {
                Input input = nameInput.get(fieldName);
                if (input != null) {
                        input.getType().setValue(fieldValue);
                }
        }

        @Override
        public Component getComponent() {
                if (comp == null) {
                        comp = new InputPanel(inputs);
                }

                return comp;
        }

        @Override
        public URL getIconURL() {
                return url;
        }

        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public SIFMessage initialize() {
                return new SIFMessage();
        }

        /**
         *
         * @param inputName the name of the component
         * @return the input value
         *
         * Example :
         *
         * new Integer(mip.getInput("AddValue"));
         */
        public String getInput(String inputName) {
                Input input = nameInput.get(inputName);
                if (input != null) {
                        return input.getType().getValue();
                } else {
                        return null;
                }
        }

        /**
         *
         * @param title of the group
         * @param inputs name of the components that you want to group.
         *
         * Example :
         *
         * addInput("AddValue1", "Value to add", "1", new IntType());
         * addInput("AddValue2", "Value to add", "1", new IntType());
         *
         * group("Values", new String[]{"AddValue1","AddValue2"});
         *
         */
        public void group(String title, String... inputs) {
                for (String inputName : inputs) {
                        nameInput.get(inputName).setGroup(title);
                }
        }

        private class NoInputType implements InputType {

                @Override
                public Component getComponent() {
                        return null;
                }

                @Override
                public int getType() {
                        return 0;
                }

                @Override
                public String getValue() {
                        return null;
                }

                @Override
                public boolean isPersistent() {
                        return false;
                }

                @Override
                public void setValue(String value) {
                }
        }

        @Override
        public SIFMessage postProcess() {
                return new SIFMessage();
        }
      
}
