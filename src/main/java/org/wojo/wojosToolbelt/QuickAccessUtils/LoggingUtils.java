package org.wojo.wojosToolbelt.QuickAccessUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.wojo.wojosToolbelt.WojosQuickAccessPlugin;

import java.util.Map;

public class LoggingUtils {
    public static void printItemContainer(ItemContainer item_container){
        if (item_container == null){
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR] WQA::LoggingUtils::printItemContainer \n - item_container is null");
            return;
        }
        
        String logString = "[INFO] WQA::LoggingUtils::printItemContainer \n - ItemCapacity: "+String.valueOf(item_container.getCapacity())+"\n - [ ";
        for (short i=0; i<item_container.getCapacity();i++){
            ItemStack item = item_container.getItemStack(i);
            if (item != null){
                logString += item.getItemId();
            }else{
                logString += "null";
            }
            if (i+1 != item_container.getCapacity()){
                logString += " | ";
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
                logString += "null";
            }
            if (i != item_stack_ary.length-1){
                logString += " | ";
            }
        }
        logString += " ]";
        WojosQuickAccessPlugin.LOGGER.atInfo().log(logString);
    }

    public static void printTagMap(Map<String,String[]> item_stack_tags) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String[]> entry : item_stack_tags.entrySet()) {
            result.append(entry.getKey()).append(":\n");

            String[] values = entry.getValue();

            if (values == null || values.length == 0) {
                result.append("  - None\n");
                continue;
            }

            for (String value : values) {
                result.append("  - ").append(value).append("\n");
            }
        }

        WojosQuickAccessPlugin.LOGGER.atInfo().log(result.toString());
    }
}
