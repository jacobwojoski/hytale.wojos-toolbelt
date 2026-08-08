package org.wojo.wojosToolbelt.Systems;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.wojosToolbelt.Components.QuickAccessContainerFilterComponent;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;

import javax.annotation.Nonnull;

public class QuickAccessContainerFilterInitSystem extends RefSystem<ChunkStore> {

    @Override
    public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason,
                              @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {

        ItemContainerBlock rack = (ItemContainerBlock)
                commandBuffer.getComponent(ref, ItemContainerBlock.getComponentType());

        // TODO: Update filter from config based on item type
        QuickAccessContainerFilterComponent filterConfig =
                (QuickAccessContainerFilterComponent)
                commandBuffer.getComponent(ref, QuickAccessContainerFilterComponent.getComponentType());

        if (rack == null || filterConfig == null) return;

        // Resolve tag name strings -> int indexes, once, here
        IntSet allowedTagIndexes = new IntOpenHashSet();
        for (String tagName : filterConfig.getAllowedTags()) {
            allowedTagIndexes.add(AssetRegistry.getOrCreateTagIndex(tagName));
        }

        SlotFilter tagFilter = (actionType, container, slot, itemStack) -> {
            if (actionType != FilterActionType.ADD) return true;
            if (itemStack == null) return true;
            Item item = itemStack.getItem();
            if (item == null) return true;

            for (int tagIndex : allowedTagIndexes) {
                if (item.getData().getExpandedTagIndexes().contains(tagIndex)) return true;
            }
            return false;
        };

        short capacity = rack.getItemContainer().getCapacity();
        for (short slot = 0; slot < capacity; slot++) {
            rack.getItemContainer().setSlotFilter(FilterActionType.ADD, slot, tagFilter);
        }
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

    }

    @Override
    public Query getQuery() {
        return Query.and(ItemContainerBlock.getComponentType(), QuickAccessContainerFilterComponent.getComponentType());
    }
}
