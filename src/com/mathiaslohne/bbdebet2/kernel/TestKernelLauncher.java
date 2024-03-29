/*
 * Copyright (C) 2021  Mathias Lohne
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

package com.mathiaslohne.bbdebet2.kernel;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import javafx.application.Application;
import javafx.stage.Stage;


public class TestKernelLauncher extends Application {
    public static void main(String[] args) throws Exception {
        Kernel kernel = new Kernel();
        kernel.getEmailSender().sendMail(new User("mathias", "mathias@lohne.nu", -1), "test", "testing emails!");
    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}
