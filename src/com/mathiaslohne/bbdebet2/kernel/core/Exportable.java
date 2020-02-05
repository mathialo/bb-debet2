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

package com.mathiaslohne.bbdebet2.kernel.core;

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
    void saveFile(File file) throws IOException;


    /**
     * Saves the object with an automatically generated filename.
     *
     * @throws IOException On IO errors
     */
    void saveFile() throws IOException;
}
