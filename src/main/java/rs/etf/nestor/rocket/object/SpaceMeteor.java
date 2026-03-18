package rs.etf.nestor.rocket.object;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class SpaceMeteor extends SpaceObject{
    public SpaceMeteor(double radius, Color diffuseColor){
        PhongMaterial material = new PhongMaterial(diffuseColor);
        Sphere sphere = new Sphere(radius);
        sphere.setMaterial(material);
        sphere.setOpacity(0.5);
        getChildren().add(sphere);
    }
}
