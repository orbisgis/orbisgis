package org.contrib.algorithm.triangulation.tin2graph;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

public abstract class GNode {
	public abstract double getAzimuth();

	public abstract Metadata getMetadata();

	public abstract double getSteepestSlope();

	public abstract void store(Integer gid, DataSource dataSource) throws DriverException;
}