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
package org.orbisgis.core.commandline;



import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


/**
 * A class to parse Unix (and DOS/Win)-style application command-lines.
 */
public class CommandLine {
    Hashtable optSpecs = new Hashtable();
    Vector optVec = new Vector(); // used to store options in order of entry
    char optionChar; // the char that indicates an option.  Default is '/', which is
                     // NT Standard, but this causes problems on Unix systems, so '-' should
                     // be used for cross-platform apps

    public CommandLine() {
        this('/');
    }

    public CommandLine(char optionCh) {
        optionChar = optionCh;
    }

    public void addOptionSpec(OptionSpec optSpec) {
        String name = optSpec.getName();

        // should check for duplicate option names here
        optSpecs.put(name.toLowerCase(), optSpec);
        optVec.add(optSpec);
    }

    OptionSpec getOptionSpec(String name) {
        if (optSpecs.containsKey(name.toLowerCase())) {
            return (OptionSpec) optSpecs.get(name.toLowerCase());
        }

        return null;
    }

    public Option getOption(String name) {
        OptionSpec spec = getOptionSpec(name);

        if (spec == null) {
            return null;
        }

        return spec.getOption(0);
    }

    public Iterator getOptions(String name) {
        OptionSpec spec = getOptionSpec(name);

        return spec.getOptions();
    }

    public boolean hasOption(String name) {
        OptionSpec spec = getOptionSpec(name);

        if (spec == null) {
            return false;
        }

        return spec.hasOption();
    }

    /**
     *  adds an option for an <B>existing</B> option spec
     */
    void addOption(Option opt) {
        String name = opt.getName();
        ((OptionSpec) optSpecs.get(name.toLowerCase())).addOption(opt);
    }

    public void printDoc(PrintStream out) {
        OptionSpec os = null;
        out.println("Options:");

        for (Iterator i = optVec.iterator(); i.hasNext();) {
            os = (OptionSpec) i.next();

            String name = optionChar + os.getName();

            if (os.getName() == OptionSpec.OPTION_FREE_ARGS) {
                name = "(free)";
            }

            out.println("  " + name + " " + os.getArgDesc() + " - " +
                os.getDocDesc());
        }
    }

    public void parse(String[] args) throws ParseException {
        String noOptMsg;
        String optName;
        Vector params = new Vector();
        int i = 0;
        int paramStart;

        while (i < args.length) {
            if (args[i].charAt(0) == optionChar) {
                optName = args[i].substring(1);
                noOptMsg = "Invalid option: " + args[i];
                paramStart = i + 1;
            } else {
                optName = OptionSpec.OPTION_FREE_ARGS;
                noOptMsg = "Invalid option: " + args[i];
                paramStart = i;
            }

            OptionSpec optSpec = getOptionSpec(optName);

            if (optSpec == null) {
                throw new ParseException(noOptMsg);
            }

            int expectedArgCount = optSpec.getAllowedArgs();

            // parse option args
            parseParams(args, params, paramStart, expectedArgCount);

            Option opt = optSpec.parse((String[]) params.toArray(new String[0]));

            // check for number of allowed instances here
            addOption(opt);
            i++;
            i += params.size();
        }
    }

    void parseParams(String[] args, Vector params, int i, int expectedArgCount) {
        params.clear();

        int count = 0;
        int expected = expectedArgCount;

        if (expectedArgCount == OptionSpec.NARGS_ZERO_OR_ONE) {
            expected = 1;
        }

        if (expectedArgCount == OptionSpec.NARGS_ZERO_OR_MORE) {
            expected = 999999999;
        }

        if (expectedArgCount == OptionSpec.NARGS_ONE_OR_MORE) {
            expected = 999999999;
        }

        while ((i < args.length) && (count < expected) &&
                (args[i].charAt(0) != optionChar)) {
            params.addElement(args[i++]);
            count++;
        }
    }
}

