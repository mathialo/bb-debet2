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

userfile = "{}/.bbdebet2/users.usl".format(str(Path.home()))


def user(args: List[str] = []):
    executor = CommandExecutor("user")
    executor.add_command(Command("list", list_, "list all users"))
    executor.add_command(Command("info", info, "get info about user"))
    executor.add_command(Command("stats", stats, "generate statistics about users"))

    verify_and_run_executor(executor, args)


def list_(args: List[str] = []):
    if is_help(args):
        print("Lists all users on this BBDebet2 system. Run user list debt to se users in debt. Run\n"
              "with the '-v' flag to get verbose output.")
        return

    users = []
    balances = []

    with open(userfile, "r") as f:
        skipfirst = True

        for line in f:
            if skipfirst:
                skipfirst = False
                continue

            users.append(line.split(",")[1])
            balances.append(float(line.split(",")[3]))

    if "debt" in args:
        users = [u for u, b in zip(users, balances) if b < 0]

    if "-v" in args or "--verbose" in args:
        info(users)

    else:
        height = len(users) // 4 + 1
        for i in range(height):
            print(
                "{:18s}  {:18s}  {:18s}  {:18s}".format(
                    users[i],
                    users[height + i],
                    users[2 * height + i],
                    users[3 * height + i] if 3 * height + i < len(users) else "",
                )
            )


def info(args: List[str] = []):
    if len(args) == 0:
        print_no_arg_error("user info")
        return

    if is_help(args):
        print("Run as 'bbctl user info <user1> ...' where all <user>s are either user names or user IDs.")
        return

    where_to_search = ["ID", "name"]

    if args[0].isdigit():
        # Search in ID
        index = 0
    else:
        # Search in name
        index = 1

    with open(userfile, "r") as f:
        skipfirst = True
        for line in f:
            if skipfirst:
                skipfirst = False
                continue

            info = line.split(",")

            if info[index] in args:
                print("Info on user '{}' ({}):".format(info[1], info[0]))
                print("  Name: {}".format(info[1].capitalize()))
                print("  Email: {}".format(info[2]))
                print("  Balance: {:.2f} kr".format(float(info[3])))
                print("  Has {}accepted EULA".format("" if info[4].strip() == "true" else "not "))


def stats(args: List[str] = []):
    if is_help(args):
        print("Print statistics for users")
        return

    total_money = 0
    total_user_debt = 0
    users_in_debt = 0
    total_users = 0

    with open(userfile, "r") as f:
        skipfirst = True

        for line in f:
            if skipfirst:
                skipfirst = False
                continue

            info = line.split(",")

            total_money += float(info[3])
            total_user_debt -= float(info[3]) if float(info[3]) < 0 else 0
            users_in_debt += float(info[3]) < 0
            total_users += 1

    print(f"Total users:    {total_users}")
    print(f"Total balance:  {total_money:.2f} kr")
    print(f"{users_in_debt} users are in debt, with a total of {total_user_debt:.2f} kr of debt")


