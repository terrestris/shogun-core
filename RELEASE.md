# Release procedure

This document describes how to create a new SHOGun2 release. The process assumes
you want to deploy from a linux machine.

## Prerequisites (only once)

* Create a PGP signature and distribute your public key as described in the
  [sonatype documentation](http://central.sonatype.org/pages/working-with-pgp-signatures.html)

* Set up SSH access to GitHub as described on the
  [GitHub help page](https://help.github.com/articles/generating-ssh-keys/)

* Adjust and add the following block to your local maven `settings.xml`
  (typically in `~/.m2/settings.xml`):

```xml
<settings>
  <servers>
      <server>
          <id>ossrh</id>
          <username>shogun2</username>
          <password>{{CAN_BE_REQUESTED}}</password>
      </server>
  </servers>
  <profiles>
      <profile>
          <id>ossrh</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <gpg.executable>gpg</gpg.executable>
              <gpg.keyname>{{GPG_KEYNAME}}</gpg.keyname>
              <gpg.passphrase>{{GPG_PASSPHRASE}}</gpg.passphrase>
          </properties>
      </profile>
  </profiles>
</settings> 
```

## Release process (for every release)

### Update source files

* In the `src` directory of a clone of **your** SHOGun2 fork.

* Checkout the latest `master` branch and update it with the latest form the
  canonical upstream repository
  ```bash
  $ git checkout master
  $ git pull https://github.com/terrestris/shogun2.git master
  ```

* The command `git status` should report that the working directory is clean.

### Run `./release.sh`

* Run the utility script `release.sh` from a terminal. Provide two arguments,
  the version identifier for the version you are about to release and another
  one for the next development cycle. E.g. if you want to release version
  `0.0.7`, the command would read:
  ```bash
  # in the scripts/ directory
  $ ./release.sh "0.0.7" "0.0.8-SNAPSHOT"
  ```

* The script will ask you for final confirmation before it does the real release
  work. Behind the scenes it will create git commits and tags, push them and
  also push generated and signed release artifacts to sonatype.
  * if you are asked to confirm the authenticity of a host, do so
  * you usually shouldn't be prompted for any passwords.

* If everything went well, one of the final lines in the terminal should read
  `BUILD SUCCESS`. Additionally (currently 4) new commits should show up on the
  canonical repository: https://github.com/terrestris/shogun2/commits/master. They
  should be made under your identity and the messages should all start with
  `[maven-release-plugin]`. The `tags`-page over at github should list a new
  tag for your version, e.g. `v0.0.7`.

### Move artifacts on sonatype

* There is only one final step needed: We have to manually move the deployed
  artifacts from the sonatype staging area to the release area. We *could*
  automate this, but decided against this so we have an additional sanity-check.

* Open https://oss.sonatype.org/ and login as user `shogun2` (password on
  request).
  * Go to `Staging Repositories` in the `Build Promotion` section.
  * Scroll down the list on the right until you see two repositories named
    `deterrestris-1011` and `deterrestris-1012`. One was created by the
    above command from your machine, and the other one was autocreated from
    some other process we don't care about.
  * Find the repository *you* created by clicking the reposoitory row and
    examining the details. Look at the `Owner` property. If the listed
    IP-address matches your IP, you found the correct repo.
  * Once you have selected the correct row, click the `Close`-button at the
    top-toolbar. A window to confirm the task will open. Add a short
    description: e.g. `Release v0.0.7`, and then click the `Confirm`-button.
  * Once confirmed, sonatype does some background work for the release. We can
    continue once all checks for the repository have passed (To see these, just
    select the row of the correct repository). Once all checks have passed, the
    `Release`-button in the upper toolbar will enabled / clickable.
  * Select the correct repository and click the `Release`-button.
  * Another window will pop up asking for confirmation. Enter a comment like
    above and click `Confirm`. The checkbox for `Automatically Drop` can be
    left checked.
  * If everything went well (and some time has passed), the repository should
    no longer be listed in the grid of `Staging Repositories`.
  * Next, please drop the other automatically created repository (Select, click
    `Drop`-button, confirm, be happy).
  * You can check if the release was successful by searching in the `Artifact
    Search` for `de.terrestris`; all artifacts (currently 10) should list the
    just released version.

### That's it…

* …it will take some time for the new release to show up on the following sites:

  * http://mvnrepository.com/artifact/de.terrestris/shogun2
  * http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.terrestris%22

