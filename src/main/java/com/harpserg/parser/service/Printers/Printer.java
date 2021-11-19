package com.harpserg.parser.service.Printers;

public interface Printer {

    void OutputInfoMessage(String message);
    void OutputSuccessMessage(String message);
    void OutputWarningMessage(String message);
    void OutputErrorMessage(String message);
}
