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
    install_requires=[
    ],
    zip_safe=False,
)
