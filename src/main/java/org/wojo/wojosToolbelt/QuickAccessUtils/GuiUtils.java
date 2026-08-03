package org.wojo.wojosToolbelt.QuickAccessUtils;

public class GuiUtils {
    public static void openQuickAccessUI(Store<EntityStore>store, Ref<EntityStore> ref) {
        // ------ Get Needed Data Stores ------
        Player player = store.getComponent(ref, Player.getComponentType());
        QuickAccessPlayerComponent qaPlayerComp = store.getComponent(ref, QuickAccessPlayerComponent.getComponentType());
    
        // ------ Get Quick Access Item ------
        InventoryComponent.Hotbar hotbar = (InventoryComponent.Hotbar) store.getComponent(ref, InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID));
        ItemStack heldItem = hotbar.getActiveItem();
        ItemStack equippedItem = hotbar.getInventory().getItemStack((short)qaPlayerComp.getEquippedPosition());
        ItemStack quickAccessItem = null;
        Integer qaItemHotbarPosition = 0;
    
        // ------ Verify Held or Equipped Item is a QuickAccessItem ------
        boolean isItemHeld = false;
        if (!QuickAccessUtils.isQuickAccessItem(heldItem) && !QuickAccessUtils.isQuickAccessItem(equippedItem)) {
          notificationHelper(store, ref, "ERROR", "Item held or equipped is not a QuickAccess Item");
          WojosQuickAccessPlugin.LOGGER.atWarning().log("[ERROR]: Item held or equipped is not a QuickAccess Item");
          return;
        }else if (QuickAccessUtils.isQuickAccessItem(heldItem)){
          // Priority to use held item over equipped item
          WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: Opening Held Items Quick Access Selection Gui");
          qaItemHotbarPosition = (int) hotbar.getActiveSlot();
          quickAccessItem = heldItem;
        }else{
          WojosQuickAccessPlugin.LOGGER.atFine().log("[DEBUG]: Opening Equipped Items Quick Access Selection Gui");
          qaItemHotbarPosition = qaPlayerComp.getEquippedPosition();
          quickAccessItem = equippedItem;
        }
    
        // ------ TODO: Ensure Quick Access Item Has Proper Config ------
        // ------ Get Needed GUI Object ------
        // -- Have a different class for each file type instead of a single UI file --
        // -- Get ANy Needed Vars needed for GUI File --
        PlayerRef playerRef = getPlayerRef(store, ref);
        // InteractiveCustomUIPage<ItemSelectionGui.SelectionUiData> quickAccessSelectionUI = QuickAccessUtils.getSelectorGui(qaPlayerComp, playerRef, store, isItemHeld);
        
        // -- Open UI --
        // - Gui file only needs location of quickAccessItem were opening, Not if its held or equipped. 
        GenericRadialSelectionUi guiPage = GuiSelectionFactory.createGui(playerRef, store, quickAccessItem, qaItemHotbarPosition);
        //RadialGui2 guiPage = new RadialGui2(playerRef, store, quickAccessItem, qaItemHotbarPosition);
        // player.getPageManager().openCustomPage(ref, store, quickAccessSelectionUI)
        
        // ------ Run GUI event ------
        // PlayerRef playerRef = getPlayerRef(store, ref);
        //RadialGui8 guiPage = new RadialGui8(playerRef, store,);
        // SelectionUiThreeByThree guiPage = new SelectionUiThreeByThree(playerRef,store,isItemHeld);
        player.getPageManager().openCustomPage(ref, store, guiPage);
    }

    public static void notificationHelper(Store<EntityStore>store, Ref<EntityStore> ref, String primary_msg, String secondary_msg) {
        var playerRef = getPlayerRef(store,ref);
        var packetHandler = playerRef.getPacketHandler();
        NotificationUtil.sendNotification(packetHandler, primary_msg, secondary_msg);
    }
}
