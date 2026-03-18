package rs.etf.nestor.rocket.state;

import rs.etf.nestor.rocket.object.Spacecraft;

public abstract class State {
    protected Spacecraft spacecraft;
    protected double fuel = 1000;
    public State(Spacecraft spacecraft, double fuel) {
        this.spacecraft = spacecraft;
        this.fuel = fuel;
    }

    public void update(double passed, int pitchDirection, int rollDirection, int accelerationDirection){}
    public void spacePressed(){}
    public void consumeFuel(){}
    public double getFuel(){return fuel;}
    public void increaseFuel(double amount){
        fuel += amount;
        if(fuel > 1000) fuel = 1000;
    }
}
