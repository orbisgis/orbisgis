package org.gdms.data;

public class GDBMSEvent {
    private InternalDataSource dataSource;

    public GDBMSEvent(InternalDataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    public InternalDataSource getDataSource() {
        return dataSource;
    }
}
