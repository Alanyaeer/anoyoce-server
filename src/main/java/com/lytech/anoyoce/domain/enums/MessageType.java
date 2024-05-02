package com.lytech.anoyoce.domain.enums;

public enum MessageType {
    ORDINARY_MESSAGE(0),
    TASK_MESSAGE(1);
    private int messageType;
    MessageType(int messageType){
        this.messageType = messageType;
    }

    public int getMessageType() {
        return this.messageType;
    }
}
