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

import re

from setuptools import find_packages, setup

version = re.search('^__version__\s*=\s*"(.*)"', open("bbctl/__init__.py").read(), re.M).group(1)

# Install python package and shell script
setup(
    name="bbdebet2-bbctl",
    description="Control tool for BBDebet2",
    version=version,
    author="Mathias Lohne",
    entry_points={"console_scripts": ["bbctl = bbctl.__main__:main"]},
    packages=find_packages(),
    license="GPLv3",
    install_requires=[
        "pytz",
        "cognite-extractor-utils"
    ],
    zip_safe=False,
)
