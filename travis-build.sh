#!/bin/bash
#if grep -q "<id>osgi" pom.xml
#then
#    mvn install -P osgi
#else
    travis_retry mvn install
#fi
exit $?
