package com.example.wateryouwaitingfor;

public class WaterIntakeHandler {
    private double weight = 0;
    private ACTIVITYLEVEL activitylevel;
    private double idealIntake;




    public void setWeight(double weight){
        this.weight = weight;
    }

    public void setActivitylevel(int level){
        activitylevel = ACTIVITYLEVEL.values()[level];
    }
    public void updateIdealIntake(){ // half body weight(oz) + 12 oz per 30 min
        idealIntake = 0.5*weight + 12 * activitylevel.ordinal();
    }

    public double getIdealIntake(){
        return idealIntake;
    }

    public enum ACTIVITYLEVEL{
        NONE,
        LOW,
        MEDIUM,
        HIGH
    }
}
