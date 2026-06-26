package com.blackgear.platform.core.util.config.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ConfigLoader;
import com.blackgear.platform.core.util.config.fabric.FabricConfigBuilder.BuilderContext;
import com.electronwill.nightconfig.core.*;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionListener;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.*;

public class FabricConfigSpec extends UnmodifiableConfigWrapper<UnmodifiableConfig> {
    static final Joiner LINE_JOINER = Joiner.on("\n");
    static final Joiner DOT_JOINER = Joiner.on(".");
    private static final Marker CORE = MarkerManager.getMarker("CORE");
    private final Map<List<String>, String> levelComments;
    private final UnmodifiableConfig values;
    private Config childConfig;
    
    private boolean isCorrecting = false;
    
    FabricConfigSpec(UnmodifiableConfig storage, UnmodifiableConfig values, Map<List<String>, String> levelComments) {
        super(storage);
        this.values = values;
        this.levelComments = levelComments;
    }
    
    public void setConfig(CommentedConfig config) {
        this.childConfig = config;
        if (config != null && !this.isCorrect(config)) {
            String configName = config instanceof FileConfig ? ((FileConfig) config).getNioPath().toString() : config.toString();
            Platform.LOGGER.warn("Configuration file {} is not correct. Correcting", configName);
            this.correct(config,
                (action, path, incorrectValue, correctedValue) ->
                    Platform.LOGGER.warn("Incorrect key {} was corrected from {} to its default, {}. {}", DOT_JOINER.join(path), incorrectValue, correctedValue, incorrectValue == correctedValue ? "This seems to be an error." : ""),
                (action, path, incorrectValue, correctedValue) ->
                    Platform.LOGGER.debug("The comment on key {} does not match the spec. This may create a backup.", DOT_JOINER.join(path)));
            
            if (config instanceof FileConfig) {
                ((FileConfig) config).save();
            }
        }
        this.afterReload();
    }

    public boolean isCorrecting() {
        return this.isCorrecting;
    }
    
    public boolean isLoaded() {
        return this.childConfig != null;
    }
    
    public UnmodifiableConfig getSpec() {
        return this.config;
    }
    
    public UnmodifiableConfig getValues() {
        return this.values;
    }
    
    public void afterReload() {
        this.resetCaches(this.getValues().valueMap().values());
    }
    
    private void resetCaches(final Iterable<Object> values) {
        values.forEach(value -> {
            if (value instanceof FabricConfigValue<?> configValue) {
                configValue.clearCache();
            } else if (value instanceof Config config) {
                this.resetCaches(config.valueMap().values());
            }
        });
    }
    
    public void save() {
        Preconditions.checkNotNull(this.childConfig, "Cannot save config value without assigned Config object present");
        if (this.childConfig instanceof FileConfig) {
            ((FileConfig) this.childConfig).save();
        }
    }
    
    public synchronized boolean isCorrect(CommentedConfig config) {
        LinkedList<String> parent = new LinkedList<>();
        return this.correct(this.config, config, null, parent, Collections.unmodifiableList(parent), (action, path, incorrectValue, correctedValue) -> {}, null, true) == 0;
    }
    
    public int correct(CommentedConfig config) {
        return this.correct(config, (action, path, incorrectValue, correctedValue) -> {}, null);
    }
    
    public synchronized int correct(CommentedConfig config, CorrectionListener listener) {
        return this.correct(config, listener, null);
    }
    
    public synchronized int correct(CommentedConfig config, CorrectionListener listener, CorrectionListener commentListener) {
        LinkedList<String> parent = new LinkedList<>();
        int ret = -1;

        try {
            this.isCorrecting = true;
            
            final Map<String, Object> defaultMap;
            if (config instanceof FileConfig fileConfig) {
                defaultMap = ConfigLoader.DEFAULT_CONFIG_VALUES.get(fileConfig.getNioPath().getFileName().toString().intern());
            } else {
                defaultMap = null;
            }
            
            ret = this.correct(this.config, config, defaultMap, parent, Collections.unmodifiableList(parent), listener, commentListener, false);
        } finally {
            this.isCorrecting = false;
        }

        return ret;
    }
    
