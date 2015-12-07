# SHOGun2

[![Build Status](https://travis-ci.org/terrestris/shogun2.svg?branch=master)](https://travis-ci.org/terrestris/shogun2?branch=master) [![Coverage Status](https://coveralls.io/repos/terrestris/shogun2/badge.svg?branch=master)](https://coveralls.io/r/terrestris/shogun2?branch=master)

SHOGun2 is a Java based WebGIS framework, based on several high-quality Open Source frameworks.

Everything you need to know about SHOGun2 or how you can start with developing is documented in the [wiki of this project](https://github.com/terrestris/shogun2/wiki).

#### How to pull updated doc/wiki?

On the shogun2 **root directory** (if wiki/doc has been changed):

0. If the submodule has not yet been initialized: `git submodule update --init --recursive`
1. `git submodule foreach git pull origin master`
2. `git add ...`
3. `git commit`
4. `git push ...`
