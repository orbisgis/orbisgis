# This scripts requires at least jaxb 2.1. If this version is ever included
# in the jvm we can move it to an Ant script to execute inside eclipse. Meanwhile
# jaxb 2.1.7 can be downloaded here https://jaxb.dev.java.net/servlets/ProjectDocumentList

EXEC="/usr/lib/jvm/java-6-sun-1.6.0.20/bin/xjc";

${EXEC} -target 2.0 -extension -d src/main/java -p org.gdms.source.directory  src/main/resources/source-info-directory.xsd