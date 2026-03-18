package rs.etf.nestor.rocket.object;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class SpacePowerUp extends SpaceObject {

    public SpacePowerUp(float size, Color diffuseColor) {

        PhongMaterial material = new PhongMaterial(diffuseColor);

        float width = size / 2;
        float height = size;
        float depth = size / 3;

        float[] points = new float[]{
                0, 0, 0,
                width, 0, 0,
                width, height, 0,

                width, height, depth,
                0, 0, depth,
                width, 0, depth
        };

        float[] texCoords = new float[]{
                0.5f, 0,
                0, 1,
                1, 1
        };

        int[] faces = new int[]{
                0,0, 1,1, 2,2,
                0,0, 2,2, 1,1,

                0,0, 2,2, 3,1,
                0,0, 3,1, 2,2,

                3,1, 4,2, 0,0,
                3,1, 0,0, 4,2,

                4,2, 5,0, 3,1,
                4,2, 3,1, 5,0,

                0,0, 1,1, 5,2,
                0,0, 5,2, 1,1,

                5,2, 4,1, 0,0,
                5,2, 0,0, 4,1,

                1,1, 5,2, 2,0,
                1,1, 2,0, 5,2,

                3,0, 5,1, 2,2,
                3,0, 2,2, 5,1
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);

        MeshView wing1 = new MeshView(mesh);
        wing1.setMaterial(material);

        MeshView wing2 = new MeshView(mesh);
        wing2.setMaterial(material);
        wing2.getTransforms().addAll(
                new Translate(width, -height, 0),
                new Rotate(180, Rotate.Z_AXIS),
                new Translate(-width, -height, 0)
        );

        Group lightning = new Group(wing1, wing2);
        lightning.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        getChildren().add(lightning);
    }
}