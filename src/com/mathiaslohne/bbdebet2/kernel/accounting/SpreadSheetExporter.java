/*
 * Copyright (C) 2020  Mathias Lohne
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

package com.mathiaslohne.bbdebet2.kernel.accounting;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mathiaslohne.bbdebet2.gui.Main.SHORT_VERSION;


public class SpreadSheetExporter {

    private Kernel kernel;

    private Map<Account, CreditDebitPair> columns;

    private Workbook exportFile;
    private Sheet sheet;
    private int currentRowNum;

    private int sumColNum;


    public SpreadSheetExporter(Kernel kernel) {
        this.kernel = kernel;
    }


    private void createBook() {
        currentRowNum = 0;
        exportFile = new HSSFWorkbook();
        sheet = exportFile.createSheet("Bilag");
    }


    private void writeHeaders() {
        Row titleRow = sheet.createRow(currentRowNum);
        currentRowNum++;

        titleRow.createCell(0).setCellValue("Regnskap");
        Font titleFont = exportFile.createFont();
        titleFont.setFontHeight((short) (18 * 20));
        titleFont.setBold(true);
        CellStyle titleStyle = exportFile.createCellStyle();
        titleStyle.setFont(titleFont);
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.createRow(currentRowNum).createCell(0).setCellValue("Generert av BBDebet " + SHORT_VERSION);

        currentRowNum += 2;

        Row baseRow = sheet.createRow(currentRowNum);
        currentRowNum++;
        Row detailRow = sheet.createRow(currentRowNum);

        baseRow.createCell(0).setCellValue("Bilag");
        baseRow.createCell(1).setCellValue("Dato");
        baseRow.createCell(2).setCellValue("Kommentar");

        sheet.setColumnWidth(1, 12 * 255);
        sheet.setColumnWidth(2, 20 * 255);

        columns = new HashMap<>();

        int col = 3;
        for (Account account : kernel.getAccounts()) {
            columns.put(account, new CreditDebitPair(col, col + 1));

            sheet.addMergedRegion(new CellRangeAddress(baseRow.getRowNum(), baseRow.getRowNum(), col, col + 1));
            baseRow.createCell(col).setCellValue(account.toString());
            detailRow.createCell(col).setCellValue("Kredit");
            sheet.setColumnWidth(col, 10 * 255);
            col++;
            detailRow.createCell(col).setCellValue("Debet");
            sheet.setColumnWidth(col, 10 * 255);
            col++;
        }
        sumColNum = col;

        baseRow.createCell(sumColNum).setCellValue("Sum");

        currentRowNum++;
    }


    private void writeExpenses() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        CellStyle commentStyle = exportFile.createCellStyle();
        commentStyle.setWrapText(true);
        CellStyle moneyStyle = exportFile.createCellStyle();
        moneyStyle.setDataFormat(exportFile.createDataFormat().getFormat("# ##0.00\\ kr"));

        Row firstRow = null;
        Row lastRow = null;

        for (Expense expense : kernel.getLedger()) {
            Row row = sheet.createRow(currentRowNum);
            if (firstRow == null) firstRow = row;

            row.createCell(0).setCellValue(expense.getId());
            row.createCell(1).setCellValue(simpleDateFormat.format(new Date(expense.getTimestamp() * 1000)));
            row.createCell(2).setCellValue(expense.getComment());
            row.getCell(2).setCellStyle(commentStyle);

            for (Expense.Transaction transaction : expense.getFrom()) {
                Cell cell = row.createCell(columns.get(transaction.getAccount()).credit);
                cell.setCellValue(-transaction.getAmount());
                cell.setCellStyle(moneyStyle);
            }
            for (Expense.Transaction transaction : expense.getTo()) {
                Cell cell = row.createCell(columns.get(transaction.getAccount()).debit);
                cell.setCellValue(transaction.getAmount());
                cell.setCellStyle(moneyStyle);
            }

            String from = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getAddress().toString();
            String to = row.getCell(sumColNum - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getAddress().toString();

            row.createCell(sumColNum).setCellFormula("SUM(" + from + ":" + to + ")");

            lastRow = row;
            currentRowNum++;
        }

        currentRowNum++;
        Row sumRow = sheet.createRow(currentRowNum);
        sumRow.createCell(0).setCellValue("Sum");

        for (int i = 3; i < sumColNum; i++) {
            Cell cell = sumRow.createCell(i);

            String from = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getAddress().toString();
            String to = lastRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getAddress().toString();
            cell.setCellStyle(moneyStyle);
            cell.setCellFormula("SUM(" + from + ":" + to + ")");
        }
    }


    public void writeToFile(File file) throws IOException {
        createBook();
        writeHeaders();
        writeExpenses();

        exportFile.write(new FileOutputStream(file));
    }


    private static class CreditDebitPair {

        public int credit;
        public int debit;


        public CreditDebitPair(int credit, int debit) {
            this.credit = credit;
            this.debit = debit;
        }
    }
}
