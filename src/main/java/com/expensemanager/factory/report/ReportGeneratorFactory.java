package com.expensemanager.factory.report;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.ReportType;
import com.expensemanager.report.ExcelReportGenerator;
import com.expensemanager.report.PdfReportGenerator;
import com.expensemanager.report.ReportGenerator;

/** Lớp khởi tạo đối tượng {@link ReportGenerator} theo mẫu Factory Method. */
public final class ReportGeneratorFactory {

    /** Không cho phép khởi tạo Factory. */
    private ReportGeneratorFactory() {}

    /**
     * Tạo mới một {@link ReportGenerator} dựa trên {@code type} được truyền vào.
     *
     * @param type loại báo cáo (EXCEL, PDF)
     * @return đối tượng {@link ReportGenerator} tương ứng với loại được chỉ định
     * @throws EmptyFieldException nếu {@code type} là null
     */
    public static ReportGenerator create(ReportType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.REPORTTYPE);
        }

        return switch (type) {
            case EXCEL -> new ExcelReportGenerator();
            case PDF -> new PdfReportGenerator();
        };
    }
}