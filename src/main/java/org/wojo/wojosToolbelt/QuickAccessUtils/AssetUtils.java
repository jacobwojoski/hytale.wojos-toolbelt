package org.wojo.wojosToolbelt.QuickAccessUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.wojo.wojosToolbelt.Config.QuickAccessConfig;

// This is a class thats used to interact with item stacks
//    - This class uses codecs to parse items JSON docs to return any needed info
public class AssetUtils {
    // ------------ Item ContaienerInteractions ------------
    public static short getItemContainerSize(ItemStack item_stack) {
        return 0;
    }

    public static ItemStack[] getItemContainerItems(ItemStack item_stack) {
        return null;
    }

    public static String getItemContainerWhitelistFilter(ItemStack item_stack){
        return "";
    }

    // ------------ Block Container Interactions ------------
    public static short getBlockContainerSize(ItemStack item_stack) {
        return 0;
    }

    public static ItemStack[] getBlockContainerItems(ItemStack item_stack) {
        return null;
    }

    public static String getBlockContainerWhitelistFilter(ItemStack item_stack){
        return "";
    }

    // ------------ Item Interactions ------------
    public static String getItemTier(ItemStack item_stack){
        return "";
    }

    public static String getItemType(ItemStack item_stack){
        return "";
    }

    public static Boolean isQuickAccessItem(String item_id) {
        return QuickAccessConfig.QUICK_ACCESS_ITEM_IDS.contains(item_id);
    }

    public static Boolean isQuickAccessItem(ItemStack item_stack) {
        if (item_stack == null) {
            return false;
        }
        return QuickAccessConfig.QUICK_ACCESS_ITEM_IDS.contains(item_stack.getItemId());
    }
}
