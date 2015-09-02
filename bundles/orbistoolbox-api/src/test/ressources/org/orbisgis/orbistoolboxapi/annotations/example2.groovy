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

import org.orbisgis.orbistoolboxapi.annotations.input.LiteralDataInput
import org.orbisgis.orbistoolboxapi.annotations.input.RawDataInput
import org.orbisgis.orbistoolboxapi.annotations.model.FormatAttribute
import org.orbisgis.orbistoolboxapi.annotations.model.Process
import org.orbisgis.orbistoolboxapi.annotations.output.LiteralDataOutput

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * This example demonstrate the use of a RawDataInput.
 * It copy the given tar.gz file with the given name.
 *
 * The RawDataInput annotation defines the format of the file. (Which is not mandatory.)
 *
 * @author Sylvain PALOMINOS
 */

@RawDataInput(
        title = "tar.gz file",
        abstrac = "tar.gz compressed file to copy and rename",
        formats = [
                @FormatAttribute(
                        mimeType = "application/x-gzip",
                        schema = "http://tools.ietf.org/html/rfc6713",
                        maximumMegaBytes = 10,
                        isDefaultFormat = true
                )
        ]
)
File f = new File("file1.tar.gz");

@LiteralDataInput(
        title = "Copy name"
)
String name = "compressedFile"

@LiteralDataOutput(
        title = "Result"
)
String result


@Process(title = "rename")
def processing() {
        if(Files.copy(f.toPath(), new File(name + ".tar.gz").toPath(), StandardCopyOption.REPLACE_EXISTING) != 0){
                result = 'ok'
        }
        else {
                result = 'error'
        }
}
