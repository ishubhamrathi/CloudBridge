package io.cloudbridge.config;

import java.util.Objects;

public class ProviderResolver {

    private final CloudBridgeProperties properties;

    public ProviderResolver(CloudBridgeProperties properties) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    public CloudProvider currentProvider() {
        return properties.getProvider();
    }

    public boolean is(CloudProvider provider) {
        return currentProvider() == provider;
    }
}
