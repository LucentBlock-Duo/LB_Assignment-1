package com.lucentblock.assignment2.entity;


import lombok.Getter;

public enum RepairStatus {
    NOT_STARTED("Not Started"),
    REPAIRING("Repairing"),
    COMPLETED("Completed"),
    DELAYED("Delayed"),
    CANCELED("Canceled");

    private final String status;
    RepairStatus(String givenStatus){
        status=givenStatus;
    }

    public static String status(int idx){
       return switch (idx){
           case 0 -> NOT_STARTED.status;
           case 1 -> REPAIRING.status;
           case 2 -> COMPLETED.status;
           case 3 -> DELAYED.status;
           case 4 -> CANCELED.status;
           default -> throw new RuntimeException();
       };
    }

    public String status(){
        return this.status;
    }

}
