package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * This class represents a mod instance, which includes both the common and client side of a mod.
 * It provides methods to set up and post-initialize the common and client side of the mod.
 *
 * @author ItsBlackGear
 */
public abstract class ModInstance {
    public final String modId;
    public Runnable onCommon;
    public Runnable onPostCommon;
    public Runnable onClient;
    public Runnable onPostClient;

    public ModInstance(String modId, Runnable onCommon, Runnable onPostCommon, Runnable onClient, Runnable onPostClient) {
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
    private void postCommonSetup(Runnable common) {
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
    private void postClientSetup(Runnable client) {
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
            this.postCommonSetup(() -> {});
        }
        if (this.onClient == null) {
            this.clientSetup(() -> {});
        }
        if (this.onPostClient == null) {
            this.postClientSetup(() -> {});
        }
    }

    public static class Builder {
        private final String modId;
        private Runnable onCommon;
        private Runnable onPostCommon;
        private Runnable onClient;
        private Runnable onPostClient;

        protected Builder(String modId) {
            this.modId = modId;
        }

        public Builder common(Runnable common) {
            this.onCommon = common;
            return this;
        }

        public Builder postCommon(Runnable common) {
            this.onPostCommon = common;
            return this;
        }

        public Builder client(Runnable client) {
            this.onClient = client;
            return this;
        }

        public Builder postClient(Runnable client) {
            this.onPostClient = client;
            return this;
        }

        public ModInstance build() {
            return builder(this.modId, this.onCommon, this.onPostCommon, this.onClient, this.onPostClient);
        }

        @ExpectPlatform
        public static ModInstance builder(String modId, Runnable common, Runnable postCommon, Runnable client, Runnable postClient) {
            throw new AssertionError();
        }
    }
}