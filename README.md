# Git Message Check Maven Plugin
[![master](https://github.com/Kambaa/gmc-maven-plugin/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/Kambaa/gmc-maven-plugin/actions/workflows/maven.yml)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.kambaa/gmc-maven-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/kambaa/gmc-maven-plugin/0.0.1-SNAPSHOT/)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/Kambaa/gmc-maven-plugin/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/Kambaa/gmc-maven-plugin/tree/master)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Kambaa_gmc-maven-plugin&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Kambaa_gmc-maven-plugin)

A maven plugin that checks given commit messages

In our company, for our maven projects, we use js solutions for commit message convention checking, so i wanted to write
a maven plugin to do it. My aim is that this plugin should do pretty much same job as the current status, without the
extra unnecessary checks or configurations.

#### current maven plugin example(that i'm using for dev & testing):

```xml

<plugin>
    <groupId>io.github.kambaa</groupId>
    <artifactId>gmc-maven-plugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <configuration>
        <!-- (OPTIONAL): fail maven build on error. Default: true -->
        <failOnError>true</failOnError>
        <!-- (OPTIONAL): skips this plugin's execution. Default: false -->
        <skip>false</skip>
    </configuration>
</plugin>

```

use piping: (TODO: add multiple git log)
```shell
 cat .git/COMMIT_EDITMSG | mvn io.github.kambaa:gmc-maven-plugin:checkPiped
```


## My thoughts on current solution:

Apperantly the js solution:

- calls the git command with arguments itself through a seperate
  process([code](https://github.com/conventional-changelog/conventional-changelog/blob/master/packages/git-raw-commits/index.js#L59)),
- gets the resulting text from it,
- splits and processes(not sure) the commits,
- and does the validations which is abstracted heavily but it's configurative, it can be read through
  the [docs]( https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules).
  It covers the variety of checks (which in my opinion and for my needs, it's too many and it's not that needed really
  😊 ).

I don't need that much complication, i can write my own bash script to get the git log combined, and give it to this
plugin to check'em. Simplicity is enough and great for me. A simple check for extending should be enough for other
peoples needs. IDK right now, I will think of a solution later if Allah is willing.

```shell
# get commits from latest tag to current with default split text
git log --format="%B%n------------------------ >8 ------------------------" 
$(git describe --tags --abbrev=0)..HEAD
```

### A POC With This Plugin

```xml

<plugins>

    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.0.0</version>
        <executions>
            <execution>
                <id>execute-git-log</id>
                <phase>initialize</phase>
                <goals>
                    <goal>exec</goal>
                </goals>
                <configuration>
                    <executable>git</executable>
                    <arguments>
                        <argument>log</argument>
                        <argument>--format=%B%n------------------------ >8 ------------------------</argument>
                        <!--                                <argument>$(git describe &#45;&#45;tags &#45;&#45;abbrev=0)..HEAD</argument>-->
                    </arguments>
                    <outputFile>${project.build.directory}/command-output.txt</outputFile>
                </configuration>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <groupId>io.github.kambaa</groupId>
        <artifactId>gmc-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>initialize</phase>
                <goals>
                    <goal>check</goal>
                </goals>
                <configuration>
                    <multipleGitLogMessages>${project.build.directory}/command-output.txt</multipleGitLogMessages>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

after adding these build configs to a projects `pom.xml`, i want maven to run the git log command, get the combine
commit messages to a file, and gmc plugin to check commits on this file via single command:

```
mvn initialize io.github.kambaa:gmc-maven-plugin:check -X
```

Only issue for me to see that there's a newline added between them in `command-output.txt`. Will check this later.

### Some links that i inspired from:

- https://gist.github.com/Kambaa/410923120ce4ae673ddd44203847e0ce
- https://github.com/Rugal/commitlinter-maven-plugin
- https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules
- https://www.conventionalcommits.org/en/v1.0.0/
- https://gist.github.com/marcojahn/482410b728c31b221b70ea6d2c433f0c
- https://github.com/conventional-changelog/conventional-changelog/blob/master/packages/conventional-changelog-conventionalcommits/parserOpts.js
- https://maven.apache.org/guides/mini/guide-maven-classloading.html
- https://stackoverflow.com/a/77206769/1020512
- https://commitlint.io/
- https://regex101.com/r/jRfCCj/1
- https://git-scm.com/docs/git-log
- https://central.sonatype.org/publish/publish-maven/#distributing-your-public-key
- https://jenkov.com/tutorials/maven/publish-to-central-maven-repository.html
- https://shields.io/badges/static-badge
- https://github.com/actions/setup-java/blob/main/docs/advanced-usage.md
- https://github.com/conventional-changelog/commitlint/blob/master/%40commitlint/is-ignored/src/defaults.ts#L17-L29
- https://github.com/conventional-changelog/commitlint/blob/master/%40commitlint/cli/fixtures/parser-preset/parser-preset.js
- https://github.com/conventional-changelog/conventional-changelog/blob/eb3ab7bcd2266cd751e94c583510c8a85216a027/packages/git-raw-commits/index.js
- https://gist.github.com/s4y/1215700
- https://stackoverflow.com/a/40206597

