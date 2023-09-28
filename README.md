# commitlint-maven-plugin

A maven plugin that checks given commit messages

In our company, for out maven projects, we use js solutions for commit message convention checking, so i wanted to write
a maven plugin to do it. My aim is that this plugin should do pretty much same job as the configuration right now.

## Comparison With CommitLint's [config-convention](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#commitlintconfig-conventional)

| [Rules](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#rules) 	                                  | Covers From Regex   	 | Covers With Customizable Checks   	 |
|------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|-------------------------------------|
| [type-enum](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-enum)	                           | 	                     | 	                                   |
| [type-case](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-case)	                           | 	                     | 	                                   |
| [type-empty](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-empty)	                         | 	                     | 	                                   |
| [subject-case](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-case)	                     | 	                     | 	                                   |
| [subject-empty](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-empty)	                   | 	                     | 	                                   |
| [subject-full-stop](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#subject-full-stop)	           | 	                     | 	                                   |
| [header-max-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#header-max-length)	           | 	                     | 	                                   |
| [footer-leading-blank](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#footer-leading-blank)	     | 	                     | 	                                   |
| [footer-max-line-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#footer-max-line-length)	 | 	                     | 	                                   |
| [body-leading-blank](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#body-leading-blank)	         | 	                     | 	                                   |
| [body-max-line-length](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#body-max-line-length)	     | 	                     | 	                                   |
