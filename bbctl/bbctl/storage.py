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



