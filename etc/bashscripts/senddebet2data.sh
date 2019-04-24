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


# Clear previous backup data
ssh -T bb@login.uio.no <<'ENDSSH'
rm -rf ~/.bbdebet2/autosave/*
ENDSSH

# Send new data
if [[ "$1" == "-f" ]] || [[ "$1" == "--force" ]]; then
	rsync --ignore-errors --info=progress2 --ignore-times --times --recursive ~/.bbdebet2/autosave ~/.bbdebet2/hist ~/.bbdebet2/usertransactions.csv ~/.bbdebet2/moneyinserts.csv ~/.bbdebet2/processedInserts.csv ~/.bbdebet2/saleshistory.csv ~/.bbdebet2/users.usl ~/.bbdebet2/storage.csv  bb@login.uio.no:~/.bbdebet2/ 2> /dev/null
else
	rsync --ignore-errors --update --info=progress2 --partial --times --recursive ~/.bbdebet2/autosave ~/.bbdebet2/hist ~/.bbdebet2/usertransactions.csv ~/.bbdebet2/moneyinserts.csv ~/.bbdebet2/processedInserts.csv ~/.bbdebet2/saleshistory.csv ~/.bbdebet2/users.usl ~/.bbdebet2/storage.csv  bb@login.uio.no:~/.bbdebet2/ 2> /dev/null
fi
