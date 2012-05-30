#!/bin/bash

# build binary
svn checkout http://geosysin.iict.ch/irstv-svn/platform/ && \
cd platform && \
mvn -Dmaven.test.skip=true install && \
mvn javadoc:javadoc && \
mvn org.orbisgis:release-maven-plugin:source && \
#Uncomment when we have a publicly accessible maven repository
#mvn -Dmaven.test.skip=true deploy && \
mvn dependency:copy-dependencies && \
mvn org.orbisgis:release-maven-plugin:binary && \
# create updates
#mvn org.orbisgis:release-maven-plugin:update && \
# build installers 
cd .. && \
svn checkout http://geosysin.iict.ch/irstv-svn/platform-installers && \
cd platform-installers && \
mvn org.orbisgis:release-maven-plugin:installer

# upload all binary packages

