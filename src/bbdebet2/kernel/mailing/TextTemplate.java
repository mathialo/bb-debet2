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
