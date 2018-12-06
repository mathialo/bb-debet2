#! /bin/bash
if [[ "$1" == "--nogui" ]]; then
    java -Dfile.encoding=UTF-8 -jar /usr/local/share/bbdebet2/bbdebet2.jar $*
else
    java -Dfile.encoding=UTF-8 -splash:/usr/local/share/bbdebet2/img/splash.png -jar /usr/local/share/bbdebet2/bbdebet2.jar $*
fi
