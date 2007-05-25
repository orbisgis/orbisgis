package org.gdms.data.command;

import org.gdms.data.InternalDataSource;

public class AbstractCommand {
    protected int index;
    protected InternalDataSource dataSource;
    protected CommandStack commandStack;
    
    public AbstractCommand(int index, InternalDataSource dataSource, CommandStack commandStack) {
        super();
        this.index = index;
        this.dataSource = dataSource;
        this.commandStack = commandStack;
    }

    protected int getIndex() {
        return index;
    }

    protected void setIndex(int index) {
        this.index = index;
    }
}
