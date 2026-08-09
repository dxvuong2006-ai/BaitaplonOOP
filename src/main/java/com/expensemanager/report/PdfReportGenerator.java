package com.expensemanager.report;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.utils.DateUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Xuất báo cáo dạng PDF, có tiêu đề, bảng tổng quan và bảng chi tiết theo danh mục/ví. */
public class PdfReportGenerator implements ReportGenerator {

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    private static final Color COLOR_PRIMARY = new Color(37, 99, 235);   // xanh dương
    private static final Color COLOR_INCOME = new Color(22, 163, 74);    // xanh lá
    private static final Color COLOR_EXPENSE = new Color(220, 38, 38);   // đỏ
    private static final Color COLOR_HEADER_BG = new Color(243, 244, 246);
    private static final Color COLOR_BORDER = new Color(209, 213, 219);
    private static final Color COLOR_TEXT = new Color(31, 41, 55);
    private static final Color COLOR_MUTED = new Color(107, 114, 128);

    private PDDocument document;
    private PDPageContentStream content;
    private PDFont fontRegular;
    private PDFont fontBold;
    private float cursorY;

    @Override
    public void generateReport(ReportData reportData, File outputFile) throws IOException {

        document = new PDDocument();
        fontRegular = loadFont(document, "/fonts/times.ttf");
        fontBold = loadFont(document, "/fonts/timesbd.ttf", "/fonts/times.ttf");

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        content = new PDPageContentStream(document, page);
        cursorY = PAGE_HEIGHT - MARGIN;

        try {
            drawTitle(reportData);
            drawSummaryCards(reportData);
            drawTable("CHI TIÊU THEO DANH MỤC", toRows(reportData.getExpenseByCategory(), Category::getName));
            drawTable("THU NHẬP THEO DANH MỤC", toRows(reportData.getIncomeByCategory(), Category::getName));
            drawTable("CHI TIÊU THEO VÍ", toRows(reportData.getExpenseByWallet(), Wallet::getName));
            drawFooter();
        } finally {
            content.close();
        }

        document.save(outputFile);
        document.close();
    }

    // ===================== TIÊU ĐỀ =====================

    private void drawTitle(ReportData reportData) throws IOException {
        setFillColor(COLOR_PRIMARY);
        drawText(fontBold, 20, MARGIN, cursorY, "BÁO CÁO CHI TIÊU CÁ NHÂN");
        cursorY -= 22;

        setFillColor(COLOR_MUTED);
        String period = "Kỳ báo cáo: " + DateUtils.formatDate(reportData.getStartDate())
                + "  -  " + DateUtils.formatDate(reportData.getEndDate());
        drawText(fontRegular, 11, MARGIN, cursorY, period);
        cursorY -= 10;

        drawHorizontalLine(COLOR_BORDER, 1f);
        cursorY -= 22;
    }

    // ===================== 3 THẺ TỔNG QUAN =====================

    private void drawSummaryCards(ReportData reportData) throws IOException {
        float cardWidth = (CONTENT_WIDTH - 2 * 12) / 3;
        float cardHeight = 56;
        float startX = MARGIN;

        drawSummaryCard(startX, cursorY - cardHeight, cardWidth, cardHeight,
                "TỔNG THU", CurrencyUtils.formatVND(reportData.getTotalIncome()), COLOR_INCOME);

        drawSummaryCard(startX + cardWidth + 12, cursorY - cardHeight, cardWidth, cardHeight,
                "TỔNG CHI", CurrencyUtils.formatVND(reportData.getTotalExpense()), COLOR_EXPENSE);

        double net = reportData.getNetSaving();
        Color netColor = net >= 0 ? COLOR_INCOME : COLOR_EXPENSE;
        drawSummaryCard(startX + 2 * (cardWidth + 12), cursorY - cardHeight, cardWidth, cardHeight,
                "CHÊNH LỆCH", CurrencyUtils.formatVND(net), netColor);

        cursorY -= (cardHeight + 26);
    }

    private void drawSummaryCard(float x, float y, float width, float height,
                                 String label, String value, Color accentColor) throws IOException {
        // Nền thẻ
        content.setNonStrokingColor(COLOR_HEADER_BG);
        content.addRect(x, y, width, height);
        content.fill();

        // Viền trái nhấn màu
        content.setNonStrokingColor(accentColor);
        content.addRect(x, y, 3, height);
        content.fill();

        setFillColor(COLOR_MUTED);
        drawText(fontRegular, 9, x + 12, y + height - 18, label);

        setFillColor(COLOR_TEXT);
        drawText(fontBold, 13, x + 12, y + 14, fitText(value, fontBold, 13, width - 20));
    }

    // ===================== BẢNG CHI TIẾT =====================

