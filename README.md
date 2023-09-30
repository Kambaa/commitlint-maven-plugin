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
| [subject-case](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-case)	                     | 	                     | 	                                   |
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

### Some links that i inspired from: 
- https://github.com/Rugal/commitlinter-maven-plugin
- https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules
- https://www.conventionalcommits.org/en/v1.0.0/
- https://gist.github.com/marcojahn/482410b728c31b221b70ea6d2c433f0c

- https://maven.apache.org/guides/mini/guide-maven-classloading.html
- https://stackoverflow.com/a/77206769/1020512