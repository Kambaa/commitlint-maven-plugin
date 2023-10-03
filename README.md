# commitlint-maven-plugin

A maven plugin that checks given commit messages

In our company, for our maven projects, we use js solutions for commit message convention checking, so i wanted to write
a maven plugin to do it. My aim is that this plugin should do pretty much same job as the configuration right now.

#### Check List


#### current maven plugin example(that i'm using for dev & testing):

```xml

<plugin>
    <groupId>dev.kambaabi</groupId>
    <artifactId>commitlint-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <testCommitMessage>
            feat: some message that is way too long and breaks the line max-length

            awdawdawdawdawdawd aw da aw aw dawd awd aw

            seni Allah bildiği gibi yapsın

            awdawd

            www
        </testCommitMessage>
        <failOnError>true</failOnError>
        <skip>false</skip>
    </configuration>
</plugin>

```

### Some links that i inspired from:

- https://github.com/Rugal/commitlinter-maven-plugin
- https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules
- https://www.conventionalcommits.org/en/v1.0.0/
- https://gist.github.com/marcojahn/482410b728c31b221b70ea6d2c433f0c
- https://github.com/conventional-changelog/conventional-changelog/blob/master/packages/conventional-changelog-conventionalcommits/parserOpts.js
- https://maven.apache.org/guides/mini/guide-maven-classloading.html
- https://stackoverflow.com/a/77206769/1020512
- https://commitlint.io/
- https://regex101.com/r/jRfCCj/1