    private int correct(UnmodifiableConfig spec, CommentedConfig config, @Nullable Map<String, Object> defaultMap, LinkedList<String> parentPath, List<String> parentPathUnmodifiable, CorrectionListener listener, CorrectionListener commentListener, boolean dryRun) {
        int count = 0;
        
        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();
        
        for (Map.Entry<String, Object> specEntry : specMap.entrySet()) {
            final String key = specEntry.getKey();
            final Object specValue = specEntry.getValue();
            final Object configValue = configMap.get(key);
            final ConfigSpec.CorrectionAction action = configValue == null ? ADD : REPLACE;
            
            parentPath.addLast(key);
            
            if (specValue instanceof Config) {
                if (configValue instanceof CommentedConfig) {
                    count += this.correct((Config) specValue, (CommentedConfig) configValue, defaultMap != null && defaultMap.get(key) instanceof Config defaultConfig ? defaultConfig.valueMap() : null, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                    
                    if (count > 0 && dryRun) {
                        return count;
                    }
                } else if (dryRun) {
                    return 1;
                } else {
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                    count += this.correct((Config) specValue, newValue, defaultMap != null && defaultMap.get(key) instanceof Config defaultConfig ? defaultConfig.valueMap() : null, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                }
                
                String newComment = levelComments.get(parentPath);
                String oldComment = config.getComment(key);
                
                if (stringsNotEqual(oldComment, newComment)) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, newComment);
                    }
                    
                    if (dryRun) {
                        return 1;
                    }
                    
                    config.setComment(key, newComment);
                }
            } else {
                ValueSpec valueSpec = (ValueSpec) specValue;
                if (!valueSpec.test(configValue)) {
                    if (dryRun) {
                        return 1;
                    }
                    
                    Object newValue = valueSpec.correct(configValue);
                    if (defaultMap != null && defaultMap.containsKey(key)) {
                        if (valueSpec.getRange() != null) {
                            newValue = valueSpec.getRange().correct(configValue, defaultMap.get(key));
                        } else {
                            newValue = defaultMap.get(key);
                        }
                        if (!valueSpec.test(newValue)) {
                            newValue = valueSpec.correct(configValue);
                        }
                    }
                    
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                }
                
                String oldComment = config.getComment(key);
                if (stringsNotEqual(oldComment, valueSpec.getComment())) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, valueSpec.getComment());
                    }
                    
                    if (dryRun) {
                        return 1;
                    }
                    
