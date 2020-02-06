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

from cognite.extractorutils.statestore import LocalStateStore

from .common import *

storagefile = "{}/.bbdebet2/storage.csv".format(str(Path.home()))


def storage(args: List[str] = []):
    executor = CommandExecutor("storage")
    executor.add_command(Command("list", list_, "list all items in storage"))
    executor.add_command(Command("summary", summary, "shortened list of items in storage"))

    verify_and_run_executor(executor, args)


def list_(args: List[str] = []):
    if is_help(args):
        print("Prints a detailed list of all items in storage")
        return

    print("{:30} {:12} {:12} {:5}".format("Product", "Bought for", "Selling for", "Pant"))
    print("-" * (31 + 13 + 13 + 5))

    for product in parse_csv_file(storagefile):
        formatted_buy = "{:.2f} kr".format(float(product[1]))
        formatted_sell = "{:.2f} kr".format(float(product[2]))
        formatted_pant = "{:.2f} kr".format(float(product[3]))

        print(
            "{:30} {:12} {:12} {:5}".format(
                product[0][0 : min(len(product[0]), 30)], formatted_buy, formatted_sell, formatted_pant
            )
        )


def summary(args: List[str] = []):
    if is_help(args):
        print("Prints a summary of all items in storage")
        return

    buyproducts = LocalStateStore("")
    sellproducts = LocalStateStore("")

    product_numbers = DefaultDict(lambda: 0)

    total_number = 0
    total_stock = 0
    total_sell = 0

    for product in parse_csv_file(storagefile):
        buyproducts.expand_state(product[0], float(product[1]), float(product[1]))
        sellproducts.expand_state(product[0], float(product[2]), float(product[2]))

        total_number += 1
        total_stock += float(product[1])
        total_sell += float(product[2])

        product_numbers[product[0]] += 1

    print("{:30} {:20} {:20} {:7}".format("Product", "Bought for", "Selling for", "Number"))
    print("-" * (31 + 21 + 21 + 7))

    for product in sorted([p for p in product_numbers]):
        buy_range = buyproducts.get_state(product)
        formatted_buy = (
            "{:.2f} - {:.2f} kr".format(*buy_range)
            if buy_range[0] != buy_range[1]
            else "{:.2f} kr".format(buy_range[0])
        )
        sell_range = sellproducts.get_state(product)
        formatted_sell = (
            "{:.2f} - {:.2f} kr".format(*sell_range)
            if sell_range[0] != sell_range[1]
            else "{:.2f} kr".format(sell_range[0])
        )

        print(
            "{:<30} {:<20} {:<20} {:<7}".format(
                product[0 : min(len(product), 30)], formatted_buy, formatted_sell, product_numbers[product]
            )
        )

    print("-" * (31 + 21 + 21 + 7))
    print(f"Total number of items:  {total_number}")
    print(f"Total buy price:        {total_stock:.2f} kr")
    print(f"Total sell price:       {total_sell:.2f} kr")
