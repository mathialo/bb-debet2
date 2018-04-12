/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import bbdebet2.kernel.Kernel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CurrencyFormatter {

    private static Kernel kernel;
    private static NumberFormat formatter;


    public static void initilalize(Kernel kernel) {
        CurrencyFormatter.kernel = kernel;
        formatter = new DecimalFormat("#0.00");
    }


    public static String format(double d) {
        if (kernel != null && kernel.getSettingsHolder() != null) {
            return formatter.format(d) + " " + kernel.getSettingsHolder().getCurrencySign();
        } else {
            return formatter.format(d);
        }
    }
}
