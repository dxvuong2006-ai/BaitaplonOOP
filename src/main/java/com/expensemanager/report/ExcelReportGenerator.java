package com.expensemanager.report;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.utils.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/** Xuất báo cáo dạng Excel, có tiêu đề, thẻ tổng quan và bảng chi tiết theo danh mục/ví. */
public class ExcelReportGenerator implements ReportGenerator {

    private static final short COLOR_PRIMARY = IndexedColors.DARK_BLUE.getIndex();
    private static final short COLOR_INCOME = IndexedColors.GREEN.getIndex();
    private static final short COLOR_EXPENSE = IndexedColors.RED.getIndex();
    private static final short COLOR_HEADER_BG = IndexedColors.GREY_25_PERCENT.getIndex();

    private Workbook workbook;
    private Sheet sheet;
    private CellStyle titleStyle;
    private CellStyle subtitleStyle;
    private CellStyle sectionHeaderStyle;
    private CellStyle tableHeaderStyle;
    private CellStyle labelStyle;
    private CellStyle moneyStyle;
    private CellStyle moneyPositiveStyle;
    private CellStyle moneyNegativeStyle;
    private int rowIndex;

    @Override
    public void generateReport(ReportData reportData, File outputFile) throws IOException {

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Báo cáo");
        buildStyles();
        rowIndex = 0;

        writeTitle(reportData);
        writeSummary(reportData);
        writeDetailTable("CHI TIÊU THEO DANH MỤC",
                reportData.getExpenseByCategory(), ExcelReportGenerator::categoryName, false);
        writeDetailTable("THU NHẬP THEO DANH MỤC",
                reportData.getIncomeByCategory(), ExcelReportGenerator::categoryName, true);
        writeDetailTable("CHI TIÊU THEO VÍ",
                reportData.getExpenseByWallet(), ExcelReportGenerator::walletName, false);

        autoSizeColumns();

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    // ===================== STYLE =====================

    private void buildStyles() {
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(COLOR_PRIMARY);
        titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);

        Font subtitleFont = workbook.createFont();
        subtitleFont.setItalic(true);
        subtitleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        subtitleStyle = workbook.createCellStyle();
        subtitleStyle.setFont(subtitleFont);

        Font sectionFont = workbook.createFont();
        sectionFont.setBold(true);
        sectionFont.setFontHeightInPoints((short) 12);
        sectionHeaderStyle = workbook.createCellStyle();
        sectionHeaderStyle.setFont(sectionFont);
        sectionHeaderStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

        Font tableHeaderFont = workbook.createFont();
        tableHeaderFont.setBold(true);
        tableHeaderFont.setColor(IndexedColors.WHITE.getIndex());
        tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setFont(tableHeaderFont);
        tableHeaderStyle.setFillForegroundColor(COLOR_PRIMARY);
        tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorder(tableHeaderStyle);

        labelStyle = workbook.createCellStyle();
        applyThinBorder(labelStyle);

        moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"₫\""));
        applyThinBorder(moneyStyle);
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);

        Font incomeFont = workbook.createFont();
        incomeFont.setColor(COLOR_INCOME);
        moneyPositiveStyle = workbook.createCellStyle();
        moneyPositiveStyle.cloneStyleFrom(moneyStyle);
        moneyPositiveStyle.setFont(incomeFont);

        Font expenseFont = workbook.createFont();
        expenseFont.setColor(COLOR_EXPENSE);
        moneyNegativeStyle = workbook.createCellStyle();
        moneyNegativeStyle.cloneStyleFrom(moneyStyle);
        moneyNegativeStyle.setFont(expenseFont);
    }

    private void applyThinBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // ===================== TIÊU ĐỀ + TỔNG QUAN =====================

    private void writeTitle(ReportData reportData) {
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO CHI TIÊU CÁ NHÂN");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Kỳ báo cáo: "
                + DateUtils.formatDate(reportData.getStartDate())
                + "  -  "
                + DateUtils.formatDate(reportData.getEndDate()));
        subtitleCell.setCellStyle(subtitleStyle);

        rowIndex++; // dòng trống
    }

    private void writeSummary(ReportData reportData) {
        writeSummaryRow("Tổng thu", reportData.getTotalIncome(), moneyPositiveStyle);
        writeSummaryRow("Tổng chi", reportData.getTotalExpense(), moneyNegativeStyle);
        double net = reportData.getNetSaving();
        writeSummaryRow("Chênh lệch", net, net >= 0 ? moneyPositiveStyle : moneyNegativeStyle);
        writeSummaryRow("Tổng số giao dịch", reportData.getTotalTransactions(), null);
        rowIndex++; // dòng trống
    }

    private void writeSummaryRow(String label, double value, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIndex++);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        if (valueStyle != null) {
            valueCell.setCellStyle(valueStyle);
        }
    }

    // ===================== BẢNG CHI TIẾT =====================

    private <K> void writeDetailTable(String title, Map<K, Double> data,
                                      java.util.function.Function<K, String> nameFn,
                                      boolean isIncome) {
        if (data == null || data.isEmpty()) {
            return; // Không tạo bảng rỗng
        }

        Row sectionRow = sheet.createRow(rowIndex++);
        Cell sectionCell = sectionRow.createCell(0);
        sectionCell.setCellValue(title);
        sectionCell.setCellStyle(sectionHeaderStyle);

        Row headerRow = sheet.createRow(rowIndex++);
        writeHeaderCell(headerRow, 0, "Tên");
        writeHeaderCell(headerRow, 1, "Số tiền");

        CellStyle valueStyle = isIncome ? moneyPositiveStyle : moneyStyle;

        for (Map.Entry<K, Double> entry : data.entrySet()) {
            Row row = sheet.createRow(rowIndex++);

            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(nameFn.apply(entry.getKey()));
            nameCell.setCellStyle(labelStyle);

            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(entry.getValue());
            valueCell.setCellStyle(valueStyle);
        }

        rowIndex++; // dòng trống giữa các bảng
    }

    private void writeHeaderCell(Row row, int col, String text) {
        Cell cell = row.createCell(col);
        cell.setCellValue(text);
        cell.setCellStyle(tableHeaderStyle);
    }

    private void autoSizeColumns() {
        for (int col = 0; col <= 3; col++) {
            sheet.autoSizeColumn(col);
            int currentWidth = sheet.getColumnWidth(col);
            sheet.setColumnWidth(col, Math.max(currentWidth, 3500));
        }
    }

    private static String categoryName(Category category) {
        return category == null ? "(Không có danh mục)" : category.getName();
    }

    private static String walletName(Wallet wallet) {
        return wallet == null ? "(Không có ví)" : wallet.getName();
    }
}
