# Wojo's Quick Access Item's (Toolbelts, Slings & More)
### ### De-clutter the Hotbar!
This mod for **Hytale** adds custom *Quick-Access Items* that allows the user to have a *Quick-Access* radial menu.

### The Problem
**Tools and weapons take up too much hotbar real-estate significantly  impacting the building experience while in adventure mode.**
The main purpose of this mod is to fix one of my largest pain-points with inventory management. 

## WARNING: 
- Now that radials are implemented there will be **limited/minor UI updates until Hytale's Noesis GUI** gets added.
    - (I'm going to need to rewite so much UI code once that happens so expect a very delayed update)

## Guide and Detailed Description
- **Quick-Access Items** are used to implement the feature
    - Craftable in workbench level 1 (Tools)
    - Multiple item tiers with increasing container size
    - Can be equipped by placing the item in a defined hotbar position (Configurable)
- When holding a *Quick Access Item*
    - Holding & **Right-Click** will open chest style inventory to hold items
    - Holding & **Left-Click** will open a radial menu listing items in the Quick-Access item inventory.
        - Selecting **Left-Click** an item on the Radial UI will swap the selected item with **target-hotbar-position** (Configurable).
        - Selecting **Right-Click** an item on the Radial UI will swap the selected item with **active-hotbar-position** (Enableable)
    - Holding & **Shift + Right-Click** will place the item in the world as a block
    - Holding & **Shift + Left-Click** will do a general break block punch
- Placed *Quick-Access Items*
    - Use (**F**) opens the blocks inventory
    - Crouch + Use (**Shift + F**) Tries to pick up the block & equip it to the hotbar.

## Settings
- **Equipped Position:** What *hotbar position* acts as the Quick-Access-equipment & open-radial slot.
- **Target Position:** When selecting an item in the radial menu, what hotbar location does it swap items to.
- **Gui File:** What radial menu file do you want to see
    - This allows the player to use a smaller radial for a given *Quick-Access Item* if desired.
- **Enable Quick-Swap:** Intercept hotbar swaps to use the *Equipped Position* as a way to open the radial menu instead of needing to **Equip and Use** the item.
- **Enable Swap-to-Active:** *Right-click* an item in the radial to swap the item into the `active-hotbar-position` instead of `target-hotbar-position`



## Quick Access Item Types (Only checked items are currently implemented)
The current design only includes the *Unrestricted Quick-Access Items*. More are planned!

- [X] **Unrestricted:** Quick access radial that can hold **Anything**
- [ ] **Toolbelt:** Quick access radial item that holds only holds **Tools** (Shovel, Pickaxe, axe, hammer)
- [ ] **Builders Pouch:** Quick access radial that only holds **Blocks & Hammer**
- [ ] **Weapon Sling:** Quick access radial that only hold **Weapons**
- [ ] **Bandolier:** Quick access radial that only holds **Consumables** (Potions, Food, Bombs etc)
- [ ] **Quiver:** Quick access radial that only holds **Arrows**

## Quick-Access Item Tier & Storage Capacity
- Unrestricted
    - Common: 2 slots
    - Uncommon: 3 slots
    - Rare: 4 slots 
    - Epic: 6 slots
    - Legendary: 8 slots
    - Debug: 12 slots

---

## Commands
#### Permission Groups
- WojosQuickAccess.admin
- WojosQuickAccess.user

#### All Commands (Note: most commands have default args that are not specified here)

```java
/wqa comp player    // (Admin) Set your QuickAccessPlayerComponent to defaults or specified values
/wqa comp printp    // (Admin) Print your QuickAccessPlayerComponent data
/wqa comp printi    // (Admin) Print held items QuickAccessItemComponent data

/wqa item swap      // (User)  Swap an item from held(Higher priority) or equipped(Lower Priority) Quick-Access Item to the defined hotbar position
/wqa item print     // (Admin) Print Hytale item data associated with the held itemStack

/wqa gui select     // (User)  Show the radial selection menu
/wqa gui store      // (User)  Show the container storage menu
/wqa gui settings   // (User)  Show the settings menu
/wqa gui help       // (User)  Show the help menu
```

#### Use Hytale's log command to update logger for detailed debugging
- `/log WojosQuickAccessItems --level fine --save`

--- 

## Code Design 
### Hytale Development Notes and Limitations
- Adding custom metadata to items is not currently supported
    - Metadata is stored on the `ItemStack` not on the `Item`. This makes handling dynamic custom data kind of a pain.
    - My work around for this issue is to have a `ComponentID -> Item Data Object` Factory  
- Custom Key Binds are not currently supported
    - Use the hotbar positions to supliment a key bind for time being.
    - Using hotbar involves async calls outside of the normal game loop and ECS structure.
        - Using concurent hash maps as a way to pass data from the async packet calls to the ECS game loop.
        - *It's hacky and I don't like it but it works until they add custom key binds*
- UI Design tools are limited until Noesis Gui gets added.
    - No dynamic backgrounds so my workaround was to change image visibility based of where mouse was
    - Cant get mouse position, UI is actually a all transparent buttons over a background image 
  
### Code Data Handleing
The plugin layout has 2 data storage locations; **QuickAccessPlayerComponent - The player** and **QuickAccessItemComponent - The Quick Access Item**.
- The **ItemComponent** is a data object that holds additional config info that I was unable to add to the item json. Data in here shouldn't change unless configs are changed. 
- The **PlayerComponent** is the only area where data does change and it gets updated when the user adjusts any settings. 

- The **QuickAccessPlayerComponent**:
    - What hotbar slot is the 'equipped' location/ what hotbar button pressed to open ui
    - Is the hotbar button enabled?
    - Is Swap-to-Active enabled?
    - What Hotbar Position to Swap Items Into
    - GuiPageString (Radial Menu to display to user)

- The **QuickAccessItemComponent**: Holds the following item into
    - Asset Info
      - Item tier (Common, Uncommon, Rare, etc)
      - Container Size
    - Config Info
      - Quick Access Size (Must be <= Container Size; likely same as Container Size but doesn't need to be)
      - Item type (Unrestricted, Toolbelt, sling, etc)

- Handling the Hotbar button press
    - Check packet to see if its a button press packet
    - If User has Quick Swap Enabled
    - If button press was enabled button
    - Tell the world thread the player is on to try to open the gui

- Handling converting `ItemStackItemContainer` to `BlockContainer`
    - Create custom placeBlockInteraction
    - Create custom equipBlockInteraction

### Code Components
- Commands
    - Varous commands that are used for debugging, configuration, or help
- Components
    - The QuickAccessPlayerComponent (ECS component attached to the player)
    - The QuickAccessItemComponent (This is not an ecs component, its used as a wrapper to get the items Json data & some config data)
- Config
    - All statically defined values (Plan to be editable)
- Events
    - Mod involves player interaction, async nature means we trigger an event when swapping items 
- Handlers
    - Logic for handling the triggered events of `Player Connected` and `QuickSwapEvent`
- Interactions
    - Items use interaction chains so these are any custom interactions created. (CustomPlaceBlock, CustomEquipBlock, CustomOpenRadialMenu)
- Packet Adapters
    - Logic to convert player hotbar interaction to a UI button (Ideally when player keybinds get intoduced this can be replaced)
- Systems
    - Do things when something happens to an ECS component. Main use is to keep Packet Adapter working when QuickAccessPlayerComponent gets updated
- Utils
    - ECS structure states components should have no methods so these are all static helper methods that do much of the validation & sanity checks. 
- UI
    - All ui classes (Radials, Settings Page, Help Page)
- Resources
    - The different resource components that are made through Hytale's Asset Editior. It inludes the **UI html files | Lang Files | Asset Files **  

---

### TODO:
##### High Priority (No set order)
- [ ] Fix permissions issues - Default perms should allow player to use item
- [ ] Allow swap item to/from a selected hotbar instead of specific value
- [ ] Allow item to be placed in world
    - [ ] Place+Break, Open inventory
- [ ] Add hud elements to show item buttons to user when holding Quick Access Item
    - Look Into HudUI setting in asset file 

##### Low Priority (No set order) 
- [ ] Custom Item model
- [ ] Add animations to using item
- [ ] Implement other QuickAccessItemTypes
- [ ] Have way to *wear* QuickAccess items so others can see when player has it equipped
- [ ] Crafting ballence
- [ ] Configurable costs when using the feature
    - [ ] Stamina ( Regen Delay, Cost, Gain )
    - [ ] Mana ( Regen Delay, Cost, Gain )
    - [ ] Movement ( Speed Modifers, Jump Modifiers ) 
    - [ ] Animation Time
    - [ ] Noise ( Audio Que for Others )


### Special Thanks
- **Hytalemodding.dev** website and discord needs all the praise I can give them. They are great source of info and helped me to many times to count during the development. [Modding Documentation Website](https://hytalemodding.dev/en)
- Thanks to TroubleDEV for the best early Hytale youtube tutorials [TroubleDEV Youtube Link](https://www.youtube.com/channel/UC8IirsfaLXk7WFn55j1zs-g)
- Thanks to Plugin Template for code template. [Template Link](https://github.com/Build-9/Hytale-Example-Project)
