#! /bin/bash

# Les info fra filer
version="$(cat version)"
buildnum="$(cat buildnum)"

javafx_path="$(cat JAVAFX_PATH 2> /dev/null)"

# Skriv feilmelding dersom filen ikke finnes
if [[ $javafx_path == "" ]]; then
	echo "Feil: Ingen 'JAVAFX_PATH'-fil"
	echo ""
	echo "Installasjonsskriptet trenger 'JAVAFX_PATH' som inneholder adressen"
	echo "til JavaFX-installasjonen. Filen er laget, du må fikse innholdet før"
	echo "neste kjøring."

	echo "/PATH/TO/JAVAFX/lib" > JAVAFX_PATH
	exit
fi


preprocess_sources() {
	echo "Konfigurerer build"

	# Oppdater versjonsnummer og build-nummer i kildefil
	echo "s/\(public static final String SHORT_VERSION\s=\s\"\)\(.*\)\(\";\)/\1$version:$buildnum\3/g"  > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java
	echo "s/\(public static final String FULL_VERSION\s=\s\"\)\(.*\)\(\";\)/\1BBdebet $version\\\\nBuild nr $buildnum\3/g" > sedcommand
	sed -f sedcommand -i src/bbdebet2/gui/Main.java

	# Dytt JavaFX-sti inn i run.sh
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
	echo "Rydder opp"

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
	echo "Kompillerer kildekode"


	# Finn alle kildefiler
	javafiles="$(find src/ -name "*java" | sed ':a;N;$!ba;s/\n/ /g')"

	# Sørg for at out finnes og er tom
	rm -rf out
	mkdir -p out

	# Kompiller
	javac -d out -Xlint:unchecked --module-path /usr/lib/jvm/javafx-sdk-11.0.2/lib --add-modules ALL-MODULE-PATH -cp src:lib/javax.mail.jar:lib/activation.jar:lib/poi-4.0.1.jar $javafiles
	mkdir -p out/bbdebet2/gui/views
	cp -r src/bbdebet2/gui/views out/bbdebet2/gui/




	# Pakk alt inn i en JAR
	cd out
	classfiles="$(find . -type f | sed ':a;N;$!ba;s/\n/ /g')"
	jar cfm bbdebet2.jar ../etc/MANIFEST.MF $classfiles
	cd ..
	mv out/bbdebet2.jar .
}


copy_files() {
	echo "Kopierer filer"

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
	# Sjekk at vi kan være root om vi vil
	echo "Sjekker privilegier"
	sudo echo "Root-tilgang OK!"

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
