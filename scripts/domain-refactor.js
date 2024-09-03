const fs = require('fs');
const path = require('path');

const rootDir = path.resolve('../')

const foldersToExclude = [
    'node_modules',
    'build',
    '.gradle',
    'gradle',
    '.idea',
    '.vscode',
    '.git',
    'scripts'
];

const toModify = [
    {
        name: "Book",
        filePath: '../src/main/kotlin/com/library/books/book/domain/Book.kt',
        toReplace: "val title: String,\n    val author: String,"
    },
    {
        name: "BookRequirement",
        filePath: '../src/main/kotlin/com/library/books/book/domain/BookRequirement.kt',
        toReplace: ["val title: String,\n    val author: String", "title = title,\n                    author = author"]
    },
    {
        name: "BookFilter",
        filePath: '../src/main/kotlin/com/library/books/book/domain/BookFilter.kt',
        toReplace: "val title: String? = null,\n    val author: String? = null,"
    },
    {
        name: "BookFilterParams",
        filePath: '../src/main/kotlin/com/library/books/book/adapter/inport/controller/model/BookFilterParams.kt',
        toReplace: ["@Schema(\n        type = \"string\",\n        example = \"A wizard of Earthsea\",\n        description = \"main title of the book\",\n        required = false\n    )\n    val title: String? = null,\n\n    @Schema(\n        type = \"string\",\n        example = \"Ursula K. Le Guin\",\n        description = \"full name of the author\",\n        required = false\n    )\n    val author: String? = null,", "title = title,\n            author = author,"]
    },
    {
        name: "BookRequest",
        filePath: '../src/main/kotlin/com/library/books/book/adapter/inport/controller/model/BookRequest.kt',
        toReplace: ["@field:Size(max = 144, message = \"title field cant be larger than 144 characters\")\n    @field:Size(min = 1, message = \"title field must have at least one character\")\n    val title: String,\n\n    @Schema(\n        type = \"string\",\n        example = \"Ursula K. Le Guin\",\n        description = \"full name of the author\"\n    )\n    @field:Size(max = 144, message = \"author field cant be larger than 144 characters\")\n    @field:Size(min = 1, message = \"author field must have at least one character\")\n    val author: String", "title = title,\n            author = author"]
    },
    {
        name: "BookResponse",
        filePath: '../src/main/kotlin/com/library/books/book/adapter/inport/controller/model/BookResponse.kt',
        toReplace: ["@Schema(\n        type = \"string\",\n        example = \"A wizard of Earthsea\",\n        description = \"main title of the book\"\n    )\n    val title: String,\n\n    @Schema(\n        type = \"string\",\n        example = \"Ursula K. Le Guin\",\n        description = \"full name of the author\"\n    )\n    val author: String,", "title = title,\n                    author = author,"]
    },
    {
        name: "BookQueryFilter",
        filePath: '../src/main/kotlin/com/library/books/book/adapter/outport/repository/model/BookQueryFilter.kt',
        toReplace: ["val title: String? = null,\n    val author: String? = null,", "{ where(\"title\").`is`(title) } to { title },\n            { where(\"author\").`is`(author) } to { author },", "title = title,\n                    author = author,"]
    },
    {
        name: "CachedBookFilter",
        filePath: '../src/main/kotlin/com/library/books/book/adapter/outport/repository/model/CachedBookFilter.kt',
        toReplace: ["val title: String? = null,\n    val author: String? = null", "title = title,\n                    author = author", "${author.keyShaped()}${title.keyShaped()}"]
    },
    {
        name: "NewBookProvider",
        filePath: '../src/main/kotlin/com/library/books/book/domain/service/BookProvider.kt',
        toReplace: "title = title,\n                author = author,"
    },
    {
        name: "UniqueBookFilterProvider",
        filePath: '../src/main/kotlin/com/library/books/book/domain/service/UniqueBookFilterProvider.kt',
        toReplace: "title = title,\n                author = author"
    },
    {
        name: "UpdatedBookProvider",
        filePath: '../src/main/kotlin/com/library/books/book/domain/service/UpdatedBookProvider.kt',
        toReplace: "title = title,\n                author = author,"
    },
    {
        name: "BookControllerTestData",
        filePath: '../src/test/kotlin/com/library/books/adapter/inport/controller/BookControllerTestData.kt',
        toReplace: ["title = title,\n        author = author,", "title = updatedTitle,\n        author = updatedAuthor"]
    },
    {
        name: "BookTestData",
        filePath: '../src/test/kotlin/com/library/books/BookTestData.kt',
        toReplace: ["const val title = \"The Lord of the Rings\"\nconst val author = \"J.R.R. Tolkien\"\nconst val updatedTitle = \"The Fellowship of the Ring\"\nconst val updatedAuthor = \"Tolkien, J.R.R.\"", "title = newTitle,\n        author = newAuthor", "newTitle: String = title, newAuthor: String = author", "title = updatedTitle,\n        author = updatedAuthor,", "title = title,\n        author = author,", "title = title,\n    author = author,"]
    }
]

