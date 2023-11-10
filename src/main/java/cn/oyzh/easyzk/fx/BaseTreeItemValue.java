package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.FXHBox;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Slf4j
public abstract class BaseTreeItemValue extends FXHBox {

    {
        this.setCursor(Cursor.HAND);
    }

    /**
     * 获取节点名称组件
     *
     * @return 节点名称组件
     */
    public Text nameText() {
        if (this.getChild(1) instanceof FXText text) {
            return text;
        }
        return null;
    }

    public Node graphic() {
        return this.getChild(0);
    }

    public BaseTreeItemValue graphic(Node node) {
        this.setChild(0, node);
        node.maxHeight(15);
        HBox.setMargin(node, new Insets(0, 3, 0, 0));
        return this;
    }

    public void flushName() {
        FXText text = new FXText(this.name());
        text.setFontSize(11);
        this.setChild(1, text);
    }

    public abstract String name();

    public abstract boolean flushGraphic();

    public abstract void flushGraphicColor();
}
