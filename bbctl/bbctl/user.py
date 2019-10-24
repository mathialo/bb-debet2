from pathlib import Path
from typing import *

from .common import *

userfile = "{}/.bbdebet2/users.usl".format(str(Path.home()))


def user(args: List[str] = []):
    executor = CommandExecutor("user")
    executor.add_command(Command("list", list_, "list all users"))
    executor.add_command(Command("info", info, "get info about user"))

    verify_and_run_executor(executor, args)


def list_(args: List[str] = []):
    if is_help(args):
        print("Lists all users on this BBDebet2 system. Run with the '-v' flag to get verbose output.")
        return

    users = []

    with open(userfile, "r") as f:
        skipfirst = True

        for line in f:
            if skipfirst:
                skipfirst = False
                continue

            users.append(line.split(",")[1])

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
