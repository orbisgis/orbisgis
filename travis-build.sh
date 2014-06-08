#!/bin/bash
#if grep -q "<id>osgi" pom.xml
#then
#    mvn install -P osgi
#else
    mvn install
#fi
exit $?
