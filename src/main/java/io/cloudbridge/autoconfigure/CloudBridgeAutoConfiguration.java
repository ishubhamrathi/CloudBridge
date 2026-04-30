package io.cloudbridge.autoconfigure;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import io.cloudbridge.aws.messaging.AwsQueueClient;
import io.cloudbridge.aws.messaging.AwsQueueConsumerFactory;
import io.cloudbridge.aws.storage.AwsKeyValueStore;
import io.cloudbridge.azure.messaging.AzureQueueConsumerFactory;
import io.cloudbridge.azure.messaging.AzureServiceBusQueueClient;
import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.config.ProviderResolver;
import io.cloudbridge.core.messaging.CloudCapabilities;
import io.cloudbridge.core.messaging.QueueClient;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.core.messaging.SimpleCloudCapabilities;
import io.cloudbridge.core.storage.KeyValueStore;
import io.cloudbridge.gcp.messaging.GcpPubSubQueueClient;
import io.cloudbridge.gcp.messaging.GcpQueueConsumerFactory;
import io.cloudbridge.listener.QueueListenerAnnotationBeanPostProcessor;
import io.cloudbridge.listener.QueueListenerContainerManager;
import io.cloudbridge.listener.QueueListenerMethodInvoker;
import io.cloudbridge.listener.QueueListenerRegistry;
import io.cloudbridge.oci.messaging.OciQueueConsumerFactory;
import io.cloudbridge.oci.messaging.OciQueueClient;
import io.cloudbridge.retry.BackoffSleeper;
import io.cloudbridge.retry.DeadLetterPublisher;
import io.cloudbridge.retry.DefaultDeadLetterPublisher;
import io.cloudbridge.retry.NoOpDeadLetterPublisher;
import io.cloudbridge.retry.RetryExecutor;
import io.cloudbridge.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@AutoConfiguration
@EnableConfigurationProperties(CloudBridgeProperties.class)
public class CloudBridgeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProviderResolver providerResolver(CloudBridgeProperties properties) {
        return new ProviderResolver(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy(CloudBridgeProperties properties) {
        return new RetryPolicy(
                properties.getMessaging().getRetry().getMaxAttempts(),
                Duration.ofMillis(properties.getMessaging().getRetry().getBackoffMs()));
    }

    @Bean
    @ConditionalOnMissingBean
    public BackoffSleeper backoffSleeper() {
        return duration -> Thread.sleep(duration.toMillis());
    }

    @Bean
    @ConditionalOnBean(QueueClient.class)
    @ConditionalOnMissingBean(DeadLetterPublisher.class)
    public DeadLetterPublisher deadLetterPublisher(QueueClient queueClient, CloudBridgeProperties properties) {
        return new DefaultDeadLetterPublisher(queueClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean(DeadLetterPublisher.class)
    public DeadLetterPublisher noOpDeadLetterPublisher() {
        return new NoOpDeadLetterPublisher();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryExecutor retryExecutor(RetryPolicy retryPolicy, DeadLetterPublisher deadLetterPublisher, BackoffSleeper sleeper) {
        return new RetryExecutor(retryPolicy, deadLetterPublisher, sleeper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "cloudBridgeListenerExecutor")
    public ThreadPoolTaskExecutor cloudBridgeListenerExecutor(CloudBridgeProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("cloudbridge-listener-");
        executor.setCorePoolSize(properties.getMessaging().getListener().getWorkerThreads());
        executor.setMaxPoolSize(properties.getMessaging().getListener().getWorkerThreads());
        executor.setQueueCapacity(properties.getMessaging().getListener().getQueueCapacity());
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public QueueListenerRegistry queueListenerRegistry() {
        return new QueueListenerRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueueListenerMethodInvoker queueListenerMethodInvoker() {
        return new QueueListenerMethodInvoker();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueueListenerAnnotationBeanPostProcessor queueListenerAnnotationBeanPostProcessor(
            QueueListenerRegistry registry,
            CloudBridgeProperties properties
    ) {
        return new QueueListenerAnnotationBeanPostProcessor(registry, properties);
    }

    @Bean
    @ConditionalOnBean(QueueConsumerFactory.class)
    @ConditionalOnMissingBean
    public QueueListenerContainerManager queueListenerContainerManager(
            QueueListenerRegistry registry,
            QueueConsumerFactory queueConsumerFactory,
            QueueListenerMethodInvoker methodInvoker,
            RetryExecutor retryExecutor,
            ThreadPoolTaskExecutor cloudBridgeListenerExecutor
    ) {
        return new QueueListenerContainerManager(registry, queueConsumerFactory, methodInvoker, retryExecutor, cloudBridgeListenerExecutor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    @ConditionalOnMissingBean
    public SqsClient sqsClient(CloudBridgeProperties properties) {
        SqsClient.Builder builder = SqsClient.builder()
                .region(Region.of(properties.getAws().getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (properties.getAws().getEndpoint() != null && !properties.getAws().getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(properties.getAws().getEndpoint()));
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient(CloudBridgeProperties properties) {
        DynamoDbClient.Builder builder = DynamoDbClient.builder()
                .region(Region.of(properties.getAws().getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (properties.getAws().getEndpoint() != null && !properties.getAws().getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(properties.getAws().getEndpoint()));
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    public QueueClient awsQueueClient(SqsClient sqsClient, CloudBridgeProperties properties) {
        return new AwsQueueClient(sqsClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    public QueueConsumerFactory awsQueueConsumerFactory(SqsClient sqsClient, CloudBridgeProperties properties) {
        return new AwsQueueConsumerFactory(sqsClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    public KeyValueStore awsKeyValueStore(DynamoDbClient dynamoDbClient, CloudBridgeProperties properties) {
        return new AwsKeyValueStore(dynamoDbClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AWS", matchIfMissing = true)
    public CloudCapabilities awsCapabilities() {
        return new SimpleCloudCapabilities(true, true, true);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AZURE")
    public ServiceBusClientBuilder serviceBusClientBuilder(CloudBridgeProperties properties) {
        return new ServiceBusClientBuilder().connectionString(properties.getAzure().getConnectionString());
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AZURE")
    public QueueClient azureQueueClient(ServiceBusClientBuilder clientBuilder) {
        return new AzureServiceBusQueueClient(clientBuilder);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AZURE")
    public QueueConsumerFactory azureQueueConsumerFactory(ServiceBusClientBuilder clientBuilder) {
        return new AzureQueueConsumerFactory(clientBuilder);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "AZURE")
    public CloudCapabilities azureCapabilities() {
        return new SimpleCloudCapabilities(true, true, true);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "GCP")
    public QueueClient gcpQueueClient(CloudBridgeProperties properties) {
        return new GcpPubSubQueueClient(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "GCP")
    public QueueConsumerFactory gcpQueueConsumerFactory(CloudBridgeProperties properties) {
        return new GcpQueueConsumerFactory(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "GCP")
    public CloudCapabilities gcpCapabilities() {
        return new SimpleCloudCapabilities(false, true, true);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "OCI")
    @ConditionalOnMissingBean
    public ConfigFileAuthenticationDetailsProvider ociAuthenticationDetailsProvider(CloudBridgeProperties properties) throws java.io.IOException {
        return new ConfigFileAuthenticationDetailsProvider(
                ConfigFileReader.parse(properties.getOci().getConfigFilePath(), properties.getOci().getProfile()));
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "OCI")
    @ConditionalOnMissingBean(name = "ociQueueSdkClient")
    public com.oracle.bmc.queue.QueueClient ociQueueSdkClient(
            ConfigFileAuthenticationDetailsProvider authenticationDetailsProvider,
            CloudBridgeProperties properties
    ) {
        com.oracle.bmc.queue.QueueClient client = com.oracle.bmc.queue.QueueClient.builder()
                .build(authenticationDetailsProvider);
        if (properties.getOci().getEndpoint() != null && !properties.getOci().getEndpoint().isBlank()) {
            client.setEndpoint(properties.getOci().getEndpoint());
        }
        return client;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "OCI")
    public QueueClient ociQueueClient(@Qualifier("ociQueueSdkClient") com.oracle.bmc.queue.QueueClient queueClient) {
        return new OciQueueClient(queueClient);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "OCI")
    public QueueConsumerFactory ociQueueConsumerFactory(
            @Qualifier("ociQueueSdkClient") com.oracle.bmc.queue.QueueClient queueClient,
            CloudBridgeProperties properties
    ) {
        return new OciQueueConsumerFactory(queueClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud", name = "provider", havingValue = "OCI")
    public CloudCapabilities ociCapabilities() {
        return new SimpleCloudCapabilities(true, true, true);
    }
}
