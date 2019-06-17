package com.example.user.SmartBartender;

import android.app.Application;
import java.util.ArrayList;
import java.util.Arrays;

public class SmartBartender extends Application {
    //настройки

    ArrayList<Integer> motors = new ArrayList<>(Arrays.asList(0,0,0,0,0,0));
    int coefficient = 1500;
    int numberOfMotors = 6;
    public SmartBartender(){
    }

    public void setMotors(ArrayList<Integer> motors) {
        this.motors = motors;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public void setNumberOfMotors(int numberOfMotors) {
        this.numberOfMotors = numberOfMotors;
    }

    public ArrayList<Integer> getMotors() {
        return motors;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public int getNumberOfMotors() {
        return numberOfMotors;
    }

    public void deleteSettings(){
        for(int i=0; i<numberOfMotors;i++){
            motors.add(i,0);
        }
    }
    public void deleteItemFromSettings(int item){
        int index;
        for(int i=0;i<numberOfMotors;i++) {
            index = motors.indexOf(item);
            if (index != -1) motors.set(index,0);
        }
    }
}
