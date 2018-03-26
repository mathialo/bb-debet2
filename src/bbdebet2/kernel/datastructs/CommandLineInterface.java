/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import java.io.PrintStream;

public interface CommandLineInterface {
    public boolean parseAndRunCommand(String rawCommand, PrintStream outputStream);
}