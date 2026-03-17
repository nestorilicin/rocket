package rs.etf.nestor.rocket.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import rs.etf.nestor.rocket.camera.AbstractCamera;
import rs.etf.nestor.rocket.camera.DefaultCamera;
import rs.etf.nestor.rocket.camera.OrbitCamera;
import rs.etf.nestor.rocket.camera.RearCamera;
import rs.etf.nestor.rocket.object.*;

import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {
    private static final int WINDOW_HEIGHT = 700;
    private static final int WINDOW_WIDTH = 900;

    private Group root;
    private Group mainSceneRoot;

    private Scene scene;
    private SubScene mainSubscene;

    private Spacecraft spacecraft;
    private LaunchPad launchPad;
    private List<SpaceObject> bubbles = new ArrayList<>();
    private int bubblesHit;

    private UpdateTimer timer = new UpdateTimer();

    private DefaultCamera defaultCamera = new DefaultCamera();
    private AbstractCamera camera;

    private double mouseXRight;
    private double mouseYRight;

    private SubScene selectionSubscene;
    private Group selectionGroup;
    private Spacecraft selectedRocket;

    private Text fuelText;
    
    private Text cameraCoords;
    
    private RearCamera rearCamera;
    private OrbitCamera orbitCamera;
    private void createSelectionScreen() {
        selectionGroup = new Group();
        
        Rocket rocket = new Rocket();
        AltRocket altrocket = new AltRocket();
        
        rocket.setTranslateX(200);
        rocket.setTranslateY(300);
        altrocket.setTranslateX(700);
        altrocket.setTranslateY(300);
        
        Rotate rocketInitial = new Rotate(90, Rotate.X_AXIS);
        Rotate rocketYaw = new Rotate(0, Rotate.Z_AXIS);
        Rotate rocketTilt = new Rotate(0, Rotate.Y_AXIS);
        Rotate rocketRoll = new Rotate(0, Rotate.X_AXIS);

        rocket.getTransforms().addAll(
                rocketInitial,
                rocketRoll,
                rocketTilt,
                rocketYaw
        );
        
        altrocket.getTransforms().addAll(
                rocketInitial,
                rocketRoll,
                rocketTilt,
                rocketYaw
        );
        
        selectionGroup.getChildren().addAll(rocket, altrocket);
        selectionSubscene = new SubScene(selectionGroup, WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
        selectionSubscene.setFill(Color.BLACK);
        Text rocketInfo = rocket.getRocketInfo("Rocket 1", Color.DARKGRAY);
        
        rocketInfo.setTranslateX(100);
        rocketInfo.setTranslateY(500);
        Text altrocketInfo = altrocket.getRocketInfo("Rocket 2", Color.CYAN);
        altrocketInfo.setTranslateX(600);
        altrocketInfo.setTranslateY(500);
        AnimationTimer rotateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                rocketYaw.setAngle(rocketYaw.getAngle() + 1);
                rocketTilt.setAngle(rocketTilt.getAngle() + 0.3);
                rocketRoll.setAngle(rocketRoll.getAngle() + 0.2);
            }
        };
        rotateTimer.start();
        selectionGroup.getChildren().addAll(rocketInfo, altrocketInfo);
        root.getChildren().add(selectionSubscene);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DIGIT1:
                    selectedRocket = rocket;
                    rocket.setTranslateX(rocket.getTranslateX() - 200);
                    rocket.setTranslateY(rocket.getTranslateY() - 300);
                    rocket.getTransforms().removeAll(
                            rocketInitial,
                            rocketYaw,
                            rocketTilt,
                            rocketRoll
                    );
                    startGame();
                    break;
                case DIGIT2:
                    selectedRocket = altrocket;
                    altrocket.setTranslateX(altrocket.getTranslateX() - 700);
                    altrocket.setTranslateY(altrocket.getTranslateY() - 300);
                    altrocket.getTransforms().removeAll(
                            rocketInitial,
                            rocketYaw,
                            rocketTilt,
                            rocketRoll
                    );
                    startGame();
                    break;
            }
        });
    }

    private void startGame() {
        root.getChildren().remove(selectionSubscene);
        createMainScene(selectedRocket);
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);
        scene.setOnMousePressed(this::onMousePressed);
        scene.setOnMouseDragged(this::onMouseDragged);
        scene.setOnScroll(this::onScroll);
    }

    private class UpdateTimer extends AnimationTimer {
        private long previous = 0;

        @Override
        public void handle(long now) {
            if(spacecraft.getFuel() <= 0){
                Text game_over = new Text("GAME OVER");
                game_over.setFont(new Font(30));
                game_over.setTranslateX(WINDOW_WIDTH / 2. - game_over.getBoundsInLocal().getWidth() / 2);
                game_over.setTranslateY(WINDOW_HEIGHT / 2.);
                game_over.setFill(Color.RED);
                root.getChildren().add(game_over);
            } else{
                if (previous == 0) previous = now;
                float passed = (now - previous) / 1e9f;
                spacecraft.update(passed);
                spacecraft.consumeFuel();
                fuelText.setText("Fuel: " + (int)spacecraft.getFuel());
                if(camera == rearCamera) cameraCoords.setText("cameraY = " + rearCamera.getCameraY() + "\ncameraZ = " + rearCamera.getCameraZ());
                else cameraCoords.setText("");
                camera.update();
                checkForCollisions();
                previous = now;
            }
        }
    }

    private void checkForCollisions() {
        Iterator<SpaceObject> iterator = bubbles.iterator();
        while (iterator.hasNext()) {
            SpaceObject spaceObject = iterator.next();
            if (spaceObject.getBoundsInParent().intersects(spacecraft.getBoundsInParent())) {
                positionObject(spaceObject);
                bubblesHit++;
            }
        }
    }

    private void drawBubble() {
        SpaceBubble bubble = new SpaceBubble(450, Color.LIGHTGREEN);
        positionObject(bubble);
        bubbles.add(bubble);
        mainSceneRoot.getChildren().add(bubble);
    }

    private void positionObject(SpaceObject object) {
        double x = (Math.random() < 0.5 ? 1f : -1f) * (Math.random() * 20000. + 10000);
        double y = (Math.random() < 0.5 ? 1f : -1f) * (Math.random() * 20000. + 10000);
        double z = (Math.random() < 0.5 ? 1f : -1f) * (Math.random() * 20000. + 10000);
        object.setTranslateX(x);
        object.setTranslateY(y);
        object.setTranslateZ(z);
    }

    private void setUpSpaceObjects() {
        for (int i = 0; i < 12; i++) {
            drawBubble();
        }
    }

    private void createMainScene(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
        mainSceneRoot = new Group();
        mainSubscene = new SubScene(mainSceneRoot, WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
        mainSubscene.setFill(Color.BLACK);
        
        launchPad = new LaunchPad();
        mainSceneRoot.getChildren().addAll(launchPad, spacecraft);
        
        setUpSpaceObjects();
        instantiateCameras();
        mainSubscene.setCamera(camera.getCamera());
        
        fuelText = new Text();
        fuelText.setFont(new Font(20));
        fuelText.setTranslateX(20);
        fuelText.setTranslateY(30);
        fuelText.setFill(Color.WHITESMOKE);
        cameraCoords = new Text();
        cameraCoords.setFill(Color.GREEN);
        cameraCoords.setTranslateX(20);
        cameraCoords.setTranslateY(100);
        
        root.getChildren().addAll(mainSubscene, fuelText, cameraCoords);
        timer.start();
    }

    private void instantiateCameras() {
        camera = defaultCamera;
        rearCamera = new RearCamera(spacecraft);
        orbitCamera = new OrbitCamera(spacecraft);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, true);
        createSelectionScreen();


        primaryStage.setTitle("Rocket");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case UP:
                spacecraft.setPitchDirection(-1);
                break;
            case DOWN:
                spacecraft.setPitchDirection(1);
                break;
            case LEFT:
                spacecraft.setRollDirection(-1);
                break;
            case RIGHT:
                spacecraft.setRollDirection(1);
                break;
            case C:
                spacecraft.setAccelerationDirection(1);
                break;
            case X:
                spacecraft.setAccelerationDirection(-1);
                break;
            case DIGIT1:
                camera = defaultCamera;
                mainSubscene.setCamera(camera.getCamera());
                break;
            case DIGIT2:
                camera = rearCamera;
                mainSubscene.setCamera(camera.getCamera());
                break;
            case DIGIT3:
                camera = orbitCamera;
                mainSubscene.setCamera(camera.getCamera());
                break;
            case EQUALS: // nemam plus na tastaturi TODO promeniti na odbrani
                if (camera instanceof  RearCamera) {
                    ((RearCamera) camera).increaseDistance();
                }
                break;
            case MINUS:
                if (camera instanceof  RearCamera) {
                    ((RearCamera) camera).decreaseDistance();
                }
                break;
            case PAGE_UP:
                if (camera instanceof  RearCamera) {
                    ((RearCamera) camera).increaseHeight();
                }
                break;
            case PAGE_DOWN:
                if (camera instanceof  RearCamera) {
                    ((RearCamera) camera).decreaseHeight();
                }
                break;
            case SPACE:
                spacecraft.spacePressed();
                break;
        }
    }

    private void onKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case UP:
                if (spacecraft.getPitchDirection() == -1) spacecraft.setPitchDirection(0);
                break;
            case DOWN:
                if (spacecraft.getPitchDirection() == 1) spacecraft.setPitchDirection(0);
                break;
            case LEFT:
                if (spacecraft.getRollDirection() == -1) spacecraft.setRollDirection(0);
                break;
            case RIGHT:
                if (spacecraft.getRollDirection() == 1) spacecraft.setRollDirection(0);
                break;
            case C:
                if (spacecraft.getAccelerationDirection() == 1) spacecraft.setAccelerationDirection(0);
                break;
            case X:
                if (spacecraft.getAccelerationDirection() == -1) spacecraft.setAccelerationDirection(0);
                break;
        }
    }

    private void onMousePressed(MouseEvent e) {
        
            mouseXRight = e.getSceneX();
            mouseYRight = e.getSceneY();
        
    }

    private void onMouseDragged(MouseEvent e) {
        if (camera == defaultCamera) {
            if (e.getButton().ordinal() == 3) {
                defaultCamera.updatePosition(e.getSceneX() - mouseXRight, e.getSceneY() - mouseYRight);
            }
        } else if(camera == orbitCamera){
            if(e.getButton().ordinal() == 1){
                orbitCamera.rotate(e.getSceneX() - mouseXRight, e.getSceneY() - mouseYRight);
            }
        }
        mouseXRight = e.getSceneX();
        mouseYRight = e.getSceneY();
    }

    private void onScroll(ScrollEvent e) {
        if(camera instanceof OrbitCamera){
            ((OrbitCamera) camera).zoom(-e.getDeltaY());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
