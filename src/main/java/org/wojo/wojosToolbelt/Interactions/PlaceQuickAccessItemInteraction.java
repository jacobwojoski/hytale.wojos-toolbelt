package org.wojo.wojosToolbelt.Interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.BlockRotation;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.PlaceBlockInteraction;
import com.hypixel.hytale.protocol.SimpleInteraction;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import org.joml.Vector3i;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;

// Custom interaction when placing a held quickAccessItem
// - Need to convert ItemStackItemContainer to ItemContainerBlock
public class PlaceQuickAccessItemInteraction extends SimpleInstantInteraction {

    public static final String PLACE_QUICK_ACCESS_ITEM_INTERACTION_ID = "WojoQuickAccessPlaceQuickAccessItemInteraction_ID";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // CODEC: Needed to link interaction with item
    public static final BuilderCodec<PlaceQuickAccessItemInteraction> CODEC = BuilderCodec.builder(
            PlaceQuickAccessItemInteraction.class, PlaceQuickAccessItemInteraction::new, SimpleInstantInteraction.CODEC
    ).build();
    
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        //  1. Get copy of container data
        //  2. Place Block using block place Util
        //      - Note: Block Place util will delete item from player so make a copy of container data first)
        //  3. Update block chunk with container data

        ItemStack quickAccessItem = interactionContext.getHeldItem();
        ItemStack[] quickAccessItemContainer = QuickAccessUtils.getContainerItems(quickAccessItem);

        Ref<EntityStore> entity_ref = interactionContext.getEntity();
        Store<EntityStore> entity_Store = entity_ref.getStore();
        Player player = entity_Store.getComponent(entity_ref, Player.getComponentType());
        ChunkStore chunk_store = world.getChunkStore();
        Store<ChunkStore> chunk_accessor = chunk_store.getStore();
        com.hypixel.hytale.protocol.BlockPosition targetPos = interactionContext.getTargetBlock();
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) entity_Store.getComponent(entity_ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        short activeSlot = hotbar.getActiveSlot();

        if (targetPos == null) {
            return;
        }


        // TODO: Set Block Rotation to face player
        // TODO: set block placement position to be above blockface selected
        // - BlockFace targetedFace = interactionContext.getClientState().blockFace;
        
        World world = player.getWorld();
        world.execute(() -> {
            
            // Place the block
            world.setBlock(targetPos.x, targetPos.y, targetPos.z, quickAccessItem.getBlockKey());

            // Access the item's nested container (the inventory inside the held item)
            //ItemStackItemContainer itemContainer = new ItemStackItemContainer(hotbar, activeSlot);

            // Access the newly placed block's ItemContainerBlock component
            ItemContainerBlock blockContainer = BlockModule.getComponent(
                    ItemContainerBlock.getComponentType(), world, targetPos.x, targetPos.y, targetPos.z
            );

            if (blockContainer != null) {
                SimpleItemContainer blockInv = blockContainer.getItemContainer();

                // Copy slot by slot (respect capacity limits)
                short limit = (short) Math.min(quickAccessItemContainer.length, blockInv.getCapacity());
                for (short i = 0; i < limit; i++) {
                    ItemStack stack = quickAccessItemContainer[i];
                    if (stack != null && !stack.isEmpty()) {
                        blockInv.setItemStackForSlot(i, stack, false);
                    }
                }
            }

            Ref<ChunkStore>  chunkEntityRef = BlockModule.getBlockEntity(world, targetPos.x, targetPos.y, targetPos.z);
            chunk_accessor.replaceComponent(chunkEntityRef, ItemContainerBlock.getComponentType(), blockContainer);

            // TODO: Handle adventure vs creative mode placement or removing of keeping item in hand
            hotbar.getInventory().removeItemStackFromSlot(activeSlot, false);
        });
    }
}