                    config.setComment(key, valueSpec.getComment());
                }
            }
            
            parentPath.removeLast();
        }
        
        // Second step: removes the unspecified values
        for (Iterator<Map.Entry<String, Object>> ittr = configMap.entrySet().iterator(); ittr.hasNext();) {
            Map.Entry<String, Object> entry = ittr.next();
            if (!specMap.containsKey(entry.getKey())) {
                if (dryRun) {
                    return 1;
                }
                
                ittr.remove();
                parentPath.addLast(entry.getKey());
                listener.onCorrect(REMOVE, parentPathUnmodifiable, entry.getValue(), null);
                parentPath.removeLast();
                count++;
            }
        }
        
        return count;
    }
    
    private boolean stringsNotEqual(@Nullable Object obj1, @Nullable Object obj2) {
        if (obj1 instanceof String string1 && obj2 instanceof String string2) {
            if (string1.length() > 0 && string2.length() > 0)
                return !string1.replaceAll("\r\n", "\n").equals(string2.replaceAll("\r\n", "\n"));
        }
        
        // Fallback for when we're not given Strings, or one of them is empty
        return !Objects.equals(obj1, obj2);
    }
    
    public static class Range<V extends Comparable<? super V>> implements Predicate<Object> {
        private final Class<? extends V> clazz;
        private final V min;
        private final V max;
        
        Range(Class<V> clazz, V min, V max) {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }
        
        public Class<? extends V> getClazz() {
            return this.clazz;
        }
        
        public V getMin() {
            return this.min;
        }
        
        public V getMax() {
            return this.max;
        }
        
        private boolean isNumber(Object other) {
            return Number.class.isAssignableFrom(this.clazz) && other instanceof Number;
        }
        
        @Override
        public boolean test(Object t) {
            if (this.isNumber(t)) {
                Number n = (Number) t;
                boolean result = ((Number) this.min).doubleValue() <= n.doubleValue() && n.doubleValue() <= ((Number) this.max).doubleValue();
                
                if (!result) {
                    Platform.LOGGER.debug(CORE, "Range value {} is not within its bounds {}-{}", n.doubleValue(), ((Number) this.min).doubleValue(), ((Number) this.max).doubleValue());
                }
                
                return result;
            }
            
            if (!this.clazz.isInstance(t)) {
                return false;
            }
            
            V c = this.clazz.cast(t);
            
            boolean result = c.compareTo(this.min) >= 0 && c.compareTo(this.max) <= 0;
            if (!result) {
                Platform.LOGGER.debug(CORE, "Range value {} is not within its bounds {}-{}", c, this.min, this.max);
            }
            
            return result;
        }
        
        public Object correct(Object value, Object def) {
            if (this.isNumber(value)) {
                Number n = (Number) value;
                return n.doubleValue() < ((Number) this.min).doubleValue()
                    ? this.min
                    : n.doubleValue() > ((Number) this.max).doubleValue()
                        ? this.max
                        : value;
            }
            
            if (!this.clazz.isInstance(value)) {
                return def;
            }
            
            V c = this.clazz.cast(value);
            return c.compareTo(this.min) < 0
                ? this.min
                : c.compareTo(this.max) > 0
                    ? this.max
                    : value;
        }
        
        @Override
        public String toString() {
            if (this.clazz == Integer.class) {
                if (this.max.equals(Integer.MAX_VALUE)) {
                    return "> " + this.min;
                } else if (this.min.equals(Integer.MIN_VALUE)) {
                    return "< " + this.max;
                }
            }
            
            return this.min + " ~ " + this.max;
        }
    }
    
    public static class ValueSpec {
        private final String comment;
        private final String langKey;
        private final Range<?> range;
        private final boolean worldRestart;
        private final Class<?> clazz;
        private final Supplier<?> supplier;
        private final Predicate<Object> validator;
        private Object _default = null;
        
        ValueSpec(Supplier<?> supplier, Predicate<Object> validator, BuilderContext context) {
            Objects.requireNonNull(supplier, "Default supplier can not be null");
            Objects.requireNonNull(validator, "Validator can not be null");
            
            this.comment = context.hasComment() ? context.buildComment() : null;
            this.langKey = context.getTranslationKey();
            this.range = context.getRange();
            this.worldRestart = context.needsWorldRestart();
            this.clazz = context.getClazz();
            this.supplier = supplier;
            this.validator = validator;
        }
        
        public String getComment() {
            return this.comment;
        }
        
        public String getTranslationKey() {
            return this.langKey;
        }
        
        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() {
            return (Range<V>) this.range;
        }
        
        public boolean needsWorldRestart() {
            return this.worldRestart;
        }
        
        public Class<?> getClazz() {
            return this.clazz;
        }
        
        public boolean test(Object value) {
            return this.validator.test(value);
        }
        
        public Object correct(Object value) {
            return this.range == null ? this.getDefault() : this.range.correct(value, this.getDefault());
        }
        
        public Object getDefault() {
            if (this._default == null) {
                this._default = this.supplier.get();
            }
            
            return this._default;
        }
    }
    
    public static class FabricConfigValue<T> implements ConfigBuilder.ConfigValue<T> {
        private static final boolean USE_CACHES = true;
        
        private final FabricConfigBuilder parent;
        private final List<String> path;
        private final Supplier<T> defaultSupplier;
        
        private T cachedValue = null;
        
        FabricConfigSpec spec;
        
        FabricConfigValue(FabricConfigBuilder parent, List<String> path, Supplier<T> defaultSupplier) {
            this.parent = parent;
            this.path = path;
            this.defaultSupplier = defaultSupplier;
            this.parent.values.add(this);
        }
        
        @Override
        public List<String> getPath() {
            return Lists.newArrayList(this.path);
        }
        
        @Override
        public T get() {
            Preconditions.checkNotNull(this.spec, "Cannot get config value before spec is built");
            
            if (this.spec.childConfig == null) {
                return this.defaultSupplier.get();
            }
            
            if (USE_CACHES && this.cachedValue == null) {
                this.cachedValue = this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
            } else if (!USE_CACHES) {
                return this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
            }
            
            return this.cachedValue;
        }
        
        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getOrElse(path, defaultSupplier);
        }
        
        @Override
        public ConfigBuilder next() {
            return this.parent;
        }
        
        @Override
        public void save() {
            Preconditions.checkNotNull(this.spec, "Cannot save config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot save config value without assigned Config object present");

            this.spec.save();
        }
        
        @Override
        public void set(T value) {
            Preconditions.checkNotNull(this.spec, "Cannot set config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot set config value without assigned Config object present");
            
            this.spec.childConfig.set(this.path, value);
            this.cachedValue = value;
        }
        
        @Override
        public void clearCache() {
            this.cachedValue = null;
        }
    }
    
    public static class BooleanValue extends FabricConfigValue<Boolean> {
        BooleanValue(FabricConfigBuilder parent, List<String> path, Supplier<Boolean> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
    }
    
    public static class IntValue extends FabricConfigValue<Integer> {
        IntValue(FabricConfigBuilder parent, List<String> path, Supplier<Integer> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
        
        @Override
        protected Integer getRaw(Config config, List<String> path, Supplier<Integer> defaultSupplier) {
            return config.getIntOrElse(path, defaultSupplier::get);
        }
    }
    
    public static class LongValue extends FabricConfigValue<Long> {
        LongValue(FabricConfigBuilder parent, List<String> path, Supplier<Long> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
        
        @Override
        protected Long getRaw(Config config, List<String> path, Supplier<Long> defaultSupplier) {
            return config.getLongOrElse(path, defaultSupplier::get);
        }
    }
    
    public static class DoubleValue extends FabricConfigValue<Double> {
        DoubleValue(FabricConfigBuilder parent, List<String> path, Supplier<Double> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
        
        @Override
        protected Double getRaw(Config config, List<String> path, Supplier<Double> defaultSupplier) {
            Number n = config.get(path);
            return n == null ? defaultSupplier.get() : n.doubleValue();
        }
    }
    
    public static class EnumValue<T extends Enum<T>> extends FabricConfigValue<T> {
        private final EnumGetMethod converter;
        private final Class<T> clazz;
        
        EnumValue(FabricConfigBuilder parent, List<String> path, Supplier<T> defaultSupplier, EnumGetMethod converter, Class<T> clazz) {
            super(parent, path, defaultSupplier);
            this.converter = converter;
            this.clazz = clazz;
        }
        
        @Override
        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getEnumOrElse(path, this.clazz, this.converter, defaultSupplier);
        }
    }
}