# Spring Boot and Elasticsearch running on [the hosted Elastic SaaS](htttp://cloud.elastic.co)

This is a demo project to run spring-boot with elasticsearch instances running [on the hosted Elastic SaaS](https://cloud.elastic.co)

In order for this to work, you'll need to specify: 

```
export SPRING_DATA_ELASTICSEARCH_PROPERTIES_PASSWORD=_password given to you when you setup a new Elastic cluster_
export SPRING_DATA_ELASTICSEARCH_CLUSTERNAME=_the long cluster name for your cluster_ 
export SPRING_DATA_ELASTICSEARCH_CLUSTERNODES=${SPRING_DATA_ELASTICSEARCH_CLUSTERNAME}.us-east-1.aws.found.io
export SPRING_DATA_ELASTICSEARCH_PROPERTIES_USERNAME=elastic
```
