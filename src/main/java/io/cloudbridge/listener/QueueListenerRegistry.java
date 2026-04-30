package io.cloudbridge.listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueueListenerRegistry {

    private final CopyOnWriteArrayList<QueueListenerEndpoint> endpoints = new CopyOnWriteArrayList<>();

    public void register(QueueListenerEndpoint endpoint) {
        endpoints.add(endpoint);
    }

    public List<QueueListenerEndpoint> getEndpoints() {
        return List.copyOf(endpoints);
    }
}
