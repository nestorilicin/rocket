package rs.etf.nestor.rocket.object;

import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.Objects;

public class LaunchPad extends SpaceObject {
    private final PhongMaterial bulb;
    private PointLight[] lights;
    private Sphere[] light_spheres;
    private boolean lightsOn = false;
    private final LightBase defaultLight;

    public LaunchPad(Spacecraft spacecraft, LightBase light) {
        this.defaultLight = light;
        Image illumin = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("bulb.png")));
        bulb = new PhongMaterial();
        bulb.setSelfIlluminationMap(illumin);
        MeshView platform = getPlatform();
        Group ramp = getRamp();
        Group lightPoles = getLights();
        ramp.setTranslateY(150);
        platform.setTranslateY(-25);
        lightPoles.setTranslateY(-25);
        for (int i = 0; i < 3; i++) {
            lights[i].getScope().addAll(platform, ramp, lightPoles, spacecraft);
        }
        toggleLight();
        getChildren().addAll(platform, ramp);
        getChildren().add(lightPoles);

        getTransforms().add(new Translate(0, 0, -200));
    }

    public void toggleLight() {
        if (lightsOn) {
            // ugasi
            for (int i = 0; i < 3; i++) {
                light_spheres[i].setMaterial(null);
                //lights[i].getScope().removeAll(platform, ramp,  lightPoles);
                lights[i].setLightOn(false);
                defaultLight.setLightOn(true);
            }
        } else {
            // upali
            for (int i = 0; i < 3; i++) {
                light_spheres[i].setMaterial(bulb);
                //lights[i].getScope().addAll(platform, ramp,  lightPoles);
                lights[i].setLightOn(true);
                defaultLight.setLightOn(false);
            }
        }
        lightsOn = !lightsOn;
    }

    private Group getLights() {
        Group group = new Group();
        lights = new PointLight[3];
        light_spheres = new Sphere[3];

        Image image = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("bulb.png")));
        PhongMaterial bulbMaterial = new PhongMaterial();
        bulbMaterial.setSelfIlluminationMap(image);

        Box pole1 = new Box(5, 5, 150);
        pole1.getTransforms().addAll(
                new Rotate(180, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );

        Box pole2 = new Box(5, 5, 150);
        pole2.getTransforms().addAll(
                new Rotate(270, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );

        Box pole3 = new Box(5, 5, 150);
        pole3.getTransforms().addAll(
                new Rotate(360, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );

        Sphere bulb1 = new Sphere(15);
        bulb1.setMaterial(null);
        bulb1.getTransforms().addAll(
                new Rotate(180, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 150.0)
        );

        Sphere bulb2 = new Sphere(15);
        bulb2.setMaterial(null);
        bulb2.getTransforms().addAll(
                new Rotate(270, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 150.0)
        );

        Sphere bulb3 = new Sphere(15);
        bulb3.setMaterial(null);
        bulb3.getTransforms().addAll(
                new Rotate(360, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 150.0)
        );

        PointLight light1 = new PointLight(Color.WHITE);
        light1.getTransforms().addAll(
                new Rotate(180, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );

        PointLight light2 = new PointLight(Color.WHITE);
        light2.getTransforms().addAll(
                new Rotate(270, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );

        PointLight light3 = new PointLight(Color.WHITE);
        light3.getTransforms().addAll(
                new Rotate(360, Rotate.Z_AXIS),
                new Translate(800 - 1, 0.0, 75.0)
        );


        lights[0] = light1;
        lights[1] = light2;
        lights[2] = light3;

        light_spheres[0] = bulb1;
        light_spheres[1] = bulb2;
        light_spheres[2] = bulb3;

        group.getChildren().addAll(
                pole1, pole2, pole3,
                bulb1, bulb2, bulb3,
                light1, light2, light3
        );
        return group;
    }

    private MeshView getPlatform() {
        float[] points = getPoints((float) (1600 * Math.sqrt(2))); // bila je premala za samo 1600
        float[] textCoords = new float[]{
                0.5f, 0.0f,
                0.0f, 0.5f,
                1.0f, 0.5f,
                0.5f, 1.0f,
                0.5f, 0.5f};
        int[] faces = new int[]{
                0, 0,
                1, 1,
                2, 4,
                3, 0,
                4, 4,
                5, 2,
                6, 1,
                7, 3,
                8, 4,
                9, 4,
                10, 3,
                11, 2
        };
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(points);
        triangleMesh.getTexCoords().addAll(textCoords);
        triangleMesh.getFaces().addAll(faces);
        MeshView meshView = new MeshView();
        meshView.setMesh(triangleMesh);
        Image diffuseMap = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("launchpad.jpg")));
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(diffuseMap);
        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.BACK);
        return meshView;
    }

    private Group getRamp() {
        Group ramp = new Group();
        PhongMaterial material = new PhongMaterial(Color.DARKGRAY);
        double square_side = 80.;
        double height = 500.0;
        for (int i = 0; i < 4; i++) {
            Box pillar = createBox(5, 5, height, material);
            pillar.setTranslateX((i < 2 ? 1 : -1) * square_side / 2);
            pillar.setTranslateY((i % 2 == 0 ? 1 : -1) * square_side / 2);
            pillar.setTranslateZ(height / 2);
            ramp.getChildren().add(pillar);
        }
        int levels = 11;
        for (int i = 0; i < levels; i++) {
            double z = i * 50;
            ramp.getChildren().addAll(
                    createTranslatedBox(square_side, 5, 5, 0, -square_side / 2, z, material),
                    createTranslatedBox(square_side, 5, 5, 0, square_side / 2, z, material),
                    createTranslatedBox(5, square_side, 5, square_side / 2, 0, z, material),
                    createTranslatedBox(5, square_side, 5, -square_side / 2, 0, z, material)
            );
        }
        double diagLength = Math.sqrt(square_side * square_side + 2500);
        double angle = Math.toDegrees(Math.atan(50.0 / square_side));
        for (int i = 0; i < levels - 1; i++) {
            double z = i * 50 + 25;
            ramp.getChildren().addAll(
                    createRotatedBox(diagLength, 5, 5, 0, -square_side / 2, z, angle, material),
                    createRotatedBox(diagLength, 5, 5, 0, square_side / 2, z, angle, material)
            );
        }
        for (int i = 0; i < 4; i++) {
            Box b = createBox(5, 5, 100 * Math.sqrt(2), material);
            b.getTransforms().addAll(
                    new Rotate(45 + i * 90, Rotate.Z_AXIS),
                    new Translate(50 + square_side * Math.sqrt(2) / 2, 0, 50),
                    new Rotate(-45, Rotate.Y_AXIS)
            );
            ramp.getChildren().add(b);
        }
        ramp.getChildren().addAll(
                createTranslatedBox(5, 80, 5, 0, -square_side / 2 - 40, height / 2, material),
                createTranslatedBox(5, 80, 5, 0, -square_side / 2 - 40, height / 2 - 50, material)
        );
        return ramp;
    }

    private Box createBox(double w, double h, double d, PhongMaterial material) {
        Box b = new Box(w, h, d);
        b.setMaterial(material);
        return b;
    }

    private Box createTranslatedBox(double w, double h, double d,
                                    double x, double y, double z,
                                    PhongMaterial material) {
        Box b = createBox(w, h, d, material);
        b.setTranslateX(x);
        b.setTranslateY(y);
        b.setTranslateZ(z);
        return b;
    }

    private Box createRotatedBox(double w, double h, double d,
                                 double x, double y, double z,
                                 double angle,
                                 PhongMaterial material) {
        Box b = createTranslatedBox(w, h, d, x, y, z, material);
        b.setRotationAxis(Rotate.Y_AXIS);
        b.setRotate(angle);
        return b;
    }

    private static float[] getPoints(float d) {
        float a = (float) (d / Math.sqrt(2));
        float half_a = a / 2.f;
        return new float[]{
                0.0f, -half_a, 0.0f,
                half_a, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,

                0.0f, -half_a, 0.0f,
                0.0f, 0.0f, 0.0f,
                -half_a, 0.0f, 0.0f,

                half_a, 0.0f, 0.0f,
                0.0f, half_a, 0.0f,
                0.0f, 0.0f, 0.0f,

                0.0f, 0.0f, 0.0f,
                0.0f, half_a, 0.0f,
                -half_a, 0.0f, 0.0f
        };
    }

}
