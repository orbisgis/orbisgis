
package org.orbisgis.view.table;

import bibliothek.util.xml.XElement;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * When the application close and start this layout retrieve/save
 * the state of this window
 */

public class TablePanelLayout implements DockingPanelLayout {
        
        private String sourceName;

        public TablePanelLayout(String sourceName) {
                this.sourceName = sourceName;
        }

        public void writeStream(DataOutputStream out) throws IOException {
                out.writeUTF(sourceName);
        }

        public void readStream(DataInputStream in) throws IOException {
                sourceName = in.readUTF();
        }

        public void writeXML(XElement element) {
                element.addString("sourceName", sourceName);
        }

        public void readXML(XElement element) {
                element.getString("sourceName");
        }
        
}
