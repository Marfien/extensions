# Extensions
This project aims to provide a simple and universal way to extend jvm based programs with
external "plugins" or "extensions".

This project is WIP. It is far from ready for anything. This README is just to collect my ideas in some place :)

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

@Extension(
        id = "example.extension", // optional - default is fully classified class name
        version = "0.0.1", // optional - default is Package#getImplementationVersion()
        name = "Example" // optional - default is simple class name
)
@Author({"Mike", "Peter", "Jeff"})
@Libraries({
    "com.google.guava:guava:31.1",
    "org.jetbrains:annotations:"
})
public class ExampleExtension {

  @Inject
  private ExamplePlatform platform;

  public ExampleExtension(Logger logger) {
    logger.info("ExampleExtensions says Hi 👋");
  }

  @After(ExampleDependencyExtension.class)
  public void afterX(ExampleDependencyExtension dep) { // or afterX() {
    // do some stuff
  }
  
  @Hook()

  @Destruct
  public void destruct() {
      // do stuff before shutdown
  }
  
}
```

### data-fetchers

```java

```

### libraries

Libraries are fetched from maven repositories. If there is a library from a specific repository