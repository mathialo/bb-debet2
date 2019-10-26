# Copyright (C) 2019  Mathias Lohne

# This program is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation, either version 3 of the License, or (at your option)
# any later version.

# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.

# You should have received a copy of the GNU General Public License along
# with this program.  If not, see <http://www.gnu.org/licenses/>.

from datetime import datetime
from pathlib import Path
import pytz
from typing import *

from .common import *

historyfile = "{}/.bbdebet2/saleshistory.csv".format(str(Path.home()))


def sale(args: List[str] = []):
    executor = CommandExecutor("sale")
    executor.add_command(Command("latest", list_latest, "get the latest sales"))
    executor.add_command(Command("list", list_all, "list all sales"))

    verify_and_run_executor(executor, args)


def list_latest(args: List[str] = []):
    if is_help(args):
        print("List the latest sales. Optional argument: number of sales (default 10).")
        return

    number = int(args[0]) if len(args) >= 1 else 10

    print("{:20} {:12} {:25} {:10} {:10}".format("Time", "User", "Product", "Price", "Earnings"))
    print("-"*(21+13+26+11+10))

    total_sold = 0
    total_earnings = 0

    for sale in parse_csv_file(historyfile)[-number:]:
        utc_dt = datetime.utcfromtimestamp(int(sale[1]))

        formatted_dt = utc_dt.astimezone(pytz.timezone("Europe/Oslo")).strftime("%d.%m - %H:%M:%S")
        formatted_price = "{:.2f} kr".format(float(sale[4]))
        formatted_earnings = "{:.2f} kr".format(float(sale[5]))

        print("{:20} {:12} {:25} {:10} {:10}".format(formatted_dt, sale[2].capitalize(), sale[3][0: min(len(sale[3]),25)], formatted_price, formatted_earnings))

        total_sold += float(sale[4])
        total_earnings += float(sale[5])

    print("-"*(21+13+26+11+10))
    print("Total sold:     {:.2f} kr".format(total_sold))
    print("Total earnings: {:.2f} kr".format(total_earnings))



def list_all(args: List[str] = []):
    if is_help(args):
        print("List all sales this semester.")
        return

    print("{:20} {:12} {:25} {:10} {:10}".format("Time", "User", "Product", "Price", "Earnings"))
    print("-"*(21+13+26+11+10))

    total_sold = 0
    total_earnings = 0

    for sale in parse_csv_file(historyfile):
        utc_dt = datetime.utcfromtimestamp(int(sale[1]))

        formatted_dt = utc_dt.astimezone(pytz.timezone("Europe/Oslo")).strftime("%d.%m - %H:%M:%S")
        formatted_price = "{:.2f} kr".format(float(sale[4]))
        formatted_earnings = "{:.2f} kr".format(float(sale[5]))

        print("{:20} {:12} {:25} {:10} {:10}".format(formatted_dt, sale[2].capitalize(), sale[3][0: min(len(sale[3]),25)], formatted_price, formatted_earnings))

        total_sold += float(sale[4])
        total_earnings += float(sale[5])

    print("-"*(21+13+26+11+10))
    print("Total sold:     {:.2f} kr".format(total_sold))
    print("Total earnings: {:.2f} kr".format(total_earnings))
