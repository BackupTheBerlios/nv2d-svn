#! /bin/bash

CLASSPATH="./build:./lib/junit.jar:./lib/piccolo.jar:./lib/piccolox.jar:./lib/colt.jar"

export CLASSPATH

echo $1

if [ "$1" = "piccolo" ]; then
	/mit/java/current/bin/java nv2d/gui/NV2DMain
	exit
	fi

if [ "$1" = "path" ]; then
	/mit/java/current/bin/java nv2d/algorithms/Test
	exit
	fi

if [ "$1" = "unit" ]; then
	/mit/java/current/bin/java nv2d/testsuit/graph/DataStoreTest
	java nv2d/testsuit/graph/DatumTest
	java nv2d/testsuit/algorithms/DijkstraTest
	exit
	fi

if [ "$1" = "package" ]; then
	cp ./doc/distfiles/NV2D.manifest ./build
	cp ./lib/piccolo.jar ./build
	cp ./lib/piccolox.jar ./build
	cp ./lib/colt.jar ./build
	cp ./lib/junit.jar ./build
	cd build
	jar xf colt.jar cern/colt edu/cornell cern/jet
	jar xf piccolo.jar edu
	jar xf piccolox.jar edu
	jar xf junit.jar junit
	rm colt.jar
	rm piccolo.jar
	rm junit.jar
	jar cmf NV2D.manifest ../dist/NV2D.jar *
	exit
	fi

if [ "$1" = "plugin" ]; then
	java nv2d.plugins.NPluginManager build/nv2d/plugins/standard
	fi

if [ "$1" = "help" ]; then
	echo "Arguments:"
	echo "  piccolo - run GUI example"
	echo "  path - run Dijkstra's algorithm test"
	echo "  unit - run JUnit tests"
else
	java nv2d/$1
fi
