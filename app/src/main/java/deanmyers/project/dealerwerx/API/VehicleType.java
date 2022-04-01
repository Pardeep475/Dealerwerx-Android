package deanmyers.project.dealerwerx.API;

/**
 * Created by mac3 on 2016-11-16.
 */

public enum VehicleType {
    Car('c'),
    Motorcycle('m'),
    Boat('w'),
    Equipment('x'),
    Other('z');

    private char value;
    public char asChar(){ return value; }

    private VehicleType(char value){
        this.value = value;
    }

    @Override
    public String toString() {
        switch (this){
            case Car:
                return "Car";
            case Motorcycle:
                return "Motorcycle";
            case Boat:
                return "Boat";
            case Equipment:
                return "Equipment";
            case Other:
                return "Other";
            default:
                return null;
        }
    }

    public static VehicleType get(char value){
        switch(value){
            case 'c':
                return VehicleType.Car;
            case 'm':
                return VehicleType.Motorcycle;
            case 'w':
                return VehicleType.Boat;
            case 'x':
                return VehicleType.Equipment;
            case 'z':
                return VehicleType.Other;
        }

        return null;
    }
}
