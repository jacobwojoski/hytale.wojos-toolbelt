package org.wojo.wojosToolbelt.Components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class QuickAccessContainerFilterComponent implements Component<ChunkStore> {

    public static final BuilderCodec<QuickAccessContainerFilterComponent> CODEC =
            BuilderCodec.builder(QuickAccessContainerFilterComponent.class, QuickAccessContainerFilterComponent::new)
                    .append(new KeyedCodec<String[]>("AllowedTags",
                                    new ArrayCodec(Codec.STRING, (size) -> new String[size])),
                            (cfg, value, info) -> cfg.allowedTags = value,
                            (cfg, info) -> cfg.allowedTags)
                    .add()
                    .build();

    private String[] allowedTags = new String[]{"Tool"};

    public String[] getAllowedTags() { return allowedTags; }

    @Override
    public Component<ChunkStore> clone() {
        QuickAccessContainerFilterComponent copy = new QuickAccessContainerFilterComponent();
        copy.allowedTags = this.allowedTags;
        return copy;
    }

    // ================ Component Type info ==================
    public static final String QUICK_ACCESS_CONTAINER_FILTER_COMPONENT_ID = "WojosQuickAccess_ContainerFilter_Component_ID";
    private static ComponentType<ChunkStore, QuickAccessContainerFilterComponent> _quick_access_container_filter_component_type;
    public static ComponentType<ChunkStore, QuickAccessContainerFilterComponent> getComponentType(){
        return _quick_access_container_filter_component_type;
    }
    public static void setComponentType(ComponentType<ChunkStore, QuickAccessContainerFilterComponent> type){
        QuickAccessContainerFilterComponent._quick_access_container_filter_component_type = type;
    }
}
