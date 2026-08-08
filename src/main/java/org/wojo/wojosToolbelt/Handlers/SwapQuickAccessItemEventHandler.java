package org.wojo.wojosToolbelt.Handlers;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.protocol.TagPattern;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemStackContainerConfig;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.TagFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.wojo.wojosToolbelt.Components.QuickAccessItemComponent;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;
import org.wojo.wojosToolbelt.Config.QuickAccessConfig;
import org.wojo.wojosToolbelt.Events.SwapQuickAccessItemEvent;
import org.wojo.wojosToolbelt.QuickAccessUtils.LoggingUtils;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
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

        ItemStack inventoryItem =
                ItemStackItemContainer
                        .getContainer(hotbar.getInventory(),equippedPosition)
                        .getItemStack(sourceInventoryPosition);

        if (targetPosition > capacity) {
            WojosQuickAccessPlugin.LOGGER.atWarning().log("[WARN] Tring to move an item to a positiion greater than container size");
            return;
        }
        
        // ------ Validate QuickAccess-Item Container can hold item type ------
        ItemStack targetItem = hotbar.getInventory().getItemStack(targetPosition);
        boolean canStoreItem = validateCanStoreItem(targetItem, quickAccessItemStack);


        // -- Compare hotbar item to filters & cancel swap/throw notification if swap is invalid
        if ( targetItem !=null && ensuredQuickAccessItem != null && !ensuredQuickAccessItem.canAddItemStackToSlot(sourceInventoryPosition, targetItem,false,true) ) {
            String notification = 
                "Quick Access Item can not hold items of type: Not Tools"
                ;
            
             QuickAccessUtils.notificationHelper(
                 store, 
                 playerRef, 
                 "ERROR", 
                 notification
             );
            return;
        }


        ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, targetItem);
        hotbar.getInventory().setItemStackForSlot(targetPosition, inventoryItem);
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
        //TagFilter quickAccessTagFilter = new TagFilter();
        int containerFilterTag = quick_access_item_stack.getItem().getItemStackContainerConfig().getTagIndex();
        quick_access_item_stack.getItem().getItemStackContainerConfig().getGlobalFilter().toString();

        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: ContainerFilterTag" + containerFilterTag);
        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: ContainerFilterTag" + containerFilterTag);


        // -- Get hotbar Item's tags
        AssetExtraInfo.Data extraInfo = item_stack_to_store.getItem().getData();
        Map<String,String[]> tags = extraInfo.getRawTags();
        LoggingUtils.printTagMap(tags);
        WojosQuickAccessPlugin.LOGGER.atInfo().log("[DEBUG]: tags_ids - "+extraInfo.getTags());

        for (Map.Entry<String, String[]> entry : tags.entrySet()) {
            String tag_key = entry.getKey();
            // if tag_key == Tool
            // or tag_key == Soil
        }
        return true;
    }
}
