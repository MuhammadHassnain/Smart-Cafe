package com.manager.smartcafe.database;

public class Menu {


    public Menu(String menuName, int menuPrice, String menuDescription, String menuImageUrl,String menuId) {

        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuDescription = menuDescription;
        this.menuImageUrl = menuImageUrl;
        this.menuId = menuId;
    }

    private String menuName;
    private int menuPrice;
    private String menuDescription;
    private String menuImageUrl;
    private String menuId;


    public String getMenuId() {
        return menuId;
    }

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
