# commitlint-maven-plugin

A maven plugin that checks given commit messages

In our company, for our maven projects, we use js solutions for commit message convention checking, so i wanted to write
a maven plugin to do it. My aim is that this plugin should do pretty much same job as the configuration right now.

## Comparison With CommitLint's [config-convention](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#commitlintconfig-conventional)

| [Rules](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules) 	                                  | Covers From Regex   	 | Covers With Customizable Checks   	 |
|------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|-------------------------------------|
| [type-enum](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-enum)	                           | X	                    | 	                                   |
| [type-case](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-case)	                           | X	                    | 	                                   |
| [type-empty](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-empty)	                         | X 	                   | 	                                   |
| [subject-case](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-case)	                     | 	                     | X	                                  |
| [subject-empty](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-empty)	                   | X 	                   | 	                                   |
| [subject-full-stop](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-full-stop)	           | 	                     | 	                                   |
| [header-max-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#header-max-length)	           | 	                     | 	                                   |
| [footer-leading-blank](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#footer-leading-blank)	     | 	                     | 	                                   |
| [footer-max-line-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#footer-max-line-length)	 | 	                     | 	                                   |
| [body-leading-blank](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#body-leading-blank)	         | 	                     | 	                                   |
| [body-max-line-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#body-max-line-length)	     | 	                     | 	                                   |

```
[INFO] Use test commit message [feat(SCOPE)!: ornek deneme
seni Allah bildiği gibi yapsın
]
[INFO] Matcher Group 0 is: [feat(SCOPE)!: ornek deneme
seni Allah bildiği gibi yapsın
]
[INFO] Matcher Group 1 is: [feat]
[INFO] Matcher Group 2 is: [(SCOPE)]
[INFO] Matcher Group 3 is: [!]
[INFO] Matcher Group 4 is: [ornek deneme]
[INFO] Matcher Group 5 is: [
seni Allah bildiği gibi yapsın
]
```

#### current maven plugin example:

```xml

<plugin>
    <groupId>dev.kambaabi</groupId>
    <artifactId>commitlint-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <testCommitMessage>feat: ornek deneme

            seni Allah bildiği gibi yapsın

            awdawd

            www
        </testCommitMessage>
        <customConfig>
            <regexes>
                <regex>
                    <!--                                <value>^(\w*)(?:\(([\w\-.]+)\))?(!)?: ([\w ]+)(\n[\s\S]*)?</value>-->
                    <!--     <value>wad^(</value>-->
                    <value>^(\w*)(?:\(([\w\-.]+)\))?(!)?: (.*)(\n[\s\S]*)?</value>
                    <captureGroups>
                        <captureGroup>
                            <index>1</index>
                            <validations>
                                <validation>
                                    <className>dev.kambaabi.validator.NonEmptyValidator</className>
                                    <level>ERROR</level>
                                </validation>
                                <validation>
                                    <!--<className>com.example.CustomCaseValidator</className>-->
                                    <className>dev.kambaabi.validator.CaseValidator</className>
                                    <level>ERROR</level>
                                    <args>
                                        <arg>LOWERCASE</arg>
                                    </args>
                                </validation>
                                <validation>
                                    <className>dev.kambaabi.validator.EnumValidator</className>
                                    <level>ERROR</level>
                                    <args>
                                        <arg>build</arg>
                                        <arg>chore</arg>
                                        <arg>ci</arg>
                                        <arg>docs</arg>
                                        <arg>feat</arg>
                                        <arg>fix</arg>
                                        <arg>perf</arg>
                                        <arg>refactor</arg>
                                        <arg>revert</arg>
                                        <arg>style</arg>
                                        <arg>test</arg>
                                    </args>
                                </validation>

                            </validations>
                        </captureGroup>
                        <captureGroup>
                            <index>2</index>
                            <validations>
                                <validation>
                                    <opts>SKIP_IF_EMPTY</opts>
                                    <className>dev.kambaabi.validator.NonEmptyValidator</className>
                                    <level>ERROR</level>
                                </validation>
                                <validation>
                                    <opts>SKIP_IF_EMPTY</opts>
                                    <className>dev.kambaabi.validator.CaseValidator</className>
                                    <args>
                                        <arg>LOWERCASE</arg>
                                    </args>
                                    <level>ERROR</level>
                                </validation>
                            </validations>
                        </captureGroup>
                        <captureGroup>
                            <index>3</index>
                            <validations></validations>
                        </captureGroup>
                        <captureGroup>
                            <index>4</index>
                            <validations>
                                <validation>
                                    <className>dev.kambaabi.validator.NonEmptyValidator</className>
                                    <level>ERROR</level>
                                </validation>
                            </validations>
                        </captureGroup>
                        <captureGroup>
                            <index>5</index>
                            <validations></validations>
                        </captureGroup>
                    </captureGroups>
                </regex>
            </regexes>
        </customConfig>
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
- https://regex101.com/