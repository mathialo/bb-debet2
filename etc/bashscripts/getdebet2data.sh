#! /bin/bash
rsync --update --info=progress2 --partial -t -r bb@login.uio.no:~/.bbdebet2 ~
