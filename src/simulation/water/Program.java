package simulation.water;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Program extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private WaterSimulator simulator;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Pane root = new Pane();

        final Pane waterPane = new Pane();

        final TextField widthField = new TextField("50");
        widthField.setTranslateX(10);
        widthField.setTranslateY(10);
        widthField.setMaxWidth(40);

        final TextField heightField = new TextField("50");
        heightField.setTranslateX(55);
        heightField.setTranslateY(10);
        heightField.setMaxWidth(40);

        final Button generate = new Button("Generate");
        generate.setTranslateX(100);
        generate.setTranslateY(10);

        root.getChildren().addAll(waterPane, widthField, heightField, generate);

        generate.setOnMouseClicked(event -> {
            if (simulator != null) simulator.stop();
            waterPane.getChildren().clear();

            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            Water water = new Water(width, height,
                    (int)primaryStage.getWidth() / width);
            waterPane.getChildren().addAll(water.getRectangles());
            simulator = new WaterSimulator(water);
            simulator.launch();
        });

        final Scene scene = new Scene(root, 900, 900);
        scene.setOnMousePressed(event -> {
            if (simulator != null) {
                simulator.click(event.getX() / scene.getWidth(),
                        event.getY() / scene.getHeight());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (simulator != null)
            simulator.stop();
        simulator = null;
        super.stop();
    }
}
