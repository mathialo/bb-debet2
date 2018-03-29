#! /bin/bash

compile_all() {
	echo "Kompillerer kildekode"

	# Finn alle kildefiler
	javafiles="$(find src/ -name "*java" | sed ':a;N;$!ba;s/\n/ /g')"

	# Sørg for at out finnes og er tom
	rm -rf out
	mkdir -p out

	# Kompiller
	javac -d out -Xlint:unchecked -cp src:lib/javax.mail.jar:lib/activation.jar $javafiles
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

	# Sørg for at /usr/local/share/bbdebet2 finnes og er tom
	sudo rm -rf /usr/local/share/bbdebet2
	sudo mkdir -p /usr/local/share/bbdebet2

	# Kopier filer
	sudo cp bbdebet2.jar /usr/local/share/bbdebet2/
	sudo cp -r lib/ /usr/local/share/bbdebet2/
	sudo cp -r etc/img /usr/local/share/bbdebet2/
	sudo cp etc/run.sh /usr/local/share/bbdebet2/
	sudo chmod +x /usr/local/share/bbdebet2/run.sh
	sudo cp etc/bashscripts/* /usr/local/share/bbdebet2/
	sudo chmod +x /usr/local/share/bbdebet2/senddebet2data.sh
	sudo chmod +x /usr/local/share/bbdebet2/getdebet2data.sh

	# Lag linker for tilgjengelighet i terminal og startmeny
	sudo cp etc/bbdebet2.desktop /usr/share/applications
	sudo ln -s /usr/local/share/bbdebet2/run.sh /usr/local/bin/bbdebet2
	sudo ln -s /usr/local/share/bbdebet2/senddebet2data.sh /usr/local/bin/senddebet2data
	sudo ln -s /usr/local/share/bbdebet2/getdebet2data.sh /usr/local/bin/getdebet2data
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

	# Kompiller
	compile_all

	# Kopier filer rundt dit de skal
	copy_files

	# Sørg for at lagre-mappene i ~ finnes.
	make_save_dirs

	# cleanup
	cleanup
}

# Kontroller at vi _ikke_ er root
if [[ "$EUID" == 0 ]]; then
	echo "Ikke kjør som root! Jeg fikser det selv."
else
	install_bbdebet2
fi
