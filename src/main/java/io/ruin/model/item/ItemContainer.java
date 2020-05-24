package io.ruin.model.item;

public class ItemContainer extends ItemContainerG<Item> {

    @Override
    protected Item newItem(int id, int amount, int uniqueValue) {
        return new Item(id, amount, uniqueValue);
    }

    @Override
    protected Item[] newArray(int size) {
        return new Item[size];
    }

}
