![The SHOGun-Core logo](resources/logo/shogun-logo-full-150px.png "SHOGun-Core")

# SHOGun-Core

[![Build Status](https://travis-ci.org/terrestris/shogun-core.svg?branch=master)](https://travis-ci.org/terrestris/shogun-core?branch=master) [![Coverage Status](https://coveralls.io/repos/terrestris/shogun-core/badge.svg?branch=master)](https://coveralls.io/r/terrestris/shogun-core?branch=master)

SHOGun-Core is the framework used to build SHOGun, based on several high-quality Open Source frameworks.

Everything you need to know about SHOGun-Core or how you can start with developing is documented in the [wiki of this project](https://github.com/terrestris/shogun-core/wiki).

#### How to pull updated doc/wiki?

On the shogun-core **root directory** (if wiki/doc has been changed):

0. If the submodule has not yet been initialized: `git submodule update --init --recursive`
1. `git submodule foreach git pull origin master`
2. `git add ...`
3. `git commit`
4. `git push ...`
