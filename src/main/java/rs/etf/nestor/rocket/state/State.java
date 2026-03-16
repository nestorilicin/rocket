package rs.etf.nestor.rocket.state;

import rs.etf.nestor.rocket.object.Spacecraft;

public abstract class State {
    protected Spacecraft spacecraft;

    public State(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
    }

    public void update(double passed, int pitchDirection, int rollDirection, int accelerationDirection){}
    public void spacePressed(){}
}
