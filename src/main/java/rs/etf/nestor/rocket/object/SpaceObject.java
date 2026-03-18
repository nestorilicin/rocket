package rs.etf.nestor.rocket.object;

import javafx.scene.Group;
import rs.etf.nestor.rocket.geometry.Vector;

public abstract class SpaceObject extends Group {
    protected Vector position;
    protected double horizontalAngle;

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector v) {
        this.position = v;
    }

    public double getHorizontalAngle() {
        return horizontalAngle;
    }

    public void setHorizontalAngle(double horizontalAngle) {
        this.horizontalAngle = horizontalAngle;
    }
}
