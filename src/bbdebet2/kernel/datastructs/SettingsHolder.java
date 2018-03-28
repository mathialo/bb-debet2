/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import bbdebet2.kernel.Kernel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SettingsHolder implements Exportable {

    private String emailAddr = "null";
    private String emailUser = "null";
    private String emailServer = "null";
    private String emailPort = "null";
    private String emailEncryption = "null";
    private String emailPass = "";
    private String emailReplyTo = "null";

    private int numOfFavourites = 3;
    private int maxInactiveTime = 30;
    private String adminPass = "";

    private int backupInterval = 12;
    private boolean autoSend = false;
    private boolean autoGet = false;

    private boolean autoNagUser = false;
    private boolean sendShoppingList = false;
    private boolean sendReports = false;

    private boolean glasUserActive = true;
    private int glasUserRoundTo = 5;
    private String glasUserName = "glass";

    private double lambda = 2;
    private int holydayWeeks = 0;


    public SettingsHolder() {
    }


    public SettingsHolder(File settingsFile) throws IOException, ErrorInFileException {
        readFile(settingsFile);
    }


    private void readFile(File settingsFile) throws IOException, ErrorInFileException {
        Scanner scanner = new Scanner(settingsFile);

        String[] line;
        int lineNum = 1;

        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().split("\\s*=\\s*");

                switch (line[0]) {
                    case "emailAddr":
                        emailAddr = line[1];
                        break;

                    case "emailUser":
                        emailUser = line[1];
                        break;

                    case "emailServer":
                        emailServer = line[1];
                        break;

                    case "emailPort":
                        emailPort = line[1];
                        break;

                    case "emailEncryption":
                        emailEncryption = line[1];
                        break;

                    case "emailReplyTo":
                        emailReplyTo = line[1];
                        break;

                    case "numOfFavourites":
                        numOfFavourites = Integer.parseInt(line[1]);
                        break;

                    case "maxInactiveTime":
                        maxInactiveTime = Integer.parseInt(line[1]);
                        break;

                    case "backupInterval":
                        backupInterval = Integer.parseInt(line[1]);
                        break;

                    case "autoGet":
                        autoGet = Boolean.parseBoolean(line[1]);
                        break;

                    case "autoSend":
                        autoSend = Boolean.parseBoolean(line[1]);
                        break;

                    case "autoNagUser":
                        autoNagUser = Boolean.parseBoolean(line[1]);
                        break;

                    case "sendShoppingList":
                        sendShoppingList = Boolean.parseBoolean(line[1]);
                        break;

                    case "sendReports":
                        sendReports = Boolean.parseBoolean(line[1]);
                        break;

                    case "lambda":
                        lambda = Double.parseDouble(line[1]);
                        break;

                    case "holydayWeeks":
                        holydayWeeks = Integer.parseInt(line[1]);
                        break;

                    case "glasUserActive":
                        glasUserActive = Boolean.parseBoolean(line[1]);
                        break;

                    case "glasUserRoundTo":
                        glasUserRoundTo = Integer.parseInt(line[1]);
                        break;

                    case "glasUserName":
                        glasUserName = line[1];
                        break;

                    default:
                }

                lineNum++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ErrorInFileException("Error in file, not enough elements on line " + lineNum);
        } catch (NumberFormatException e) {
            throw new ErrorInFileException(
                "Error in file, parsing error on line " + lineNum + ": " + e.getMessage());
        }

        // read passwords from dedicated file, as they may contain special characters in encrypted form
        Scanner scAdminPass = new Scanner(new File(Kernel.SAVE_DIR + ".adminPass"));
        Scanner scEmailPass = new Scanner(new File(Kernel.SAVE_DIR + ".emailPass"));

        StringBuilder encryptedAdminPass = new StringBuilder();
        while (scAdminPass.hasNext()) {
            encryptedAdminPass.append(scAdminPass.next());
        }

        adminPass = decipher(encryptedAdminPass.toString());

        StringBuilder encryptedEmailPass = new StringBuilder();
        while (scEmailPass.hasNext()) {
            encryptedEmailPass.append(scEmailPass.next());
        }

        emailPass = decipher(encryptedEmailPass.toString());
    }


    private String cipher(String rawText) {
        char[] asArr = rawText.toCharArray();
        char[] result = new char[asArr.length];

        for (int i = 0; i < asArr.length; i++) {
            result[i] = (char) (asArr[i] + calculateShift(i));
        }

        return 0 + new String(result);
    }


    private String decipher(String code) {
        char[] asArr = code.toCharArray();
        char[] result = new char[asArr.length - 1];

        for (int i = 1; i < asArr.length; i++) {
            result[i - 1] = (char) (asArr[i] - calculateShift(i - 1));
        }

        return new String(result);
    }


    private int calculateShift(int i) {
        return (i * 7) % 23 + i + 5;
    }


    public String getEmailAddr() {
        return emailAddr;
    }


    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }


    public String getEmailUser() {
        return emailUser;
    }


    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }


    public String getEmailServer() {
        return emailServer;
    }


    public void setEmailServer(String emailServer) {
        this.emailServer = emailServer;
    }


    public String getEmailPort() {
        return emailPort;
    }


    public void setEmailPort(String emailPort) {
        this.emailPort = emailPort;
    }


    public String getEmailEncryption() {
        return emailEncryption;
    }


    public void setEmailEncryption(String emailEncryption) {
        this.emailEncryption = emailEncryption;
    }


    public String getEmailPass() {
        return emailPass;
    }


    public void setEmailPass(String emailPass) {
        this.emailPass = emailPass;
    }


    public String getEmailReplyTo() {
        return emailReplyTo;
    }


    public void setEmailReplyTo(String emailReplyTo) {
        this.emailReplyTo = emailReplyTo;
    }


    public int getNumOfFavourites() {
        return numOfFavourites;
    }


    public void setNumOfFavourites(int numOfFavourites) {
        this.numOfFavourites = numOfFavourites;
    }


    public int getMaxInactiveTime() {
        return maxInactiveTime;
    }


    public void setMaxInactiveTime(int maxInactiveTime) {
        this.maxInactiveTime = maxInactiveTime;
    }


    public String getAdminPass() {
        return adminPass;
    }


    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }


    public int getBackupInterval() {
        return backupInterval;
    }


    public void setBackupInterval(int backupInterval) {
        this.backupInterval = backupInterval;
    }


    public boolean isAutoSend() {
        return autoSend;
    }


    public void setAutoSend(boolean autoSend) {
        this.autoSend = autoSend;
    }


    public boolean isAutoGet() {
        return autoGet;
    }


    public void setAutoGet(boolean autoGet) {
        this.autoGet = autoGet;
    }


    public boolean isAutoNagUser() {
        return autoNagUser;
    }


    public void setAutoNagUser(boolean autoNagUser) {
        this.autoNagUser = autoNagUser;
    }


    public boolean isSendShoppingList() {
        return sendShoppingList;
    }


    public void setSendShoppingList(boolean sendShoppingList) {
        this.sendShoppingList = sendShoppingList;
    }


    public boolean isSendReports() {
        return sendReports;
    }


    public void setSendReports(boolean sendReports) {
        this.sendReports = sendReports;
    }


    public boolean isGlasUserActive() {
        return glasUserActive;
    }


    public void setGlasUserActive(boolean glasUserActive) {
        this.glasUserActive = glasUserActive;
    }


    public int getGlasUserRoundTo() {
        return glasUserRoundTo;
    }


    public void setGlasUserRoundTo(int glasUserRoundTo) {
        this.glasUserRoundTo = glasUserRoundTo;
    }


    public String getGlasUserName() {
        return glasUserName;
    }


    public void setGlasUserName(String glasUserName) {
        this.glasUserName = glasUserName;
    }


    public double getLambda() {
        return lambda;
    }


    public void setLambda(double lambda) {
        this.lambda = lambda;
    }


    public int getHolydayWeeks() {
        return holydayWeeks;
    }


    public void setHolydayWeeks(int holydayWeeks) {
        this.holydayWeeks = holydayWeeks;
    }


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter pw = new PrintWriter(file);

        pw.println("emailAddr=" + emailAddr);
        pw.println("emailUser=" + emailUser);
        pw.println("emailServer=" + emailServer);
        pw.println("emailPort=" + emailPort);
        pw.println("emailEncryption=" + emailEncryption);
        pw.println("emailReplyTo=" + emailReplyTo);
        pw.println("numOfFavourites=" + numOfFavourites);
        pw.println("backupInterval=" + backupInterval);
        pw.println("autoSend=" + autoSend);
        pw.println("autoGet=" + autoGet);
        pw.println("maxInactiveTime=" + maxInactiveTime);
        pw.println("autoNagUser=" + autoNagUser);
        pw.println("sendShoppingList=" + sendShoppingList);
        pw.println("sendReports=" + sendReports);
        pw.println("lambda=" + lambda);
        pw.println("holydayWeeks=" + holydayWeeks);
        pw.println("glasUserActive=" + glasUserActive);
        pw.println("glasUserRoundTo=" + glasUserRoundTo);
        pw.println("glasUserName=" + glasUserName);

        pw.close();


        // save special files
        pw = new PrintWriter(new File(Kernel.SAVE_DIR + ".adminPass"));
        pw.println(cipher(adminPass));
        pw.close();

        pw = new PrintWriter(new File(Kernel.SAVE_DIR + ".emailPass"));
        pw.println(cipher(emailPass));
        pw.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.SETTINGS_FILEPATH));
    }
}
