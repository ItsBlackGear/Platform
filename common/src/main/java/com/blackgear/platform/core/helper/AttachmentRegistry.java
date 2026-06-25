package com.blackgear.platform.core.helper;

import com.blackgear.platform.common.data.Attachment;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.LivingEntity;

public class AttachmentRegistry {
    @ExpectPlatform
    public static <T> void register(Attachment<T> attachment) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> boolean hasAttachment(LivingEntity entity, Attachment<T> attachment) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> T getAttachmentValue(LivingEntity entity, Attachment<T> attachment) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> void setAttachment(LivingEntity entity, Attachment<T> attachment, T value) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> void removeAttachment(LivingEntity entity, Attachment<T> attachment) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void bootstrap() {
        throw new AssertionError();
    }
}