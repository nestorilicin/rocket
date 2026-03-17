package rs.etf.nestor.rocket.camera;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import rs.etf.nestor.rocket.object.SpaceObject;

public class RearCamera extends AbstractCamera {
    private final SpaceObject target;
    private int cameraZ = -1000;
    private int cameraY = 200;
    static final double offsetY = 200;
    static final double offsetZ = 100;

    public RearCamera(SpaceObject object) {
        this.target = object;
    }


    public void update() {
        double targetX = target.getPosition().getX();
        double targetY = target.getPosition().getY();
        double targetZ = target.getPosition().getZ();

        double translateY = cameraY - offsetY;
        double translateZ = cameraZ - offsetZ;
        camera.getTransforms().setAll(
                new Translate(targetX, targetY, targetZ), 
                new Rotate(-90, Rotate.X_AXIS), 
                new Rotate(-target.getHorizontalAngle(), Rotate.Y_AXIS), 
                new Translate(0, translateY, translateZ));
    }

    public void increaseDistance() { if(cameraZ < -200) cameraZ += 50; }
    public void decreaseDistance() { if (cameraZ > -1500) cameraZ -= 50; }
    public void increaseHeight() { if(cameraY < 660) cameraY += 20; }
    public void decreaseHeight() { if(cameraY > -260) cameraY -= 20; }

    public int getCameraY() {
        return cameraY;
    }

    public int getCameraZ() {
        return cameraZ;
    }
    
}
