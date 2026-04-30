package io.cloudbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud")
public class CloudBridgeProperties {

    private CloudProvider provider = CloudProvider.AWS;
    private final MessagingProperties messaging = new MessagingProperties();
    private final AwsProperties aws = new AwsProperties();
    private final AzureProperties azure = new AzureProperties();
    private final GcpProperties gcp = new GcpProperties();
    private final OciProperties oci = new OciProperties();
    private final StorageProperties storage = new StorageProperties();

    public CloudProvider getProvider() {
        return provider;
    }

    public void setProvider(CloudProvider provider) {
        this.provider = provider;
    }

    public MessagingProperties getMessaging() {
        return messaging;
    }

    public AwsProperties getAws() {
        return aws;
    }

    public AzureProperties getAzure() {
        return azure;
    }

    public GcpProperties getGcp() {
        return gcp;
    }

    public OciProperties getOci() {
        return oci;
    }

    public StorageProperties getStorage() {
        return storage;
    }

    public static class MessagingProperties {
        private final ListenerProperties listener = new ListenerProperties();
        private final RetryProperties retry = new RetryProperties();
        private String dlqSuffix = ".dlq";
        private int receiveWaitSeconds = 10;
        private long idleBackoffMs = 1_000;

        public ListenerProperties getListener() {
            return listener;
        }

        public RetryProperties getRetry() {
            return retry;
        }

        public String getDlqSuffix() {
            return dlqSuffix;
        }

        public void setDlqSuffix(String dlqSuffix) {
            this.dlqSuffix = dlqSuffix;
        }

        public int getReceiveWaitSeconds() {
            return receiveWaitSeconds;
        }

        public void setReceiveWaitSeconds(int receiveWaitSeconds) {
            this.receiveWaitSeconds = receiveWaitSeconds;
        }

        public long getIdleBackoffMs() {
            return idleBackoffMs;
        }

        public void setIdleBackoffMs(long idleBackoffMs) {
            this.idleBackoffMs = idleBackoffMs;
        }
    }

    public static class ListenerProperties {
        private int defaultConcurrency = 1;
        private int workerThreads = 4;
        private int queueCapacity = 100;

        public int getDefaultConcurrency() {
            return defaultConcurrency;
        }

        public void setDefaultConcurrency(int defaultConcurrency) {
            this.defaultConcurrency = defaultConcurrency;
        }

        public int getWorkerThreads() {
            return workerThreads;
        }

        public void setWorkerThreads(int workerThreads) {
            this.workerThreads = workerThreads;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }

    public static class RetryProperties {
        private int maxAttempts = 3;
        private long backoffMs = 2_000;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getBackoffMs() {
            return backoffMs;
        }

        public void setBackoffMs(long backoffMs) {
            this.backoffMs = backoffMs;
        }
    }

    public static class AwsProperties {
        private String region = "us-east-1";
        private String endpoint;
        private String queuePrefix = "";
        private String dynamoTable = "cloud_bridge_kv";

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getQueuePrefix() {
            return queuePrefix;
        }

        public void setQueuePrefix(String queuePrefix) {
            this.queuePrefix = queuePrefix;
        }

        public String getDynamoTable() {
            return dynamoTable;
        }

        public void setDynamoTable(String dynamoTable) {
            this.dynamoTable = dynamoTable;
        }
    }

    public static class AzureProperties {
        private String connectionString;

        public String getConnectionString() {
            return connectionString;
        }

        public void setConnectionString(String connectionString) {
            this.connectionString = connectionString;
        }
    }

    public static class GcpProperties {
        private String projectId;
        private String emulatorHost;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getEmulatorHost() {
            return emulatorHost;
        }

        public void setEmulatorHost(String emulatorHost) {
            this.emulatorHost = emulatorHost;
        }
    }

    public static class OciProperties {
        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    public static class StorageProperties {
        private String tableName = "cloud_bridge_kv";

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }
    }
}
