package rs.etf.nestor.rocket.object;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import rs.etf.nestor.rocket.geometry.Vector;
import rs.etf.nestor.rocket.state.IdleState;

import java.util.Objects;

public class AltRocket extends Spacecraft {
    public AltRocket() {
        position = new Vector(0, 0, 0);

        PhongMaterial sphereMaterial = new PhongMaterial(Color.CYAN);
        PhongMaterial coneMaterial = new PhongMaterial(Color.CYAN);
        Image sphereDiffuse = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("t1.jpg")));
        Image coneDiffuse = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("t3.jpg")));
        sphereMaterial.setDiffuseMap(sphereDiffuse);
        coneMaterial.setDiffuseMap(coneDiffuse);
        // od vrha na dole
        // polulopta
        Sphere topSphere = new Sphere(10);
        topSphere.setMaterial(sphereMaterial);
        topSphere.getTransforms().add(new Translate(0, 110, 0));

        // zarubljena kupa
        Group cone1 = makeCutCone(10, 30, 50, coneMaterial);
        cone1.getTransforms().add(new Translate(0, 85, 0));

        // valjak 
        Cylinder body1 = new Cylinder(30, 60);
        body1.setMaterial(sphereMaterial);
        body1.getTransforms().add(new Translate(0, 30, 0));

        // zarubljena kupa
        Group cone2 = makeCutCone(30, 12, 100, coneMaterial);
        cone2.getTransforms().add(new Translate(0, -50, 0));

        // ispupceni valjak
        Cylinder body2 = new Cylinder(14, 15);
        body2.setMaterial(sphereMaterial);
        body2.getTransforms().add(new Translate(0, -107.5, 0));

        // zarubljena kupa - rep
        Group cone3 = makeCutCone(12, 22, 20, coneMaterial);
        cone3.getTransforms().add(new Translate(0, -125, 0));

        // valjak - dno rakete 
        Cylinder body3 = new Cylinder(22, 1);
        body3.setMaterial(sphereMaterial);
        body3.getTransforms().add(new Translate(0, -135, 0));

        // krila trouglasta
        Group wing1 = makeWing(sphereMaterial);
        Group wing2 = makeWing(sphereMaterial);
        Group wing3 = makeWing(sphereMaterial);
        
        wing1.getTransforms().addAll(new Translate(-50, -75, 0)); // desno
        wing2.getTransforms().addAll(new Scale(-1, 1, 1), new Translate(-50, -75, 0)); // levo (flip)
        wing3.getTransforms().addAll(new Rotate(90, Rotate.Y_AXIS), new Translate(-50, -75, 0)); // pozadi
        
        this.getChildren().addAll(topSphere, cone1, body1, cone2, body2, cone3, body3, wing1, wing2, wing3);

        state = new IdleState(this);
        this.getTransforms().add(new Translate(position.getX(), position.getY(), position.getZ()));

    }

    @Override
    public void update(double passed) {
        state.update(passed, pitchDirection, rollDirection, accelerationDirection);
    }

    private Group makeCutCone(float topRadius, float bottomRadius, float height, PhongMaterial material) {

        Group cone = new Group();

        int numberOfTriangles = NUM_OF_TRIANGLES;
        float halfHeight = height / 2f;

        float[] points = new float[numberOfTriangles * 2 * 3 * 3];
        float[] textCoords = {
                0.5f, 0,
                0, 1,
                1, 1
        };

        int[] faces = new int[numberOfTriangles * 2 * 3 * 2];

        for (int i = 0; i < numberOfTriangles; i++) {

            double angle1 = Math.toRadians(i);
            double angle2 = Math.toRadians(i + 1);

            float x1Top = (float) Math.cos(angle1) * topRadius;
            float y1Top = halfHeight * 1;
            float z1Top = (float) Math.sin(angle1) * topRadius;

            float x2Top = (float) Math.cos(angle2) * topRadius;
            float y2Top = halfHeight * 1;
            float z2Top = (float) Math.sin(angle2) * topRadius;

            float x1Bottom = (float) Math.cos(angle1) * bottomRadius;
            float y1Bottom = -halfHeight;
            float z1Bottom = (float) Math.sin(angle1) * bottomRadius;

            float x2Bottom = (float) Math.cos(angle2) * bottomRadius;
            float y2Bottom = -halfHeight;
            float z2Bottom = (float) Math.sin(angle2) * bottomRadius;

            int index = i * 18;
            
            points[index] = x1Top;
            points[index + 1] = y1Top;
            points[index + 2] = z1Top;

            points[index + 3] = x2Top;
            points[index + 4] = y2Top;
            points[index + 5] = z2Top;

            points[index + 6] = x1Bottom;
            points[index + 7] = y1Bottom;
            points[index + 8] = z1Bottom;
            
            points[index + 9] = x2Top;
            points[index + 10] = y2Top;
            points[index + 11] = z2Top;

            points[index + 12] = x2Bottom;
            points[index + 13] = y2Bottom;
            points[index + 14] = z2Bottom;

            points[index + 15] = x1Bottom;
            points[index + 16] = y1Bottom;
            points[index + 17] = z1Bottom;
        }

        for (int i = 0; i < numberOfTriangles * 2; i++) {
            int index = i * 6;

            faces[index] = i * 3;
            faces[index + 1] = 0;

            faces[index + 2] = i * 3 + 1;
            faces[index + 3] = 1;

            faces[index + 4] = i * 3 + 2;
            faces[index + 5] = 2;
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(textCoords);
        mesh.getFaces().addAll(faces);

        MeshView meshView = new MeshView();
        meshView.setMesh(mesh);
        meshView.setMaterial(material);
        cone.getChildren().add(meshView);

        return cone;
    }

    private Group makeWing(PhongMaterial material) {
        Group wing = new Group();

        float width = 40;   
        float height = 50;   
        float depth = 3;    

        float[] points = new float[]{
                0, 0, 0,           
                width, 0, 0,  
                width, height, 0,  
                
                width, height, depth,
                0,0, depth,
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

        MeshView wingMesh = new MeshView(mesh);
        wingMesh.setMaterial(material);
        wing.getChildren().add(wingMesh);
        

        return wing;
    }
}
