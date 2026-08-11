package org.wojo.QuickAccess.Commands.item;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ItemCommandCollection extends AbstractCommandCollection {
    public ItemCommandCollection(){
        super("item","All Item Specific commands");

        this.addSubCommand(new PrintItemCommand());
        this.addSubCommand(new SwapItemCommand());
        this.addSubCommand(new PrintItemTagsCommand());

        addAliases("items","I");
    }
}