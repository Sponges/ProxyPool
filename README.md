# ProxyPool
Thread safe pool of proxies!

Features:

* Tests for a working proxy when getting one from the pool.
* Support for as many proxies as you input.
* Removes broken proxies from the pool automatically.
* Re-checks each proxy if it hasn't been used in over an hour.

## Usage
### Proxy format
All proxies should be formatted `ip:port`.

The proxy pool takes proxies as `ArrayList<String[]>`.

### Instantiating the pool
Assuming "proxies" is an arraylist...
```java
ProxyPool pool = new ProxyPool(proxies);
```

### Getting a proxy
```java
try (HttpProxy proxy = pool.getProxy()) {
  // Here's your proxy
}
```
A try-with-resources is used because `HttpProxy` implements the `AutoClosable` interface, which will return the proxy to the pool after use.
If you are using a java version that does not support this, it is important to call `ProxyPool#returnResource(HttpProxy)` once used.

### Testing a list of working proxies
As well as testing proxies when getting from the pool, you can get a list of working proxies which will all be tested.
```java
List<String[]> working = pool.getWorking();
```

## Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Sponges</groupId>
    <artifactId>ProxyPool</artifactId>
    <version>x</version>
</dependency>
```
Versions are found on https://jitpack.io/#Sponges/ProxyPool - see the commits tab.

## License
This project is licensed under the MIT license. See the `LICENSE` file for the full license.