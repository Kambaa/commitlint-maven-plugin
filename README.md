# Git Message Check Maven Plugin

A maven plugin that checks given commit messages

In our company, for our maven projects, we use js solutions for commit message convention checking, so i wanted to write a maven plugin to do it. My aim is that this plugin should do pretty much same job as the current status, without the extra unnecessary checks or configurations.

#### current maven plugin example(that i'm using for dev & testing):

```xml

<plugin>
    <groupId>io.github.kambaa</groupId>
    <artifactId>gmc-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>

        <!--
            <multipleGitLogMessages>
             
                Enter your historical git log messages here 
                with splitting value, default is:
                ------------------------ >8 ------------------------
                or customize you can customize it.
                
            </multipleGitLogMessages>

            <multipleGitLogMessageSplitter>
                (OPTIONAL) Enter your custom splitting text for the plugin to understand. 
                Again default value is:
                ------------------------ >8 ------------------------
            </multipleGitLogMessageSplitter>
        -->

        <!-- Test or validate a single message -->
        <testCommitMessage>
            feat: some message that is way too long and breaks the line max-length

            awdawdawdawdawdawd aw da aw aw dawd awd aw

            seni Allah bildiği gibi yapsın

            awdawd

            www
        </testCommitMessage>

        <!-- (OPTIONAL): fail maven build on error. Default: true -->
        <failOnError>true</failOnError>
        <!-- (OPTIONAL): skips this plugin's execution. Default: false -->
        <skip>false</skip>
    </configuration>
</plugin>

```

You can get the log messages with `git` command, get the list of commit messages to check, and give it to this plugin like this: 
```shell
mvn io.github.kambaa:gmc-maven-plugin:check -DmultipleGitLogMessages=<COMBINED_LOG_MESSAGES_HERE>
```
or add `<pluginGroup>gmc-maven-plugin</pluginGroup>` to your plugin groups in your `settings.xml`, and call like this: 

```shell
mvn gmc:check -DmultipleGitLogMessages=<COMBINED_LOG_MESSAGES_HERE>
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
