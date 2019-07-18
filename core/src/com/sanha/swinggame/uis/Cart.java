package com.sanha.swinggame.uis;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.sanha.swinggame.tools.Setting;

class Cart {
    private CartUI cartUI;

    private Array<CartItem> items;
    private Table cartTable;

    private int selectedId;

    Cart(CartUI cartUI) {
        this.cartUI = cartUI;
        initItems();
        createCartTable();

        for (int i=0; i<items.size; i++) {
            if (Setting.isUnlocked(i)) items.get(i).unlock();
        }
        selectItem(Setting.characterId);
    }

    Table getCartTable() {
        return cartTable;
    }
    int getSelectedId() {
        return selectedId;
    }
    int getItemPrice(int id) {
        return items.get(id).getPrice();
    }

    void unlockIem(int id) {
        items.get(id).unlock();
    }
    void selectItem(int id) {
        for (CartItem item : items) {
            if (item.getId() == id) item.select();
            else item.unselect();
        }
        selectedId = id;
    }

    private void initItems() {
        items = new Array<CartItem>();

        items.add(new CartItem(cartUI, 0, 0));
        items.add(new CartItem(cartUI, 1, 100));
        items.add(new CartItem(cartUI, 2, 300));
        items.add(new CartItem(cartUI, 3, 500));
        items.add(new CartItem(cartUI, 4, 1000));
        items.add(new CartItem(cartUI, 5, 1500));
        items.add(new CartItem(cartUI, 6, 2000));
        items.add(new CartItem(cartUI, 7, 3000));
        items.add(new CartItem(cartUI, 8, 4000));
        items.add(new CartItem(cartUI, 9, 5000));
    }
    private void createCartTable() {
        cartTable = new Table();
        cartTable.setFillParent(true);
        cartTable.padBottom(100);
        cartTable.add(items.get(0)).pad(40);
        cartTable.add(items.get(1)).pad(40);
        cartTable.add(items.get(2)).pad(40);
        cartTable.add(items.get(3)).pad(40);
        cartTable.add(items.get(4)).pad(40);
        cartTable.row();
        cartTable.add(items.get(5)).pad(40);
        cartTable.add(items.get(6)).pad(40);
        cartTable.add(items.get(7)).pad(40);
        cartTable.add(items.get(8)).pad(40);
        cartTable.add(items.get(9)).pad(40);
    }
}
