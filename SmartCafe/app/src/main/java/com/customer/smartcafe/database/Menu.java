package com.customer.smartcafe.database;

public class Menu {





    public Menu(String menuName, int menuPrice, String menuDescription, String menuImageUrl) {

        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuDescription = menuDescription;
        this.menuImageUrl = menuImageUrl;
    }

    private String menuName;
    private int menuPrice;
    private String menuDescription;
    private String menuImageUrl;

    public String getMenuName() {
        return menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public String getMenuDescription() {
        return menuDescription;
    }

    public String getMenuImageUrl() {
        return menuImageUrl;
    }


    public Menu() {
    }
}
