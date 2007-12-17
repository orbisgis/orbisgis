#! /bin/sh
# ======================================================================
# Thomas LEDUC - le 17/12/2007
# ======================================================================

function schemasAndTargetPackages() {
	cat <<EOF
org.orbisgis.core/schema/action.xsd						org.orbisgis.core.persistence
org.orbisgis.core/schema/window.xsd						org.orbisgis.core.persistence
org.orbisgis.geocatalog/schema/resourceAction.xsd		org.orbisgis.geocatalog.persistence
org.orbisgis.geocatalog/schema/resourceWizard.xsd		org.orbisgis.geocatalog.persistence
org.orbisgis.geoview/schema/newLayerWizard.xsd			org.orbisgis.geoview.persistence
org.orbisgis.geoview/schema/view.xsd					org.orbisgis.geoview.persistence
org.orbisgis.geoview/schema/sqlSemanticRepository.xsd	org.orbisgis.geoview.sqlSemanticRepository.persistence
org.orbisgis.geoview/schema/layerAction.xsd				org.orbisgis.geoview.persistence
EOF
}

function fromTargetPackageToTargetDir() {
	xsd=${1};
	targetPackage=${2};
	parentDir=${xsd/\/*/};
	partialTargetDir=`echo ${targetPackage} | tr . /`;
	echo ${parentDir}/src/main/java/${partialTargetDir};
}

function jaxbOrders() {
	schemasAndTargetPackages | while read xsd targetPackage; do
		targetDir=`fromTargetPackageToTargetDir ${xsd} ${targetPackage}`;
		echo xjc -d ${targetDir} -p ${targetPackage} ${xsd};
	done
}
# ======================================================================
jaxbOrders;
