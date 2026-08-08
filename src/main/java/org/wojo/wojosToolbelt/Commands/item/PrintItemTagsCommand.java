package org.wojo.wojosToolbelt.Commands.item;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.wojosToolbelt.QuickAccessUtils.LoggingUtils;

import java.util.Map;

public class PrintItemTagsCommand extends AbstractPlayerCommand {

    public PrintItemTagsCommand() {
        super("tags", "Print a held items tags. User can add any tags to container filters in the server config.");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> entity_Store, @NonNullDecl Ref<EntityStore> entity_ref, @NonNullDecl PlayerRef player_ref, @NonNullDecl World world) {
        InventoryComponent.Hotbar hotbar =
                (InventoryComponent.Hotbar) entity_Store.getComponent(
                        entity_ref,
                        InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID)
                );

        ItemStack heldItem = hotbar.getActiveItem();
        if (heldItem == null) {
            context.sendMessage(Message.raw("No Held Item"));
        }else{
            AssetExtraInfo.Data extraInfo = heldItem.getItem().getData();
            Map<String,String[]> item_tags = extraInfo.getRawTags();
            StringBuilder tag_string = LoggingUtils.getTagMapString(item_tags);
            context.sendMessage(Message.raw("Tags for held item are:\n "+tag_string));
        }
    }
}
