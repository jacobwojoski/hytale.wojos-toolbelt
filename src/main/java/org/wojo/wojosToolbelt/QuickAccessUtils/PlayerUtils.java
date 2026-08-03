package org.wojo.wojosToolbelt.QuickAccessUtils;

public class PlayerUtils {
    public static PlayerRef getPlayerRef(Store<EntityStore>store, Ref<EntityStore> ref) {
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        UUID uuid = uuidComponent.getUuid();
        return Universe.get().getPlayer(uuid);
    }

    public static short getActiveHotbarPosition(Store<EntityStore>store, Ref<EntityStore> ref) {
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        ItemStack heldItem = hotbar.getActiveItem();
        Boolean isActiveItemTheQuickAccessItem = QuickAccessUtils.isQuickAccessItem(heldItem);
    
        if (isActiveItemTheQuickAccessItem){
            notificationHelper(store, ref, "WARNING", "Active slot is a Quick-Access Item. Item swap Cancled");
            return -1;
        }
        return hotbar.getActiveSlot();
    }

    public staic ItemStack getItemAtHotbarPosition(Store<EntityStore>entity_store, Ref<EntityStore> entity_ref, short hotbar_position) {
        QuickAccessPlayerComponent playerComponent = entity_store.getComponent(entity_ref, QuickAccessPlayerComponent.getComponentType());
        Player player = entity_store.getComponent(entity_ref, Player.getComponentType());
        if (playerComponent == null || player == null) {
          return null;
        }
    
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(player_ref.getReference(), InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (hotbar != null) {
            return hotbar.getInventory().getItemStack((short) hotbar_position);
        } 
        return null;
    }

    public static ItemStack geActiveItem(){
        QuickAccessPlayerComponent playerComponent = entity_store.getComponent(entity_ref, QuickAccessPlayerComponent.getComponentType());
        Player player = entity_store.getComponent(entity_ref, Player.getComponentType());
        if (playerComponent == null || player == null) {
          return null;
        }
    
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(player_ref.getReference(), InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (hotbar != null) {
            return hotbar.getActiveItem();
        } 
        return null;
    }
}
