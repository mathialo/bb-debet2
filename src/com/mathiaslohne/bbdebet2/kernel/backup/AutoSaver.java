/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mathiaslohne.bbdebet2.kernel.backup;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;


public class AutoSaver {

    private Kernel kernel;

    private String subdir;

    private boolean isInLoop;

    private int interval;

    private boolean placeInSubdirs;
    private boolean autoSend;

    private Process sendProc;


    public AutoSaver(Kernel kernel) {
        this(kernel, "");
    }


    public AutoSaver(Kernel kernel, String subdir) {
        this.kernel = kernel;
        this.subdir = subdir;

        isInLoop = false;
        interval = 10;
        placeInSubdirs = false;
        autoSend = false;
    }


    public void placeInSubdirs(boolean placeInSubdirs) {
        this.placeInSubdirs = placeInSubdirs;
    }


    public void autoSendToRemoteOnSave(boolean autoSend) {
        this.autoSend = autoSend;
    }


    public void start(int interval) {
        isInLoop = true;
        this.interval = interval;
        scheduleNew(interval);
    }


    public void scheduleNew(int minsFromNow) {
        Timeline autoSaveTimer = new Timeline(new KeyFrame(
            Duration.millis(minsFromNow * 60 * 1000),
            e -> saveAll()));

        autoSaveTimer.play();
    }


    private void saveAll() {
        if (placeInSubdirs) {
            // use epoch timestamp as new folder name
            long timestamp = System.currentTimeMillis() / 1000L;
            String newSubdir = subdir + timestamp + "/";

            // make sure directory exists
            File newDir = new File(Kernel.SAVE_DIR + newSubdir);
            newDir.mkdir();
            Kernel.getLogger().log("Backuping... " + timestamp);

            try {
                kernel.getSalesHistory().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.SALESHISTORY_FILENAME);
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }

            try {
                kernel.getUserList().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.USERLIST_FILENAME, true);
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }
            try {
                kernel.getInactiveUserList().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.INACTIVE_USERLIST_FILEPATH, true);
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }

            try {
                kernel.getStorage().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.STORAGE_FILENAME);
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }

            try {
                kernel.getLedger().saveFile(new File(Kernel.SAVE_DIR + newSubdir + Kernel.LEDGER_FILENAME));
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }

        } else {
            kernel.saveAll();
        }

        if (autoSend) {
            // make sure we dont end up with ghost procs
            if (sendProc != null && sendProc.isAlive()) {
                Kernel.getLogger().log("Killing previous sending process");
                sendProc.destroyForcibly();
            }

            Kernel.getLogger().log("Sending data to remote server");
            try {
                ProcessBuilder pb = new ProcessBuilder("senddebet2data");
                sendProc = pb.start();
            } catch (IOException e) {
                Kernel.getLogger().log(e);
            }
        }

        if (isInLoop) {
            scheduleNew(interval);
        }
    }

}