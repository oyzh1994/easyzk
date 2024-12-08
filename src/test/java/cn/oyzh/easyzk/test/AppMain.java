package cn.oyzh.easyzk.test;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeView;
import cn.oyzh.common.thread.ExecutorUtil;
import cn.oyzh.common.util.ResourceUtil;
import cn.oyzh.common.util.SystemUtil;
import cn.oyzh.fx.gui.page.PagePane;
import cn.oyzh.fx.gui.text.area.MsgTextArea;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author oyzh
 * @since 2022/5/18
 */
public class AppMain extends Application {

    private EventType type = new EventType(Event.ANY, "test11");

    public static void main(String[] args) {
        launch(AppMain.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // test1(stage);
        // test2(stage);
        // test3();
        // test4();
        // test5();
        // test6(stage);
        // test7(stage);
        // test8(stage);
        // test9(stage);
//        test10(stage);
//        test11(stage);
//        test12(stage);
//        test13(stage);
        // test14(stage);
        // test15(stage);
        // test16(stage);
        // test17(stage);
        // test18(stage);
        // test19(stage);
        // test20(stage);
        // test21(stage);
        // test22(stage);
        // test23(stage);
        // test24(stage);
        // test25(stage);
    }

    private void test1(Stage stage) {
        stage.addEventHandler(type, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                System.out.println("111111111111111");
            }
        });

