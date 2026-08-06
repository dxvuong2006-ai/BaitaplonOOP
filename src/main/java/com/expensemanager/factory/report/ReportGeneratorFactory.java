package com.expensemanager.factory.report;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.report.ExcelReportGenerator;
import com.expensemanager.report.PdfReportGenerator;
import com.expensemanager.report.ReportGenerator;
import com.expensemanager.model.enums.ReportType;

public final class ReportGeneratorFactory {

    private ReportGeneratorFactory() {}

    public static ReportGenerator create(ReportType type) {

        if(type == null){
            throw new EmptyFieldException(FieldType.REPORTTYPE);
        }

        switch(type){

            case EXCEL:
                return new ExcelReportGenerator();

            case PDF:
                return new PdfReportGenerator();

            default:
                throw new UnsupportedOperationException(
                        "Unsupported report type: " + type
                );
        }
    }

}
