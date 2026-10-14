package org.wojo.QuickAccess.Config;

public class QuickAccessItemType {
  public static final int UNKNOWN = 0;
  // Anything
  public static final int UNRESTRICTED = 1;
  // Tools only
  public static final int TOOLBELT = 2;
  // Weapons Only (May create sub types in future ex heavy vs light only weapons)
  public static final int WEAPON_SLING = 3;
  // Blocks Only 
  public static final int BUILDERS_POUCH = 4;
  // Consumables Only (Food, potions, Bombs) 
  public static final int BANDOLEER = 5;
  // Arrows Only
  public static final int QUIVER = 6;

  public String toString(int item_type_int) {
    switch (item_type_int) {
      case 1:
        return "Unrestricted";
      case 2:
        return "Tools";
      case 3:
        return "WeaponSling";
      case 4:
        return "BuildersPuch";
      case 5:
        return "Bandoleer";
      case 6:
        return "Quiver";
      default:
        return "Unknown";
    }      
  }

  public int toInt(String item_type_string) {
    switch (item_type_string) {
      case "Unrestricted":
        return 1;
      case "Tools":
        return 2;
      case "WeaponSling":
        return 3;
      case "BuildersPuch":
        return 4;
      case "Bandoleer":
        return 5;
      case "Quiver":
        return 6;
      default:
        return 0;
    }
  }
}
