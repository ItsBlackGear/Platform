package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Consumer;

/**
 * Utility class represents a mod instance, which includes both the common and client side of a mod.
 * It provides methods to set up and post-initialize the common and client side of the mod.
 *
 * <p>Example of a mod instance creation:</p>
 *
 * <pre>{@code
 *
 * ModInstance INSTANCE = ModInstance.create(MOD_ID)
 *  // custom class holding all the common setup methods
 * 	.common(CommonSetup::onStartup)
 * 	.postCommon(CommonSetup::postStartup)
 * 	// custom class holding all the client setup methods
 * 	.client(ClientSetup::onStartup)
 * 	.postClient(ClientSetup::postStartup)
 * 	// build the mod instance
 * 	.build();
 *
 * // For CommonSetup and ClientSetup, we just create two public static void methods.
 * // Alternatively, you can also use lambda expressions like this:
 *
 * ModInstance INSTANCE = ModInstance.create(MOD_ID)
 * 	.common(() -> {})
 * 	.postCommon(() -> {})
 * 	.client(() -> {})
 * 	.postClient(() -> {})
 * 	.build();
 *
 * // It is not mandatory to initialize all the methods, just the ones that you need.
 * // Here's an example of an instance for a client-side only mod:
 *
 * ModInstance INSTANCE = ModInstance.create(MOD_ID)
 * 	.client(() -> {})
 * 	.postClient(() -> {})
 * 	.build();
 *
 * // Alternatively, you can do the same for a server-side only mod:
 *
 * }</pre>
 *
 * @author ItsBlackGear
 */
public abstract class ModInstance {
    public final String modId;
    public Runnable onCommon;
    public Consumer<ParallelDispatch> onPostCommon;
    public Runnable onClient;
    public Consumer<ParallelDispatch> onPostClient;

    public ModInstance(
        String modId,
        Runnable onCommon,
        Consumer<ParallelDispatch> onPostCommon,
        Runnable onClient,
        Consumer<ParallelDispatch> onPostClient
    ) {
        this.modId = modId;
        this.onCommon = onCommon;
        this.onPostCommon = onPostCommon;
        this.onClient = onClient;
        this.onPostClient = onPostClient;
        this.populateIfEmpty();
    }

    public static Builder create(String modId) {
        return new Builder(modId);
    }

    /**
     * method used to initialize the ModInstance.
     **/
    public abstract void bootstrap();

    /**
     * call the common setup instances for the mod initialization.
     **/
    private void commonSetup(Runnable common) {
        this.onCommon = common;
    }

    /**
     * call the common setup instances for the mod post-initialization.
     **/
    private void postCommonSetup(Consumer<ParallelDispatch> common) {
        this.onPostCommon = common;
    }

    /**
     * call the client setup instances for the mod initialization.
     **/
    private void clientSetup(Runnable client) {
        this.onClient = client;
    }

    /**
     * call the client setup instances for the mod post-initialization.
     **/
    private void postClientSetup(Consumer<ParallelDispatch> client) {
        this.onPostClient = client;
    }

    /**
     * prevents null pointer exceptions by populating the fields with empty methods.
     **/
    private void populateIfEmpty() {
        if (this.onCommon == null) {
            this.commonSetup(() -> {});
        }
        if (this.onPostCommon == null) {
            this.postCommonSetup(dispatch -> {});
        }
        if (this.onClient == null) {
            this.clientSetup(() -> {});
        }
        if (this.onPostClient == null) {
            this.postClientSetup(dispatch -> {});
        }
    }

    public static class Builder {
        private final String modId;
        private Runnable onCommon;
        private Consumer<ParallelDispatch> onPostCommon;
        private Runnable onClient;
        private Consumer<ParallelDispatch> onPostClient;

        protected Builder(String modId) {
            this.modId = modId;
        }

        public Builder common(Runnable common) {
            this.onCommon = common;
            return this;
        }

        public Builder postCommon(Consumer<ParallelDispatch> common) {
            this.onPostCommon = common;
            return this;
        }

        public Builder client(Runnable client) {
            this.onClient = client;
            return this;
        }

        public Builder postClient(Consumer<ParallelDispatch> client) {
            this.onPostClient = client;
            return this;
        }

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