#! /bin/bash

# Oppdatter versjon og buildnum
echo "Oppdatter buildnum"
prev_buildnum="$(cat buildnum)"
new_buildnum=$((prev_buildnum + 1))
echo "$new_buildnum" > buildnum

# Les versjon fra fil
version="$(cat version)"

# Tagger siste commit med versjon- og build-nummber
git add -A
git commit -m "$version:$new_buildnum: $*"
git tag "v$version.$new_buildnum"
#git push origin master

