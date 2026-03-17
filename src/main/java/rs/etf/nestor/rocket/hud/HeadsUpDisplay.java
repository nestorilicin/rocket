package rs.etf.nestor.rocket.hud;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import rs.etf.nestor.rocket.game.Main;
import rs.etf.nestor.rocket.object.Spacecraft;

public class HeadsUpDisplay {

    private SubScene scene;
    private Text coordinates;
    private Text bubblesHitText;
    private Circle player;
    private Polygon arrow;
    
    private final Spacecraft spacecraft;

    private static final double width = Main.WINDOW_WIDTH / 3.;
    private static final double height = Main.WINDOW_HEIGHT / 3.;

    public HeadsUpDisplay(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
        create();
    }

    private void create() {
        Group root = new Group();
        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        scene.setTranslateX(Main.WINDOW_WIDTH - width);
        scene.setTranslateY(Main.WINDOW_HEIGHT - height);
        Rectangle rec = new Rectangle(width, height);
        rec.setFill(Color.LIGHTGREEN);
        rec.setArcHeight(45);
        rec.setArcWidth(60);
        rec.setOpacity(0.2);
        root.getChildren().add(rec);

        coordinates = new Text();
        coordinates.setFill(Color.RED);
        coordinates.setFont(new Font(10));
        coordinates.setTranslateX(20);
        coordinates.setTranslateY(height - 40);

        bubblesHitText = new Text();
        bubblesHitText.setFill(Color.RED);
        bubblesHitText.setFont(new Font(10));
        bubblesHitText.setTranslateX(280);
        bubblesHitText.setTranslateY(height - 40);
        root.getChildren().addAll(coordinates, bubblesHitText);
        Circle c1 = new Circle(2);
        c1.setFill(Color.WHITE);
        c1.setTranslateX(width / 2.);
        c1.setTranslateY(height / 2.);
        root.getChildren().add(c1);

        Circle c2 = new Circle(40);
        c2.setFill(null);
        c2.setStroke(Color.WHITE);
        c2.setStrokeWidth(2);
        c2.setTranslateX(width / 2.);
        c2.setTranslateY(height / 2.);
        root.getChildren().add(c2);

        Circle c3 = new Circle(80);
        c3.setFill(null);
        c3.setStroke(Color.WHITE);
        c3.setStrokeWidth(2);
        c3.setTranslateX(width / 2.);
        c3.setTranslateY(height / 2.);
        root.getChildren().add(c3);

        player = new Circle(5);
        player.setTranslateX(width / 2.);
        player.setTranslateY(height / 2.);
        player.setFill(Color.RED);
        root.getChildren().add(player);

        Circle compassCircle = new Circle(33);
        compassCircle.setFill(null);
        compassCircle.setStroke(Color.RED);
        compassCircle.setStrokeWidth(3);
        compassCircle.setTranslateY(33);
        compassCircle.setTranslateX(width - 33);
        root.getChildren().add(compassCircle);
        
        arrow = new Polygon(0, -33, -3, 33, 3, 33);
        arrow.setTranslateY(33);
        arrow.setTranslateX(width - 33);
        arrow.setFill(Color.WHITE);
        root.getChildren().add(arrow);
                
    }

    public void update(int bubblesHit) {
        coordinates.setText(
                String.format("%.4f", spacecraft.getPosition().getX()) + "\n" +
                String.format("%.4f", spacecraft.getPosition().getY()) + "\n" +
                String.format("%.4f", spacecraft.getPosition().getZ())
        );
        bubblesHitText.setText(
                String.valueOf(bubblesHit)
        );
        double hudX = spacecraft.getPosition().getX() / 3000.;
        double hudY = spacecraft.getPosition().getY() / 3000.;
        player.setTranslateX(width / 2. + hudX);
        player.setTranslateY(height / 2. - hudY);
        arrow.setRotate(-spacecraft.getHorizontalAngle());
    }

    public SubScene getScene() {
        return scene;
    }

    public void toggleHide() {
        scene.setVisible(!scene.isVisible());
    }
}