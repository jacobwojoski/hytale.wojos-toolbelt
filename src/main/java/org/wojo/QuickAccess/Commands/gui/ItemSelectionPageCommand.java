package org.wojo.QuickAccess.Commands.gui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.wojo.QuickAccess.QuickAccessUtils.QuickAccessUtils;


public class ItemSelectionPageCommand extends AbstractPlayerCommand {
    private final DefaultArg<String> eventArg;

    // Constructor
    public ItemSelectionPageCommand(){
        super("itemSelectionPage", "Do something with the tool selection GUI");
        addAliases("select", "SEL");

        this.eventArg = this.withDefaultArg("event", "Run gui event like open/close/reset", ArgTypes.STRING, "open", "Default is to open the gui");
    };

    // Run the command
    // conetext - info about who ran the command. Server console?, Some player?
    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        // ------ Run GUI event ------
        QuickAccessUtils.openQuickAccessUI(store,ref);
    }
}
