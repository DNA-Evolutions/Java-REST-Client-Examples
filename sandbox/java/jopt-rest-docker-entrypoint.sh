#!/bin/sh
set -eu

echo "Trying to download JOpt Rest Client Examples"

folder="/home/coder/project/jopt.rest.examples/"
url="https://github.com/DNA-Evolutions/Java-REST-Client-Examples.git"
if ! git clone "${url}" "${folder}" 2>/dev/null && [ -d "${folder}" ] ; then
    echo "Clone skipped as the folder ${folder} already exists. If you want a 'fresh' clone, choose another volume or rename the existing folder."
fi

exec /usr/bin/entrypoint.sh "$@"
