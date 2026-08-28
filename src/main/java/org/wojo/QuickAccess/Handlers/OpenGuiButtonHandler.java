package org.wojo.QuickAccess.Handlers;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.InventorySetActiveSlotEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.QuickAccess.Components.QuickAccessPlayerComponent;
import org.wojo.QuickAccess.QuickAccessUtils.QuickAccessUtils;

import java.util.function.Consumer;

public class OpenGuiButtonHandler extends EntityEventSystem<EntityStore, InventorySetActiveSlotEvent> {
    public OpenGuiButtonHandler() {
        super(InventorySetActiveSlotEvent.class);
    }

    @Override
    public void handle(int i,
                       @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                       @NonNullDecl Store<EntityStore> store,
                       @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                       @NonNullDecl InventorySetActiveSlotEvent inventorySetActiveSlotEvent) {
        // Validate it's a hotbar swap
        int inventoryId = inventorySetActiveSlotEvent.getInventorySectionId();
        if (inventoryId != InventoryComponent.HOTBAR_SECTION_ID) {
            return;
        }

        // return if swapActive is disabled
        QuickAccessPlayerComponent quickAccessPlayerComponent = archetypeChunk.getComponent(i, QuickAccessPlayerComponent.getComponentType());
        if (quickAccessPlayerComponent.getIsEnabled() == false){
            return;
        }

        // Check if swap is to equipped position
        int pressedHotbarSlot = inventorySetActiveSlotEvent.getNewSlot();
        if (pressedHotbarSlot != quickAccessPlayerComponent.getEquippedPosition()) {
            return;
        }

        // Open Ui
        Player playerComponent = archetypeChunk.getComponent(i, Player.getComponentType());
        QuickAccessUtils.openQuickAccessUI(store, playerComponent.getReference());

        // Revert Hotbar Selection
        InventoryComponent.Hotbar hotbarComponent = (InventoryComponent.Hotbar) archetypeChunk.getComponent(i, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        int originalHotbarSlot = inventorySetActiveSlotEvent.getPreviousSlot();

        hotbarComponent.setActiveSlot(originalHotbarSlot, store,);

        SetActiveSlot setActiveSlotPacket = new SetActiveSlot(
                InventoryComponent.HOTBAR_SECTION_ID,   // -1 indicates the hotbar
                originalHotbarSlot                      // The slot index to select
        );
        PlayerRef playerRef = QuickAccessUtils.getPlayerRef(store, playerComponent.getReference());
        playerRef.getPacketHandler().write(setActiveSlotPacket);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), QuickAccessPlayerComponent.getComponentType());
    }
}