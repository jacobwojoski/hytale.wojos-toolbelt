# Changelog
## 1.5.1
- Features
  - Mod icon for in game world mod list
- Bugs
  - Fix mod not working in singleplayer worlds by changing packet adapter to Event handler

## 1.5.0
- Features
  - New Assets: Create custom art asset for the *Quick-Access Items*.
    - Toolbox Design
    - Bag-of-Holding Design
  - Placeable: Place and Equip the block into the world.
- Bugs
  - bug/empty-item-container: Fix issue where player was unable to use radial menu if they didn't add something to storage first. 

## 1.2.0
- Features
    - feature/Add-Swap-With-Equipped: New feature so user can right click to swap with currently equipped hotbar location instead of target location. Needs to be enabled in settings.
    - feature/Ui-Status Buttons: Add Inventory button to UI so players can more easily open inventory when quick-swap is active
- Bugs
    - bug/swap-to-empty: Fix issue where selecting an empty locatioin in radial would do nothing even if location was a valid storage location.
    - bug/target-is-quickAccessItem: Fix possibility of swapping item into the quickAccessItems space deleting the quickAccessItem entirely.
    - Additional tooltips: Add tooltips for new buttons and settings better describe what each setting does.
    - Notification Improvements:
        - Better notifications to tell user to add a item to container storage when first equipping a QuickAccessItem
        - Warning when target location was quick access item so we cancled swap

## 1.1.1
- Fix issue with crafting EPIIC tier Quick Access Item
- Remove log spam from console
- Slightly reduce item icon to allow most icons to be visible
- Add tooltip descriptions to the settings page

## 1.1.0
- Change UI design to use real radials
- Proper OOP for UI Code

## 1.0.1
- Fixed a crash when moving items in specific way

## 1.0.0 - Initial release
- Unrestricted QuickAccess Items Added