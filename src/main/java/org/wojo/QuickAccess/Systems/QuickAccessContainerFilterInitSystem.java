package org.wojo.QuickAccess.Systems;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.QuickAccess.Components.QuickAccessContainerFilterComponent;

import javax.annotation.Nonnull;

public class QuickAccessContainerFilterInitSystem extends RefSystem<ChunkStore> {

    @Override
    public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason,
                              @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {

        setFilterOnContainer(ref, commandBuffer);

        copyItemsToContainer(ref, reason, store, commandBuffer);
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

    }

    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(ItemContainerBlock.getComponentType(), QuickAccessContainerFilterComponent.getComponentType());
    }

    private void setFilterOnContainer(@Nonnull Ref<ChunkStore> ref, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        ItemContainerBlock quickAccessBlock = (ItemContainerBlock)
                commandBuffer.getComponent(ref, ItemContainerBlock.getComponentType());

        // TODO: Update filter from config based on item type
        QuickAccessContainerFilterComponent filterConfig =
                (QuickAccessContainerFilterComponent)
                        commandBuffer.getComponent(ref, QuickAccessContainerFilterComponent.getComponentType());

        if (quickAccessBlock == null || filterConfig == null) return;

        // Resolve tag name strings -> int indexes, once, here
        IntSet allowedTagIndexes = new IntOpenHashSet();
        for (String tagName : filterConfig.getAllowedTags()) {
            allowedTagIndexes.add(AssetRegistry.getOrCreateTagIndex(tagName));
        }

        SlotFilter tagFilter = (actionType, container, slot, itemStack) -> {
            if (actionType != FilterActionType.ADD) return true;
            if (itemStack == null) return true;
            Item item = itemStack.getItem();

            for (int tagIndex : allowedTagIndexes) {
                if (item.getData().getExpandedTagIndexes().contains(tagIndex)) return true;
            }
            return false;
        };

        short capacity = quickAccessBlock.getItemContainer().getCapacity();
        for (short slot = 0; slot < capacity; slot++) {
            quickAccessBlock.getItemContainer().setSlotFilter(FilterActionType.ADD, slot, tagFilter);
        }
    }

    // TODO: see if we can get the player info of who picked up the item
    private void copyItemsToContainer(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason,
                                      @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {


    }
}
