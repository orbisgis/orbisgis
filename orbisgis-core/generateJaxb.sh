# This scripts requires at least jaxb 2.1. If this version is ever included
# in the jvm we can move it to an Ant script to execute inside eclipse. Meanwhile
# jaxb 2.1.7 can be downloaded here https://jaxb.dev.java.net/servlets/ProjectDocumentList

EXEC="/usr/lib/jvm/java-6-sun-1.6.0.10/bin/xjc";

${EXEC} -target 2.0 -d src/main/java -p org.orbisgis.core.renderer.symbol.collection.persistence -episode src/main/resources/org/orbisgis/core/renderer/symbol/symbol.episode src/main/resources/org/orbisgis/core/renderer/symbol/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.core.renderer.legend.carto.persistence -episode src/main/resources/org/orbisgis/core/renderer/legend/legend.episode -b src/main/resources/org/orbisgis/core/renderer/symbol/symbol.episode src/main/resources/org/orbisgis/core/renderer/legend/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.core.layerModel.persistence -b src/main/resources/org/orbisgis/core/renderer/symbol/symbol.episode -b src/main/resources/org/orbisgis/core/renderer/legend/legend.episode src/main/resources/org/orbisgis/core/layerModel/persistence.xsd
