#! /bin/bash

if [[ "$#" -eq 1 ]]; then
	echo "Oppdatterer versjon"
    echo "$1" > version
fi

# Oppdatter versjon og buildnum
echo "Oppdatter buildnum"
prev_buildnum="$(cat buildnum)"
new_buildnum=$((prev_buildnum + 1))
echo "$new_buildnum" > buildnum

# Les versjon fra fil
version="$(cat version)"

# Tagger siste commit med versjon- og build-nummber
if [[ "$2" == "git" ]]; then
	git add buildnum
	git commit -m "new build $new_buildnum"
    git tag "v$version.$new_buildnum"
    git push origin master
fi

# Kjør installasjonsskript
bash install.sh
