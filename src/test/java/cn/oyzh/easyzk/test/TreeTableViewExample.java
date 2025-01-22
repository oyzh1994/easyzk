package cn.oyzh.easyzk.test;

import cn.oyzh.easyzk.fx.svg.glyph.ZookeeperSVGGlyph;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TreeTableViewExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        TreeTableView<Person> treeTableView = new TreeTableView<>();
        treeTableView.setOnContextMenuRequested(event -> {
            System.out.println("11111");
        });
        // 创建第一列
        TreeTableColumn<Person, String> firstNameColumn = new TreeTableColumn<>("First Name");
        firstNameColumn.setPrefWidth(150);
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().firstNameProperty());

        // 创建第二列
        TreeTableColumn<Person, String> lastNameColumn = new TreeTableColumn<>("Last Name");
        lastNameColumn.setPrefWidth(150);
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().lastNameProperty());

        // 将列添加到TreeTableView中
        treeTableView.getColumns().add(firstNameColumn);
        treeTableView.getColumns().add(lastNameColumn);

        Person person1 = new Person("aaa", "111");
        TreeItem<Person> rootItem = new TreeItem<>(person1);
        rootItem.setGraphic(new ZookeeperSVGGlyph());
        for (int i = 0; i < 10; i++) {
            Person person2 = new Person("bbb" + i, "111");
            rootItem.getChildren().add(new TreeItem<>(person2));
        }
        treeTableView.setRoot(rootItem);

        // 设置场景
        StackPane root = new StackPane();
        root.getChildren().add(treeTableView);
        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("TreeTableView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class Person {
        private final StringProperty firstName = new SimpleStringProperty();
        private final StringProperty lastName = new SimpleStringProperty();

        // 构造函数
        public Person(String firstName, String lastName) {
            setFirstName(firstName);
            setLastName(lastName);
        }

        // 获取firstName的getter和setter
        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public StringProperty firstNameProperty() {
            return firstName;
        }

        // 获取lastName的getter和setter
        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String lastName) {
            this.lastName.set(lastName);
        }

        public StringProperty lastNameProperty() {
            return lastName;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class TreeTableViewExampleMain {
        public static void main(String[] args) {
            TreeTableViewExample.main(args);
        }
    }
}
