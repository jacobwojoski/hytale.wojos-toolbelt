package org.wojo.QuickAccess.QuickAccessUtils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.wojo.QuickAccess.Components.QuickAccessPlayerComponent;
import org.wojo.QuickAccess.Config.QuickAccessConfig;
import org.wojo.QuickAccess.WojosQuickAccessPlugin;

import java.util.Arrays;

// This class is utility methods for when needing to interact with either the quickAccessItemComponent or QuickAccessPlayerComponent
public class ComponentUtils {
    public static QuickAccessPlayerComponent validateQuickAccessPlayerComponent(QuickAccessPlayerComponent component){
        if (component.getEquippedPosition() < 0 || component.getEquippedPosition() > 8) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR]: QuickAccessComponent.equipedPosition is out of range. Setting to default (8)");
            component.setTargetPosition(0); // Reset to default value
        }
    
        if (component.getTargetPosition() < 0 || component.getTargetPosition() > 8) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR]: QuickAccessComponent.targetPosition is out of range. Setting to default (0).");
            component.setTargetPosition(0); // Reset to default value
        }else if ( component.getTargetPosition() == component.getEquippedPosition()) {
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR]: QuickAccessComponent.targetPosition is same as equippedPosition. Moving target location to (0 or 1 if taken)");
            if (component.getTargetPosition() == 0) {
                component.setTargetPosition(1);
            }else{
                component.setTargetPosition(0);
            }
        }
    
        String guiFile = component.getGuiFile();
        if (!Arrays.asList(QuickAccessConfig.ALL_SELECTION_GUI_FILES).contains(guiFile)){
            WojosQuickAccessPlugin.LOGGER.atInfo().log("[ERROR]: QuickAccessComponent.guiFile is not one of the expected. Resetting to default (Three-By-Three Radial)");
            component.setGuiFile(QuickAccessConfig.DEFAULT_SELECTION_GUI_FILE);
        }
        return component;
    }

    public static QuickAccessPlayerComponent getAndAddQuickAccessPlayerComponent(Ref<EntityStore> playerRef, Store<EntityStore> store) {
        QuickAccessPlayerComponent quickAccessPlayerComponent = null;
        Player player = store.getComponent(playerRef, Player.getComponentType());
        if (player != null) {
            quickAccessPlayerComponent = store.getComponent(playerRef, QuickAccessPlayerComponent.getComponentType());
            if (quickAccessPlayerComponent == null) {
                quickAccessPlayerComponent = new QuickAccessPlayerComponent();
                store.addComponent(playerRef, QuickAccessPlayerComponent.getComponentType(), quickAccessPlayerComponent);
            }
        }
        return quickAccessPlayerComponent;
    }

    public static short getQuickAccessItemEquippedLocationOrDefault(Ref<EntityStore> playerRef, Store<EntityStore> store) {
        QuickAccessPlayerComponent quickAccessPlayerComponent = store.getComponent(playerRef, QuickAccessPlayerComponent.getComponentType());
        if (quickAccessPlayerComponent != null){
            int intPos = quickAccessPlayerComponent.getEquippedPosition();
            return (short) intPos;
        }
        return 8; // Return default of 8
    }

    
    
}
