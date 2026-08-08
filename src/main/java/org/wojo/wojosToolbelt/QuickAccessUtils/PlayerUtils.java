package org.wojo.wojosToolbelt.QuickAccessUtils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

import java.util.UUID;

import static org.wojo.wojosToolbelt.QuickAccessUtils.GuiUtils.notificationHelper;

public class PlayerUtils {
    public static PlayerRef getPlayerRef(Store<EntityStore>store, Ref<EntityStore> ref) {
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        UUID uuid = uuidComponent.getUuid();
        return Universe.get().getPlayer(uuid);
    }

    public static short getActiveHotbarPosition(Store<EntityStore> store, Ref<EntityStore> ref) {
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        ItemStack heldItem = hotbar.getActiveItem();
        Boolean isActiveItemTheQuickAccessItem = QuickAccessUtils.isQuickAccessItem(heldItem);
    
        if (isActiveItemTheQuickAccessItem){
            notificationHelper(store, ref, "WARNING", "Active slot is a Quick-Access Item. Item swap Canceled");
            return -1;
        }
        return hotbar.getActiveSlot();
    }

    public static ItemStack getItemAtHotbarPosition(Store<EntityStore>entity_store, Ref<EntityStore> entity_ref, short hotbar_position) {
        QuickAccessPlayerComponent playerComponent = entity_store.getComponent(entity_ref, QuickAccessPlayerComponent.getComponentType());
        Player player = entity_store.getComponent(entity_ref, Player.getComponentType());
        if (playerComponent == null || player == null) {
          return null;
        }


        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) entity_store.getComponent(entity_ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (hotbar != null) {
            return hotbar.getInventory().getItemStack((short) hotbar_position);
        } 
        return null;
    }

    public static ItemStack geActiveItem(Store<EntityStore> entity_store, Ref<EntityStore> entity_ref) {
        QuickAccessPlayerComponent playerComponent = entity_store.getComponent(entity_ref, QuickAccessPlayerComponent.getComponentType());
        Player player = entity_store.getComponent(entity_ref, Player.getComponentType());
        if (playerComponent == null || player == null) {
          return null;
        }
    
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) entity_store.getComponent(entity_ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (hotbar != null) {
            return hotbar.getActiveItem();
        } 
        return null;
    }
    
    // TODO: should prob remove this util fn entirely 
    public static short getQuickAccessItemEquippedLocationOrDefault(Ref<EntityStore> playerRef, Store<EntityStore> store) {
        QuickAccessPlayerComponent quickAccessPlayerComponent = store.getComponent(playerRef, QuickAccessPlayerComponent.getComponentType());
        if (quickAccessPlayerComponent != null){
            int intPos = quickAccessPlayerComponent.getEquippedPosition();
            return (short) intPos;
        }
        return 8; // Return default of 8
    }

    
    public static ItemStack getEquippedQaItemOrNull(PlayerRef player_ref, Store<EntityStore> store) {
        WojosQuickAccessPlugin.LOGGER.atInfo().log("QuickAccessUtils.getEquippedQaItemOrNull");
        if (player_ref == null || player_ref.getReference() == null || !player_ref.isValid()) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("WARN: Player is NULL");
            return null;
        }
        
        QuickAccessPlayerComponent quickAccessPlayerComp = store.getComponent(player_ref.getReference(), QuickAccessPlayerComponent.getComponentType());
        Player player = store.getComponent(player_ref.getReference(), Player.getComponentType());
        if (player == null) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("WARN: Player Comp is NULL");
            return null;
        } else if (quickAccessPlayerComp == null) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("WARN: QaPlayerComp is NULL, Adding one to player.");
            quickAccessPlayerComp = new QuickAccessPlayerComponent();
            store.addComponent(player_ref.getReference(), QuickAccessPlayerComponent.getComponentType(), quickAccessPlayerComp);
        }
        
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(player_ref.getReference(), InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        int equippedPosition = quickAccessPlayerComp.getEquippedPosition();
        ItemStack quickAccessItem = hotbar.getInventory().getItemStack((short) equippedPosition);
        
        if (!QuickAccessUtils.isQuickAccessItem(quickAccessItem)) {
            return null;
        }
        return quickAccessItem;
    }

    public static ItemStack getHeldQaItemOrNull(PlayerRef player_ref, Store<EntityStore> store) {
        WojosQuickAccessPlugin.LOGGER.atInfo().log("QuickAccessUtils.getHeldQaItemOrNull");
        if (player_ref == null || player_ref.getReference() == null || !player_ref.isValid()) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("WARN: Player is NULL");
            return null;
        } else if (store.getComponent(player_ref.getReference(), QuickAccessPlayerComponent.getComponentType()) == null) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("WARN: QaPlayerComp is NULL, Adding one to player.");
            QuickAccessPlayerComponent quickAccessPlayerComponent = new QuickAccessPlayerComponent();
            store.addComponent(player_ref.getReference(), QuickAccessPlayerComponent.getComponentType(), quickAccessPlayerComponent);
        }
        
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(player_ref.getReference(), InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        ItemStack quickAccessItem = hotbar.getActiveItem();
        
        if (!QuickAccessUtils.isQuickAccessItem(quickAccessItem)) {
            return null;
        }
        return quickAccessItem;
    }

    public static ItemStack getEquippedTargetItemOrNull(PlayerRef player_ref, Store<EntityStore> store) {
        if (player_ref == null || player_ref.getReference() == null || !player_ref.isValid()) {
            return null;
        }
        
        QuickAccessPlayerComponent playerComponent = store.getComponent(player_ref.getReference(), QuickAccessPlayerComponent.getComponentType());
        Player player = store.getComponent(player_ref.getReference(), Player.getComponentType());
        if (playerComponent == null || player == null) {
            return null;
        }
        
        int targetPosition = playerComponent.getTargetPosition();
        ItemStack targetItem;
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(player_ref.getReference(), InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (targetPosition == -1) {
            targetItem = hotbar.getActiveItem();
        } else {
            targetItem = hotbar.getInventory().getItemStack((short) targetPosition);
        }
        
        return targetItem;
    }

    public static ItemStack getHeldQaItemOrEquippedQaItemOrNull(PlayerRef player_ref, Store<EntityStore> store) {
        ItemStack quickAccessItem = null;

        // Check Held item
        quickAccessItem = getHeldQaItemOrNull(player_ref, store);
        if (quickAccessItem != null) {
            return quickAccessItem;
        }

        // Check Equipped item is held is not a QA Item
        return getEquippedQaItemOrNull(player_ref, store);
    }
}
