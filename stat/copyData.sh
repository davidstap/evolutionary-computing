#!/usr/bin/bash

if [ -z "$3" ] ; then
    echo "Insufficient arguments <indir> <outdir> <int>!"
    exit 1
elif [ ! -d "$1" ] ; then
    echo "Input directory does not exists!"
    exit 1
elif [ -d "$2" ] ; then
    echo "Output directory already exists!"
    exit 1
fi

mkdir $2
for (( i = 0; i < $3; i++ )) do
    cp $1/$i.out $2/$i.out
done

