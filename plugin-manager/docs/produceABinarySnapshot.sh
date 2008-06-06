#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 09/01/2008
# ======================================================================
PLUGINS_LIST="org.orbisgis.core-ui org.orbisgis.processing org.urbsat";

# ======================================================================
if [ "$#" -ne "2" ] && [ "$#" -ne "1" ]; then
  echo "Usage: releaseVersion.sh http://geosysin.iict.ch/irstv-svn/platform [ oficial ]";
  exit 1;
fi

BASE_DIRECTORY="/tmp";
# BASE_DIRECTORY="/import/tmp-3jours";
# DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${$}";
DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-svn";
DATE=`date +%Y%m%d-%H%M`;
# RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${DATE}";
RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-zip";
URL="$1";
if [ "$2" == "oficial" ]; then
  OFICIAL="oficial";
fi

if [ $OFICIAL ]; then
echo "Please, create a change log (pulse intro when done)"
read $foo
echo "Please, change the pom version numbers depending on the change log (pulse intro when done)"
read $foo
echo "Please, change the OrbisGIS version number on the splash screen and Help->About (pulse intro when done)"
read $foo
echo "It's done. Don't forget to publish change log and zip (pulse intro)"
read $foo
echo "A binary and a source package will be created (pulse intro to proceed)"
read $foo
fi

MAIN_CLASS="org.orbisgis.pluginManager.Main";

RSYNC="rsync --archive --verbose --exclude=.svn";
MVN="mvn -Dmaven.test.skip=true";
# ======================================================================
svnCheckout() {
	rm -fr ${DST_SVN_DIRECTORY};
	mkdir -p ${DST_SVN_DIRECTORY};
	cd ${DST_SVN_DIRECTORY};
	# svn checkout http://geosysin.iict.ch/irstv-svn/platform-releases/${1} platform;
	svn checkout $URL;
}

createZipOfAllSrcAndJavadoc() {
	cd ${DST_SVN_DIRECTORY};
	rm -fr $(find . -type d -name .svn);

	cd ${DST_SVN_DIRECTORY}/platform;
	for dir in $(find * -type d -prune); do
		cd ${DST_SVN_DIRECTORY}/platform/${dir};
		${MVN} javadoc:javadoc;
		mv target/site/apidocs ${DST_SVN_DIRECTORY}/platform/${dir}-javadoc;
	done

	cd ${DST_SVN_DIRECTORY};
	zip -r ${BASE_DIRECTORY}/orbisgis-${DATE}-src platform;
}

mvnPackage() {
	cd ${DST_SVN_DIRECTORY}/platform;
	if [ $OFICIAL ]; then
      ${MVN} deploy;
    else
      ${MVN} install;
    fi
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

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/ -name \*.jar); do
		cp --archive ${jar} ${RELEASE_DIRECTORY}/lib/$(basename ${jar});
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
# export TRIANGLE_HOME="/home/leduc/dev/eclipse-java/platform/org.orbisgis.rasterProcessing/lib";
# PATH="/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands/java:\${PATH}";
java -Xmx512M -cp "${UNX}" ${MAIN_CLASS} \${@}
EOF
	chmod +x ${RELEASE_DIRECTORY}/orbisgis.sh;

cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.bat;
REM set TRIANGLE_HOME=D:\leduc\eclipse\platform\rasterProcessing\lib
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
if [ $OFICIAL ]; then
 createZipOfAllSrcAndJavadoc;
fi
mvnPackage;
createPluginListXml;
copyAllJarFiles;
copyDependenciesAndPluginXmlAndSchema;
produceBatAndShellFiles;
makeZip;