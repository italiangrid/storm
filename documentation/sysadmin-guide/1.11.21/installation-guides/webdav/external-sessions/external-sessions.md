# Externalized session support

Starting with version 1.4.0, StoRM WebDAV supports storing HTTP session 
information in an external [redis][redis] server.

This can be useful when deploying multiple replicas of the StoRM WebDAV
service.

## Configuring support for externalized sessions 

Externalized session support can be enabled by adding the following
configuration to the `/etc/storm/webdav/config/application.yaml` file:

```yaml
spring:
  session:
    store-type: redis

  redis:
    host: redis.host.example
    port: 6379

```

For other redis connection configuration options, see the [Spring boot reference guide][spring-boot-reference].

[redis]: https://redis.io/
[spring-boot-reference]: https://docs.spring.io/spring-boot/docs/2.2.9.RELEASE/reference/htmlsingle/#data-properties