package cn.oyzh.easyzk.tabs;

import cn.oyzh.fx.plus.controls.FXTab;
import cn.oyzh.fx.plus.util.FXUtil;

/**
 * zk基础tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKBaseTab extends FXTab {

    {
        // 加载内容
        this.loadContent();
    }

    /**
     * 刷新tab标题
     */
    public void flushTitle() {
    }

    /**
     * 刷新tab图标
     */
    public void flushGraphic() {
    }

    /**
     * 刷新tab图标颜色
     */
    public void flushGraphicColor() {
    }

    /**
     * 加载内容
     */
    protected void loadContent() {
    }

    /**
     * 关闭当前tab
     */
    protected void closeTab() {
        if (this.isClosable()) {
            FXUtil.runLater(() -> this.getTabPane().getTabs().remove(this));
        }
    }
}