    private <K> List<Map.Entry<K, Double>> toRows(Map<K, Double> source, java.util.function.Function<K, String> ignored) {
        return source == null ? List.of() : List.copyOf(source.entrySet());
    }

    private void drawTable(String title, List<? extends Map.Entry<?, Double>> rows) throws IOException {
        if (rows.isEmpty()) {
            return; // Không vẽ bảng rỗng, tránh báo cáo dài vô ích
        }

        ensureSpace(30 + rows.size() * 20 + 20);

        setFillColor(COLOR_TEXT);
        drawText(fontBold, 12, MARGIN, cursorY, title);
        cursorY -= 18;

        float col1X = MARGIN;
        float col2X = MARGIN + CONTENT_WIDTH - 140;
        float rowHeight = 20;

        // Header
        content.setNonStrokingColor(COLOR_HEADER_BG);
        content.addRect(MARGIN, cursorY - rowHeight + 6, CONTENT_WIDTH, rowHeight);
        content.fill();
        setFillColor(COLOR_MUTED);
        drawText(fontBold, 9, col1X + 8, cursorY - 8, "Tên");
        drawText(fontBold, 9, col2X, cursorY - 8, "Số tiền");
        cursorY -= rowHeight;

        Color stripeColor = new Color(250, 250, 251);
        boolean stripe = false;
        for (Map.Entry<?, Double> entry : rows) {
            if (stripe) {
                content.setNonStrokingColor(stripeColor);
                content.addRect(MARGIN, cursorY - rowHeight + 6, CONTENT_WIDTH, rowHeight);
                content.fill();
            }
            stripe = !stripe;

            String label = entryLabel(entry.getKey());
            setFillColor(COLOR_TEXT);
            drawText(fontRegular, 10, col1X + 8, cursorY - 8, fitText(label, fontRegular, 10, col2X - col1X - 16));
            drawText(fontRegular, 10, col2X, cursorY - 8, CurrencyUtils.formatVND(entry.getValue()));

            cursorY -= rowHeight;
        }

        content.setStrokingColor(COLOR_BORDER);
        content.setLineWidth(0.5f);
        content.moveTo(MARGIN, cursorY + rows.size() * rowHeight + rowHeight - rowHeight + 6);
        content.moveTo(MARGIN, cursorY + rowHeight);
        content.lineTo(MARGIN + CONTENT_WIDTH, cursorY + rowHeight);
        content.stroke();

        cursorY -= 20;
    }

    private String entryLabel(Object key) {
        if (key instanceof Category category) {
            return category.getName();
        }
        if (key instanceof Wallet wallet) {
            return wallet.getName();
        }
        return key == null ? "(Không có danh mục)" : key.toString();
    }

    // ===================== FOOTER =====================

    private void drawFooter() throws IOException {
        setFillColor(COLOR_MUTED);
        drawText(fontRegular, 8, MARGIN, MARGIN - 20,
                "Được tạo bởi Expense Manager - " + DateUtils.formatDate(java.time.LocalDate.now()));
    }

    // ===================== TIỆN ÍCH VẼ =====================

    private void drawText(PDFont font, float size, float x, float y, String text) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private void drawHorizontalLine(Color color, float width) throws IOException {
        content.setStrokingColor(color);
        content.setLineWidth(width);
        content.moveTo(MARGIN, cursorY);
        content.lineTo(MARGIN + CONTENT_WIDTH, cursorY);
        content.stroke();
    }

    private void setFillColor(Color color) throws IOException {
        content.setNonStrokingColor(color);
    }

    /** Cắt bớt chuỗi nếu quá dài so với chiều rộng cho phép, thêm "..." ở cuối. */
    private String fitText(String text, PDFont font, float size, float maxWidth) throws IOException {
        if (font.getStringWidth(text) / 1000 * size <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            String candidate = result.toString() + c + ellipsis;
            if (font.getStringWidth(candidate) / 1000 * size > maxWidth) {
                break;
            }
            result.append(c);
        }
        return result + ellipsis;
    }

    /** Nếu không đủ chỗ ở trang hiện tại cho khối tiếp theo, sang trang mới. */
    private void ensureSpace(float neededHeight) throws IOException {
        if (cursorY - neededHeight < MARGIN) {
            content.close();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            cursorY = PAGE_HEIGHT - MARGIN;
        }
    }

    private PDFont loadFont(PDDocument document, String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Không tìm thấy font tại " + path
                        + ". Vui lòng đặt file font vào thư mục resources tương ứng.");
            }
            return PDType0Font.load(document, in);
        }
    }

    /** Thử nạp font bold; nếu không có, dùng font thường thay thế (không lỗi). */
    private PDFont loadFont(PDDocument document, String preferredPath, String fallbackPath) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(preferredPath)) {
            if (in != null) {
                return PDType0Font.load(document, in);
            }
        }
        return loadFont(document, fallbackPath);
    }
}
