#! /bin/bash

if [[ "$#" -lt 1 ]]; then
	echo "Skriv versjonsnummer som første argument"
	exit;
fi

# Oppdatter versjon og buildnum
echo "Oppdatter versjon og buildnum"
echo "$1" > version
prev_buildnum="$(cat buildnum)"
echo $((prev_buildnum + 1)) > buildnum

# Sender ny versjon til github
git add -A
git commit -m "New build $version-$buildnum"
git push origin master

# Kjør installasjonsskript
bash install.sh
