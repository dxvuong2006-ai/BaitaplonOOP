package com.expensemanager.report;

import com.expensemanager.model.report.ReportData;
import java.io.File;
import java.io.IOException;

/** Interface định nghĩa hành vi xuất báo cáo. */
public interface ReportGenerator {

    /** Xuất báo cáo ra file. */
    void generateReport(ReportData reportData, File outputFile) throws IOException;
}
