package org.wojo.wojosToolbelt.Interactions;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemStackContainerConfig;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.wojosToolbelt.Components.QuickAccessPlayerComponent;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessLoggingUtils;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

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
        // 2. Verify we have space to hold item else, tell user they have no room!
        // 3. Get Container from placed QuickAccess Block
        // 4. Create QuickAccessItem & Fill in with storage data 
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

        // 2. Verify player can hold the container
        int qaEquippedPositiion = quickAccessPlayerComponent.getEquippedPosition();
        ItemStack equippedPositiion = hotbar.getInventory().getItemStack((short)qaEquippedPositiion);
        if (equippedPositiion != null) {
            QuickAccessUtils.notificationHelper(entity_store, entity_ref, "EQUIP FAILED", "Something is already equipped in hotbar location");
            return;
        }
        
        // 3. Get Container from placed block
        ItemContainerBlock blockContainer = BlockModule.getComponent(
                ItemContainerBlock.getComponentType(), world, targetPos.x, targetPos.y, targetPos.z
        );
        short block_capacity = blockContainer.getCapacity();
        ItemContainer container = blockContainer.getItemContainer();
        ItemStack[] items = new ItemStack[block_capacity];
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Block container storage - "+String.valueOf(block_capacity));

        // TODO: Get the item from the block
        // 4. Create QuickAccess Item that we will give to player
        BlockType blockType = BlockModule.getComponent(
                BlockType.getComponentType(), world, targetPos.x, targetPos.y, targetPos.z
        );
        String itemId = blockType.getItem().getId();
        ItemStack quickAccessItem = new ItemStack(itemId, 1);
        hotbar.getInventory().setItemStackForSlot((short)qaEquippedPositiion, quickAccessItem);

        short capacity = QuickAccessConfig.getContainerSize(quickAccessItem.getItemId());
        ItemStackItemContainer unconfiguredQuickAccessItem =
                ItemStackItemContainer.ensureContainer(
                        hotbar.getInventory(),          // parentContainer
                        (short) qaEquippedPositiion,    // slot containing the backpack
                        capacity                        // capacity
                );

        // Move Items from block to ItemStackItemContainer
        if (capacity != blockContainer.getItemContainer().getCapacity()){
            WojosQuickAccessPlugin.LOGGER.atSevere().log("[ERROR] WQA::EquipQuickAccessItemInteraction::firstRun - Block capacity, vs item capacity is different");
        }
        short limit = (short) Math.min(capacity, blockContainer.getItemContainer().getCapacity());
        for (short i=0; i<limit; i++) {
            ItemStack item = blockContainer.getItemContainer().getItemStack(i);
            unconfiguredQuickAccessItem.setItemStackForSlot(i, item);
        }

        // 5. Delete Block in world
        // Clear blocks inventory so items in container are gone
        Ref<ChunkStore> block_ref = BlockModule.getBlockEntity(world,targetPos.x, targetPos.y, targetPos.z);
        chunk_accessor.removeComponent(block_ref,ItemContainerBlock.getComponentType());

        // Delete block in world
        world.execute(() -> {
            String blockTypeKey = BlockType.EMPTY_KEY;
            world.setBlock(targetPos.x, targetPos.y, targetPos.z, blockTypeKey);
        });
    }
}
