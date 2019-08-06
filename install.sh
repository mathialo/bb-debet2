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

# Avbryt på feil
set -e

# Les info fra filer
version="$(cat version)"
buildnum="$(cat buildnum)"


infoprint() {
	echo "Installasjonsskript for BBDebet2, versjon $version build nummer $buildnum."
	echo ""
}


oscheck() {
	echo "[i] Detekterer operativsystem"
	if [[ "$OSTYPE" == "linux-gnu" ]]; then
    	os="linux"
	elif [[ "$OSTYPE" == "darwin"* ]]; then
		os="mac"
	else
		echo "Kjenner ikke igjen OS. Avbryter."
		exit 1
	fi

	echo "[i] Sjekker at nødvendig programvare er installert"
	echo ""
	error=0
	if ! command -v "wget" > /dev/null; then
	    echo "Kan ikke finne en installasjon av wget"
	    error=1
	fi

	if [[ "$error" == "1" ]]; then
	    echo "Du må installere overnevnt programvare for å fortsette installasjonen"
	    exit 1
	fi
	echo "Starter installasjon for $os"
	echo ""
}


licence_review() {
	echo "BBDebet2 er lisensiert under GPLv3. I tillegg bygger BBDebet2 på følgende"
	echo "pakker med tilhørende lisenser:"
	echo ""
	echo "  - OpenJDK, lisensiert under GPLv2 + CP"
	echo "  - OpenJFX, lisensiert under GPLv2 + CP"
	echo "  - JavaMail, lisensiert under CDDL og GPLv2 + CP"
	echo "  - JavaBeans Activation Framework, lisensiert under Sun ENTITLEMENT for SOFTWARE"
	echo "  - Apache POI, lisensiert under Apache v2"

	while [[ 1 ]]; do
		echo ""
		echo "Godtar du lisensene?"
		echo " y:  Godta"
		echo " n:  Avbryt"
		echo " l:  Se gjennom"
		read -p "[y/n/l]:   " yn

		case $yn in
			[Yy]* ) return;;
			[Nn]* ) echo "Lisensene må godtas for å kunne installere. Avbryter.";exit 0;;
			[Ll]* ) echo;;
			* ) echo "Ugyldig input. Antar avvisning av lisenser. Avbryter"; exit 0;;
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
		echo -n "  - JavaBeans Activation Framework "
		read
		less lib/activation.license
		echo -n "  - Apache POI "
		read
		less lib/poi-4.0.1.license
	done
}


