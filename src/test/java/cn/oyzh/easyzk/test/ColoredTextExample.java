package cn.oyzh.easyzk.test;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ColoredTextExample extends JFrame {
    public ColoredTextExample() {
        // 设置窗口标题
        setTitle("Colored Text Example");
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口布局
        setLayout(new BorderLayout());

        // 创建 JTextPane 组件
        JTextPane textPane = new JTextPane();
        // 获取 StyledDocument 对象
        StyledDocument doc = textPane.getStyledDocument();

        // 创建 StyleContext 对象
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        // 创建默认样式
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);

        // 定义红色文本样式
        Style redStyle = styleContext.addStyle("RedStyle", defaultStyle);
        StyleConstants.setForeground(redStyle, Color.RED);

        // 定义蓝色文本样式
        Style blueStyle = styleContext.addStyle("BlueStyle", defaultStyle);
        StyleConstants.setForeground(blueStyle, Color.BLUE);

        try {
            // 插入红色文本
            String text1 = "This is red text. ";
            int len1 = doc.getLength();
            doc.insertString(doc.getLength(), text1, null);
            doc.setCharacterAttributes(len1, text1.length(), redStyle, true);
            String text2 = "This is blue text. ";
            int len2 = doc.getLength();
            // 插入蓝色文本
            doc.insertString(doc.getLength(), text2, null);
            doc.setCharacterAttributes(len2, text2.length(), blueStyle, true);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // 创建滚动面板并添加 JTextPane
        JScrollPane scrollPane = new JScrollPane(textPane);
        // 将滚动面板添加到窗口中心
        add(scrollPane, BorderLayout.CENTER);


        // 设置窗口大小
        setSize(400, 300);
        // 显示窗口
        setVisible(true);
    }

    public static void main(String[] args) {
        // 在事件调度线程中创建和显示 GUI
        SwingUtilities.invokeLater(ColoredTextExample::new);
    }
}