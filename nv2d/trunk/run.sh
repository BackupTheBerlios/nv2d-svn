#! /bin/bash

# BASE='/home/bshi/cvs/nv2d/v2'
# CLASSPATH="${BASE}/src/build:${BASE}/lib/junit.jar:${BASE}/lib/piccolo.jar:${BASE}/lib/piccolox.jar:${BASE}/lib/colt.jar"

CLASSPATH="./build:./lib/junit.jar:./lib/piccolo.jar:./lib/piccolox.jar:./lib/colt.jar"

export CLASSPATH

echo $1

if [ "$1" = "piccolo" ]; then
	java nv2d/gui/NV2DMain
	exit
	fi

if [ "$1" = "path" ]; then
	java nv2d/algorithms/Test
	exit
	fi

if [ "$1" = "unit" ]; then
	java nv2d/testsuit/graph/DataStoreTest
	java nv2d/testsuit/graph/DatumTest
	java nv2d/testsuit/algorithms/DijkstraTest
	exit
	fi

if [ "$1" = "help" ]; then
	echo "Arguments:"
	echo "  piccolo - run GUI example"
	echo "  path - run Dijkstra's algorithm test"
	echo "  unit - run JUnit tests"
else
	java nv2d/$1
fi
