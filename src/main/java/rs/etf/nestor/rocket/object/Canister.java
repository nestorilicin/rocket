package rs.etf.nestor.rocket.object;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

import java.util.Objects;


public class Canister extends SpaceObject {
    public Canister(double width, Color diffusecolor){
        double height = width * 8.5 / 5.;
        double depth = width * 1.75 / 5.;
        double pipe_side = width *.5 / 5.;
        // model za ovo je bila kutija cigareta americke proizvodnje
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(diffusecolor);
        Box box = new Box(width, depth , height);
        Image diffuseMap =new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("canister.jpg")));
        material.setDiffuseMap(diffuseMap);
        material.setSpecularColor(Color.WHITE);
        box.setMaterial(material);
        
        Box pipe =  new Box(pipe_side, pipe_side, pipe_side*4);
        PhongMaterial material2 = new PhongMaterial();
        material2.setDiffuseColor(Color.GAINSBORO);
        pipe.setMaterial(material2);
        pipe.getTransforms().addAll(new Translate(-width / 2 + pipe_side / 2., -depth / 2 + pipe_side / 2., width + pipe_side / 2.));
        getChildren().addAll(box, pipe);
    }
}