function main() {
    const domainJson = JSON.parse(fs.readFileSync('domain.json', 'utf8'))
    console.log(domainJson);
    const oldPrefix = 'Book';
    const newPrefix = domainJson.name;
    console.log(newPrefix)

    modifyBaseObject(domainJson)
    renameFilesAndOccurrences(rootDir, oldPrefix, newPrefix);

    console.log('Refactoring completed.');
}

function renameFilesAndOccurrences(dirPath, oldPrefix, newPrefix) {
    fs.readdirSync(dirPath).forEach(item => {
        const itemPath = path.join(dirPath, item);
        const stats = fs.statSync(itemPath);

        if (stats.isDirectory() && !foldersToExclude.includes(item)) {
            const newDirName = replacePrefix(item, oldPrefix, newPrefix, true);
            const newDirPath = path.join(dirPath, newDirName);

            if (item !== newDirName) {
                renameFileOrDirectory(itemPath, newDirPath);
            }

            renameFilesAndOccurrences(newDirPath, oldPrefix, newPrefix);
        } else if (stats.isFile()) {
            const newFileName = replacePrefix(item, oldPrefix, newPrefix, false);
            const newFilePath = path.join(dirPath, newFileName);

            if (item !== newFileName) {
                renameFileOrDirectory(itemPath, newFilePath);
            }

            modifyFileContent(newFilePath, oldPrefix, newPrefix);
        }
    });
}

function replacePrefix(str, oldPrefix, newPrefix, isFolder) {
    const regexFlags = isFolder ? 'gi' : 'g';
    const regex = new RegExp(oldPrefix, regexFlags);

    return str.replace(regex, match => {
        const isExactMatch = match.toLowerCase() === oldPrefix.toLowerCase();

        if (isExactMatch) {
            // Match exact case
            if (match[0] === match[0].toUpperCase()) {
                return newPrefix[0].toUpperCase() + newPrefix.slice(1);
            } else {
                return newPrefix[0].toLowerCase() + newPrefix.slice(1);
            }
        } else {
            return newPrefix + match.slice(oldPrefix.length);
        }
    });
}


function renameFileOrDirectory(oldPath, newPath) {
    try {
        fs.renameSync(oldPath, newPath);
    } catch (error) {
        if (error.code === 'ENOTEMPTY') {
            const tempPath = path.join(path.dirname(newPath), `temp_${Date.now()}`);
            // Move the existing directory to a temporary location
            fs.renameSync(newPath, tempPath);
            // Rename the directory
            fs.renameSync(oldPath, newPath);

            // Move the contents back to the renamed directory (recursive)
            moveContents(tempPath, newPath);

            // Manually remove the temporary directory (recursive)
            removeDirectory(tempPath);
        } else {
            throw error;
        }
    }
}

function moveContents(sourceDir, targetDir) {
    fs.readdirSync(sourceDir).forEach(item => {
        const sourceItemPath = path.join(sourceDir, item);
        const targetItemPath = path.join(targetDir, item);
        const stats = fs.statSync(sourceItemPath);

        if (stats.isDirectory()) {
            // If it's a directory, recursively move its contents
            moveContents(sourceItemPath, targetItemPath);
        } else {
            // Move the file
            fs.renameSync(sourceItemPath, targetItemPath);
        }
    });
}

function removeDirectory(dirPath) {
    if (fs.existsSync(dirPath)) {
        fs.readdirSync(dirPath).forEach(item => {
            const itemPath = path.join(dirPath, item);
            const stats = fs.statSync(itemPath);

            if (stats.isDirectory()) {
                // Recursively remove subdirectories
                removeDirectory(itemPath);
            } else {
                // Remove files
                fs.unlinkSync(itemPath);
            }
        });

        // Remove the empty directory
        fs.rmdirSync(dirPath);
    }
}


/* Refactor domain objects */
function modifyFileContent(filePath, oldPrefix, newPrefix) {
    const fileContent = fs.readFileSync(filePath, 'utf-8');
    const newContent = replacePrefix(fileContent, oldPrefix, newPrefix, true);
    fs.writeFileSync(filePath, newContent, 'utf-8');
}

