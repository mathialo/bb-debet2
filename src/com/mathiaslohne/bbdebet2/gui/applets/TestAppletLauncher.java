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

package com.mathiaslohne.bbdebet2.gui.applets;

import com.mathiaslohne.bbdebet2.gui.customelements.ConfirmFuzzyProductFind;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;


public class TestAppletLauncher extends Application {
    public static Kernel kernel;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        kernel = new Kernel();
//        InactiveUsers.createAndDisplayDialog();
        Product p = new Product("ipa", 1, 2);
        Set<String> f = kernel.getSalesHistory().fuzzyFind(p.getName());
        System.out.println(new ConfirmFuzzyProductFind(p.getName(), f).showAndWait());
    }
}
