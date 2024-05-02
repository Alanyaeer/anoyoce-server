package com.lytech.anoyoce.utils;

import io.minio.messages.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemToList {
    public static<T> List<T> getList(T item){
        ArrayList<T> itemList = new ArrayList<>();
        itemList.add(item);
        return itemList;
    }
}
