/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

import java.io.File;
import java.io.IOException;

/**
 * Ensures that the objects of the class can be saved as a file in some way.
 */
public interface Exportable {

    /**
     * Saves the object. If the File object is a directory, it should place itself in the directory with a suitable, automatically generated filename.
     *
     * @param file Where to save
     *
     * @throws IOException On IO errors
     */
    public void saveFile(File file) throws IOException;


    /**
     * Saves the object with an automatically generated filename.
     *
     * @throws IOException On IO errors
     */
    public void saveFile() throws IOException;
}
