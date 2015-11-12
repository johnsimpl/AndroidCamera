package com.protectsoft.camshare.drawer;

/**
 * Created by abraham on 23/10/2015.
 */
public class NavDrawerItem {

    private String title;
    private int icon;


    public NavDrawerItem(String title){
        this.title = title;
    }

    public NavDrawerItem(String title,int icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle(){
        return this.title;
    }


    public void setTitle(String title){
        this.title = title;
    }



}
