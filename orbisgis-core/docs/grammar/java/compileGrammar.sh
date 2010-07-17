#! /bin/sh

PACKAGE="org.orbisgis.javaManager.parser";
ODIR="../src/main/java/org/orbisgis/javaManager/parser";

~/applications/javacc-4.1/bin/jjtree -NODE_PACKAGE:${PACKAGE} -OUTPUT_DIRECTORY:${ODIR} -VISITOR:true -NODE_SCOPE_HOOK:true -MULTI:true Java1.5.jj
~/applications/javacc-4.1/bin/javacc -OUTPUT_DIRECTORY:${ODIR} ${ODIR}/Java1.5.jj.jj
~/applications/javacc-4.1/bin/jjdoc Java1.5.jj