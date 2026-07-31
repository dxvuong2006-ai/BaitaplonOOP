package com.expensemanager.report;

import com.expensemanager.model.report.ReportData;
import com.expensemanager.utils.CurrencyUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;

/** Xuất file dạng pdf. */
public class PdfReportGenerator implements ReportGenerator {

    /** Ghi đè. */
    @Override
    public void generateReport(ReportData reportData, File outputFile) throws IOException {

        PDDocument document = new PDDocument();

        PDPage page = new PDPage();

        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        content.beginText();

        content.setFont(PDType1Font.HELVETICA,12);

        content.setLeading(18);

        content.newLineAtOffset(50,750);

        content.showText("PERSONAL EXPENSE REPORT");

        content.newLine();

        content.showText("Period: "
                + reportData.getStartDate()
                + " - "
                + reportData.getEndDate());

        content.newLine();

        content.showText("Total income: "
                + CurrencyUtils.formatVND(
                reportData.getTotalIncome()));

        content.newLine();

        content.showText("Total expense: "
                + CurrencyUtils.formatVND(
                reportData.getTotalExpense()));

        content.newLine();

        content.showText("Net saving: "
                + CurrencyUtils.formatVND(
                reportData.getNetSaving()));

        content.endText();

        content.close();

        document.save(outputFile);

        document.close();
    }

}
