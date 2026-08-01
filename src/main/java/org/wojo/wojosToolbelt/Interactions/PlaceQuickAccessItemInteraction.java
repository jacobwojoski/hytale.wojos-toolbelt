package org.wojo.wojosToolbelt.Interactions;

import com.hypixel.hytale.builtin.hytalegenerator.props.ManualProp;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.BlockInteractionUtils;
import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.SetBlockSettings;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3i;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.wojo.wojosToolbelt.QuickAccessUtils.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Custom interaction when placing a held quickAccessItem
// - Need to convert ItemStackItemContainer to ItemContainerBlock
// - Need SimpleBlockInteraction to get selected block face direction, Use block face to get XYZ offset instead of replacing block at position
public class PlaceQuickAccessItemInteraction extends SimpleBlockInteraction {

    public static final String PLACE_QUICK_ACCESS_ITEM_INTERACTION_ID = "WojoQuickAccessPlaceQuickAccessItemInteraction_ID";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // CODEC: Needed to link interaction with item
    public static final BuilderCodec<PlaceQuickAccessItemInteraction> CODEC = BuilderCodec.builder(
            PlaceQuickAccessItemInteraction.class, PlaceQuickAccessItemInteraction::new, SimpleBlockInteraction.CODEC
    ).build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> cmdBuffer,
                                     @Nonnull InteractionType intType, @Nonnull InteractionContext interactionContext, @Nullable ItemStack itmStack,
                                     @Nonnull Vector3i blockPos, @Nonnull CooldownHandler cooldownHndlr) {

        ItemStack quickAccessItem = interactionContext.getHeldItem();
        ItemStack[] quickAccessItemContainer = QuickAccessUtils.getContainerItems(quickAccessItem);

        Ref<EntityStore> entity_ref = interactionContext.getEntity();
        Store<EntityStore> entity_Store = entity_ref.getStore();
        Player player = entity_Store.getComponent(entity_ref, Player.getComponentType());
        ChunkStore chunk_store = player.getWorld().getChunkStore();

        Store<ChunkStore> chunk_accessor = chunk_store.getStore();
        BlockPosition targetPos = interactionContext.getTargetBlock();
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) entity_Store.getComponent(entity_ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        short activeSlot = hotbar.getActiveSlot();

        if (targetPos == null) {
            return;
        }


        BlockFace targetedFace = null;
        if (interactionContext.getClientState() != null){
            targetedFace = interactionContext.getClientState().blockFace;
        }else{
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[Debug] Bad Client: "+targetedFace.getValue());
        }

        int x = targetPos.x;
        int y = targetPos.y;
        int z = targetPos.z;

        switch (targetedFace) {
            case BlockFace.Up:     y += 1; break; // The face on top of the block
            case BlockFace.Down:   y -= 1; break; // The bottom face
            case BlockFace.North:  z -= 1; break;
            case BlockFace.South:  z += 1; break;
            case BlockFace.East:   x += 1; break;
            case BlockFace.West:   x -= 1; break;
            default:
                WojosQuickAccessPlugin.LOGGER.atWarning().log("[WARN] WQA::PlaceQuickAccessItemInteraction::firstRun - No Block Face Found");
        }

// (x, y, z) is now the position of the empty block space above the clicked face

        final int fx = x;
        final int fy = y;
        final int fz = z;

        TransformComponent transformComponent = entity_Store.getComponent(entity_ref, TransformComponent.getComponentType());

        // Get BlockPlaceUtilData
        //World world = player.getWorld();
        Ref<ChunkStore> chunkRef = chunk_store.getChunkSectionReferenceAtBlock(fx, fy, fz);
        org.joml.Vector3d playerPos = transformComponent.getPosition();
        org.joml.Vector3d placePos = new org.joml.Vector3d(fx + 0.5, fy + 0.5, fz + 0.5);
        org.joml.Vector3d toPlayer = playerPos.sub(placePos).normalize();
        Vector3i toPlayerNorm = new Vector3i((int)Math.floor(toPlayer.x+0.5), (int)Math.floor(toPlayer.y+0.5), (int)Math.floor(toPlayer.z+0.5));

        BlockRotation rotation = null;
        HeadRotation headRotation = entity_Store.getComponent(entity_ref, HeadRotation.getComponentType());
        final float yaw = headRotation.getRotation().yaw();
        double deg = Math.toDegrees(yaw);
        deg += 180; // Deg is from -180 -> 180, Convert to 0-360

        if (deg >=45 && deg < 135){
            rotation = new BlockRotation(Rotation.TwoSeventy,Rotation.None,Rotation.None);
        } else if (deg >= 135 && deg <225) {
            rotation = new BlockRotation(Rotation.None,Rotation.None,Rotation.None);
        } else if (deg >= 225 && deg < 315){
            rotation = new BlockRotation(Rotation.Ninety,Rotation.None,Rotation.None);
        } else {
            rotation = new BlockRotation(Rotation.OneEighty,Rotation.None,Rotation.None);
        }

        BlockRotation blockRotation = interactionContext.getServerState().blockRotation;
        BlockRotation serverBlockRotation = interactionContext.getClientState().blockRotation;
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Client Rotation is "+blockRotation);
        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Server Rotation is "+serverBlockRotation);

        WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG] Degrees: "+deg);

        final BlockRotation finalBlockRot = rotation;
        //BlockRotation rotation = new BlockRotation( new Rotation.fromValue()) transformComponent.
        BlockPlaceUtils.placeBlock(
                entity_ref,                          // ref: player entity ref
                quickAccessItem,                     // itemStack: the item being placed
                quickAccessItem.getBlockKey(),       // blockTypeKey: block to place
                hotbar.getInventory(),               // itemContainer: player's inventory
                toPlayerNorm,                        // placementNormal: face normal
                new Vector3i(fx, fy, fz),            // blockPosition: destination position
                rotation,                       // blockRotation: computed rotation
                (byte) activeSlot,                   // activeSlot: hotbar slot
                false,                               // removeItemInHand: false (you handle this yourself)
                chunkRef,                            // chunkReference: chunk ref for destination
                chunk_accessor,                      // chunkStore: ChunkStore's Store for component access
                entity_Store,                        // entityStore: EntityStore for player component access
                false,                               // quickReplace: creative quick replace
                false,                               // quickRetype: creative quick retype
                false                                // noPhysics: skip physics checks
        );

        // Access the newly placed block's ItemContainerBlock component
        ItemContainerBlock blockContainer = BlockModule.getComponent(
                ItemContainerBlock.getComponentType(), world, fx, fy, fz
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

        Ref<ChunkStore>  chunkEntityRef = BlockModule.getBlockEntity(world, fx, fy, fz);
        chunk_accessor.replaceComponent(chunkEntityRef, ItemContainerBlock.getComponentType(), blockContainer);

        // TODO: Handle adventure vs creative mode placement or removing of keeping item in hand
        hotbar.getInventory().removeItemStackFromSlot(activeSlot, false);

        interactionContext.getState().state = InteractionState.Finished;
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType intType, @Nonnull InteractionContext intCxt,
                                             @Nullable ItemStack itmStk, @Nonnull World world, @Nonnull Vector3i blockPos) {
        // Needed to be overridden, but not needed to be used
    }
}

