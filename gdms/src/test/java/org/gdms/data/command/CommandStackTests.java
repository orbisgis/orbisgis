package org.gdms.data.command;

import junit.framework.TestCase;

import org.gdms.data.command.Command;
import org.gdms.data.command.CommandStack;
import org.gdms.data.driver.DriverException;


public class CommandStackTests extends TestCase {
    public void testNormal() throws Exception {
        CommandStack cs = new CommandStack();
        cs.setLimit(3);
        cs.setUseLimit(true);
        
        assertTrue(!cs.canRedo());
        assertTrue(!cs.canUndo());
        
        cs.put(new C());
        assertTrue(!cs.canRedo());
        assertTrue(cs.canUndo());
        
        cs.put(new C());
        cs.put(new C());
        assertTrue(!cs.canRedo());
        assertTrue(cs.canUndo());

        cs.undo();
        assertTrue(cs.canRedo());
        assertTrue(cs.canUndo());
        cs.undo();
        assertTrue(cs.canRedo());
        assertTrue(cs.canUndo());
        cs.undo();
        assertTrue(cs.canRedo());
        assertTrue(!cs.canUndo());
    }
    
    public void testLimit() throws Exception {
        CommandStack cs = new CommandStack();
        cs.setLimit(2);
        cs.setUseLimit(true);
        
        cs.put(new C(1));
        cs.put(new C(2));
        cs.put(new C(3));
        assertTrue(cs.undo().equals(new C(3)));
        assertTrue(cs.undo().equals(new C(2)));
        assertTrue(cs.canRedo());
        assertTrue(!cs.canUndo());
    }
    
    public void testPutUndoPut() throws Exception {
        CommandStack cs = new CommandStack();
        cs.setUseLimit(false);
        
        cs.put(new C(1));
        cs.put(new C(2));
        cs.put(new C(3));
        cs.put(new C(4));
        assertTrue(cs.undo().equals(new C(4)));
        assertTrue(cs.undo().equals(new C(3)));
        assertTrue(cs.undo().equals(new C(2)));
        assertTrue(cs.redo().equals(new C(2)));
        assertTrue(cs.canRedo());
        cs.put(new C(3));
        assertTrue(!cs.canRedo());
        assertTrue(cs.canUndo());
    }
    
    public class C implements Command {

        private int id;

        public C(){}
        
        public C(int id) {
            this.id = id;
        }
        
        public void redo() throws DriverException {
            
        }

        public void undo() throws DriverException {
            
        }

        @Override
        public boolean equals(Object obj) {
            C c = (C) obj;
            return this.id == c.id;
        }
        
    }

}
