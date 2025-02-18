package cn.oyzh.easyzk.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ColoredTextInJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 TextFlow 容器
        TextFlow textFlow = new TextFlow();

        // 创建红色文本节点
        Text redText = new Text("This is red text. ");
        redText.setFill(Color.RED);

        // 创建蓝色文本节点
        Text blueText = new Text("This is blue text. ");
        blueText.setFill(Color.BLUE);

        // 将文本节点添加到 TextFlow 中
        textFlow.getChildren().addAll(redText, blueText);

        // 创建 VBox 布局容器并添加 TextFlow
        VBox vbox = new VBox(textFlow);

        // 创建场景并设置到舞台
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Colored Text in JavaFX");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class ColoredTextInJavaFXTest {
        public static void main(String[] args) {
            ColoredTextInJavaFX.main(args);
        }
    }
}