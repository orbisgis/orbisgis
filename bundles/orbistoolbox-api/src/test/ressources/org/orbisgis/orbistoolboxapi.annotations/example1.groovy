/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolboxapi.annotations

import org.orbisgis.orbistoolboxapi.annotations.input.Input
import org.orbisgis.orbistoolboxapi.annotations.input.LiteralDataInput
import org.orbisgis.orbistoolboxapi.annotations.model.DataTypeAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.LiteralDataDomainAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.LiteralValueAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.PossibleLiteralValuesChoiceAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.ValuesAttribute
import org.orbisgis.orbistoolboxapi.annotations.output.LiteralDataOutput

/**
 * This example script return the number of time that a word appear in an sentence.
 * It show how to use LiteralDataInput/Output.
 * Only the mandatory attributes a set, for the others, the default value is used.
 *
 * @author Sylvain PALOMINOS
 */

@LiteralDataInput(
        description = @DescriptionTypeAttribute(title = "word"),
        validDomains = [
                @LiteralDataDomainAttribute(
                        plvc = @PossibleLiteralValuesChoiceAttribute(),
                        dataType = DataTypeAttribute.STRING,
                        defaultValue = @ValuesAttribute(value = ""),
                        isDefaultDomain = true)
        ],
        valueAttribute = @LiteralValueAttribute(dataType = DataTypeAttribute.STRING)
)
word = "Ring"

@Input
sentence = "One Ring to rule them all, one Ring to find them,\n" +
        "One Ring to bring them all and in the darkness bind them"

@LiteralDataOutput(
    description = @DescriptionTypeAttribute(title = "word"),
    validDomains = [
            @LiteralDataDomainAttribute(
                    plvc = @PossibleLiteralValuesChoiceAttribute(),
                    dataType = DataTypeAttribute.STRING,
                    defaultValue = @ValuesAttribute(value = ""),
                    isDefaultDomain = true)
    ],
    valueAttribute = @LiteralValueAttribute(dataType = DataTypeAttribute.STRING)
)
int occur

occur = sentence.split(word).length-1