repo_clean() {
	rm -f jdk/*.tar.gz
}


ask_install_path() {
	if [[ "$os" == "linux" ]]; then
		# Beregn plasskrav
		space_req=$(du -b --exclude "./.*" --max-depth=0 | cut -f 1)  # Fra repo
		space_req=$((space_req + 243971123))                          # Pluss JDK

		echo ""
		echo "Hvor skal BBDebet2 installeres? Trykk [Enter] uten å skrive noe for"
		echo "standard-plassering (/usr/local/share). Det må være $(numfmt --to=iec --suffix=B $space_req) ledig på"
		echo "partsisjonen du installerer på."

		read -p "[plassering]:  " userentered_path

		if [[ "$userentered_path" == "" ]]; then
			install_path="/usr/local/share/bbdebet2"
		else
			install_path=$userentered_path/bbdebet2
		fi
	elif [[ "$os" == "mac" ]]; then
		install_path="/Applications/BBDebet2.app/Contents/MacOS"
	fi
}


read_install_path() {
	install_path="$(cat ~/.bbdebet2/.installdir)"
	jdk_path="$install_path/jdk/jdk-12.0.1/bin"
	javafx_path="$install_path/jdk/javafx-sdk-12/lib/"
}


make_install_dirs() {
	jdk_version="$(tar -tzf jdk/openjdk.tar.gz | cut -d "/" -f 1 | uniq | tail -n 1)"
	jfx_version="$(tar -tzf jdk/openjfx.tar.gz | cut -d "/" -f 1 | uniq | tail -n 1)"

	jdk_path="$install_path/jdk/$jdk_version/bin"
	javafx_path="$install_path/jdk/$jfx_version/lib/"

	sudo mkdir -p $install_path
	sudo mkdir -p $install_path/jdk
	sudo mkdir -p $install_path/plugins

	if [[ "$os" == "mac" ]]; then
		sudo mkdir -p "/Applications/BBDebet2.app/Contents/Resources"
	fi
}


jdk_download() {
	echo "[i] Laster ned dependencies"
	if [[ "$os" == "linux" ]]; then
		wget -q --show-progress -P jdk/ http://folk.uio.no/bb/bbdebet2stuff/jdk/openjdk-12.0.1_linux-x64_bin.tar.gz
		wget -q --show-progress -P jdk/ http://folk.uio.no/bb/bbdebet2stuff/jdk/openjfx-12_linux-x64_bin-sdk.tar.gz
		mv jdk/openjdk-12.0.1_linux-x64_bin.tar.gz jdk/openjdk.tar.gz
		mv jdk/openjfx-12_linux-x64_bin-sdk.tar.gz jdk/openjfx.tar.gz
	elif [[ "$os" == "mac" ]]; then
		wget -q --show-progress -P jdk/ http://folk.uio.no/bb/bbdebet2stuff/jdk/openjdk-12.0.1_osx-x64_bin.tar.gz
		wget -q --show-progress -P jdk/ http://folk.uio.no/bb/bbdebet2stuff/jdk/openjfx-12.0.1_osx-x64_bin-sdk.tar.gz
		mv jdk/openjdk-12.0.1_osx-x64_bin.tar.gz jdk/openjdk.tar.gz
		mv jdk/openjfx-12.0.1_osx-x64_bin-sdk.tar.gz jdk/openjfx.tar.gz
	fi
}


jdk_install() {
	echo "[i] Installerer dependencies"
	echo " - OpenJDK (for lokalt bruk, eksisterende java-installasjoner blir"
	echo "   ikke påvirket)"
	sudo tar -xzf jdk/openjdk.tar.gz -C $install_path/jdk/
	echo " - OpenJFX"
	sudo tar -xzf jdk/openjfx.tar.gz -C $install_path/jdk/
}


substitute() {
	# sed er implementert forskjellig i BSD og GNU
	if [[ "$os" == "mac" ]]; then
		sed -i "" -f sedcommand $1
	elif [[ "$os" == "linux" ]]; then
		sed -i -f sedcommand $1
	fi
}


preprocess_sources() {
	echo "[i] Konfigurerer build"

	# Oppdater versjonsnummer og build-nummer i kildefil
	echo "s/\(public static final String SHORT_VERSION\s=\s\"\)\(.*\)\(\";\)/\1$version:$buildnum\3/g"  > sedcommand
	substitute src/bbdebet2/gui/Main.java
	echo "s/\(public static final String FULL_VERSION\s=\s\"\)\(.*\)\(\";\)/\1BBdebet $version\\\\nBuild nr $buildnum\3/g" > sedcommand
	substitute src/bbdebet2/gui/Main.java

	# Dytt JDK og JavaFX inn i run.sh
	# Escape / i $jdk_path
	echo "$jdk_path/java" > JDK_PATH_ESCAPED_SLASHES
	echo 's/\//\\\//g' > sedcommand
	substitute JDK_PATH_ESCAPED_SLASHES

	jdk_path_escaped_slashes="$(cat JDK_PATH_ESCAPED_SLASHES)"
	rm JDK_PATH_ESCAPED_SLASHES

	echo "s/JAVA_PATH/$jdk_path_escaped_slashes/g" > sedcommand
	substitute etc/run.sh

	# Escape / i $javafx_path
	echo "$javafx_path" > JAVAFX_PATH_ESCAPED_SLASHES
	echo 's/\//\\\//g' > sedcommand
	substitute JAVAFX_PATH_ESCAPED_SLASHES
	javafx_path_escaped_slashes="$(cat JAVAFX_PATH_ESCAPED_SLASHES)"
	rm JAVAFX_PATH_ESCAPED_SLASHES

	echo "s/JAVAFX_PATH/$javafx_path_escaped_slashes/g" > sedcommand
	substitute etc/run.sh

	# Escape / i $javafx_path
	echo "$install_path" > INSTALL_PATH_ESCAPED_SLASHES
	echo 's/\//\\\//g' > sedcommand
	substitute INSTALL_PATH_ESCAPED_SLASHES
	install_path_escaped_slashes="$(cat INSTALL_PATH_ESCAPED_SLASHES)"
	rm INSTALL_PATH_ESCAPED_SLASHES

	echo "s/INSTALL_PATH/$install_path_escaped_slashes/g" > sedcommand
	substitute etc/run.sh

	# Fiks desktop-fil til startmenyen
	echo "s/INSTALL_PATH/$install_path_escaped_slashes/g" > sedcommand
	substitute etc/bbdebet2.desktop

	rm sedcommand
}


postprocess_sources() {
	# Endre tilbake for å unngå kjipe commits
	echo "[i] Rydder opp"

	# Main.java
	echo "s/\(public static final String SHORT_VERSION\s=\s\"\)\(.*\)\(\";\)/\1\3/g"  > sedcommand
	substitute src/bbdebet2/gui/Main.java
	echo "s/\(public static final String FULL_VERSION\s=\s\"\)\(.*\)\(\";\)/\1\3/g" > sedcommand
	substitute src/bbdebet2/gui/Main.java

	# run.sh
	echo "s/$jdk_path_escaped_slashes/JAVA_PATH/g" > sedcommand
	substitute etc/run.sh

	echo "s/$javafx_path_escaped_slashes/JAVAFX_PATH/g" > sedcommand
	substitute etc/run.sh

	echo "s/$install_path_escaped_slashes/INSTALL_PATH/g" > sedcommand
	substitute etc/run.sh

	# bbdebet2.desktop
	echo "s/$install_path_escaped_slashes/INSTALL_PATH/g" > sedcommand
	substitute etc/bbdebet2.desktop
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
	$jdk_path/javac -d out -Xlint:unchecked --module-path $javafx_path --add-modules ALL-MODULE-PATH -cp src:lib/javax.mail.jar:lib/poi-4.0.1.jar:lib/activation.jar $javafiles
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

	# Kopier filer
	echo " - BBDebet2"
	sudo cp bbdebet2.jar $install_path/
	sudo cp etc/run.sh $install_path/
	sudo chmod +x $install_path/run.sh
	sudo cp etc/bashscripts/* $install_path/
	sudo chmod +x $install_path/senddebet2data.sh
	sudo chmod +x $install_path/getdebet2data.sh
	sudo cp etc/manual_bbdebet2.html $install_path/
	sudo cp -r etc/img $install_path/

	for f in lib/*.jar; do echo " - $(echo $f | cut -d "/" -f 2 | sed 's/.jar//g')"; done
	sudo cp -r lib $install_path/

	# Lag linker for tilgjengelighet i terminal og startmeny.
	if [[ "$os" == "linux" ]]; then
		echo " - Lager lenker i /usr/local/bin og /usr/share/applications"
		sudo cp etc/bbdebet2.desktop /usr/share/applications
		sudo ln -sf $install_path/run.sh /usr/local/bin/bbdebet2
		sudo ln -sf $install_path/senddebet2data.sh /usr/local/bin/senddebet2data
		sudo ln -sf $install_path/getdebet2data.sh /usr/local/bin/getdebet2data
	else
		echo " - Lager lenker i /usr/local/bin"
		sudo ln -sf $install_path/run.sh /usr/local/bin/bbdebet2
		sudo ln -sf $install_path/senddebet2data.sh /usr/local/bin/senddebet2data
		sudo ln -sf $install_path/getdebet2data.sh /usr/local/bin/getdebet2data

		echo " - Gjør ferdig pakking av mac-applikasjon"
		sudo cp etc/img/bblogo_512.icns /Applications/BBDebet2.app/Contents/Resources/bblogo.icns
		sudo cp etc/Info.plist /Applications/BBDebet2.app/Contents/
	fi

	# Lagre sti til installeringsmappe
	echo "$install_path" > ~/.bbdebet2/.installdir
}


make_save_dirs() {
	mkdir -p ~/.bbdebet2
	mkdir -p ~/.bbdebet2/autosave
	mkdir -p ~/.bbdebet2/templates
}


cleanup() {
	rm -f bbdebet2.jar
}


root_check() {
	echo "[i] Sjekker privilegier. "
	sudo echo "    -> Root-tilgang OK!"
}


install_bbdebet2() {
	# Info og lisensstyr
	infoprint
	oscheck
	licence_review
	# repo_clean

	# Spør om plassering
	ask_install_path

	echo ""
	echo "Begynner installasjon."
	echo ""

	# Sjekk at vi kan være root om vi vil
	root_check

	# Sørg for at lagre-mappene i ~ finnes.
	make_save_dirs

	# Last ned JDK (leser info fra disse i make_install_dirs)
	# jdk_download

	# Sørg for at $install_path og tilhørende submapper finnes
	make_install_dirs

	# Installer JDK
	# jdk_install

	# Gjør småendringer i kildefiler
	preprocess_sources

	# Kompiller
	compile_all

	# Kopier filer rundt dit de skal
	copy_files

	# cleanup
	postprocess_sources
	cleanup
}


update_bbdebet2() {
	# Info
	infoprint
	oscheck

	echo "Begynner oppdattering."
	echo ""

	# Sjekk at vi kan være root om vi vil
	root_check

	# Sørg for at $install_path finnes
	read_install_path

	# Gjør småendringer i kildefiler
	preprocess_sources

	# Kompiller
	compile_all

	# Kopier filer rundt dit de skal
	copy_files

	# cleanup
	postprocess_sources
	cleanup
}


remove_bbdebet2() {
	infoprint
	oscheck

	# Spør om bekreftelse
	echo "Programmet vil nå slette BBDebet2 fra systemet. Er du sikker?"
	read -p "[y/n]:   " yn
	case $yn in
		[Yy]* ) echo;;
		[Nn]* ) echo "Avbryter.";exit 0;;
		* ) echo "Ugyldig input. Avbryter"; exit 0;;
	esac

	read_install_path
	root_check

	echo "[i] Sletter filer"
	sudo rm -r $install_path
	sudo rm -r /usr/local/bin/bbdebet2
	sudo rm -r /usr/local/bin/senddebet2data
	sudo rm -r /usr/local/bin/getdebet2data
	if [[ "$os" == "linux" ]]; then
		sudo rm -r /usr/share/applications/bbdebet2.desktop
	elif [[ "$os" == "mac" ]]; then
		sudo rm -r /Applications/BBDebet2.app
	fi
	sudo rm -rf ~/.bbdebet2.gui.Main

	echo ""
	echo "Ønsker du å slette lagrede filer (salgshistorikk, brukerdata, etc..) også?"
	read -p "[y/n]:   " yn
	case $yn in
		[Yy]* ) echo;;
		[Nn]* ) exit 0;;
		* ) echo "Ugyldig input. Sletter ikke."; exit 0;;
	esac

	echo "[i] Sletter brukerdata"
	sudo rm -r ~/.bbdebet2
}


# Kontroller at vi _ikke_ er root
if [[ "$EUID" == 0 ]]; then
	echo "Ikke kjør som root! Jeg fikser det selv."

elif [[ "$1" == "avinstaller" ]]; then
	remove_bbdebet2

	echo ""
	echo "BBDebet2 er avinstallert. Generelt!"

elif [[ "$1" == "installer" ]]; then
	install_bbdebet2

	echo ""
	echo "BBDebet2 er installert. Generelt!"

elif [[ "$1" == "oppdatter" ]]; then
	update_bbdebet2

	echo ""
	echo "BBDebet2 er oppdattert. Generelt!"

else
	echo "Ingen kommando gitt. Installasjonsprogrammet kjøres slik:"
	echo ""
	echo "   $ ./install.sh <kommando>"
	echo ""
	echo "Der <kommando> kan være:"
	echo "    - installer"
	echo "    - avinstaller"
	echo "    - oppdatter"
fi
