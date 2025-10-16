#! /bin/sh
java -Xmx30G -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
