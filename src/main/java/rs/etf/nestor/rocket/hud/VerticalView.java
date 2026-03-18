package rs.etf.nestor.rocket.hud;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import rs.etf.nestor.rocket.game.Main;
import rs.etf.nestor.rocket.object.Spacecraft;

public class VerticalView {
    private SubScene scene;
    private Circle player;
    
    private final Spacecraft spacecraft;

    private static final double width = Main.WINDOW_HEIGHT / 6.;
    private static final double height = Main.WINDOW_HEIGHT / 3.;

    private final Text boundUp = new Text();
    private final Text boundDown = new Text();
    public VerticalView(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
        create();
    }

    private void create() {
        Group root = new Group();
        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        scene.setTranslateX(Main.WINDOW_WIDTH - width);
        scene.setTranslateY(Main.WINDOW_HEIGHT - height - height);
        Rectangle rec = new Rectangle(width, height);
        rec.setFill(Color.color(0.44, 0.54, 0.23)); // color picker inspect element
        rec.setStroke(Color.BLACK);
        root.getChildren().add(rec);
        
        Line line = new Line(width / 2., 20, width / 2., height - 20);
        line.setStroke(Color.RED);
        root.getChildren().add(line);
        
        Rectangle base =  new Rectangle(20, 5);
        base.setArcWidth(45);
        base.setArcHeight(60);
        base.setFill(Color.RED);
        base.setTranslateX(width / 2 - base.getWidth() / 2);
        base.setTranslateY(height / 2 - base.getHeight() / 2);
        root.getChildren().add(base);
        player = new Circle(7.5);
        player.setTranslateX(width / 2.);
        player.setTranslateY(height / 2.);
        player.setFill(Color.RED);
        root.getChildren().add(player);
        
        boundUp.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        boundUp.setFill(Color.RED);
        boundDown.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        boundDown.setFill(Color.RED);
        boundUp.setTranslateX(width / 2. + 10);
        boundUp.setTranslateY(20);
        boundDown.setTranslateX(width / 2. + 10);
        boundDown.setTranslateY(height - 20);
        root.getChildren().addAll(boundUp, boundDown);
        
    }

    public void update() {
        double worldZ = spacecraft.getPosition().getZ();
        double hudZ = worldZ / 300.;
        player.setTranslateY(height / 2. - hudZ);
        double range = (height / 2.) * 300.;
        double upper = worldZ + range;
        double lower = worldZ - range;
        boundUp.setText(String.format("+%.0f", upper));
        boundDown.setText(String.format("%.0f", lower));
        
    }

    public SubScene getScene() {
        return scene;
    }

    public void toggleHide() {
        scene.setVisible(!scene.isVisible());
    }
}
