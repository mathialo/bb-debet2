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

# Sender ny versjon til github
git add -A
git commit -m "New build $1.$new_buildnum"
git push origin master

# Kjør installasjonsskript
bash install.sh
