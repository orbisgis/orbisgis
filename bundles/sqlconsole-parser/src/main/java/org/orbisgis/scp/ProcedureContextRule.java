/*
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
package org.orbisgis.scp;

import org.h2.bnf.Bnf;
import org.h2.bnf.Rule;
import org.h2.bnf.RuleElement;
import org.h2.bnf.RuleHead;
import org.h2.bnf.RuleList;
import org.h2.bnf.Sentence;
import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbContextRule;
import org.h2.bnf.context.DbProcedure;
import org.h2.bnf.context.DbSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Mueller
 * @author Nicolas Fortin
 */
public class ProcedureContextRule  extends DbContextRule {
    private Logger LOGGER = LoggerFactory.getLogger(ProcedureContextRule.class);

    private final DbContents contents;

    public ProcedureContextRule(DbContents contents, int type) {
        super(contents, type);
        this.contents = contents;
        // Create treemap of functions for speed
    }

    @Override
    public boolean autoComplete(Sentence sentence) {
        try {
            String query = sentence.getQuery(), s = query;
            String up = sentence.getQueryUpper();
            DbSchema schema = sentence.getLastMatchedSchema();
            if (schema == null) {
                schema = contents.getDefaultSchema();
            }
            String incompleteSentence = sentence.getQueryUpper();
            String incompleteFunctionName = incompleteSentence;
            if (incompleteSentence.contains("(")) {
                incompleteFunctionName = incompleteSentence.substring(0, incompleteSentence.indexOf('(')).trim();
            }

            // Common elements
            RuleElement openBracket = new RuleElement("(", "Function");
            RuleElement closeBracket = new RuleElement(")", "Function");
            RuleElement comma = new RuleElement(",", "Function");

            // Fetch all elements
            for (DbProcedure procedure : schema.getProcedures()) {
                final String procName = procedure.getName();
                if (procName.startsWith(incompleteFunctionName)) {
                    // That's it, build a RuleList from this function
                    RuleElement procedureElement = new RuleElement(procName, "Function");
                    RuleList rl = new RuleList(procedureElement, openBracket, false);
                    // Go further only if the user use open bracket
                    if (incompleteSentence.contains("(")) {
                        for (DbColumn parameter : procedure.getParameters()) {
                            if (parameter.getPosition() > 1) {
                                rl = new RuleList(rl, comma, false);
                            }
                            DbContextRule columnRule = new DbContextRule(contents, COLUMN);
                            String parameterType = parameter.getDataType();
                            // Remove precision
                            if (parameterType.contains("(")) {
                                parameterType = parameterType.substring(0, parameterType.indexOf('('));
                            }
                            columnRule.setColumnType(parameterType);
                            rl = new RuleList(rl, columnRule, false);
                        }
                        rl = new RuleList(rl, closeBracket, false);
                    }
                    rl.autoComplete(sentence);
                }
            }
            if (!s.equals(query)) {
                while (Bnf.startWithSpace(s)) {
                    s = s.substring(1);
                }
                sentence.setQuery(s);
                return true;
            }
            return false;
        } catch (Exception ex) {
            LOGGER.debug(ex.getLocalizedMessage(), ex);
            return false;
        }
    }
}
