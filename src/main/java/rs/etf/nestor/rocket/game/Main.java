package rs.etf.nestor.rocket.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import rs.etf.nestor.rocket.camera.AbstractCamera;
import rs.etf.nestor.rocket.camera.DefaultCamera;
import rs.etf.nestor.rocket.camera.OrbitCamera;
import rs.etf.nestor.rocket.camera.RearCamera;
import rs.etf.nestor.rocket.hud.HeadsUpDisplay;
import rs.etf.nestor.rocket.hud.VerticalView;
import rs.etf.nestor.rocket.object.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static final int WINDOW_HEIGHT = 700;
    public static final int WINDOW_WIDTH = 900;
    private static int remainingTime = 90;
    private final static int TIME_ON_BUBBLE_HIT_INCREMENT = 10;
    private final static int CANISTER_FUEL_INCREMENT = 100;
    private final static int SLOW_TIMER_DURATION = 5;
    private final static int POWERUP_TIMER_DURATION = 5;
    private final static int POWERUP_MAX_SPEED_INCREMENT = 1500;
    private Group root;
    private Group mainSceneRoot;

    private Scene scene;
    private SubScene mainSubscene;

    private Spacecraft spacecraft;
    private LaunchPad launchPad;
    private final List<SpaceObject> bubbles = new ArrayList<>();
    private int bubblesHit;

    private final List<SpaceObject> canisters = new ArrayList<>();
    private final UpdateTimer timer = new UpdateTimer();

    private final List<SpaceObject> meteors = new ArrayList<>();
    private boolean slowed = false;
    private int slowTimer = 0;

    private final List<SpaceObject> powerups = new ArrayList<>();
    private boolean poweredUp = false;
    private int powerUpTimer = 0;
    private final DefaultCamera defaultCamera = new DefaultCamera();
    private AbstractCamera camera;

    private double mouseXRight;
    private double mouseYRight;

    private SubScene selectionSubscene;
    private Spacecraft selectedRocket;

    private Text fuelText;
    private Text timeText;
    private double secondCounter = 0;
    private int totalGameTime = 0;
    private RearCamera rearCamera;
    private OrbitCamera orbitCamera;
    private HeadsUpDisplay hud;
    private VerticalView verticalHud;
    private LightBase defaultLight;

    private Text maxSpeedText;
    private Text currSpeedText;
    private Text minSpeedText;
    private Text powerUpsText;
    private int powerUpCounter = 0;

    private void createSelectionScreen() {
        Group selectionGroup = new Group();

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

        rocket.getTransforms().addAll(rocketInitial, rocketRoll, rocketTilt, rocketYaw);

        altrocket.getTransforms().addAll(rocketInitial, rocketRoll, rocketTilt, rocketYaw);

        selectionGroup.getChildren().addAll(rocket, altrocket);
        selectionSubscene = new SubScene(selectionGroup, WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
        selectionSubscene.setFill(Color.BLACK);
        Text rocketInfo = rocket.getRocketInfo("Rocket 1", Color.DARKGRAY);

        rocketInfo.setTranslateX(100);
        rocketInfo.setTranslateY(500);
        Text altrocketInfo = altrocket.getRocketInfo("Rocket 2", Color.TEAL);
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
                    rocket.getTransforms().removeAll(rocketInitial, rocketYaw, rocketTilt, rocketRoll);
                    startGame();
                    break;
                case DIGIT2:
                    selectedRocket = altrocket;
                    altrocket.setTranslateX(altrocket.getTranslateX() - 700);
                    altrocket.setTranslateY(altrocket.getTranslateY() - 300);
                    altrocket.getTransforms().removeAll(rocketInitial, rocketYaw, rocketTilt, rocketRoll);
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
            if (spacecraft.getFuel() <= 0 || remainingTime <= 0) {
                Text game_over = new Text("GAME OVER\nTotal game time " + totalGameTime + "\nBubbles hit " + bubblesHit);
                game_over.setTextAlignment(TextAlignment.CENTER);
                game_over.setFont(new Font(30));
                game_over.setTranslateX(WINDOW_WIDTH / 2. - game_over.getBoundsInLocal().getWidth() / 2);
                game_over.setTranslateY(WINDOW_HEIGHT / 2.);
                game_over.setFill(Color.RED);
                root.getChildren().add(game_over);
            } else {
                if (previous == 0) previous = now;
                float passed = (now - previous) / 1e9f;
                spacecraft.update(passed);
                spacecraft.consumeFuel();
                fuelText.setText("Fuel: " + (int) spacecraft.getFuel());
                secondCounter += passed;
                if (secondCounter > 1.0) {
                    secondCounter = 0;
                    remainingTime--;
                    totalGameTime++;
                    timeText.setText("Remaining time: " + remainingTime);
                    if (slowed) {
                        slowTimer--;
                        if (slowTimer <= 0) {
                            slowTimer = 0;
                            slowed = false;
                        }
                        currSpeedText.setFill(Color.RED);
                    } else {
                        currSpeedText.setFill(Color.WHITESMOKE);
                    }
                    if(poweredUp){
                        powerUpTimer--;
                        if(powerUpTimer <= 0){
                            powerUpTimer = 0;
                            poweredUp = false;
                            spacecraft.revertMaxSpeed();
                        }
                        maxSpeedText.setFill(Color.YELLOW);
                    } else{
                        maxSpeedText.setFill(Color.WHITESMOKE);
                    }
                }
                maxSpeedText.setText("Maximum speed: " + spacecraft.getMaxSpeed());
                currSpeedText.setText("Current speed: " + spacecraft.getSpeed());
                minSpeedText.setText("Minimum speed: " + spacecraft.getMinSpeed());
                hud.update(bubblesHit);
                verticalHud.update();
                camera.update();
                checkForCollisions();
                previous = now;
            }
        }
    }

    private void checkForCollisions() {
        for (SpaceObject spaceObject : bubbles) {
            if (spaceObject.getBoundsInParent().intersects(spacecraft.getBoundsInParent())) {
                positionObject(spaceObject);
                remainingTime += TIME_ON_BUBBLE_HIT_INCREMENT;
                timeText.setText("Remaining time: " + remainingTime);
                bubblesHit++;
            }
        }
        for (SpaceObject spaceObject : canisters) {
            if (spaceObject.getBoundsInParent().intersects(spacecraft.getBoundsInParent())) {
                positionObject(spaceObject);
                spacecraft.increaseFuel(CANISTER_FUEL_INCREMENT);
            }
        }
        for (SpaceObject spaceObject : meteors) {
            if (spaceObject.getBoundsInParent().intersects(spacecraft.getBoundsInParent())) {
                positionObject(spaceObject);
                slowed = true;
                slowTimer = SLOW_TIMER_DURATION;
                spacecraft.setMinSpeed();
            }
        }
        for (SpaceObject spaceObject : powerups) {
            if (spaceObject.getBoundsInParent().intersects(spacecraft.getBoundsInParent())) {
                positionObject(spaceObject);
                powerUpCounter++;
                powerUpsText.setText("PowerUps Counter: " + powerUpCounter);
            }
        }
    }

    private void drawBubble() {
        SpaceBubble bubble = new SpaceBubble(450, Color.LIGHTGREEN);
        positionObject(bubble);
        bubbles.add(bubble);
        mainSceneRoot.getChildren().add(bubble);
    }

    private void drawCanister() {
        Canister canister = new Canister(300, Color.RED);
        positionObject(canister);
        canisters.add(canister);
        mainSceneRoot.getChildren().add(canister);
    }

    private void drawMeteor() {
        SpaceMeteor meteor = new SpaceMeteor(300, Color.SADDLEBROWN);
        positionObject(meteor);
        meteors.add(meteor);
        mainSceneRoot.getChildren().add(meteor);
    }

    private void drawPowerup() {
        SpacePowerUp powerup = new SpacePowerUp(300, Color.YELLOW);
        positionObject(powerup);
        powerups.add(powerup);
        mainSceneRoot.getChildren().add(powerup);
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
            drawMeteor();
            drawPowerup();
            if (i % 2 == 0) {
                drawCanister();
            }
        }
    }

    private void createMainScene(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
        mainSceneRoot = new Group();
        mainSubscene = new SubScene(mainSceneRoot, WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
        mainSubscene.setFill(Color.BLACK);
        instantiateCameras();
        launchPad = new LaunchPad(spacecraft, defaultLight);
        mainSceneRoot.getChildren().addAll(launchPad, spacecraft);

        setUpSpaceObjects();

        mainSubscene.setCamera(camera.getCamera());

        fuelText = new Text();
        fuelText.setFont(new Font(20));
        fuelText.setTranslateX(20);
        fuelText.setTranslateY(30);
        fuelText.setFill(Color.WHITESMOKE);

        timeText = new Text();
        timeText.setFont(new Font(20));
        timeText.setTranslateY(30);
        timeText.setFill(Color.WHITESMOKE);
        timeText.setText("Remaining time: " + remainingTime);
        timeText.setTranslateX(WINDOW_WIDTH / 2. - timeText.getBoundsInLocal().getWidth() / 2);

        maxSpeedText = new Text();
        maxSpeedText.setFont(new Font(20));
        maxSpeedText.setTranslateX(20);
        maxSpeedText.setTranslateY(60);
        maxSpeedText.setFill(Color.WHITESMOKE);
        maxSpeedText.setText("Maximum speed: " + spacecraft.getMaxSpeed());
        
        currSpeedText = new Text();
        currSpeedText.setFont(new Font(20));
        currSpeedText.setTranslateX(20);
        currSpeedText.setTranslateY(90);
        currSpeedText.setFill(Color.WHITESMOKE);
        currSpeedText.setText("Current speed: " + spacecraft.getSpeed());
        
        minSpeedText = new Text();
        minSpeedText.setFont(new Font(20));
        minSpeedText.setTranslateX(20);
        minSpeedText.setTranslateY(120);
        minSpeedText.setFill(Color.WHITESMOKE);
        minSpeedText.setText("Minimum speed: " + spacecraft.getMinSpeed());
        
        powerUpsText = new Text();
        powerUpsText.setFont(new Font(20));
        powerUpsText.setTranslateX(20);
        powerUpsText.setTranslateY(150);
        powerUpsText.setFill(Color.WHITESMOKE);
        powerUpsText.setText("PowerUps Counter: " + powerUpCounter);
        hud = new HeadsUpDisplay(spacecraft);
        verticalHud = new VerticalView(spacecraft);
        root.getChildren().addAll(mainSubscene, fuelText, timeText, hud.getScene(), verticalHud.getScene(), maxSpeedText, currSpeedText, minSpeedText, powerUpsText);
        timer.start();
    }

    private void instantiateCameras() {
        defaultLight = new PointLight();
        camera = defaultCamera;
        rearCamera = new RearCamera(spacecraft);
        orbitCamera = new OrbitCamera(spacecraft);
        mainSceneRoot.getChildren().add(defaultLight);
        bindLightToCamera(camera);
    }

    private void bindLightToCamera(AbstractCamera cam) {
        defaultLight.translateXProperty().unbind();
        defaultLight.translateYProperty().unbind();
        defaultLight.translateZProperty().unbind();
        defaultLight.translateXProperty().bind(cam.getCamera().localToSceneTransformProperty().map(Transform::getTx));
        defaultLight.translateYProperty().bind(cam.getCamera().localToSceneTransformProperty().map(Transform::getTy));
        defaultLight.translateZProperty().bind(cam.getCamera().localToSceneTransformProperty().map(Transform::getTz));
    }

    @Override
    public void start(Stage primaryStage) {
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
                if (!slowed) spacecraft.setAccelerationDirection(1);
                break;
            case X:
                if (!slowed) spacecraft.setAccelerationDirection(-1);
                break;
            case H:
                hud.toggleHide();
                verticalHud.toggleHide();
                break;
            case L:
                launchPad.toggleLight();
                break;
            case V:
                if(powerUpCounter > 0){
                    powerUpCounter--;
                    powerUpsText.setText("PowerUps Counter: " + powerUpCounter);
                    powerUpTimer = POWERUP_TIMER_DURATION;
                    poweredUp = true;
                    spacecraft.changeMaxSpeed(POWERUP_MAX_SPEED_INCREMENT);
                }
                break;
            case DIGIT1:
                camera = defaultCamera;
                mainSubscene.setCamera(camera.getCamera());
                bindLightToCamera(camera);
                break;
            case DIGIT2:
                camera = rearCamera;
                mainSubscene.setCamera(camera.getCamera());
                bindLightToCamera(camera);
                break;
            case DIGIT3:
                camera = orbitCamera;
                mainSubscene.setCamera(camera.getCamera());
                bindLightToCamera(camera);
                break;
            case EQUALS: // nemam plus na tastaturi TODO promeniti na odbrani
                if (camera instanceof RearCamera) {
                    ((RearCamera) camera).increaseDistance();
                }
                break;
            case MINUS:
                if (camera instanceof RearCamera) {
                    ((RearCamera) camera).decreaseDistance();
                }
                break;
            case PAGE_UP:
                if (camera instanceof RearCamera) {
                    ((RearCamera) camera).increaseHeight();
                }
                break;
            case PAGE_DOWN:
                if (camera instanceof RearCamera) {
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
        } else if (camera == orbitCamera) {
            if (e.getButton().ordinal() == 1) {
                orbitCamera.rotate(e.getSceneX() - mouseXRight, e.getSceneY() - mouseYRight);
            }
        }
        mouseXRight = e.getSceneX();
        mouseYRight = e.getSceneY();
    }

    private void onScroll(ScrollEvent e) {
        if (camera instanceof OrbitCamera) {
            ((OrbitCamera) camera).zoom(-e.getDeltaY());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
