#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 09/01/2008
# ======================================================================
PLUGINS_LIST="org.orbisgis.core org.orbisgis.geocatalog org.orbisgis.geoview org.urbsat";

# ======================================================================
BASE_DIRECTORY="/tmp";
# BASE_DIRECTORY="/import/tmp-3jours";
# DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${$}";
DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-svn";
DATE=`date +%Y%m%d-%H%M`;
# RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${DATE}";
RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-zip";

MAIN_CLASS="org.orbisgis.pluginManager.Main";

RSYNC="rsync --archive --verbose --exclude=.svn";
MVN="mvn -Dmaven.test.skip=true";
# ======================================================================
svnCheckout() {
	if [ -d ${DST_SVN_DIRECTORY} ]; then
		cd ${DST_SVN_DIRECTORY};
		rm --force platform/pom.xml
		svn update platform;
	else
		mkdir -p ${DST_SVN_DIRECTORY};
		cd ${DST_SVN_DIRECTORY};
		svn checkout http://geosysin.iict.ch/irstv-svn/platform-releases/${1} platform;
		# svn checkout http://geosysin.iict.ch/irstv-svn/platform platform;
	fi
}

createDummyPlugin() {
	cd "${DST_SVN_DIRECTORY}/platform";
	mkdir -p dummy;
	cp plugin-manager/docs/deploy-pom.xml dummy/pom.xml;
}

modifyParentPomXml() {
	perl -pi -e 's#</modules>#\t<module>dummy</module>\n</modules>#' ${DST_SVN_DIRECTORY}/platform/pom.xml;
}

mvnPackage() {
	cd ${DST_SVN_DIRECTORY}/platform;
	# ${MVN} package;
	${MVN} install;
	${MVN} dependency:copy-dependencies;
}

createPluginListXml() {
	rm -fr "${RELEASE_DIRECTORY}";
	mkdir -p "${RELEASE_DIRECTORY}";
	echo "<plugins>" > "${RELEASE_DIRECTORY}/plugin-list.xml";
	for plugin in ${PLUGINS_LIST}; do
		echo "  <plugin dir=\"plugins/${plugin}\"/>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
	done
	# echo "  <plugin dir=\"lib\"/>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
	echo "</plugins>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
}

createUniqFileName() {
	dstDir="${1}";
	fileName="${2}";
	
	if [ -e "${dstDir}/${fileName}" ]; then
		while [ -e "${dstDir}/${fileName}" ]; do
			fileName="_${fileName}";
		done
	fi
	echo "${dstDir}/${fileName}";
}

copyAllJarFiles() {
	mkdir -p "${RELEASE_DIRECTORY}/lib";
	cd "${RELEASE_DIRECTORY}"

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/dependencies -name \*.jar); do
		cp --archive ${jar} $(createUniqFileName lib $(basename ${jar}));
	done
}

copyDependenciesAndPluginXmlAndSchema() {
	cd ${DST_SVN_DIRECTORY}/platform;
	for plugin in ${PLUGINS_LIST}; do
		mkdir -p ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/plugin.xml ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/schema ${RELEASE_DIRECTORY}/plugins/${plugin};
	done
}

produceBatAndShellFiles() {
	cd ${RELEASE_DIRECTORY};

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/plugin-manager -name \*.jar -printf "%f\n"); do
		UNX="lib/${jar}:${UNX}";
		WIN="lib\\${jar};${WIN}";
	done

	cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.sh;
#! /bin/sh
# PATH="/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands/java:\${PATH}";
java -Xmx512M -cp "${UNX}" ${MAIN_CLASS} \${@}
EOF
	chmod +x ${RELEASE_DIRECTORY}/orbisgis.sh;

cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.bat;
start javaw -Xmx512M -cp "${WIN}" ${MAIN_CLASS} %1
EOF
	unix2dos ${RELEASE_DIRECTORY}/orbisgis.bat;
}

makeZip() {
	cp ${DST_SVN_DIRECTORY}/platform/plugin-manager/docs/license.txt ${RELEASE_DIRECTORY};
	cd $(dirname ${RELEASE_DIRECTORY}) && zip -r orbisgis-${DATE} $(basename ${RELEASE_DIRECTORY});
}
# ======================================================================
if [ ${#} -eq 1 ]; then
	DATE_OF_RELEASE=${1};
else
	DATE_OF_RELEASE=$(date +%Y%m%d);
fi

svnCheckout ${DATE_OF_RELEASE};
createDummyPlugin;
modifyParentPomXml;
mvnPackage;
createPluginListXml;
copyAllJarFiles;
copyDependenciesAndPluginXmlAndSchema;
produceBatAndShellFiles;
makeZip;

cat <<EOF

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * TAKE CARE TO THE dummy/pom.xml FILE !!!
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF
