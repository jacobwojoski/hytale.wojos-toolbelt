package org.wojo.wojosToolbelt.Interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;

// Custom interaction when quipping a placed quickAccessItem
// - Need to convert ItemContainerBlock to ItemStackItemContainer
public class EquipQuickAccessItemInteraction extends SimpleInstantInteraction {
    public static final String EQUIP_QUICK_ACCESS_ITEM_INTERACTION_ID = "WojoQuickAccessEquipQuickAccessItemInteraction_ID";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // CODEC: Needed to link interaction with item
    public static final BuilderCodec<EquipQuickAccessItemInteraction> CODEC = BuilderCodec.builder(
            EquipQuickAccessItemInteraction.class, EquipQuickAccessItemInteraction::new, EquipQuickAccessItemInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        // 1. Get data Structures
        // 2. Get Container from placed QuickAccess Block
        // 3. Verify we have space to hold item else, tell user they have no room!
        // 4. Create QuickAccessItem & Fill in with storage data & equip
        // 5. Delete Block In world

        // 1. Get data structs
        Ref<EntityStore> entity_ref = interactionContext.getEntity();
        Store<EntityStore> entity_store = entity_ref.getStore();
        Player player = entity_store.getComponent(entity_ref, Player.getComponentType());
        World world = player.getWorld();
        ChunkStore chunk_store = world.getChunkStore();
        Store<ChunkStore> chunk_accessor = chunk_store.getStore();
        com.hypixel.hytale.protocol.BlockPosition targetPos = interactionContext.getTargetBlock();
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) entity_store.getComponent(entity_ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        short activeSlot = hotbar.getActiveSlot();
        QuickAccessPlayerComponent quickAccessPlayerComponent = entity_store.getComponent(entity_ref, QuickAccessPlayerComponent.getComponentType());

        // 2. Get Container from placed block & Convert to ItemStack[] that it needs
        ItemContainerBlock blockContainer = BlockModule.getComponent(
                ItemContainerBlock.getComponentType(), world, targetPos.x, targetPos.y, targetPos.z
        );
        short capacity = blockContainer.getCapacity();
        ItemContainer container = blockContainer.getItemContainer();
        ItemStack[] items = new ItemStack[capacity];
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Block container storage - "+String.valueOf(capacity));
        for (short i = 0; i < capacity; i++) {
            ItemStack item = container.getItemStack(i);
            if (item != null){
                WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Saving item from block container - "+item.getItemId());
                items[i] = item;
            }
        }

        // 3. Verify player can hold the container
        int qaEquippedPositiion = quickAccessPlayerComponent.getEquippedPosition();
        ItemStack equippedPositiion = hotbar.getInventory().getItemStack((short)qaEquippedPositiion);
        if (equippedPositiion != null) {
            QuickAccessUtils.notificationHelper(entity_store, entity_ref, "EQUIP FAILED", "Something is already equipped in hotbar location");
            return;
        }

        // 4. Create QuickAccessItem
        // TODO: Get it somehow
        ItemStack quickAccessItem = new ItemStack("Quick_Access_Item_Unrestricted_Debug");
        // TODO: See why settings items isnt working
        quickAccessItem.withMetadata(ItemStackItemContainer.ITEMS_CODEC,items);
        hotbar.getInventory().setItemStackForSlot((short)qaEquippedPositiion, quickAccessItem);
        //ItemStackItemContainer.writeToItemStack(hotbar.getInventory(),(short)qaEquippedPositiion,quickAccessItem,items);

        // 5. Delete Block in world
        world.execute(() -> {
            String blockTypeKey = BlockType.EMPTY_KEY;
            world.setBlock(targetPos.x, targetPos.y, targetPos.z, blockTypeKey);
        });
    }
}
