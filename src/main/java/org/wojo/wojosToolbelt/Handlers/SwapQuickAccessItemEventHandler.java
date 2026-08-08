package org.wojo.wojosToolbelt.Handlers;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.wojo.wojosToolbelt.Config.QuickAccessConfig;
import org.wojo.wojosToolbelt.Events.SwapQuickAccessItemEvent;
import org.wojo.wojosToolbelt.QuickAccessUtils.LoggingUtils;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class SwapQuickAccessItemEventHandler implements Consumer<SwapQuickAccessItemEvent> {
    @Override
    public void accept(SwapQuickAccessItemEvent swapQuickAccessItemEvent) {
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Running Swap event handler");
        Ref<EntityStore> playerRef = swapQuickAccessItemEvent.playerRef();
        Store<EntityStore> store = swapQuickAccessItemEvent.store();
        short sourceInventoryPosition = swapQuickAccessItemEvent.sourceInventoryPosition();
        short equippedPosition = swapQuickAccessItemEvent.equippedPosition();
        short targetPosition = swapQuickAccessItemEvent.targetPosition();
        
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(playerRef, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        if (hotbar==null){
            WojosQuickAccessPlugin.LOGGER.atSevere().log("[ERROR] WQA::SwapQuickAccessItemEventHandler::accept - No hotbar found on entity.");
            return;
        }
        ItemStack quickAccessItemStack = hotbar.getInventory().getItemStack(equippedPosition);
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Handler Data: \n - Target Pos: "+targetPosition+"\n - Source Pos: "+sourceInventoryPosition+"\n - Equipped Pos: "+equippedPosition);

        if (equippedPosition == targetPosition) {
            QuickAccessUtils.notificationHelper(store,playerRef, "ERROR", "You are trying to swap something into the position that the Quick-Access item is stored in.\nMove the Quick-Access item or change the target location!");
            return;
        }

        // Ensure Item has an inventory if it doesn't
        //  (This is needed if player has not added an item to the container yet)
        short capacity = quickAccessItemStack.getItem().getItemStackContainerConfig().getCapacity();
        if (capacity == 0) {
            WojosQuickAccessPlugin.LOGGER.atSevere().log("[ERROR] WQA::SwapQuickAccessItemEventHandler::accept - ItemStackItemContainer capacity is 0");
            return;
        }

        ItemStackItemContainer ensuredQuickAccessItem =
                ItemStackItemContainer.ensureContainer(
                        hotbar.getInventory(),      // parentContainer
                        equippedPosition,           // slot containing the backpack
                        capacity                    // capacity
                );
        if (ensuredQuickAccessItem == null) {
            WojosQuickAccessPlugin.LOGGER.atWarning().log("[WARN] WQA::SwapQuickAccessItemEvent::accept - Swap failed,  QA-Item is null");
            return;
        }

        ItemStack inventoryItem =
                ItemStackItemContainer
                        .getContainer(hotbar.getInventory(),equippedPosition)
                        .getItemStack(sourceInventoryPosition);

        ItemStack currentEquippedItem = hotbar.getInventory().getItemStack(targetPosition);

        if (targetPosition > capacity) {
            WojosQuickAccessPlugin.LOGGER.atWarning().log("[WARN] WQA::SwapQuickAccessItemEvent::accept - Trying to move an item to a position greater than container size");
            return;
        }
        
        // ------ Validate QuickAccess-Item Container can hold item type ------


        // -- Compare hotbar item to filters & cancel swap/throw notification if swap is invalid
        // TODO: handle canAddItemStackToSlot() when its a non empty slot
        ItemStack internalQaItemClone = null;
        // Set container item to null so canAddItemStackToSlot doesnt fail from item already existing
        if ( inventoryItem != null ){
            internalQaItemClone = inventoryItem.cleanCopy();
            ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, null);
        }

        boolean canSwap = true;
        if ( currentEquippedItem != null ) {
            boolean canSwap1 = ensuredQuickAccessItem.canAddItemStack(currentEquippedItem,true,true);
            boolean canSwap2 = ensuredQuickAccessItem.canAddItemStackToSlot(sourceInventoryPosition, currentEquippedItem,true,true);
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[INFO] WQA::SwapQuickAccessItemEvent::accept - CanSwap1&2: ["+canSwap1+","+canSwap2+"]");
            canSwap = canSwap1 && canSwap2;
            canSwap = canSwap && validateCanStoreItem(currentEquippedItem, quickAccessItemStack);
        }

        if (!canSwap) {
            String notification =
                    "Quick Access Item can not hold items of type: Not Tools"
                    ;

            QuickAccessUtils.notificationHelper(
                    store,
                    playerRef,
                    "<b style='color:red'>ERROR</b>",
                    notification
            );
            ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, internalQaItemClone);
            return;
        } else {
            // Add item back for swap command
            ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, internalQaItemClone);
        }

        //ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, currentEquippedItem, true);
        //hotbar.getInventory().setItemStackForSlot(targetPosition, internalQaItemClone);
        ensuredQuickAccessItem.swapItems(sourceInventoryPosition,hotbar.getInventory(),targetPosition,(short)1);
    }

    private boolean validateCanStoreItem(ItemStack item_stack_to_store, ItemStack quick_access_item_stack) {
        // TODO: Find where these enums come from
        // TOOL_TAG = 364;
        // WEAPON_TAG = 343;
        // BLOCK_TAG = 1;

        // No item_stack_to_store, just pulling from container so event is always valid
        if (item_stack_to_store == null){
            return true;
        }

        // -- Get container filters
        String[] filters = QuickAccessConfig.getQuickAccessItemWhitelistTags(quick_access_item_stack);
        if (filters == null || filters.length == 0) {
            // No filter found, so item storage is always valid.
            return true;
        }


        // Filters are stored on the item slots so don't think its possible to get them through the item data
//        int containerFilterTag = quick_access_item_stack.getItem().getItemStackContainerConfig().getTagIndex();
//        quick_access_item_stack.getItem().getItemStackContainerConfig().getGlobalFilter().toString();
//        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: ContainerFilterTag" + containerFilterTag);
//        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: ContainerFilterTag" + containerFilterTag);


        // -- Get hotbar Item's tags
        AssetExtraInfo.Data extraInfo = item_stack_to_store.getItem().getData();
        Map<String,String[]> item_tags = extraInfo.getRawTags();
        LoggingUtils.printTagMap(item_tags);
        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: tags_ids - "+extraInfo.getTags());

        for (int i=0; i< filters.length; i++) {
            String whitelistedTag = filters[i];
            if (!whitelistedTag.isEmpty() && !whitelistedTag.isBlank() && item_tags.containsKey(whitelistedTag) ) {
                return true;
            }
        }

        return false;
    }
}
