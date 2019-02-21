/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.mailing;

public enum TextTemplate {
    WELCOME("welcome", "Velkomstmail"),
    USERTURNEDNEGATIVE("userTurnedNegative", "\"Nå er du i minus\"-påminnelse"),
    USERADDEDMONEY("userAddedMoney", "Etter påfyll av saldo"),
    EULA("eula", "EULA (End-User License Agreement)");


    private String filename;
    private String displayText;


    TextTemplate(String filename, String displayText) {
        this.filename = filename;
        this.displayText = displayText;
    }


    public String getFilename() {
        return filename;
    }


    public boolean equals(TextTemplate other) {
        return filename.equals(other.getFilename());
    }


    @Override
    public String toString() {
        return displayText;
    }
}
