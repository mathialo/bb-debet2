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

if [[ "$1" == "--nogui" ]]; then
    JAVA_PATH -Dfile.encoding=UTF-8 --module-path JAVAFX_PATH --add-modules ALL-MODULE-PATH -jar INSTALL_PATH/bbdebet2.jar $*
else
    JAVA_PATH -Dfile.encoding=UTF-8 --module-path JAVAFX_PATH --add-modules ALL-MODULE-PATH -splash:INSTALL_PATH/img/splash.png -jar INSTALL_PATH/bbdebet2.jar $*
fi
