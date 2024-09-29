package cn.oyzh.easyzk.controller;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.event.TreeChildChangedEvent;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKSearchFireEvent;
import cn.oyzh.easyzk.search.ZKSearchHandler;
import cn.oyzh.easyzk.search.ZKSearchHistoryPopup;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore2;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.search.SearchResult;
import cn.oyzh.fx.plus.controls.search.SearchTextField;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.util.Objects;

/**
 * zk搜索子组件
 *
 * @author oyzh
 * @since 2023/4/11
 */
//@Lazy
//@Component
public class SearchController extends SubStageController {

    /**
     * 搜索-搜索词
     */
    @FXML
    private SearchTextField searchKW;

    /**
     * 搜索中标志位
     */
    private boolean searching;

    /**
     * 搜索-替换词
     */
    @FXML
    private SearchTextField replaceKW;

    /**
     * 替换中标志位
     */
    private boolean replacing;

    /**
     * 搜索-主面板
     */
    @FXML
    private FlexVBox searchMain;

    /**
     * 搜索-下一个
     */
    @FXML
    private SVGGlyph searchNext;

    /**
     * 搜索-上一个
     */
    @FXML
    private SVGGlyph searchPrev;

    /**
     * 搜索-替换
     */
    @FXML
    private SVGGlyph replace;

    /**
     * 搜索-分析
     */
    @FXML
    private SVGGlyph searchAnalyse;

    /**
     * 搜索-过滤模式
     */
    @FXML
    private FXCheckBox filterMode;

    /**
     * 搜索-搜索值
     */
    @FXML
    private FXCheckBox searchData;

    /**
     * 搜索-匹配全文
     */
    @FXML
    private FXCheckBox matchFull;

    /**
     * 搜索-匹配大小写
     */
    @FXML
    private FXCheckBox matchCase;

    /**
     * 搜索-搜索路径
     */
    @FXML
    private FXCheckBox searchPath;

    /**
     * 搜索-搜索结果
     */
    @FXML
    private FlexText searchResult;

    /**
     * 搜索-替换提示
     */
    @FXML
    private FlexText replaceTips;

    // /**
    //  * zk树
    //  */
    // private ZKTreeView treeView;

    /**
     * zk主页搜索处理
     */
//    @Autowired
    private ZKSearchHandler searchHandler;

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore2 historyStore = ZKSearchHistoryStore2.INSTANCE;

