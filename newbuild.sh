#! /bin/bash

# Oppdatter versjon og buildnum
echo "Oppdatter buildnum"
prev_buildnum="$(cat buildnum)"
new_buildnum=$((prev_buildnum + 1))
echo "$new_buildnum" > buildnum

# Les versjon fra fil
version="$(cat version)"

if [[ -z $(git status | grep "Changes to be committed") ]]; then
    echo "Tom git stage, legger til '-A'"
    git add -A
fi

# Tagger siste commit med versjon- og build-nummber
echo "Lager commit"
git commit -m "$version:$new_buildnum: $*"
#git tag "v$version.$new_buildnum"

echo "Push til GitHub"
git push origin master
