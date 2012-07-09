package org.orbisgis.sif.multiInputPanel;

import org.orbisgis.sif.SIFMessage;

/**
 *
 * @author ebocher
 */


public interface MIPValidation {
        
       /**
        * This method is used to validate the MultiInputPanel UI.
        */
       SIFMessage validate(MultiInputPanel mid);
        
}
