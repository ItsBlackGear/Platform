package com.blackgear.platform.core.helper.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.Attachment;
import com.blackgear.platform.core.helper.AttachmentRegistry;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class AttachmentRegistryImpl extends AttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Platform.MOD_ID);
    private static final Map<ResourceLocation, Supplier<AttachmentType<?>>> ATTACHMENT_SUPPLIERS = new HashMap<>();

    public static <T> void register(Attachment<T> attachment) {
        Supplier<AttachmentType<?>> supplier = ATTACHMENTS.register(
            attachment.valueId().getPath(),
            () -> AttachmentType.builder(attachment.defaultSyncedValue())
                .serialize(attachment.codec())
                .copyOnDeath()
                .build()
        );

        ATTACHMENT_SUPPLIERS.put(attachment.valueId(), supplier);
    }

    private static <T> AttachmentType<T> getAttachmentType(Attachment<T> attachment) {
        var supplier = ATTACHMENT_SUPPLIERS.get(attachment.valueId());
        if (supplier == null) {
            Platform.LOGGER.warn("Attachment not registered: {}", attachment.valueId());
            return null;
        }
        return (AttachmentType<T>) supplier.get();
    }


    public static <T> boolean hasAttachment(LivingEntity entity, Attachment<T> attachment) {
        var type = getAttachmentType(attachment);
        return type != null && entity.hasData(type);
    }

    public static <T> T getAttachmentValue(LivingEntity entity, Attachment<T> attachment) {
        var type = getAttachmentType(attachment);
        if (type == null) {
            Platform.LOGGER.warn("Getting attachment that has not been registered: {}", attachment.valueId());
            return null;
        }
        return entity.getData(type);
    }

    public static <T> void setAttachment(LivingEntity entity, Attachment<T> attachment, T value) {
        var type = getAttachmentType(attachment);
        if (type == null) {
            Platform.LOGGER.warn("Setting attachment that has not been registered: {}", attachment.valueId());
            return;
        }
        entity.setData(type, value);
    }

    public static <T> void removeAttachment(LivingEntity entity, Attachment<T> attachment) {
        var type = getAttachmentType(attachment);
        if (type == null) {
            Platform.LOGGER.warn("Removing attachment that has not been registered: {}", attachment.valueId());
            return;
        }
        entity.removeData(type);
    }

    public static void bootstrap() {
        ATTACHMENTS.register(EventBus.get(EventBus.MOD));
    }
}