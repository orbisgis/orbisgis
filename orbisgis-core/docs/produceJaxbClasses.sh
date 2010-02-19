#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 17/12/2007
# ======================================================================

schemasAndTargetPackages() {
	cat <<EOF
gdms/src/main/resources/source-info-directory.xsd													org.gdms.source.directory
org.orbisgis.core/src/main/resources/org/orbisgis/core/persistence/windows.xsd						org.orbisgis.core.persistence
org.orbisgis.geocatalog/src/main/resources/org/orbisgis/geocatalog/persistence/catalog.xsd			org.orbisgis.geocatalog.persistence
org.orbisgis.geoview/src/main/resources/org/orbisgis/geoview/viewContext.xsd						org.orbisgis.geoview.persistence
org.orbisgis.geoview/src/main/resources/org/orbisgis/geoview/persistence/sqlSemanticRepository.xsd	org.orbisgis.geoview.views.sqlSemanticRepository.persistence
EOF
}

jaxbOrders() {
	schemasAndTargetPackages | while read xsd targetPackage; do
		targetDir=${xsd/\/*/}/src/main/java;
		echo " * * * * * ${xsd} -> ${targetDir}//${targetPackage} * * * * *";
		xjc -d ${targetDir} -p ${targetPackage} ${xsd};
	done
}
# ======================================================================
jaxbOrders;
