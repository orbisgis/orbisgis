from __future__ import print_function
import os
import shutil
from subprocess import call
import mmap


def pom_with_gettext(filename):
    with open(filename) as f:
        s = mmap.mmap(f.fileno(), 0, access=mmap.ACCESS_READ)
        return s.find('<artifactId>gettext-maven-plugin</artifactId>') > 0
def callext(tab):
    print(" ".join(tab))
    call(tab)

merge_po = False

##
# Copy and merge language file into each projects
og_folder = os.path.abspath("../")
for root, dirs, files in os.walk(og_folder):
    for file in files:
        if file == "pom.xml" and pom_with_gettext(os.path.join(root, file)):
            os.chdir(root)
            # Read source file to create key file and merge into PO
            callext(["mvn" ,"gettext:gettext"])
            if merge_po:
                callext(["mvn" ,"gettext:merge", "-DmsgmergeCmd\"msgmerge --backup=off\""])
                #remove obsolete and fuzzy entry in po
                for proot, pdirs, pfiles in os.walk(root):
                    for pfile in pfiles:
                        if pfile.endswith(".po"):
                            os.chdir(proot)
                            callext(["msgattrib" ,pfile,"--no-obsolete","--no-fuzzy","-o",pfile])
