wget http://cm.bell-labs.com/netlib/voronoi/triangle.zip

Linux compilation command :
-------------------------
cc -O6 -static -o triangle-linux triangle.c -lm
strip triangle-linux

Windows compilation command :
---------------------------
CompilerOptions=-DNO_TIMER -DBUILDING_DLL=1