        stage.addEventFilter(type, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                System.out.println("22222222222222222");
            }
        });
        // EventUtil.fireEvent(new Event(type));


        stage.fireEvent(new Event("2222", stage, type));
    }

    private void test2(Stage stage) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M100 500l100 100h113q0 47 5 100h-218l100 100h135q37 167 112 257q117 141 297 141q242 0 354 -189q60 -103 66 -209h-181q0 55 -25.5 99t-63.5 68t-75 36.5t-67 12.5q-24 0 -52.5 -10t-62.5 -32t-65.5 -67t-50.5 -107h379l-100 -100h-300q-6 -46 -6 -100h406l-100 -100 h-300q9 -74 33 -132t52.5 -91t62 -54.5t59 -29t46.5 -7.5q29 0 66 13t75 37t63.5 67.5t25.5 96.5h174q-31 -172 -128 -278q-107 -117 -274 -117q-205 0 -324 158q-36 46 -69 131.5t-45 205.5h-217z");
        SVGPath svgPath1 = new SVGPath();
        svgPath1.setContent("M563.8 512l262.5-312.9c4.4-5.2 0.7-13.1-6.1-13.1h-79.8c-4.7 0-9.2 2.1-12.3 5.7L511.6 449.8 295.1 191.7c-3-3.6-7.5-5.7-12.3-5.7H203c-6.8 0-10.5 7.9-6.1 13.1L459.4 512 196.9 824.9c-4.4 5.2-0.7 13.1 6.1 13.1h79.8c4.7 0 9.2-2.1 12.3-5.7l216.5-258.1 216.5 258.1c3 3.6 7.5 5.7 12.3 5.7h79.8c6.8 0 10.5-7.9 6.1-13.1L563.8 512z");
        // svgPath.setContent("M576 64H448v384H64v128h384v384h128V576h384V448H576z");

        // svgPath.prefHeight(20);
        // svgPath.prefWidth(20);
        // svgPath.maxHeight(20);
        // svgPath.maxWidth(20);
        Region region = new Region();

        // region.setCursor(JFXCursors.NOT_ALLOW);
        region.setShape(svgPath1);
        // region.setMinSize(20, 20);
        region.setMaxWidth(10);
        region.setPrefWidth(10);
        // region.setMaxSize(10, 10);
        // region.setPrefSize(10, 10);
        region.setStyle("-fx-background-color: black;");

        Button button = new Button();
        button.setShape(svgPath);
        // button.setMinSize(40, 40);
        button.setMaxSize(40, 40);
        button.setPrefSize(40, 40);
        button.setCursor(Cursor.HAND);
        button.setStyle("-fx-background-color: black;");

        Button button1 = new Button();
        button1.setShape(svgPath1);
        button1.setMaxSize(10, 10);
        button1.setPrefSize(10, 10);
        button1.setCursor(Cursor.HAND);
        button1.setStyle("-fx-background-color: black;");

        HBox group = new HBox(region, button, button1);
        stage.setScene(new Scene(group, 600, 300));
        stage.show();
    }

    // private void test3() throws InterruptedException {
    //     FXToastUtil.ok("111");
    // }
    //
    // private void test4() throws InterruptedException {
    //     FXToastUtil.warn("222");
    // }
    //
    // private void test5() throws InterruptedException {
    //     FXToastUtil.question("333");
    // }

    private void test6(Stage stage) throws InterruptedException {
        HBox group = new HBox(new ImageView("loading.gif"));
        stage.setScene(new Scene(group, 600, 300));
        stage.show();
    }

    // private void test7(Stage stage) throws InterruptedException {
    //    Button button = new Button(" click me");
    //    //HBox group = new HBox( button);
    //    HBox group = new HBox(new ImageView("loading.gif"), button);
    //    stage.setScene(new Scene(group, 600, 300));
    //    stage.show();
    //
    //
    //    long start = System.currentTimeMillis();
    //    GIFLoading loading = new GIFLoading();
    //    //loading.setOnShowing(new EventHandler<WindowEvent>() {
    //    //    @Override
    //    //    public void handle(WindowEvent windowEvent) {
    //    //        long end = System.currentTimeMillis();
    //    //        System.out.println(end - start);
    //    //
    //    //        //stage.notify();
    //    //    }
    //    //});
    //    //loading.title("111222");
    //    //loading.show();
    //
    //    //loading.show();
    //    //stage.wait();
    //
    //
    //    new Timer().schedule(new TimerTask() {
    //        @Override
    //        public void run() {
    //            FXUtil.runLater((loading::close));
    //        }
    //    }, 3000);
    //}

    // private void test8(Stage stage) throws InterruptedException {
    //    SwitchButton switchButton = new SwitchButton();
    //    //switchButton.setNoText(true);
    //    stage.setScene(new Scene(switchButton, 600, 300));
    //    stage.show();
    //}

    private void test9(Stage stage) {
        PagePane<String> pagePane = new PagePane<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("data" + i);
        }
        // pagePane.setPrevClicked(() -> new PageInfo<>(list));
        // pagePane.setNextClicked(() -> new PageInfo<>(list));
        stage.setScene(new Scene(pagePane, 600, 300));
        stage.show();
    }

    // private void test10(Stage stage) {
    //    Loading<?> loading = new SVGLoading();
    //    //Loading<?> loading = new GIFLoading();
    //    loading.autoClose(true);
    //    stage.setScene(new Scene(new PagePane<>(), 600, 600));
    //    loading.showCenter(stage, () -> {
    //        for (int i = 0; i < 15; i++) {
    //            try {
    //                Thread.sleep(1000);
    //                System.out.println(i);
    //            } catch (InterruptedException e) {
    //                throw new RuntimeException(e);
    //            }
    //        }
    //    });
    //    stage.show();
    //}

    private void test11(Stage stage) throws IOException {
        URL url = ResourceUtil.getResource("/test.fxml");
        Parent parent = FXMLLoaderExt.load(url);
        stage.setScene(new Scene(parent, 400, 600));
        stage.show();
    }

    private void test12(Stage stage) {
        ZKConnectTreeView treeView = new ZKConnectTreeView();

        // TreeItem<String> root = new TreeItem<>();
        // root.setValue("zk连接列表");
        //
        // treeView.setRoot(root);

        // ZKInfo zkInfo = new ZKInfo();
        // zkInfo.setName("test1");
        // ZKInfoItem treeItem = new ZKInfoItem(zkInfo);
        //
        //
        // treeView.getRoot().getChildren().add(treeItem);

        // treeView.loadConnects();

        stage.setScene(new Scene(treeView, 400, 600));
        stage.show();
    }

//    private void test13(Stage stage) {
//
//        ToggleSwitch toggleSwitch = new ToggleSwitch("测试");
//
//        toggleSwitch.setMaxWidth(10);
//        toggleSwitch.setPrefWidth(10);
//        toggleSwitch.setStyle(toggleSwitch.getStyle() + ";-fx-content-display: right");
//
//
//        stage.setScene(new Scene(toggleSwitch, 400, 600));
//        stage.show();
//    }

