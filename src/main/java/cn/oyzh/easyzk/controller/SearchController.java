package cn.oyzh.easyzk.controller;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZKSearchHistoryPopup;
import cn.oyzh.easyzk.search.ZKSearchHandler;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.controls.textfield.SearchTextField;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.search.SearchResult;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * zk搜索子组件
 *
 * @author oyzh
 * @since 2023/4/11
 */
@Lazy
@Component
public class SearchController extends SubController {

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
     * 搜索-更多1
     */
    @FXML
    private FlexHBox searchMore1;

    /**
     * 搜索-更多2
     */
    @FXML
    private FlexHBox searchMore2;

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
     * 搜索-搜索模式
     */
    @FXML
    private FlexCheckBox mode;

    /**
     * 搜索-搜索值
     */
    @FXML
    private FlexCheckBox searchData;

    /**
     * 搜索-全文匹配
     */
    @FXML
    private FlexCheckBox fullMatch;

    /**
     * 搜索-匹配大小写
     */
    @FXML
    private FlexCheckBox compareCase;

    /**
     * 搜索-搜索路径
     */
    @FXML
    private FlexCheckBox searchPath;

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

    /**
     * 搜索-更多
     */
    @FXML
    private SVGGlyph showSearchMore;

    /**
     * 搜索-更少
     */
    @FXML
    private SVGGlyph hideSearchMore;

    /**
     * zk树
     */
    private ZKTreeView treeView;

    /**
     * zk主页搜索处理
     */
    @Autowired
    private ZKSearchHandler searchHandler;

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    /**
     * 搜索-更多
     */
    @FXML
    private void showSearchMore() {
        this.searchMore1.display();
        this.searchMain.setRealHeight(90);
        this.treeView.setFlexHeight("100% - 152");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.display();
        this.showSearchMore.disappear();
    }

