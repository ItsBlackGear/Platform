package com.blackgear.platform.core.util.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeConfigBuilder implements ConfigBuilder {
    private final ForgeConfigSpec.Builder builder;
    
    public ForgeConfigBuilder(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
    }
    
    @Override
    public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
        return new ForgeConfigValue<>(this.builder.define(path, defaultSupplier, validator, clazz));
    }
    
    @Override
    public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return new ForgeConfigValue<>(this.builder.defineInRange(path, defaultSupplier, min, max, clazz));
    }
    
    @Override
    public <T> ConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return new ForgeConfigValue<>(this.builder.defineList(path, defaultSupplier, elementValidator));
    }
    
    @Override
    public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return new ForgeConfigValue<>(this.builder.defineListAllowEmpty(path, defaultSupplier, elementValidator));
    }
    
    @Override
    public <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return new ForgeConfigValue<>(this.builder.defineEnum(path, defaultSupplier, converter, validator, clazz));
    }
    
    @Override
    public ConfigValue<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier) {
        return new ForgeConfigValue<>(this.builder.define(path, defaultSupplier));
    }
    
    @Override
    public ConfigValue<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
        return new ForgeConfigValue<>(this.builder.defineInRange(path, defaultSupplier, min, max));
    }
    
    @Override
    public ConfigValue<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
        return new ForgeConfigValue<>(this.builder.defineInRange(path, defaultSupplier, min, max));
    }
    
    @Override
    public ConfigValue<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
        return new ForgeConfigValue<>(this.builder.defineInRange(path, defaultSupplier, min, max));
    }
    
    @Override
    public ConfigBuilder comment(String comment) {
        this.builder.comment(comment);
        return this;
    }
    
    @Override
    public ConfigBuilder comment(String... comment) {
        this.builder.comment(comment);
        return this;
    }
    
    @Override
    public ConfigBuilder translation(String translationKey) {
        this.builder.translation(translationKey);
        return this;
    }
    
    @Override
    public ConfigBuilder worldRestart() {
        this.builder.worldRestart();
        return this;
    }
    
    @Override
    public ConfigBuilder push(List<String> path) {
        this.builder.push(path);
        return this;
    }
    
    @Override
    public ConfigBuilder pop(int count) {
        this.builder.pop(count);
        return this;
    }
    
    public <T> Pair<T, ForgeConfigSpec> configure(Function<ConfigBuilder, T> consumer) {
        T o = consumer.apply(this);
        return Pair.of(o, this.builder.build());
    }
}