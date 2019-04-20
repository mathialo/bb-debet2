
echo "BBDebet2 er lisensiert under GPLv3. I tillegg bygger BBDebet2 på følgende"
echo "pakker med tilhørende lisenser:"
echo ""
echo "  - OpenJDK, lisensiert under GPLv2 + CP"
echo "  - OpenJFX, lisensiert under GPLv2 + CP"
echo "  - JavaMail, lisensiert under CDDL og GPLv2 + CP"
echo "  - Apache POI, lisensiert under Apache v2"

while [[ 1 ]]; do
	echo ""
	echo "Godtar du lisensene?"
	echo "[y]  Godta"
	echo "[n]  Avbryt"
	echo "[l]  Se gjennom"
	read -p "[y/n/l]:   " yn
	
	case $yn in
		[Yy]* ) return;;
		[Nn]* ) echo "Lisensene må godtas for å kunne installere. Avbryter.";exit;;
		[Ll]* ) echo;;
		* ) echo "Ugyldig input. Antar avvisning av lisenser. Avbryter"; exit;;
	esac

	echo "Viser lisenser. Trykk [Enter] for å åpne, trykk [q] for å lukke."
	echo -n "  - BBDebet2 "
	read
	less LICENSE
	echo -n "  - OpenJDK "
	read 
	less jdk/openjdk-12.license
	echo -n "  - OpenJFX "
	read 
	less jdk/openjfx-12.license
	echo -n "  - JavaMail "
	read 
	less lib/javax.mail.license
	echo -n "  - Apache POI "
	read 
	less lib/poi-4.0.1.license
done
