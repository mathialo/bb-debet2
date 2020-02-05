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

import com.mathiaslohne.bbdebet2.gui.customelements.DeleteCategoryDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.EditCategoryDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.NewCategoryDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.SuggestionMenu;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewCategoryRule;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProduct;
import com.mathiaslohne.bbdebet2.kernel.core.CategoryDict;
import com.mathiaslohne.bbdebet2.kernel.core.Listable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;


public class CategoryManagement extends Applet {

    @FXML
    private TextField productNameInput;
    @FXML
    private TableView<ViewCategoryRule> categoryRuleTableView;
    @FXML
    private TableColumn<ViewCategoryRule, String> productNameView;
    @FXML
    private TableColumn<ViewCategoryRule, String> categoryNameView;
    @FXML
    private ChoiceBox<CategoryDict.Category> categoryChoiceBox;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Kategorier", "CategoryManagementView");
    }


    private void populateTableView() {
        productNameView.setCellValueFactory(
            new PropertyValueFactory<ViewCategoryRule, String>("productName")
        );
        categoryNameView.setCellValueFactory(
            new PropertyValueFactory<ViewCategoryRule, String>("categoryName")
        );

        categoryRuleTableView.getItems().clear();
        for (String productName : kernel.getCategories().getProductNames()) {
            categoryRuleTableView.getItems().add(
                new ViewCategoryRule(productName, kernel.getCategories().getProductCategory(productName))
            );
        }
    }


    @FXML
    public void newCategory(ActionEvent event) {
        NewCategoryDialog dialog = new NewCategoryDialog();
        dialog.showAndWait();
        populateChoiceBox();
    }


    @FXML
    public void editCategory(ActionEvent event) {
        EditCategoryDialog dialog = new EditCategoryDialog();
        dialog.showAndWait();
        populateChoiceBox();
        populateTableView();
    }


    @FXML
    public void deleteCategory(ActionEvent event) {
        DeleteCategoryDialog dialog = new DeleteCategoryDialog();
        dialog.showAndWait();
        populateChoiceBox();
        populateTableView();
    }


    @FXML
    public void addProductRule(ActionEvent event) {
        if (categoryChoiceBox.getSelectionModel().isEmpty()) return;

        kernel.getCategories().newCategoryRule(
            productNameInput.getText(),
            categoryChoiceBox.getSelectionModel().getSelectedItem()
        );

        populateTableView();
    }


    @FXML
    public void deleteSelectedProductRule(ActionEvent event) {
        if (categoryRuleTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Velg produktregel som skal slettes");
            alert.setHeaderText("Ingen produktregel valgt");
            alert.showAndWait();
            return;
        }

        kernel.getCategories().deleteCategoryRule(categoryRuleTableView.getSelectionModel().getSelectedItem().getProductName());
        populateTableView();
    }


    private void populateChoiceBox() {
        categoryChoiceBox.getItems().setAll(kernel.getCategories().getCategories());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Create suggestion menu for name input
        List<Listable<ViewProduct>> searchHere = new LinkedList<>();
        searchHere.add(kernel.getStorage());
        searchHere.add(kernel.getSalesHistory());
        SuggestionMenu<ViewProduct> suggestionMenu = new SuggestionMenu<>(productNameInput, searchHere);

        // Populate GUI
        populateTableView();
        populateChoiceBox();
    }


}
