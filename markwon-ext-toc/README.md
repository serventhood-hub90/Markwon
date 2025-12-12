# markwon-ext-toc

Extension to support Gitiles-style `[TOC]` directive for automatic table of contents generation.

## Installation

```gradle
implementation "io.noties.markwon:ext-toc:${markwonVersion}"
```

## Usage

```java
Markwon markwon = Markwon.builder(context)
    .usePlugin(TocPlugin.create())
    .build();
```

## Example

Given the following markdown:

```markdown
# Introduction
Some introductory content here.

[TOC]

# Chapter 1
Chapter 1 content.

## Section 1.1
Section content.

## Section 1.2
More section content.

# Chapter 2
Chapter 2 content.
```

The `[TOC]` directive will be automatically replaced with a formatted bullet list containing links to all headings:

* Introduction
* Chapter 1
  * Section 1.1
  * Section 1.2
* Chapter 2

## Features

- Detects `[TOC]` on a line by itself (case-insensitive)
- Generates a nested bullet list based on heading levels
- Creates clickable links to headings (requires proper anchor handling)
- Follows Gitiles markdown specification

## Notes

- The `[TOC]` directive can appear anywhere in the document
- Multiple `[TOC]` directives are supported
- Heading anchor links require additional setup for scrolling functionality (see `AnchorHeadingPlugin` in sample app)
