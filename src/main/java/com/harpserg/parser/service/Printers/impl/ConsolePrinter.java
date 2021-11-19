package com.harpserg.parser.service.Printers.impl;

import com.harpserg.parser.service.Printers.Printer;
import org.springframework.stereotype.Service;

@Service
public class ConsolePrinter implements Printer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    @Override
    public void OutputInfoMessage(String message) {
        System.out.println(ANSI_RESET + message + ANSI_RESET);
    }

    @Override
    public void OutputSuccessMessage(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    @Override
    public void OutputWarningMessage(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    @Override
    public void OutputErrorMessage(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }
}
