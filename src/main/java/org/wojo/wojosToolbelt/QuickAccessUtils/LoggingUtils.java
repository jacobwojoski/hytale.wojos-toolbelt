package org.wojo.wojosToolbelt.QuickAccessUtils;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

public class LoggingUtils {
    public static void printItemContainer(ItemContainer item_container, short capacity){
        if (item_container == null){
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR] WQA::LoggingUtils::printItemContainer \n - item_container is null");
            return;
        }
        
        String logString = "[INFO] WQA::LoggingUtils::printItemContainer \n - ItemCapacity: "+String.valueOf(capacity)+"\n - [ ";
        for (short i=0; i<capacity;i++){
            ItemStack item = item_container.getItemAtSlot(i);
            if (item != null){
                logString += item.getItemId();
            }else{
                logString += "null"
            }
            if (i != capacity-1){
                logString += " | "
            }
        }
        logString += " ]";
        WojosQuickAccessPlugin.LOGGER.atInfo().log(logString);
    }

    public static void printItemStackArray(ItemStack[] item_stack_ary) {
        if (item_stack_ary == null){
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR] WQA::LoggingUtils::printItemStackArray \n - item_stack_ary is null");
        }
        
        String logString = "[INFO] WQA::LoggingUtils::printItemStackArray \n - ItemCapacity: "+String.valueOf(item_stack_ary.length)+"\n - [ ";
        for (short i=0; i<item_stack_ary.length;i++){
            ItemStack item = item_stack_ary[i];
            if (item != null){
                logString += item.getItemId();
            }else{
                logString += "null"
            }
            if (i != capacity-1){
                logString += " | "
            }
        }
        logString += " ]";
        WojosQuickAccessPlugin.LOGGER.atInfo().log(logString);
    }
}
