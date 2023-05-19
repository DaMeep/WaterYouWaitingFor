package com.example.wateryouwaitingfor;

public class WaterIntakeHandler {
    private double weight = 0; // user weight
    private ACTIVITYLEVEL activitylevel; // user activity level
    private double idealIntake; // user daily intake


    /**
     * Sets the user's weight for calculations
     * @param weight the user's weight
     */
    public void setWeight(double weight){
        this.weight = weight;
    }

    /**
     * Sets the user's activity level for calculations
     * @param level the user's activity level
     */
    public void setActivitylevel(int level){
        activitylevel = ACTIVITYLEVEL.values()[level];
    }

    /**
     * Sets the user's ideal daily water intake
     */
    public void updateIdealIntake(){ // half body weight(oz) + 12 oz per 30 min
        idealIntake = 0.5*weight + 12 * activitylevel.ordinal();
    }

    /**
     * Gets the user's ideal daily water intake
     * @return the user's daily water intake
     */
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
