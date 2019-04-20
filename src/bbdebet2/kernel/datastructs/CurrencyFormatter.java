/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
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
