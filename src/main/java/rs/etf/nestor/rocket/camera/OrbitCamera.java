package rs.etf.nestor.rocket.camera;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import rs.etf.nestor.rocket.object.SpaceObject;

public class OrbitCamera extends AbstractCamera {
    private final SpaceObject target;
    private double horizontalAngle = 90;
    private double verticalAngle = 90;
    private double radius = 500;

    public OrbitCamera(SpaceObject object) {
        this.target = object;
    }

    @Override
    public void update() {
        double targetX = target.getPosition().getX();
        double targetY = target.getPosition().getY();
        double targetZ = target.getPosition().getZ();
        camera.getTransforms().setAll(
                new Translate(targetX, targetY, targetZ),
                new Rotate(horizontalAngle, Rotate.Z_AXIS),
                new Rotate(-verticalAngle, Rotate.X_AXIS),
                new Translate(0, 0, -radius));

    }

    public void rotate(double dx, double dy) {
        horizontalAngle += dx * 0.3;
        verticalAngle += dy * 0.3;
        if (verticalAngle > 180) verticalAngle = 180;
        if (verticalAngle < 0) verticalAngle = 0;
    }

    public void zoom(double delta) {
        radius += delta;

        if (radius < 50) radius = 50;
        if (radius > 5000) radius = 5000;
    }

}
