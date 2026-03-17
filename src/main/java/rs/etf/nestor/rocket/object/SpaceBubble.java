package rs.etf.nestor.rocket.object;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Random;

public class SpaceBubble extends SpaceObject {

    public SpaceBubble(double radius, Color diffuseColor) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(diffuseColor);
        Image bumpMap =new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("bump.jpg")));
        material.setBumpMap(bumpMap);
        Sphere sphere = new Sphere(radius);
        sphere.setMaterial(material);
        this.getChildren().addAll(sphere);
        Random rand = new Random();
        for(int i = 0; i < 50; i++){
            Sphere little = new Sphere(radius / 30.);
            little.setMaterial(material);
            little.getTransforms().addAll(new Rotate(rand.nextInt(360), Rotate.X_AXIS), new Rotate(rand.nextInt(360), Rotate.Y_AXIS));
            little.getTransforms().add(new Translate(radius, 0, 0));
            this.getChildren().add(little);
        }
        for (int i = 0; i < 3; i++){
            Sphere medium = new Sphere(radius / 10.);
            medium.setMaterial(material);
            Rotate orbit = new Rotate(0, Rotate.Z_AXIS);
            orbit.setPivotX(-radius / radius / 2.);
            orbit.setPivotY(0);
            orbit.setPivotZ(0);
            medium.getTransforms().addAll(
                    new Rotate(rand.nextInt(360), Rotate.X_AXIS),
                    new Rotate(rand.nextInt(360), Rotate.Y_AXIS),
                    orbit,
                    new Translate(radius + radius/20., 0, 0)
            );
            this.getChildren().add(medium);
            Timeline animation = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(orbit.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(orbit.angleProperty(), 360))
            );
            animation.setCycleCount(Animation.INDEFINITE);
            animation.play();
            
        }
        
    }

}
