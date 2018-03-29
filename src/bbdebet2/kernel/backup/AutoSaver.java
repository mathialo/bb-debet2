/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.backup;

import bbdebet2.kernel.Kernel;
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
            Duration.millis(minsFromNow*60*1000),
            e -> saveAll()));

        autoSaveTimer.play();
    }

    private void saveAll() {
        String newSubdir;

        if (placeInSubdirs) {
            // use epoch timestamp as new folder name
            long timestamp = System.currentTimeMillis() / 1000L;
            newSubdir = subdir + timestamp + "/";

            // make sure directory exists
            File newDir = new File(Kernel.SAVE_DIR + newSubdir);
            newDir.mkdir();
            kernel.getLogger().log("Backuping... " + timestamp);

        } else {
            newSubdir = subdir;
        }


        try {
            kernel.getSalesHistory().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.SALESHISTORY_FILENAME);
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }

        try {
            kernel.getUserList().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.USERLIST_FILENAME, true);
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }

        try {
            kernel.getStorage().saveFile(Kernel.SAVE_DIR + newSubdir + Kernel.STORAGE_FILENAME);
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }

        if (autoSend) {
            // make sure we dont end up with ghost procs
            if (sendProc != null && sendProc.isAlive()) {
                kernel.getLogger().log("Killing previous sending process");
                sendProc.destroyForcibly();
            }

            kernel.getLogger().log("Sending data to remote server");
            try {
                ProcessBuilder pb = new ProcessBuilder("senddebet2data");
                sendProc = pb.start();
            } catch (IOException e) {
                kernel.getLogger().log(e);
            }
        }

        if (isInLoop) {
            scheduleNew(interval);
        }
    }

}