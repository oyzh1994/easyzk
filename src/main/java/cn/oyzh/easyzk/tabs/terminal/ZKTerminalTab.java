package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.tabs.ZKBaseTab;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalTab extends ZKBaseTab {

    /**
     * 内容controller
     */
    private ZKTerminalTabContentController contentController;

    {
        this.setClosable(true);
        this.loadContent();
        // 关闭连接
        this.setOnCloseRequest(event -> ZKConnectUtil.close(this.contentController.client(), true));
    }


    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/terminal/zkTerminalTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/code library.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param info redis信息
     */
    public void init(ZKInfo info) {
        try {
            if (info == null) {
                info = new ZKInfo();
                info.setName("未命名连接");
            }
            // 设置文本
            this.setText(info.getName());
            // 刷新图标
            this.flushGraphic();
            // 初始化redis连接
            this.contentController.client(new ZKClient(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.contentController.client();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.contentController.info();
    }
}
