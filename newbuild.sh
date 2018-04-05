#! /bin/bash

if [[ "$#" -lt 1 ]]; then
	echo "Skriv versjonsnummer som første argument"
	exit;
fi

# Oppdatter versjon og buildnum
echo "Oppdatter versjon og buildnum"
echo "$1" > version
prev_buildnum="$(cat buildnum)"
new_buildnum=$((prev_buildnum + 1))
echo "$new_buildnum" > buildnum

# Tagger siste commit med versjon- og build-nummber
if [[ "$2" == "git" ]]; then
	git add buildnum
	git commit -m "new build $new_buildnum"
    git tag "v$1.$new_buildnum"
    git push origin master
fi

# Kjør installasjonsskript
bash install.sh
