# Spring-Boot elasticsearch running on cloud.elastic.co

This is a demo project to run spring-boot with elasticsearch instances running
on https://cloud.elastic.co.

Change your settings in `DemoApplication` class :

```java
	private static final String CLUSTER_NAME = "ELASTIC_CO_CLUSTER_LONG_ID";
	private static final String PASSWORD = "PASSWORD";
```

