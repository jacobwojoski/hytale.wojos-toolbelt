import QuickAccessItemType;
import QuickAccessItemQuality;

public class ItemConfig {

  public static final Set<String> QUICK_ACCESS_ITEM_IDS = Set.of(
    // No limit containers
    "Quick_Access_Item_Unrestricted_Common",
    "Quick_Access_Item_Unrestricted_Uncommon",
    "Quick_Access_Item_Unrestricted_Rare",
    "Quick_Access_Item_Unrestricted_Epic",
    "Quick_Access_Item_Unrestricted_Legendary",
    "Quick_Access_Item_Unrestricted_Debug",

    // Tool only containers
    "Quick_Access_Item_Tools_Common",
    "Quick_Access_Item_Tools_Uncommon",
    "Quick_Access_Item_Tools_Rare",
    "Quick_Access_Item_Tools_Epic",
    "Quick_Access_Item_Tools_Legendary",
    "Quick_Access_Item_Tools_Debug"
  
    // Weapons only containers
    // Blocks only
    // Consumables Only
    // Arrows only
  );

  public static String getQuickAccessItemId(int item_type, int item_quality) {
    String itemType = QuickAccessItemType.toString(item_type);
    String itemQuality = QuickAccessItemQuality.toString(item_quality);
    String itemId = "Quick_Aceess_Item_"+itemType+"_"+itemQuality;
    return itemId;
  }

  public static QuickAccessItemComponent QuickAccessItemComponentBuilder(String quick_access_item_id) {
    switch (quick_access_item_id) {
      // =======================================================
      // -------------------- UNRESTRICTED ---------------------
      case "Quick_Access_Item_Unrestricted_Common":
        return new QuickAccessItemComponent(
          QuickAccessItemType.UNRESTRICTED,    // item type
          QuickAccessItemQuality.COMMON,       // item qualty
          2,  // Container storage size
          2,  // quickAccess Button size
          "Pages/WojosQuickAccess/Radials/RadialTwo.ui",   // ui file
          false                                            // is ui changeable
        );

      // =======================================================
      // ----------------------- TOOLS -------------------------
      case "Quick_Access_Item_Tools_Common":
        return new QuickAccessItemComponent(
          QuickAccessItemType.TOOLBELT,        // item type
          QuickAccessItemQuality.COMMON,       // item qualty
          4,  // Container storage size
          4,  // quickAccess Button size
          "Pages/WojosQuickAccess/Radials/RadialFour.ui",  // ui file
          false                                            // is ui changeable
        );
      // ========================================================
      default:
        return null;
    }
  } 
}
