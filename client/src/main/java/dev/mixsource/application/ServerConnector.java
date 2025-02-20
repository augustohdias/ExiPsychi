package dev.mixsource.application;

import java.util.function.Consumer;

public interface ServerConnector {
    void connect();
    void setOnUpdate(Consumer<String> updateHandler);
} 