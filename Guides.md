# Developer Eamples

#### Using Bson
```java
BsonDocument containerBSON = quickAccessItemStack.getFromMetadataOrNull(ItemStackItemContainer.CONTAINER_CODEC);
ItemStack[] containerItems = ItemStackItemContainer.ITEMS_CODEC.getOrNull(containerBSON, new ExtraInfo());

containerItems[sourceInventoryPosition] = equippedItem;
ItemStackItemContainer.ITEMS_CODEC.put(containerBSON, containerItems, new ExtraInfo());
ItemStack updatedQuickAccessItem = quickAccessItemStack.withMetadata(ItemStackItemContainer.CONTAINER_CODEC, containerBSON);
hotbar.getInventory().removeItemStackFromSlot(equippedPosition);
hotbar.getInventory().setItemStackForSlot(equippedPosition, updatedQuickAccessItem);

ComponentType componentType = InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID);
store.replaceComponent(playerRef, componentType, hotbar);
```

#### Using Codec
```java

```

#### Running command through code
```java
CommandManager.get().handleCommand(player,"Open");
```
