package org.wojo.wojosToolbelt.Handlers;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.wojo.wojosToolbelt.Components.QuickAccessItemComponent;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;
import org.wojo.wojosToolbelt.Config.QuickAccessConfig;
import org.wojo.wojosToolbelt.Events.SwapQuickAccessItemEvent;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

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
        ItemStack quickAccessItemStack = hotbar.getInventory().getItemStack(equippedPosition);
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Handler Data: \n - Target Pos: "+targetPosition+"\n - Source Pos: "+sourceInventoryPosition+"\n - Equipped Pos: "+equippedPosition);

        if (equippedPosition == targetPosition) {
            QuickAccessUtils.notificationHelper(store,playerRef, "ERROR", "You are trying to swap something into the position that the Quick-Access item is stored in.\nMove the Quick-Access item or change the target location!");
            return;
        }


        // TODO: TRY new Swap event
        short capacity = QuickAccessUtils.getItemContainerSize(quickAccessItemStack);
        ItemStack targetItem = hotbar.getInventory().getItemStack(targetPosition);

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

        ensuredQuickAccessItem.setItemStackForSlot(sourceInventoryPosition, targetItem);
        hotbar.getInventory().setItemStackForSlot(targetPosition, inventoryItem);
    }
}
