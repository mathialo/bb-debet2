/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.mailing;

import bbdebet2.kernel.Kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class EmailTemplateLoader {

    public static String getTemplate(EmailTemplate template) {
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


    public static void saveTemplate(EmailTemplate template, String newContent) {
        try {
            PrintWriter writer = new PrintWriter(
                new File(Kernel.SAVE_DIR + "templates/" + template.getFilename()));
            writer.println(newContent);
            writer.close();
        } catch (FileNotFoundException ignored) {
        }
    }
}
