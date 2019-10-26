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

from typing import *
from collections import OrderedDict

class Command:
    def __init__(self, command: str, call: Callable, help_string: str):
        self.command = command
        self.call = call
        self.help_string = help_string


class CommandExecutor:
    def __init__(self, name=None, run_epilog=False):
        self.commands: OrderedDict[str, Command] = {}
        self.name = name
        self.run_epilog = run_epilog

    def add_command(self, command: Command):
        self.commands[command.command] = command

    def format_help_section(self):
        if self.name is None:
            help_string = "list of commands:"
        else:
            help_string = "Commands for '{}':".format(self.name)

        for command in self.commands:
            help_string += "\n  {:10s}  {}".format(command, self.commands[command].help_string)

        if self.run_epilog:
            help_string += "\n\nRun 'bbctl {}<command> --help' for help on particular commands.".format(
                "" if self.name is None else self.name + " "
            )

        return help_string

    def execute(self, command: str, args: List[str]):
        if command in self.commands:
            self.commands[command].call(args)
        else:
            print(
                "Unknown command '{}'{}".format(
                    command, " for 'bbctl {}'".format(self.name) if self.name is not None else ""
                )
            )

            print(
                "Run 'bbctl {}-h' for a list of available commands".format(self.name+" " if self.name is not None else "")
            )


def parse_csv_file(filename):
    l = []

    with open(filename) as f:
        f.readline()
        for line in f:
            l.append(list(map(lambda s: s.strip(), line.split(","))))

    return l


def is_help(args: List[str]):
    return len(args) > 0 and (args[0] == "--help" or args[0] == "-h")


def print_no_arg_error(command: str):
    print("Error: No argument given for command '{}'.".format(command))
    print("Run 'bbctl {} --help' for further info".format(command))


def verify_and_run_executor(executor: CommandExecutor, args: List[str]):
    if len(args) == 0:
        print_no_arg_error(executor.name)

    elif is_help(args):
        print(executor.format_help_section())

    else:
        executor.execute(args[0], args[1:])
