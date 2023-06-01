# Extensions
This project aims to provide a simple and universal way to extend jvm based programs with
external "plugins" or "extensions".

## Abstract
This library uses three different types of extensions: 
- [libraries](#libraries) for providing needed utilities and interfaces. These should also be
  used as interface for interacting with other extensions. 
- [data-fetchers](#data-fetchers) are used to fetch data from external sources, such as 
  databases, http requests, etc.
- [extensions](extensions) providing the actual logic that runs and interacts with the primary
  application. They can depend on each other but are preferred to just use declared libraries
  and data-fetchers

## Usage

### Implementation

### extensions

```java

```

### data-fetchers

```java

```

### libraries

```java

```