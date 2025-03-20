package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility class represents a mod instance, which includes both the common and client side of a mod.
 * It provides methods to set up and post-initialize the common and client side of the mod.
 */
public abstract class ModInstance {
    public final String modId;
    protected Runnable onCommon;
    protected Consumer<ParallelDispatch> onPostCommon;
    protected Runnable onClient;
    protected Consumer<ParallelDispatch> onPostClient;

    protected ModInstance(
        String modId,
        Runnable onCommon,
        Consumer<ParallelDispatch> onPostCommon,
        Runnable onClient,
        Consumer<ParallelDispatch> onPostClient
    ) {
        this.modId = Objects.requireNonNull(modId, "Mod ID cannot be null");
        this.onCommon = onCommon;
        this.onPostCommon = onPostCommon;
        this.onClient = onClient;
        this.onPostClient = onPostClient;
        this.populateIfEmpty();
    }

    /**
     * Creates a new builder for a mod instance with the specified ID.
     *
     * @param modId the mod ID
     * @return a new builder
     * @throws NullPointerException if modId is null
     */
    public static Builder create(@NotNull String modId) {
        return new Builder(modId);
    }

    /**
     * Initializes the ModInstance, registering necessary events for the appropriate platform.
     */
    public abstract void bootstrap();

    /**
     * Prevents null pointer exceptions by populating the fields with empty methods.
     */
    private void populateIfEmpty() {
        if (this.onCommon == null) {
            this.onCommon = () -> {};
        }
        if (this.onPostCommon == null) {
            this.onPostCommon = dispatch -> {};
        }
        if (this.onClient == null) {
            this.onClient = () -> {};
        }
        if (this.onPostClient == null) {
            this.onPostClient = dispatch -> {};
        }
    }

    /**
     * Builder class for creating ModInstance objects in a fluent manner.
     */
    public static class Builder {
        private final String modId;
        private Runnable onCommon;
        private Consumer<ParallelDispatch> onPostCommon;
        private Runnable onClient;
        private Consumer<ParallelDispatch> onPostClient;

        protected Builder(String modId) {
            this.modId = Objects.requireNonNull(modId, "Mod ID cannot be null");
        }

        /**
         * Sets the common setup code to run during mod initialization.
         *
         * @param common the common setup code
         * @return this builder
         */
        public Builder common(Runnable common) {
            this.onCommon = common;
            return this;
        }

        /**
         * Sets the common post-setup code to run after mod initialization.
         *
         * @param common the common post-setup code
         * @return this builder
         */
        public Builder postCommon(Consumer<ParallelDispatch> common) {
            this.onPostCommon = common;
            return this;
        }

        /**
         * Sets the client-side setup code to run during mod initialization.
         *
         * @param client the client-side setup code
         * @return this builder
         */
        public Builder client(Runnable client) {
            this.onClient = client;
            return this;
        }

        /**
         * Sets the client-side post-setup code to run after mod initialization.
         *
         * @param client the client-side post-setup code
         * @return this builder
         */
        public Builder postClient(Consumer<ParallelDispatch> client) {
            this.onPostClient = client;
            return this;
        }

        /**
         * Builds a ModInstance with the configured setup methods.
         *
         * @return a new ModInstance
         */
        public ModInstance build() {
            return builder(this.modId, this.onCommon, this.onPostCommon, this.onClient, this.onPostClient);
        }

        @ExpectPlatform
        public static ModInstance builder(
            String modId,
            Runnable common,
            Consumer<ParallelDispatch> postCommon,
            Runnable client,
            Consumer<ParallelDispatch> postClient
        ) {
            throw new AssertionError();
        }
    }
}