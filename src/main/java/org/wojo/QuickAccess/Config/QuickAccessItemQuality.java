public class QuickAccessItemQuality {
  public static final int UNKNOWN = 0;
  public static final int COMMON = 1;
  public static final int UNCOMMON = 2;
  public static final int RARE = 3;
  public static final int EPIC = 4;
  public static final int LEGENDARY = 5;
  public static final int DEBUG = 6;

  public String toString(int item_quality_int) {
    switch (item_quality_int) {
      case 1:
        return "Common";
      case 2:
        return "Uncommon";
      case 3:
        return "Rare";
      case 4:
        return "Epic";
      case 5:
        return "Legendary";
      case 6:
        return "Debug";
      default:
        return "Unknown";
    }      
  }

  public int toInt(String item_quality_string) {
    switch (item_quality_string) {
      case "Common":
        return 1;
      case "Uncommon":
        return 2;
      case "Rare":
        return 3;
      case "Epic":
        return 4;
      case "Legendary":
        return 5;
      case "Debug":
        return 6;
      default:
        return 0;
    }
  }
}
