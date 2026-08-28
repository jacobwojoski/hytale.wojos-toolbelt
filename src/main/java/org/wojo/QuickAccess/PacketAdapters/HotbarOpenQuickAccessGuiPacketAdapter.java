package org.wojo.QuickAccess.PacketAdapters;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

// Send Packet to player to tell them they are actually holding the original slected item not hotbar 9
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import org.wojo.QuickAccess.QuickAccessUtils.QuickAccessUtils;
import org.wojo.QuickAccess.WojosQuickAccessPlugin;

import java.util.UUID;

import static org.wojo.QuickAccess.Components.QuickAccessPlayerComponent.*;

// Update the hotbar interaction to check if the player it swaping to the same item tey have equipped.
// If they are then open UI.
// NOTE: Use Packet watcher vs Filter to not block the player from swapping weapons.
//      We're adding features to swapping tools
public class HotbarOpenQuickAccessGuiPacketAdapter implements PlayerPacketFilter {

    // Returns boolean - "blockPacket"
    //    - True: Block Packet
    //    - False: Let Packet Through
    @Override
    public boolean test(PlayerRef playerRef, Packet packet) {
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Handling Packet");
        // 290 == SyncInteractionCain packet ID
        if (packet.getId() != SyncInteractionChains.PACKET_ID ){
            WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Bad packet id");
            return false;
        }
        
        // Recast packet as type we care about.
        SyncInteractionChains syncPacket = (SyncInteractionChains) packet;
        
        Ref<EntityStore> entityRef = playerRef.getReference();
        if (entityRef != null && entityRef.isValid()) {

            UUID playersUUID = playerRef.getUuid();

            // If player doesn't exist in hash map, add them then don't block packet
            if (!quickAccessBtnEnabledMap.containsKey(playersUUID) || !quickAccessHotbarLocationEquipMap.containsKey(playersUUID)){
                quickAccessBtnEnabledMap.put(playersUUID, false);
                quickAccessHotbarLocationEquipMap.put(playersUUID, 8);

                WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Adding player to hashmap");
                return false;

                // Player exists but button disabled so don't block packed
            } else if (!quickAccessBtnEnabledMap.get(playersUUID)) {
                WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Player Exists but disabled");
                return false;
            }



            // Check were in a swap item Interaction!
            Store<EntityStore> store = entityRef.getStore();
            World world = store.getExternalData().getWorld();
            Integer equippedHotbarPos = quickAccessHotbarLocationEquipMap.get(playersUUID);
            // Check is user is trying to swap to quick access equipped position
            for (SyncInteractionChain chain : syncPacket.updates) {
               if ( (chain.interactionType == InteractionType.SwapFrom || chain.interactionType == InteractionType.SwapTo)
                       && chain.data != null // data exists
                       && chain.data.targetSlot == equippedHotbarPos // slot to switch too
                       && chain.initial // start of new chain
                   ){


                    WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Trying to open Gui");
                    // Interacting with following comps required us to be threadsafe so update on world thread not network thread.
                    world.execute(() -> {
                        // Revert Selected Hotbar Item
                        revertSelectedHotbarItem(chain.activeHotbarSlot, playerRef);

                        // Open UI
                        //openQuickAccessUI(playerRef, equippedHotbarPos.shortValue());
                        QuickAccessUtils.openQuickAccessUI(world.getEntityStore().getStore(), playerRef.getReference());
                    });

                   WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Blocking Packet");
                    // Block Packet as we don't want player to actually change to equipped hotbar slot
                   return true;
               } // End chain type
            } // End chain packet for loop
        }// Bad entity Ref

        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: WQA::HotbarOpenQuickAccessGuiPacketAdapter::test - Something went wrong, Don't block anything");
        // Something went wrong or user wasn't pressing key 9, so don't block sync packet.
        return false;
    }

    // Don't let player select hotbar 9 as its to be used to open the gui
    // Params:
    // - originalHotbarSlot: Hotbar slot the player was swapping from when pressing (open Gui button)
    // - playerRef: Reference to the player entity that hit the open GUI button.
    private void revertSelectedHotbarItem(Integer originalHotbarSlot, PlayerRef playerRef) {
        Ref<EntityStore> entityRef = playerRef.getReference();
        if (entityRef == null || !entityRef.isValid()){
            WojosQuickAccessPlugin.LOGGER.atWarning().log("[WARN] Bad entity ref when reverting selected hotbar item.");
            return;
        }
        Store<EntityStore> store = entityRef.getStore();
        // Update server-side state
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null || player.getInventory() == null){
            WojosQuickAccessPlugin.LOGGER.atWarning().log("[Warn] Bad player when getting component");
            return;
        }

        //Inventory playerInventory = store.getComponent(entityRef, Inventory.getComponentType());
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(entityRef, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        byte hotbarPos = originalHotbarSlot.byteValue();
        
        // Send packet to force client to the correct slot
        SetActiveSlot setActiveSlotPacket = new SetActiveSlot(
            InventoryComponent.HOTBAR_SECTION_ID,   // -1 indicates the hotbar
            originalHotbarSlot                      // The slot index to select
        );
        playerRef.getPacketHandler().write(setActiveSlotPacket);
    }
}