    /**
     * 搜索-更少
     */
    @FXML
    private void hideSearchMore() {
        this.searchMore1.disappear();
        this.searchMain.setRealHeight(30);
        this.treeView.setFlexHeight("100% - 92");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.disappear();
        this.showSearchMore.display();
    }

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
            MessageBox.warn("路径和值请勾选一项！");
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
                    this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
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
            MessageBox.warn("搜索名称和值请最少勾选一项！");
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
                    this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
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
            this.replaceTips.setText("替换词、搜索词相同");
            return;
        }
        // 检查搜索参数
        if (this.searchHandler.searchParam() == null) {
            this.replaceTips.setText("请初始化搜索参数");
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
                            MessageBox.warn("数据太大，无法替换！");
                        } else if (item.saveData()) {
                            // 更新搜索结果
                            this.updateSearchResult();
                            // 更新搜索、替换历史
                            this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
                            this.historyStore.addReplaceHistory(this.replaceKW.getTextTrim());
                        }
                    } else { // 未找到匹配项
                        // 更新搜索结果
                        this.updateSearchResult();
                        this.replaceTips.setText("没有找到可替换项");
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
                this.treeView.disable();
                ZKSearchParam param = this.getSearchParam();
                if (!this.searchNext.isDisable()) {
                    // 执行预搜索
                    this.searchResult.setText("搜索中...");
                    this.searchHandler.preSearch(param);
                    // 搜索开始
                    ZKEventUtil.searchStart(param);
                    // 更新搜索结果
                    this.searchResult.setText("");
                    this.updateSearchResult();
                } else {// 搜索结束
                    ZKEventUtil.searchFinish(param);
                }
                this.treeView.enable();
            } catch (Exception ex) {
                ex.printStackTrace();
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
        searchParam.setMode(this.mode.isSelected() ? 1 : 0);  // 设置搜索模式
        searchParam.setKw(this.searchKW.getTextTrim());  // 设置搜索关键字
        searchParam.setFullMatch(this.fullMatch.isSelected());  // 设置是否全匹配
        searchParam.setSearchData(this.searchData.isSelected());  // 设置是否搜索数据
        searchParam.setSearchPath(this.searchPath.isSelected());  // 设置是否搜索路径
        searchParam.setCompareCase(this.compareCase.isSelected());  // 设置是否区分大小写
        return searchParam;  // 返回搜索参数
    }

    /**
     * 检查搜索配置
     */
    private void searchCheck() {
        try {
            // 搜索值、名称均未选择
            if (!this.searchData.isSelected() && !this.searchPath.isSelected()) {
                this.mode.disable();
                this.replace.disable();
                this.replaceKW.disable();
                this.searchKW.disable();
                this.searchNext.disable();
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
        // 绑定搜索更多1的事件
        this.searchMore1.managedBindVisible();
        // 将searchMore2的visible属性绑定到searchMore1的visibleProperty()
        this.searchMore2.managedProperty().bind(this.searchMore1.visibleProperty());
        // 将searchMore2的visibleProperty绑定到searchMore1的visibleProperty()
        this.searchMore2.visibleProperty().bind(this.searchMore1.visibleProperty());
        // 将searchPrev的disableProperty绑定到searchNext的disableProperty
        this.searchPrev.disableProperty().bind(this.searchNext.disableProperty());
        // 将searchAnalyse的disableProperty绑定到searchNext的disableProperty
        this.searchAnalyse.disableProperty().bind(this.searchNext.disableProperty());
        // 绑定showSearchMore的事件，使其managedBindVisible()
        this.showSearchMore.managedBindVisible();
        // 绑定mode的selectedChanged事件，当mode的值发生变化时调用preSearch()方法
        this.mode.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        // 绑定hideSearchMore的事件，使其managedProperty绑定到hideSearchMore的visibleProperty
        this.hideSearchMore.managedProperty().bind(this.hideSearchMore.visibleProperty());
        // 当fullMatch的值发生变化时调用preSearch()方法
        this.fullMatch.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        // 当searchPath的值发生变化时调用preSearch()方法
        this.searchPath.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        // 当searchData的值发生变化时调用preSearch()方法
        this.searchData.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        // 当compareCase的值发生变化时调用preSearch()方法
        this.compareCase.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        // 当searchKW添加文本时调用preSearch()方法
        this.searchKW.addTextChangeListener((observable, oldValue, newValue) -> this.preSearch());
        // 当replaceKW添加文本时，清空replaceTips的文本并调用searchCheck()方法
        this.replaceKW.addTextChangeListener((observable, oldValue, newValue) -> {
            this.replaceTips.setText("");
            this.searchCheck();
        });

        // 搜索触发事件
        // 监听stage的键盘按下事件，如果按下的键为F且Ctrl键同时按下，将焦点移至searchKW文本框并选中所有文本
        KeyListener.listen(this.stage, new KeyHandler().keyType(KeyEvent.KEY_RELEASED).keyCode(KeyCode.F).controlDown(true).handler(e -> {
            this.searchKW.requestFocus();
            this.searchKW.selectEnd();
        }));
    }

    /**
     * 刷新搜索结果
     */
    @EventReceiver(value = ZKEventTypes.TREE_CHILD_CHANGED, async = true, verbose = true)
    public void flushSearchResult() {
        if (this.treeView.searching()) {
            TaskManager.startDelay("zk:search:flushSearchResult", () -> {
                this.searchHandler.updateResult();
                this.updateSearchResult();
            }, 300);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        this.treeView = this.parent().tree;
        // 初始化搜索
        this.searchHandler.init(this.treeView, this.parent().tabPane);
        this.searchKW.setHistoryPopup(new ZKSearchHistoryPopup(1));
        this.replaceKW.setHistoryPopup(new ZKSearchHistoryPopup(2));
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
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
            if (this.searchMore1.isVisible()) {
                this.replaceKW.requestFocus();
            }
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
