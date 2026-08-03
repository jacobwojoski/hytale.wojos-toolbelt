package org.wojo.wojosToolbelt.QuickAccessUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;

// This is a class thats used to interact with item stacks
//    - This class uses codecs to parse items JSON docs to return any needed info
public class AssetInteractionUtils {
    // ------------ Item ContaienerInteractions ------------
    public static short getItemContainerSize(ItemStack item_stack) {
    }
    public static ItemStack[] getItemContainerItems(ItemStack item_stack) {
    }
    public static String getItemContainerWhitelistFilter(ItemStack item_stack){
    }

    // ------------ Block Container Interactions ------------
    public static short getBlockContainerSize(ItemStack item_stack) {
    
    }
    public static ItemStack[] getBlockContainerItems(ItemStack item_stack) {
    
    }
    public static String getBlockContainerWhitelistFilter(ItemStack item_stack){
    }

    // ------------ Item Interactions ------------
    public static String getItemTier(ItemStack item_stack){
    }
    public static String getItemType(ItemStack item_stack){
    }
    public static Boolean isQuickAccessItem(String item_id) {
        return QuickAccessConfig.QUICK_ACCESS_ITEM_IDS.contains(item_id);
    }
}
