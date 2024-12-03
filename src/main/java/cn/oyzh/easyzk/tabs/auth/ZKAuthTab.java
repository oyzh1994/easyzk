package cn.oyzh.easyzk.tabs.auth;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.easyzk.controller.auth.ZKAuthAddController;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.dto.ZKAuthVO;
import cn.oyzh.easyzk.event.ZKAuthAddedEvent;
import cn.oyzh.easyzk.store.ZKAuthJdbcStore;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk认证列表tab
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKAuthTab extends DynamicTab {

    public ZKAuthTab(){
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/audit.svg", "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/auth/zkAuthTabContent.fxml";
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.authList();
    }

    /**
     * zk认证列表tab内容组件
     *
     * @author oyzh
     * @since 2023/11/03
     */
    public static class ZKAuthTabContent extends DynamicTabController {

        /**
         * 搜索词汇
         */
        @FXML
        private ClearableTextField searchKeyWord;

        /**
         * 分页组件
         */
        @FXML
        private PageBox<ZKAuth> pagePane;

        /**
         * 数据列表
         */
        @FXML
        private FlexTableView<ZKAuthVO> listTable;

        /**
         * 分页数据
         */
        private Paging<ZKAuth> pageData;

        /**
         * 认证信息储存
         */
        private final ZKAuthJdbcStore authStore = ZKAuthJdbcStore.INSTANCE;

        /**
         * 初始化数据列表
         *
         * @param pageNo 页码
         */
        private void initDataList(long pageNo) {
            if (this.pageData != null) {
                pageNo = this.pageData.fixPageNo(pageNo);
            }
            this.pageData = this.authStore.getPage(pageNo, 15, this.searchKeyWord.getText());
            this.listTable.setItem(ZKAuthVO.convert(this.pageData.dataList()));
            this.pagePane.setPaging(this.pageData);
        }

        /**
         * 首页
         */
        private void firstPage() {
            this.initDataList(0);
        }

        /**
         * 上一页
         */
        @FXML
        private void prevPage() {
            this.initDataList(this.pageData.currentPage() - 1);
        }

        /**
         * 下一页
         */
        @FXML
        private void nextPage() {
            this.initDataList(this.pageData.currentPage() + 1);
        }

        /**
         * 添加zk认证信息
         */
        @FXML
        private void add() {
            StageManager.showStage(ZKAuthAddController.class);
        }

        /**
         * 删除认证
         */
        @FXML
        private void delete() {
            ZKAuthVO auth = this.listTable.getSelectedItem();
            if (auth == null) {
                return;
            }
            if (MessageBox.confirm(I18nHelper.deleteData())) {
                this.authStore.delete(auth.getUser(), auth.getPassword());
                this.listTable.removeItem(auth);
                if (this.listTable.isItemEmpty()) {
                    this.firstPage();
                }
            }
        }

        /**
         * 复制认证
         */
        @FXML
        private void copy() {
            ZKAuthVO auth = this.listTable.getSelectedItem();
            if (auth == null) {
                return;
            }
            String data = I18nHelper.userName() + " " + auth.getUser() + System.lineSeparator()
                    + I18nHelper.password() + " " + auth.getPassword();
            ClipboardUtil.setStringAndTip(data);
        }

        /**
         * 认证新增事件
         */
        @EventSubscribe
        private void authAdded(ZKAuthAddedEvent event) {
            this.initDataList(this.pageData.currentPage());
        }

        @Override
        protected void bindListeners() {
            super.bindListeners();
            this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            super.initialize(url, resourceBundle);
            // 显示首页
            this.firstPage();
        }
    }
}
