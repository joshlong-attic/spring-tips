package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public TransportClient elasticsearchClient(ElasticsearchProperties elasticsearchProperties)
            throws Exception {

        String clusterName = elasticsearchProperties.getClusterName();
        log.info("clusterName: " + clusterName);

        String clusterUrl = elasticsearchProperties.getClusterNodes();
        log.info("clusterUrl: " + clusterUrl);

        Map<String, String> properties = elasticsearchProperties.getProperties();
        String credentials = properties.get("username") + ":" + properties.get("password");

        Settings settings = Settings.builder()
                .put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false)
                .put("transport.tcp.compress", true)
                .put("cluster.name", clusterName/* CLUSTER_NAME*/)
                .put("xpack.security.transport.ssl.enabled", true)
                .put("request.headers.X-Found-Cluster", clusterName)
                .put("xpack.security.user", credentials)
                .build();

        return new PreBuiltXPackTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(clusterUrl, 9343)));
    }

    @Bean
    public ApplicationRunner runner(TransportClient client,
                                    CustomerRepository repository) {

        return (args) -> {
            ClusterHealthResponse response = client.admin().cluster().prepareHealth().get();
            log.info("response = " + response.getClusterName());

            repository.deleteAll();
            repository.save(new Customer("Alice", "Smith"));
            repository.save(new Customer("Bob", "Smith"));
            log.info("Customers found with findAll():");
            log.info("-------------------------------");
            for (Customer customer : repository.findAll()) {
                log.info(customer.toString());
            }

        };
    }

}