//    private void test14(Stage stage) {
//        SearchableComboBox<String> comboBox = new SearchableComboBox<>();
//        comboBox.getItems().add("123456789");
//        TextField field = TextFields.createClearableTextField();
//        CustomTextField textField = new CustomTextField();
//        TextField inputField = new TextField();
//        stage.setScene(new Scene(new HBox(comboBox, field, textField), 400, 600));
//        stage.show();
//    }

//     private void test15(Stage stage) {
//         JMetro jMetro = new JMetro(Style.LIGHT);
//
//         ComboBox<String> comboBox = new ComboBox<>();
//         comboBox.getItems().add("111");
//         comboBox.getItems().add("222");
//         comboBox.getItems().add("333");
//         TextField field = new TextField();
//         field.setText("aaaa");
//         TextField textField = new TextField();
//         textField.setText("bbbb");
//
//
//         TreeView<String> treeView = new TreeView<>();
//
//         //treeView.setRoot(new TreeItem<String>("测试"));
//         //treeView.getRoot().getChildren().add(new TreeItem<>("测试2"));
//         //treeView.getRoot().getChildren().add(new TreeItem<>("测试3"));
//         //treeView.getRoot().getChildren().add(new TreeItem<>("测试4"));
//
//         ZKTreeView treeView1 = new ZKTreeView();
//         FlexTreeView treeView2 = new FlexTreeView();
//
//         ZKRootTreeItem zkRootTreeItem = new ZKRootTreeItem(treeView1);
// //        ZKRootTreeItem zkRootTreeItem = new ZKRootTreeItem(treeView1);
//
//         ZKInfo zkInfo1 = new ZKInfo();
//         ZKInfo zkInfo2 = new ZKInfo();
//         ZKInfo zkInfo3 = new ZKInfo();
//         zkInfo1.setName("连接1");
//         zkInfo2.setName("连接2");
//         zkInfo3.setName("连接2");
//
//         zkRootTreeItem.getChildren().add(new ZKConnectTreeItem(zkInfo1, treeView1));
//         zkRootTreeItem.getChildren().add(new ZKConnectTreeItem(zkInfo2, treeView1));
//         zkRootTreeItem.getChildren().add(new ZKConnectTreeItem(zkInfo3, treeView1));
//
//         treeView.setRoot(zkRootTreeItem);
//         treeView1.setRoot(zkRootTreeItem);
//         treeView2.setRoot(zkRootTreeItem);
//
//         HBox box = new HBox(comboBox, field, textField, treeView, treeView1, treeView2);
//
//         Scene scene = new Scene(box, 400, 600);
//         box.getStyleClass().add(JMetroStyleClass.BACKGROUND);
//         jMetro.setScene(scene);
//
//         stage.setScene(scene);
//         stage.show();
//     }

    private void test16(Stage stage) {
        Pagination pagination = new Pagination(100, 1);
        pagination.setMinWidth(300);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setStyle("-fx-page-information-visible:false");
        Scene scene = new Scene(new HBox(pagination), 400, 600);
        stage.setScene(scene);
        stage.show();
    }

    // private void test17(Stage stage) throws InterruptedException, InvocationTargetException {
    //    SwingNode swingNode = new SwingNode();
    //
    //    JTextArea textArea = new JTextArea();
    //    textArea.setLineWrap(true);
    //    //textArea.setText("123456789");
    //    textArea.setSize(600, 600);
    //    //textArea.setSelectionStart(0);
    //    //textArea.setSelectionEnd(1);
    //    textArea.setMinimumSize(new Dimension(600,600));
    //    textArea.setMaximumSize(new Dimension(600,600));
    //
    //
    //    JScrollPane scrollPane = new JScrollPane();
    //    scrollPane.getViewport().add(textArea, null);
    //    scrollPane.setSize(600,600);
    //
    //    //swingNode.prefWidth(600);
    //    //swingNode.prefHeight(600);
    //
    //
    //    //SwingUtilities.invokeAndWait(()->{
    //    //    JTextArea textArea = new JTextArea();
    //    //    //textArea.setLineWrap(true);
    //    //    textArea.setText("123456789");
    //    //    //textArea.setSize(600, 600);
    //    //    //textArea.setSelectionStart(0);
    //    //    //textArea.setSelectionEnd(1);
    //    //    swingNode.setContent(textArea);
    //    //    //textArea.setVisible(true);
    //    //});
    //    swingNode.setContent(scrollPane);
    //
    //    //swingNode.minWidth(600);
    //    //swingNode.minHeight(600);
    //
    //    HBox box = new HBox(swingNode);
    //    box.setMinWidth(600);
    //    box.setMinHeight(600);
    //
    //    Scene scene = new Scene(box);
    //    stage.setScene(scene);
    //    stage.setTitle("hello fx");
    //
    //
    //    //textArea.paintImmediately(textArea.getBounds());
    //
    //
    //    stage.setOnShown(event -> {
    //        //textArea.repaint();
    //        //textArea.paintImmediately(textArea.getBounds());
    //
    //        SwingUtilities.invokeLater(()->{
    //            //textArea.setText("123456789");
    //            textArea.append("1111");
    //            textArea.repaint();
    //            textArea.paintImmediately(textArea.getBounds());
    //        });
    //    });
    //
    //    stage.show();
    //    //System.out.println(textArea.getWidth());
    //    //System.out.println(textArea.getHeight());
    //
    //}

    private void test18(Stage stage) {
        MsgTextArea textArea = new MsgTextArea();
        textArea.setLineLimit(2000);
        // textArea.setBuffSize(50);
        Scene scene = new Scene(textArea, 600, 600);
        LongAdder adder = new LongAdder();
        stage.setScene(scene);
        stage.setOnShown((e) -> ExecutorUtil.start(() -> {
            for (int i = 0; i < 1000; i++) {
                adder.increment();
                textArea.appendLine("测试内容-->" + adder.longValue());
            }
            if (adder.longValue() % 10000 == 0) {
                SystemUtil.gcLater();
            }
        }, 0, 1000));
        stage.show();
    }

    // private void test19(Stage stage) {
    //
    //    SystemTray tray = SystemTray.get();
    //
    //    tray.setImage(ResourceUtil.getResource(ZKConst.ICON_PATH));
    //    tray.getMenu().asSwingComponent().setMenuLocation(500,500);
    //    JMenu menu = new JMenu();
    //    //menu.setLocation(100, 100);
    //    menu.add(new JMenuItem("test1"));
    //    menu.add(new JMenuItem("test2"));
    //    menu.add(new JMenuItem("test3"));
    //    //menu.setMenuLocation(500, 500);
    //    tray.setMenu(menu);
    //
    //}

    private void test20(Stage stage) {
        Image image = Toolkit.getDefaultToolkit().getImage(ResourceUtil.getResource(ZKConst.ICON_PATH));
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(image, "My Application");
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 点击托盘图标时弹出托盘菜单
                JPopupMenu menu = new JPopupMenu();
                JMenuItem exitItem = new JMenuItem("退出", new ImageIcon(ResourceUtil.getResource(ZKConst.ICON_PATH)));
                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                menu.add(exitItem);
                menu.setVisible(true);
                menu.setSize(300, 300);
                menu.setLocation(300, 300);
                menu.pack();
                //// 点击托盘图标时弹出托盘菜单
                // java.awt.PopupMenu menu = new  java.awt.PopupMenu();
                // java.awt.MenuItem exitItem = new  java.awt.MenuItem("Exit");
                // exitItem.addActionListener(new ActionListener() {
                //    @Override
                //    public void actionPerformed(ActionEvent e) {
                //        System.exit(0);
                //    }
                //});
                // menu.add(exitItem);
                // trayIcon.setPopupMenu(menu);
            }
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

    }


    // private void test21(Stage stage) {
    //    // 初始化托盘
    //    SystemTrayExt tray = new SystemTrayExt(ZKConst.ICON_PATH);
    //    // 设置标题
    //    tray.setTitle("test1");
    //
    //    ImageIcon icon1 = new ImageIcon(ResourceUtil.getResource("/font/desktop.svg"));
    //    ImageIcon icon2 = new ImageIcon(ResourceUtil.getResource("/font/setting.svg"));
    //    ImageIcon icon3 = new ImageIcon(ResourceUtil.getResource("/font/poweroff.svg"));
    //    // 打开主页
    //    tray.addMenuItem("打开",icon1, () -> {
    //
    //    });
    //    // 打开设置
    //    tray.addMenuItem("设置",icon2, () -> {
    //
    //    });
    //    // 退出程序
    //    tray.addMenuItem("退出", icon3, () -> {
    //    });
    //    // 鼠标事件
    //    tray.onMouseClicked(e -> {
    //        // 单击鼠标主键，显示主页
    //        if (e.getButton() == MouseEvent.BUTTON1) {
    //        }
    //    });
    //
    //    tray.show();
    //
    //}

    // private void test22(Stage stage) {
    //     // 初始化托盘
    //     FXSystemTray tray = new FXSystemTray(ZKConst.ICON_PATH);
    //     // 设置标题
    //     tray.setTitle("test1");
    //     // 打开主页
    //     tray.addMenuItem("打开", new SVGGlyph("/font/desktop.svg", "12"), () -> {
    //
    //     });
    //     // 打开设置
    //     tray.addMenuItem("设置", new SVGGlyph("/font/setting.svg", "12"), () -> {
    //
    //     });
    //     // 退出程序
    //     tray.addMenuItem("退出", new SVGGlyph("/font/poweroff.svg", "12"), () -> {
    //     });
    //     // 鼠标事件
    //     tray.onMouseClicked(e -> {
    //         // 单击鼠标主键，显示主页
    //         if (e.getButton() == MouseEvent.BUTTON1) {
    //         }
    //     });
    //
    //     tray.show();
    //
    // }

    private void test23(Stage stage) {
        SplitPane pane = new SplitPane();
        pane.setMinWidth(600);
        pane.setMinHeight(600);

        pane.getItems().add(new Button("test1"));
        pane.getItems().add(new Button("test2"));
        pane.setOrientation(Orientation.VERTICAL);

        stage.setScene(new Scene(pane));
        stage.show();
    }

    // private void test24(Stage stage) {
    //     VBox pane = new VBox();
    //     pane.setMinWidth(600);
    //     pane.setMinHeight(600);
    //
    //     String xx = """
    //             {
    //               "name": "Alice",
    //               "age": "20",
    //               "hobbies": ["reading", "writing", "coding"],
    //               "sub" : {
    //                     "name": "Alice",
    //                     "age": "20",
    //                     "hobbies": ["reading", "writing", "coding"]
    //               }
    //             }
    //             """;
    //
    //     ZKRichDataTextArea textArea = new ZKRichDataTextArea();
    //     textArea.setFlexWidth("100%");
    //     textArea.setFlexHeight("100%");
    //     // textArea.setShowType((byte) 1);
    //     textArea.showData((byte) 1, xx);
    //
    //
    //     // FlexVirtualizedScrollPane<BaseRichTextArea> sp = new FlexVirtualizedScrollPane<>(textArea);
    //     //
    //     //
    //     // sp.setOnScroll(scrollEvent -> {
    //     //     System.out.println("----------");
    //     // });
    //     // textArea.setOnScroll(scrollEvent -> {
    //     //     System.out.println("+++++++++++++++++");
    //     // });
    //     // textArea.prefWidthProperty().bind(sp.prefWidthProperty());
    //     // textArea.prefHeightProperty().bind(sp.prefHeightProperty());
    //     // sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    //     // sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    //     // sp.setFlexWidth("100%");
    //     // sp.setFlexHeight("100%");
    //     //
    //     // pane.getChildren().add(sp);
    //
    //     pane.setOnScroll(scrollEvent -> {
    //         System.out.println("11111");
    //     });
    //     // pane.getChildren().add(textArea);
    //     stage.setScene(new Scene(pane));
    //     stage.getScene().setOnScroll(scrollEvent -> {
    //         System.out.println("2222");
    //     });
    //     stage.show();
    // }

    private void test25(Stage stage) {
        TextArea textArea = new TextArea();
        VBox vbox = new VBox(textArea);
        textArea.setText("This is <span style=\"color:red;\">red</span> text.");

        // String styleString = "`Hello`{color:red;} `World`{color:blue;}";
        // textArea.setText(styleString);
        stage.setScene(new Scene(vbox));
        stage.show();
    }


}
