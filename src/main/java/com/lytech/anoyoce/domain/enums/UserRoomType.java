package com.lytech.anoyoce.domain.enums;

public enum UserRoomType {
    //普通用户
    ROOM_MEMBER(0),
    //管理员用户
    ROOM_MANAGER(1),
    //群主
    ROOM_LEADER(2);
    private final int userType;
    UserRoomType(int userType){
        this.userType = userType;
    }
    public int getUserType(){
        return this.userType;
    }
}
