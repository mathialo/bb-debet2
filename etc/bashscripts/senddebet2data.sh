#! /bin/bash
# Clear previous backup data
ssh -T bb@login.uio.no <<'ENDSSH'
rm -rf ~/.bbdebet2/autosave/*
ENDSSH

# Send new data
rsync --update --info=progress2 --partial -t -r ~/.bbdebet2/autosave ~/.bbdebet2/hist ~/.bbdebet2/usertransactions.csv ~/.bbdebet2/saleshistory.csv ~/.bbdebet2/users.usl ~/.bbdebet2/storage.csv  bb@login.uio.no:~/.bbdebet2/