function modifyBaseObject({content}) {
    toModify.forEach(file => {
        const contentFile = fs.readFileSync(file.filePath, 'utf8')
        let newContent = ""
        let current
        switch (file.name) {

            case "Book":
                current = file.toReplace
                newContent = ""
                newContent = contentFile.replace(current,
                    content.reduce((acc, currentVal, index) => {
                        if (index !== content.length - 1) {
                            return acc += `val ${currentVal.field}: ${currentVal.type},\n    `
                        } else {
                            return acc += `val ${currentVal.field}: ${currentVal.type},`
                        }
                    }, "")
                )
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;

            case "BookRequirement":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},\n    `
                                } else {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t\t\t    `
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},    `
                                }
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "BookResponse":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},\n    `
                                } else {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t    `
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},    `
                                }
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;

            case "BookFilter":
                current = file.toReplace
                newContent = ""
                newContent = contentFile.replace(current,
                    content.reduce((acc, currentVal, index) => {
                        if (index !== content.length - 1) {
                            return acc += `val ${currentVal.field}: ${currentVal.type}? = null,\n    `
                        } else {
                            return acc += `val ${currentVal.field}: ${currentVal.type}? = null,`
                        }
                    }, "")
                )
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "BookFilterParams":
            case "BookRequest":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},\n    `
                                } else {
                                    return acc += `val ${currentVal.field}: ${currentVal.type},`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t    `
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},`
                                }
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "BookQueryFilter":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `val ${currentVal.field}: ${currentVal.type}? = null,\n    `
                                } else {
                                    return acc += `val ${currentVal.field}: ${currentVal.type}? = null,`
                                }
                            }, "")
                        )
                    } else if (index === 1) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `{ where("${currentVal.field}").\`is\`(${currentVal.field}) } to { ${currentVal.field} },\n\t\t\t`
                                } else {
                                    return acc += `{ where("${currentVal.field}").\`is\`(${currentVal.field}) } to { ${currentVal.field} },`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t\t\t\t`
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},`
                                }
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "CachedBookFilter":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `val ${currentVal.field}: ${currentVal.type}? = null,\n    `
                                } else {
                                    return acc += `val ${currentVal.field}: ${currentVal.type}? = null,`
                                }
                            }, "")
                        )
                    } else if (index === 1) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t\t\t\t`
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `\${${currentVal.field}.keyShaped()}`
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case  "NewBookProvider":
            case  "UniqueBookFilterProvider":
            case  "UpdatedBookProvider":
                current = file.toReplace
                newContent = ""
                newContent = contentFile.replace(current,
                    content.reduce((acc, currentVal, index) => {
                        if (index !== content.length - 1) {
                            return acc += `${currentVal.field} = ${currentVal.field},\n\t\t\t\t`
                        } else {
                            return acc += `${currentVal.field} = ${currentVal.field},`
                        }
                    }, "")
                )
                newContent = newContent.replace(current,
                    content.reduce((acc, currentVal, index) => {
                        if (index !== content.length - 1) {
                            return acc += `${currentVal.field} = ${currentVal.field},\n\t\t\t\t`
                        } else {
                            return acc += `${currentVal.field} = ${currentVal.field}`
                        }
                    }, "")
                )
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "BookControllerTestData":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t`
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},`
                                }
                            }, "")
                        )
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = ${currentVal.field},\n\t\t`
                                } else {
                                    return acc += `${currentVal.field} = ${currentVal.field},`
                                }
                            }, "")
                        )
                    } else {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)},\n\t\t`
                                } else {
                                    return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)}`
                                }
                            }, "")
                        )
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                if (index !== content.length - 1) {
                                    return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)},\n\t\t`
                                } else {
                                    return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)}`
                                }
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
            case "BookTestData":
                current = file.toReplace
                newContent = ""
                current.forEach((value, index) => {
                    if (index === 0) {
                        newContent = contentFile.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                console.log(typeof currentVal.example)
                                if (typeof currentVal.example === "string") {
                                    return acc += `const val ${currentVal.field} = "${currentVal.example}"\n` +
                                        `const val updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)} = "updated${currentVal.example.charAt(0).toUpperCase() + currentVal.example.slice(1)}"\n`
                                } else {
                                    return acc += `const val ${currentVal.field} = ${currentVal.example}\n` +
                                        `const val updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)} = ${currentVal.example + 1}\n`
                                }
                            }, "")
                        )
                    } else if (index === 1) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `${currentVal.field} = new${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)},\n\t\t`
                            }, "")
                        )
                    } else if (index === 2) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `new${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)}: ${currentVal.type} = ${currentVal.field},`
                            }, "")
                        )
                    } else if (index === 3) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)},\n\t\t`
                            }, "")
                        )
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `${currentVal.field} = updated${currentVal.field.charAt(0).toUpperCase() + currentVal.field.slice(1)},\n\t\t`
                            }, "")
                        )
                    } else if (index === 4 || index === 5) {
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `${currentVal.field} = ${currentVal.field},\n\t\t`
                            }, "")
                        )
                        newContent = newContent.replace(value,
                            content.reduce((acc, currentVal, index) => {
                                return acc += `${currentVal.field} = ${currentVal.field},\n\t`
                            }, "")
                        )
                    }
                })
                fs.writeFileSync(file.filePath, newContent, 'utf-8');
                break;
        }


        console.log(newContent)
        console.log("--------")
    })
}

/* End */

main();
