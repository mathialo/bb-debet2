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

package com.mathiaslohne.bbdebet2.kernel.mailing;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TextTemplateLoader {

    public static String getTemplate(TextTemplate template) {
        StringBuilder content = new StringBuilder();

        try {
            Scanner fileReader = new Scanner(
                new File(Kernel.SAVE_DIR + "templates/" + template.getFilename()));

            while (fileReader.hasNextLine()) {
                content.append(fileReader.nextLine()).append("\n");
            }

            fileReader.close();
        } catch (FileNotFoundException ignored) {
        }

        return content.toString();
    }


    public static void saveTemplate(TextTemplate template, String newContent) {
        try {
            PrintWriter writer = new PrintWriter(
                new File(Kernel.SAVE_DIR + "templates/" + template.getFilename()));
            writer.println(newContent);
            writer.close();
        } catch (FileNotFoundException ignored) {
        }
    }
}
