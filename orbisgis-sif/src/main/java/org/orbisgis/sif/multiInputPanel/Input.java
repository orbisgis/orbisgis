/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.sif.multiInputPanel;

/**
 * Represents input that can be asked from the UI.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class Input {
	private String text;
	private String initialValue;
	private InputType type;
	private String name;
	private String group;

        /**
         * Builds a new {@code Input}. It has a name, is described with some
         * text, can have an initial value. Finally, its {@code InputType} must
         * be specified. It will be used to build the UI that will let the user
         * enter its inputs.
         * @param name
         * @param text
         * @param initialValue
         * @param type
         */
	public Input(String name, String text, String initialValue, InputType type) {
		super();
		this.name = name;
		this.text = text;
		this.initialValue = initialValue;
		this.type = type;
	}

        /**
         * Gets the text used to describe the input.
         * @return
         */
	public String getText() {
		return text;
	}

        /**
         * Get the value that was originally set.
         * @return
         */
	public String getInitialValue() {
		return initialValue;
	}

        /**
         * Gets the {@code InputType} of this {@code Input}. It must be used to
         * built the UIs.
         * @return
         */
	public InputType getType() {
		return type;
	}

        /**
         * Gets the name of the {@code Input}.
         * @return
         */
	public String getName() {
		return name;
	}
}
