#! /bin/sh
# ======================================================================
# Thomas LEDUC - le 10/01/2008
# ======================================================================
BASE_DIRECTORY="/tmp";
DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-svn";
DST_JAR_DIRECTORY="${BASE_DIRECTORY}/orbisgis-jar";

DATE=`date +%Y%m%d-%H%M`;

RSYNC="rsync --archive --verbose --exclude=.svn";
MVN="mvn -Dmaven.test.skip=true";
# ======================================================================
svnCheckout() {
	rm -fr ${DST_SVN_DIRECTORY};
	mkdir -p ${DST_SVN_DIRECTORY};
	cd ${DST_SVN_DIRECTORY};
	# svn checkout http://geosysin.iict.ch/irstv-svn/platform-releases/${1} platform;
	svn checkout http://geosysin.iict.ch/irstv-svn/platform platform;

	# clean ${DST_SVN_DIRECTORY}
	find . -name \.svn | xargs rm -r;
}

mvnCommands() {
	cd ${DST_SVN_DIRECTORY}/platform;
	${MVN} source:jar;	

	for dir in $(find * -type d -prune); do
		cd ${DST_SVN_DIRECTORY}/platform/${dir};
		${MVN} javadoc:javadoc
		${MVN} javadoc:jar;
	done

	${MVN} install;
}

copyTheProducedJars() {
	mkdir -p "${DST_JAR_DIRECTORY}";

	# + org.urbsat...
	for r in gdms h2spatial grap; do
		mkdir -p "${DST_JAR_DIRECTORY}/${r}";
		cd ${DST_SVN_DIRECTORY}/platform/${r}/target;
		${RSYNC} ${r}-*-SNAPSHOT.jar ${DST_JAR_DIRECTORY}/${r};
		${RSYNC} ${r}-*-sources.jar ${DST_JAR_DIRECTORY}/${r};

		cd ${DST_SVN_DIRECTORY}/platform/${r}/target/site;
		jar cf ${DST_JAR_DIRECTORY}/${r}/${r}-javadoc.jar apidocs;
	done
}

createZip() {
	cd ${DST_JAR_DIRECTORY};
	for r in gdms h2spatial grap; do
		cp --archive ${DST_SVN_DIRECTORY}/platform/plugin-manager/docs/license.txt ${r}/;
		zip -r ${r}-${1}.zip ${r};
	done

	cd ${DST_SVN_DIRECTORY}/platform;
	for r in *; do
		if [ -d ${r} ]; then
			mv --force ${r}/target/apidocs $r-apidocs;
		fi
	done
	find . -name target | xargs rm -r;

	cd ${DST_SVN_DIRECTORY};
	zip -r ${DST_JAR_DIRECTORY}/orbisgis-${1}-src.zip platform;
}
# ======================================================================
if [ ${#} -ne 1 ]; then
	echo >&2 "Usage: $0 <nom de version>";
	exit;
fi

VERSION=${1};
svnCheckout ${DATE_OF_RELEASE};
mvnCommands;
copyTheProducedJars;
createZip ${VERSION};
