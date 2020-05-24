package io.ruin.model.item.containers.bank;

import com.google.gson.annotations.Expose;
import io.ruin.model.item.Item;

public class BankItem extends Item {

    @Expose protected int tab;

    protected int sortSlot = -1;

    public BankItem(int id, int amount, int uniqueValue, int tab) {
        super(id, amount, uniqueValue);
        this.tab = tab;
    }

    @Override
    public BankItem copy() {
        return new BankItem(getId(), getAmount(), getUniqueValue(), tab);
    }

    public void toBlank() {
        setId(Bank.BLANK_ID);
        setAmount(0);
        setUniqueValue(0);
    }

}
