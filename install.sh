#! /bin/bash
# Copyright (C) 2019  Mathias Lohne

# This program is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation, either version 3 of the License, or (at your option)
# any later version.

# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.

# You should have received a copy of the GNU General Public License along
# with this program.  If not, see <http://www.gnu.org/licenses/>.


# Les info fra filer
version="$(cat version)"
buildnum="$(cat buildnum)"

# Installasjonsstier
jdk_path="/usr/local/share/bbdebet2/jdk/jdk-12.0.1/bin"
javafx_path="/usr/local/share/bbdebet2/jdk/javafx-sdk-12/lib/"


infoprint() {
	echo "Installasjonsskript for BBDebet2."
	echo ""
}


licence_review() {
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
}


make_install_dirs() {
	sudo mkdir -p /usr/local/share/bbdebet2
	sudo mkdir -p /usr/local/share/bbdebet2/jdk
	sudo mkdir -p /usr/local/share/bbdebet2/plugins
}


jdk_install() {
	echo "[i] Installerer dependencies"
	echo " - OpenJDK (for lokalt bruk, eksisterende java-installasjoner blir"
	echo "   ikke påvirket)"
	sudo tar -xzf jdk/openjdk-12.0.1_linux-x64_bin.tar.gz -C /usr/local/share/bbdebet2/jdk/
	echo " - OpenJFX"
	sudo tar -xzf jdk/openjfx-12_linux-x64_bin-sdk.tar.gz -C /usr/local/share/bbdebet2/jdk/
}


preprocess_sources() {
	echo "[i] Konfigurerer build"

	# Oppdater versjonsnummer og build-nummer i kildefil
	echo "s/\(public static final String SHORT_VERSION\s=\s\"\)\(.*\)\(\";\)/\1$version:$buildnum\3/g"  > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java
	echo "s/\(public static final String FULL_VERSION\s=\s\"\)\(.*\)\(\";\)/\1BBdebet $version\\\\nBuild nr $buildnum\3/g" > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java

	# Dytt JDK og JavaFX inn i run.sh
	# Escape / i $jdk_path
	echo "$jdk_path/java" > JDK_PATH_ESCAPED_SLASHES
	sed 's/\//\\\//g' -i JDK_PATH_ESCAPED_SLASHES
	jdk_path_escaped_slashes="$(cat JDK_PATH_ESCAPED_SLASHES)"
	rm JDK_PATH_ESCAPED_SLASHES

	echo "s/JAVA_PATH/$jdk_path_escaped_slashes/g" > sedcommand
	sed -f sedcommand -i etc/run.sh
	rm sedcommand

	# Escape / i $javafx_path
	echo "$javafx_path" > JAVAFX_PATH_ESCAPED_SLASHES
	sed 's/\//\\\//g' -i JAVAFX_PATH_ESCAPED_SLASHES
	javafx_path_escaped_slashes="$(cat JAVAFX_PATH_ESCAPED_SLASHES)"
	rm JAVAFX_PATH_ESCAPED_SLASHES

	echo "s/JAVAFX_PATH/$javafx_path_escaped_slashes/g" > sedcommand
	sed -f sedcommand -i etc/run.sh
	rm sedcommand
}


postprocess_sources() {
	# Endre tilbake for å unngå kjipe commits
	echo "[i] Rydder opp"

	# Main.java 
	echo "s/\(public static final String SHORT_VERSION\s=\s\"\)\(.*\)\(\";\)/\1\3/g"  > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java
	echo "s/\(public static final String FULL_VERSION\s=\s\"\)\(.*\)\(\";\)/\1\3/g" > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java
	rm sedcommand

	# run.sh
	echo "s/$javafx_path_escaped_slashes/JAVAFX_PATH/g" > sedcommand
	sed -f sedcommand -i etc/run.sh
	rm sedcommand
}


compile_all() {
	echo "[i] Kompillerer kildekode"

	# Finn alle kildefiler
	javafiles="$(find src/ -name "*java" | sed ':a;N;$!ba;s/\n/ /g')"

	# Sørg for at out finnes og er tom
	rm -rf out
	mkdir -p out

	# Kompiller
	$jdk_path/javac -d out -Xlint:unchecked --module-path /usr/lib/jvm/javafx-sdk-11.0.2/lib --add-modules ALL-MODULE-PATH -cp src:lib/javax.mail.jar:lib/poi-4.0.1.jar $javafiles
	mkdir -p out/bbdebet2/gui/views
	cp -r src/bbdebet2/gui/views out/bbdebet2/gui/

	# Pakk alt inn i en JAR
	cd out
	classfiles="$(find . -type f | sed ':a;N;$!ba;s/\n/ /g')"
	$jdk_path/jar cfm bbdebet2.jar ../etc/MANIFEST.MF $classfiles
	cd ..
	mv out/bbdebet2.jar .
}


copy_files() {
	echo "[i] Kopierer filer"

	# Sørg for at /usr/local/share/bbdebet2 finnes
	sudo mkdir -p /usr/local/share/bbdebet2
    sudo mkdir -p /usr/local/share/bbdebet2/plugins

	# Kopier filer
	sudo cp bbdebet2.jar /usr/local/share/bbdebet2/
	sudo cp -r lib/ /usr/local/share/bbdebet2/
	sudo cp -r etc/img /usr/local/share/bbdebet2/
	sudo cp etc/run.sh /usr/local/share/bbdebet2/
	sudo chmod +x /usr/local/share/bbdebet2/run.sh
	sudo cp etc/bashscripts/* /usr/local/share/bbdebet2/
	sudo chmod +x /usr/local/share/bbdebet2/senddebet2data.sh
	sudo chmod +x /usr/local/share/bbdebet2/getdebet2data.sh
	sudo cp etc/manual_bbdebet2.html /usr/local/share/bbdebet2/

	# Lag linker for tilgjengelighet i terminal og startmeny. Ignorer feil, da
	# det kommer av at filene allerede finnes (hvis man innstallerer ny versjon)
	sudo cp etc/bbdebet2.desktop /usr/share/applications
	sudo ln -s /usr/local/share/bbdebet2/run.sh /usr/local/bin/bbdebet2 2> /dev/null
	sudo ln -s /usr/local/share/bbdebet2/senddebet2data.sh /usr/local/bin/senddebet2data 2> /dev/null
	sudo ln -s /usr/local/share/bbdebet2/getdebet2data.sh /usr/local/bin/getdebet2data 2> /dev/null
}


make_save_dirs() {
	mkdir -p ~/.bbdebet2
	mkdir -p ~/.bbdebet2/autosave
	mkdir -p ~/.bbdebet2/templates
}


cleanup() {
	rm -f bbdebet2.jar
}


install_bbdebet2() {
	# Info og lisensstyr
	infoprint
	licence_review

	echo ""
	echo "Begynner installasjon"

	# Sjekk at vi kan være root om vi vil
	echo "[i] Sjekker privilegier"
	sudo echo "Root-tilgang OK!"

	# Sørg for at /usr/local/share/bbdebet2 finnes
	make_install_dirs

	# Spør om JDK
	jdk_install

	# Gjør småendringer i kildefiler
	preprocess_sources

	# Kompiller
	compile_all

	# Kopier filer rundt dit de skal
	copy_files

	# Sørg for at lagre-mappene i ~ finnes.
	make_save_dirs

	# cleanup
	postprocess_sources
	cleanup
}


# Kontroller at vi _ikke_ er root
if [[ "$EUID" == 0 ]]; then
	echo "Ikke kjør som root! Jeg fikser det selv."
else
	install_bbdebet2
fi
