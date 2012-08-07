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
package org.orbisgis.core.commandline;

import java.util.Iterator;
import java.util.Vector;

/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Pierre-Yves FADET, computer engineer.
 * Previous computer developer :
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    Pierre-Yves.Fadet _at_ ec-nantes.fr
 **/
public class OptionSpec {
    public final static int NARGS_ZERO_OR_MORE = -1;
    public final static int NARGS_ONE_OR_MORE = -2;
    public final static int NARGS_ZERO_OR_ONE = -3;
    public final static String OPTION_FREE_ARGS = "**FREE_ARGS**"; // option name for free args
    String name;
    int nAllowedArgs = 0; // number of arguments allowed
    String syntaxPattern;
    String argDoc = ""; // arg syntax description
    String doc = ""; // option description
    Vector options = new Vector();

    public OptionSpec(String optName) {
        name = optName;
        nAllowedArgs = 0;
    }

    public OptionSpec(String optName, int nAllowed) {
        this(optName);

        // check for invalid input
        if (nAllowedArgs >= NARGS_ZERO_OR_ONE) {
            nAllowedArgs = nAllowed;
        }
    }

    public OptionSpec(String optName, String _syntaxPattern) {
        this(optName);
        syntaxPattern = _syntaxPattern;
    }

    public void setDoc(String _argDoc, String docLine) {
        argDoc = _argDoc;
        doc = docLine;
    }

    public String getArgDesc() {
        return argDoc;
    }

    public String getDocDesc() {
        return doc;
    }

    public int getNumOptions() {
        return options.size();
    }

    public Option getOption(int i) {
        if (options.size() > 0) {
            return (Option) options.elementAt(i);
        }

        return null;
    }

    public Iterator getOptions() {
        return options.iterator();
    }

    public boolean hasOption() {
        return options.size() > 0;
    }

    void addOption(Option opt) {
        options.addElement(opt);
    }

    String getName() {
        return name;
    }

    int getAllowedArgs() {
        return nAllowedArgs;
    }

    Option parse(String[] args) throws ParseException {
        checkNumArgs(args);

        return new Option(this, args);
    }

    void checkNumArgs(String[] args) throws ParseException {
        if (nAllowedArgs == NARGS_ZERO_OR_MORE) {
            // args must be ok
        } else if (nAllowedArgs == NARGS_ONE_OR_MORE) {
            if (args.length <= 0) {
                throw new ParseException("option " + name +
                    ": expected one or more args, found " + args.length);
            }
        } else if (nAllowedArgs == NARGS_ZERO_OR_ONE) {
            if (args.length > 1) {
                throw new ParseException("option " + name +
                    ": expected zero or one arg, found " + args.length);
            }
        } else if (args.length != nAllowedArgs) {
            throw new ParseException("option " + name + ": expected " +
                nAllowedArgs + " args, found " + args.length);
        }
    }
}
