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

from pathlib import Path
from typing import *

from .common import *

storagefile = "{}/.bbdebet2/storage.csv".format(str(Path.home()))


def storage(args: List[str] = []):
    executor = CommandExecutor("storage")
    executor.add_command(Command("list", list_, "list all items in storage"))

    verify_and_run_executor(executor, args)


def list_(args: List[str] = []):
    print("{:30} {:12} {:12}".format("Product", "Bought for", "Selling for"))
    print("-"*(31+13+12))

    total_stock = 0
    total_sell = 0

    for product in parse_csv_file(storagefile):
        formatted_buy = "{:.2f} kr".format(float(product[1]))
        formatted_sell = "{:.2f} kr".format(float(product[2]))

        print("{:30} {:12} {:12}".format(product[0][0: min(len(product[0]),30)], formatted_buy, formatted_sell))

        total_stock += float(product[1])
        total_sell += float(product[2])

    print("-"*(31+13+12))
    print("Total stock value: {:.2f} kr".format(total_stock))
    print("Total sell value:  {:.2f} kr".format(total_sell))




