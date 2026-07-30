package com.expensemanager.report;

import com.expensemanager.model.report.ReportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelReportGenerator implements ReportGenerator {

    @Override
    public void generateReport(ReportData reportData,
                               File outputFile) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Report");

        int rowIndex = 0;

        Row row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("PERSONAL EXPENSE REPORT");

        row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("Start Date");

        row.createCell(1).setCellValue(
                reportData.getStartDate().toString());

        row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("End Date");

        row.createCell(1).setCellValue(
                reportData.getEndDate().toString());

        row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("Total Income");

        row.createCell(1).setCellValue(
                reportData.getTotalIncome());

        row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("Total Expense");

        row.createCell(1).setCellValue(
                reportData.getTotalExpense());

        row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue("Net Saving");

        row.createCell(1).setCellValue(
                reportData.getNetSaving());

        try (FileOutputStream fos =
                     new FileOutputStream(outputFile)) {

            workbook.write(fos);

        }

        workbook.close();
    }

}