    /**
     * 搜索-搜索下一个
     */
    @FXML
    private void searchNext() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.searching) {
            return;
        }
        // 检查选项
        if (!this.searchPath.isSelected() && !this.searchData.isSelected()) {
            MessageBox.warn(this.i18nString("zk.search.tip1"));
            return;
        }
        this.searching = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    // 执行搜索下一个
                    this.searchHandler.searchNext(this.getSearchParam());
                    // 更新搜索结果
                    this.updateSearchResult();
                    // 更新搜索历史
                    this.historyStore.addSearchKw(this.searchKW.getTextTrim());
                })
                .onFinish(() -> this.searching = false)
                .onError(MessageBox::exception)
                .build();
        TaskManager.startDelay("zk:search:searchNext", task, 100);
    }

    /**
     * 搜索-搜索上一个
     */
    @FXML
    private void searchPrev() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.searching) {
            return;
        }
        if (!this.searchPath.isSelected() && !this.searchData.isSelected()) {
            MessageBox.warn(this.i18nString("zk.search.tip1"));
            return;
        }
        this.searching = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    // 执行搜索上一个
                    this.searchHandler.searchPrev(this.getSearchParam());
                    // 更新搜索结果
                    this.updateSearchResult();
                    // 更新搜索历史
                    this.historyStore.addSearchKw(this.searchKW.getTextTrim());
                })
                .onFinish(() -> this.searching = false)
                .onError(MessageBox::exception)
                .build();
        TaskManager.startDelay("zk:search:searchPrev", task, 100);
    }

    /**
     * 搜索-替换
     */
    @FXML
    private void replace() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.replaceKW.isEmpty() || this.replacing) {
            return;
        }
        // 无需替换
        if (this.searchKW.getText().equals(this.replaceKW.getText())) {
            this.replaceTips.setText(this.i18nString("zk.search.tip2"));
            return;
        }
        // 检查搜索参数
        if (this.searchHandler.searchParam() == null) {
            this.replaceTips.setText(this.i18nString("zk.search.tip3"));
            return;
        }
        this.replacing = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    // 执行替换
                    if (this.searchHandler.replace(this.replaceKW.getText())) {
                        this.replaceTips.setText("");
                        ZKNodeTreeItem item = this.parent().activeItem();
                        if (item.isDataTooLong()) {
                            MessageBox.warn(I18nHelper.dataTooLarge());
                        } else if (item.saveData()) {
                            // 更新搜索结果
                            this.updateSearchResult();
                            // 更新搜索、替换历史
                            this.historyStore.addSearchKw(this.searchKW.getTextTrim());
                            this.historyStore.addReplaceKw(this.replaceKW.getTextTrim());
                        }
                    } else { // 未找到匹配项
                        // 更新搜索结果
                        this.updateSearchResult();
                        this.replaceTips.setText(I18nHelper.noMoreResult());
                    }
                })
                .onFinish(() -> this.replacing = false)
                .onError(MessageBox::exception)
                .build();
        TaskManager.startDelay("zk:search:replace", task, 100);
    }

    /**
     * 预搜索
     */
    private void preSearch() {
        TaskManager.startDelay("zk:search:preSearch", () -> {
            try {
                this.searchCheck();
                this.searchHandler.treeNode().disable();
                ZKSearchParam param = this.getSearchParam();
                if (!this.searchNext.isDisable()) {
                    // 执行预搜索
                    this.searchResult.setText(I18nHelper.searching());
                    this.searchHandler.preSearch(param);
                    // 搜索开始
                    ZKEventUtil.searchStart(param);
                    // 更新搜索结果
                    this.searchResult.setText("");
                    this.updateSearchResult();
                } else {// 搜索结束
                    ZKEventUtil.searchFinish(param);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                this.searchHandler.treeNode().enable();
            }
        }, 300);
    }

    /**
     * 搜索分析
     */
    @FXML
    private void searchAnalyse() {
        this.searchHandler.doAnalyse();
    }

    /**
     * 获取搜索参数
     *
     * @return 搜索参数
     */
    private ZKSearchParam getSearchParam() {
        ZKSearchParam searchParam = new ZKSearchParam();
        searchParam.setMode(this.filterMode.isSelected() ? 1 : 0);  // 设置搜索模式
        searchParam.setKw(this.searchKW.getTextTrim());  // 设置搜索关键字
        searchParam.setFullMatch(this.matchFull.isSelected());  // 设置是否全匹配
        searchParam.setSearchData(this.searchData.isSelected());  // 设置是否搜索数据
        searchParam.setSearchPath(this.searchPath.isSelected());  // 设置是否搜索路径
        searchParam.setCompareCase(this.matchCase.isSelected());  // 设置是否区分大小写
        // 返回搜索参数
        return searchParam;
    }

    /**
     * 检查搜索配置
     */
    private void searchCheck() {
        try {
            // 搜索值、名称均未选择
            if (!this.searchData.isSelected() && !this.searchPath.isSelected()) {
                this.replace.disable();
                this.replaceKW.disable();
                this.searchKW.disable();
                this.searchNext.disable();
                this.filterMode.disable();
                return;
            }

            // 替换相关检查
            this.replace.setDisable(!this.searchData.isSelected() || this.replaceKW.isEmpty() || this.searchKW.isEmpty() || Objects.equals(this.searchKW.getText(), this.replaceKW.getText()));
            this.replaceKW.setDisable(!this.searchData.isSelected());

            // 搜索相关检查
            this.searchKW.enable();
            if (StrUtil.isBlank(this.searchKW.getText())) {
                this.searchNext.disable();
                this.searchResult.setText("");
                this.searchHandler.clear();
            } else {
                this.searchNext.enable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 更新搜索结果
     */
    private void updateSearchResult() {
        SearchResult result = this.searchHandler.searchResult();
        if (result != null) {
            String matchType = result.getMatchTypeText();
            if (matchType.isEmpty()) {
                this.searchResult.setText(result.getIndex() + "/" + result.getCount());
            } else {
                this.searchResult.setText(result.getIndex() + "/" + result.getCount() + "[" + result.getMatchTypeText() + "]");
            }
        }
    }

    @Override
    protected void bindListeners() {
        // 搜索相关处理
        this.searchMain.managedBindVisible();
        this.searchPrev.disableProperty().bind(this.searchNext.disableProperty());
        this.searchAnalyse.disableProperty().bind(this.searchNext.disableProperty());
        this.matchCase.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.matchFull.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchPath.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.filterMode.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchData.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchKW.addTextChangeListener((observable, oldValue, newValue) -> this.preSearch());
        this.replaceKW.addTextChangeListener((observable, oldValue, newValue) -> {
            this.replaceTips.setText("");
            this.searchCheck();
        });

        // 监听搜索组件显示事件
        this.searchMain.visibleProperty().addListener((t1, t2, newValue) -> {
            if (newValue) {
                this.preSearch();
            } else {
                this.searchHandler.preSearch(null);
                ZKEventUtil.searchFinish(null);
            }
        });
    }

    /**
     * 刷新搜索结果
     */
    @Subscribe
    public void flushSearchResult(TreeChildChangedEvent event) {
        if (this.searchHandler.treeNode().searching()) {
            TaskManager.startDelay("zk:search:flushSearchResult", () -> {
                this.searchHandler.updateResult();
                this.updateSearchResult();
            }, 300);
        }
    }

    /**
     * 搜索触发
     */
    @Subscribe
    public void searchFire(ZKSearchFireEvent event) {
        if (this.searchMain.isVisible()) {
            this.searchMain.disappear();
            this.searchHandler.treeNode().setFlexHeight("100% - 60");
        } else {
            this.searchMain.display();
            this.searchHandler.treeNode().setFlexHeight("100% - 150");
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.searchHandler = super.getProp("searchHandler");
        // this.treeView = this.parent().tree;
        // // 初始化搜索
        // this.searchHandler.init(this.treeView, this.parent().tabPane);
        this.searchKW.setHistoryPopup(new ZKSearchHistoryPopup((byte) 1));
        this.replaceKW.setHistoryPopup(new ZKSearchHistoryPopup((byte) 2));
    }

    @Override
    public ZKMainController parent() {
        return (ZKMainController) super.parent();
    }

    /**
     * zk搜索控件按键事件
     *
     * @param event 事件
     */
    @FXML
    private void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            this.replaceKW.requestFocus();
        }
    }

    /**
     * zk替换控件按键事件
     *
     * @param event 事件
     */
    @FXML
    private void onReplaceKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            this.searchKW.requestFocus();
        }
    }
}
