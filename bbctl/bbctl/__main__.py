#! /usr/bin/env python3
import argparse
from pathlib import Path

from typing import *

from bbctl import __version__, __bbdebetversion__
from bbctl.common import *
from bbctl.user import user
from bbctl.sale import sale
from bbctl.storage import storage


def create_command_executor() -> CommandExecutor:
    executor = CommandExecutor(run_epilog=True)
    executor.add_command(Command("user", user, "obtain info about users"))
    executor.add_command(Command("sale", sale, "obtain info about sales"))
    executor.add_command(Command("storage", storage, "obtain info about storage"))

    return executor


def create_arg_parser(executor: CommandExecutor) -> argparse.ArgumentParser:
    arg_parser = argparse.ArgumentParser(
        prog="bbctl",
        description="Control tool for BBDebet2",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=executor.format_help_section()
    )
    arg_parser.add_argument("-v", "--version", action="version", version="bbctl v{}\nPart of BBDebet v{}".format(__version__, __bbdebetversion__))
    arg_parser.add_argument("command", type=str, nargs=1, help="command to run")
    arg_parser.add_argument("args", type=str, nargs=argparse.REMAINDER, help="arguments to command")

    return arg_parser


def main():
    executor = create_command_executor()
    arg_parser = create_arg_parser(executor)

    args = arg_parser.parse_args()
    executor.execute(args.command[0], args.args)


if __name__ == "__main__":
    main()
