package com.blackgear.platform.core.util.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

@SuppressWarnings("unchecked")
public interface IConfigSpec<T extends IConfigSpec<T>> extends UnmodifiableConfig {
    default T self() {
        return (T) this;
    }
    
    void acceptConfig(CommentedConfig config);
    
    boolean isCorrecting();
    
    boolean isCorrect(CommentedConfig config);
    
    int correct(CommentedConfig config);
    
    void afterReload();
}