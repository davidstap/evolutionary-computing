#!/usr/bin/bash

if [ -z "$1" ] ; then
    echo "No output directory given!"
    exit 1
elif [ -d "$1" ] ; then
    echo "Output directory already exists!"
    exit 1
fi

N=100
make
mkdir "$1"

mydir="$1/cigar/"
mkdir $mydir
for (( i = 0; i < $N; i++ )) do
    make testc SEED=$RANDOM > $mydir$i.out
    echo "c"$i
done

mydir="$1/schaffers/"
mkdir $mydir
for (( i = 0; i < $N; i++ )) do
    make tests SEED=$RANDOM > $mydir$i.out
    echo "s"$i
done

mydir="$1/katsuura/"
mkdir $mydir
for (( i = 0; i < $N; i++ )) do
    make testk SEED=$RANDOM > $mydir$i.out
    echo "k"$i
done

