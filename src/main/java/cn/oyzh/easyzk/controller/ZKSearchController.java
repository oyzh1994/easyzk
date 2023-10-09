package cn.oyzh.easyzk.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.fx.plus.controls.FlexCheckBox;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.FlexText;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.ext.ClearableTextField;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyboardListener;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.easyzk.dto.ZKSearchParam;
import cn.oyzh.easyzk.dto.ZKSearchResult;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.fx.ZKSearchHistoryPopup;
import cn.oyzh.easyzk.fx.ZKTreeView;
import cn.oyzh.easyzk.handler.ZKMainSearchHandler;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * zk搜索子组件
 *
 * @author oyzh
 * @since 2023/4/11
 */
@Lazy
@Component
public class ZKSearchController extends FXController {

    /**
     * 搜索-搜索词
     */
    @FXML
    private ClearableTextField searchKW;

    /**
     * 搜索-替换词
     */
    @FXML
    private ClearableTextField replaceKW;

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
     * 搜索-搜索历史
     */
    @FXML
    private SVGGlyph searchHistory;

    /**
     * 搜索-替换历史
     */
    @FXML
    private SVGGlyph replaceHistory;
    /**
     * zk树
     */
    private ZKTreeView treeView;

    /**
     * zk主页搜索处理
     */
    private final ZKMainSearchHandler searchHandler = new ZKMainSearchHandler();

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    /**
     * 搜索历史弹窗
     */
    private ZKSearchHistoryPopup searchHistoryPopup;

    /**
     * 替换历史弹窗
     */
    private ZKSearchHistoryPopup replaceHistoryPopup;

    /**
     * 搜索-搜索历史
     */
    @FXML
    private void searchHistory(MouseEvent event) {
        if (this.searchHistoryPopup == null) {
            this.searchHistoryPopup = new ZKSearchHistoryPopup(1);
        }
        this.searchHistoryPopup.show(this.searchHistory, event.getScreenX(), event.getScreenY());
    }

    /**
     * 搜索历史点击事件
     *
     * @param kw 点击关键词
     */
    @EventReceiver(ZKEventTypes.ZK_SEARCH_HISTORY_SELECTED)
    private void searchHistorySelected(String kw) {
        if (!this.searchKW.getTextTrim().equals(kw)) {
            this.searchKW.setText(kw);
        }
    }

    /**
     * 搜索-搜索历史
     */
    @FXML
    private void replaceHistory(MouseEvent event) {
        if (this.replaceHistoryPopup == null) {
            this.replaceHistoryPopup = new ZKSearchHistoryPopup(2);
        }
        this.replaceHistoryPopup.show(this.replaceHistory, event.getScreenX(), event.getScreenY());
    }

    /**
     * 替换历史点击事件
     *
     * @param kw 点击关键词
     */
    @EventReceiver(ZKEventTypes.ZK_REPLACE_HISTORY_SELECTED)
    private void replaceHistorySelected(String kw) {
        if (!this.replaceKW.getTextTrim().equals(kw)) {
            this.replaceKW.setText(kw);
        }
    }

    /**
     * 搜索-更多
     */
    @FXML
    private void showSearchMore() {
        this.searchMore1.showNode();
        this.searchMain.setHeightAll(108);
        this.treeView.setFlexHeight("100% - 144");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.showNode();
        this.showSearchMore.hideNode();
    }

    /**
     * 搜索-更少
     */
    @FXML
    private void hideSearchMore() {
        this.searchMore1.hideNode();
        this.searchMain.setHeightAll(36);
        this.treeView.setFlexHeight("100% - 72");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.hideNode();
        this.showSearchMore.showNode();
    }

    /**
     * 搜索-搜索下一个
     */
    @FXML
    private void searchNext() {
        // 内容为空
        if (this.searchKW.isEmpty()) {
            return;
        }
        if (!this.searchPath.isSelected() && !this.searchData.isSelected()) {
            FXAlertUtil.warn("搜索名称和值请最少勾选一项！");
            return;
        }
        TaskManager.startDelayTask("zk:search:searchNext", () -> {
            try {
                this.treeView.disable();
                // 执行搜索下一个
                this.searchHandler.searchNext(this.getSearchParam());
                // 更新搜索结果
                this.updateSearchResult();
                // 更新搜索历史
                this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
                this.treeView.enable();
            } catch (Exception ex) {
                ex.printStackTrace();
                FXAlertUtil.warn("发生异常！");
            }
        }, 50);
    }

    /**
     * 搜索-搜索上一个
     */
    @FXML
    private void searchPrev() {
        // 内容为空
        if (this.searchKW.isEmpty()) {
            return;
        }
        if (!this.searchPath.isSelected() && !this.searchData.isSelected()) {
            FXAlertUtil.warn("搜索名称和值请最少勾选一项！");
            return;
        }
        TaskManager.startDelayTask("zk:search:searchPrev", () -> {
            try {
                this.treeView.disable();
                // 执行搜索上一个
                this.searchHandler.searchPrev(this.getSearchParam());
                // 更新搜索结果
                this.updateSearchResult();
                // 更新搜索历史
                this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
                this.treeView.enable();
            } catch (Exception ex) {
                ex.printStackTrace();
                FXAlertUtil.warn("发生异常！");
            }
        }, 50);
    }

    /**
     * 搜索-替换
     */
    @FXML
    private void replace() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.replaceKW.isEmpty()) {
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
        TaskManager.startDelayTask("zk:search:replace", () -> {
            // 执行替换
            this.searchHandler.replace(this.replaceKW.getText(), b -> {
                try {
                    // 找到匹配项
                    if (b) {
                        this.replaceTips.setText("");
                        ZKNodeTreeItem item = this.parent().activeItem();
                        if (item.isDataTooLong()) {
                            FXAlertUtil.warn("数据太大，无法替换！");
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
                } catch (Exception ex) {
                    // ex.printStackTrace();
                    // FXAlertUtil.warn("发生异常！");
                }
            });
        }, 50);
    }

    /**
     * 预搜索
     */
    private void preSearch() {
        TaskManager.startDelayTask("zk:search:preSearch", () -> {
            try {
                this.searchCheck();
                this.treeView.disable();
                if (!this.searchNext.isDisable()) {
                    this.searchResult.setText("搜索中...");
                    // 触发事件
                    EventUtil.fire(ZKEventTypes.ZK_SEARCH_START);
                    // 执行预搜索
                    this.searchHandler.preSearch(this.getSearchParam());
                    this.searchResult.setText("");
                    // 更新搜索结果
                    this.updateSearchResult();
                } else {// 搜索结束
                    EventUtil.fire(ZKEventTypes.ZK_SEARCH_FINISH);
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
        searchParam.setKw(this.searchKW.getTextTrim());
        searchParam.setFullMatch(this.fullMatch.isSelected());
        searchParam.setSearchData(this.searchData.isSelected());
        searchParam.setSearchPath(this.searchPath.isSelected());
        searchParam.setCompareCase(this.compareCase.isSelected());
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
        ZKSearchResult result = this.searchHandler.searchResult();
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
        this.searchMore1.managedProperty().bind(this.searchMore1.visibleProperty());
        this.searchMore2.managedProperty().bind(this.searchMore1.visibleProperty());
        this.searchMore2.visibleProperty().bind(this.searchMore1.visibleProperty());
        this.searchPrev.disableProperty().bind(this.searchNext.disableProperty());
        this.searchAnalyse.disableProperty().bind(this.searchNext.disableProperty());
        this.showSearchMore.managedProperty().bind(this.showSearchMore.visibleProperty());
        this.hideSearchMore.managedProperty().bind(this.hideSearchMore.visibleProperty());
        this.fullMatch.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchPath.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchData.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.compareCase.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchKW.addTextChangedListener((observable, oldValue, newValue) -> this.preSearch());
        this.replaceKW.addTextChangedListener((observable, oldValue, newValue) -> {
            this.replaceTips.setText("");
            this.searchCheck();
        });

        // 搜索触发事件
        KeyboardListener.listenKey(this.view, new KeyHandler().keyType(KeyEvent.KEY_RELEASED).keyCode(KeyCode.F).controlDown(true).handler(e -> {
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
            TaskManager.startDelayTask("zk:search:flushSearchResult", () -> {
                this.searchHandler.updateResult();
                this.updateSearchResult();
            }, 300);
        }
    }

    @Override
    public void onViewShown(WindowEvent event) {
        super.onViewShown(event);
        // 注册事件处理
        EventUtil.register(this);
        this.treeView = this.parent().tree;
        // 初始化搜索
        this.searchHandler.init(this.treeView, this.parent().tabPane);
    }

    @Override
    public void onViewHidden(WindowEvent event) {
        super.onViewHidden(event);
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
     * @param e 事件
     */
    @FXML
    private void onSearchKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.searchNext();
        } else if (e.getCode() == KeyCode.TAB) {
            if (this.searchMore1.isVisible()) {
                this.replaceKW.requestFocus();
            }
        } else if (e.getCode() == KeyCode.UP) {
            String currKW = this.searchKW.getTextTrim();
            List<String> list = this.historyStore.getSearchKw();
            String historyKW = this.getHistoryKW(currKW, list, true);
            if (historyKW != null) {
                this.searchHistorySelected(historyKW);
            }
        } else if (e.getCode() == KeyCode.DOWN) {
            String currKW = this.searchKW.getTextTrim();
            List<String> list = this.historyStore.getSearchKw();
            String historyKW = this.getHistoryKW(currKW, list, false);
            if (historyKW != null) {
                this.searchHistorySelected(historyKW);
            }
        }
    }

    /**
     * zk替换控件按键事件
     *
     * @param e 事件
     */
    @FXML
    private void onReplaceKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.replace();
        } else if (e.getCode() == KeyCode.TAB) {
            this.searchKW.requestFocus();
        } else if (e.getCode() == KeyCode.UP) {
            String currKW = this.replaceKW.getTextTrim();
            List<String> list = this.historyStore.getReplaceKw();
            String historyKW = this.getHistoryKW(currKW, list, true);
            if (historyKW != null) {
                this.replaceHistorySelected(historyKW);
            }
        } else if (e.getCode() == KeyCode.DOWN) {
            String currKW = this.replaceKW.getTextTrim();
            List<String> list = this.historyStore.getReplaceKw();
            String historyKW = this.getHistoryKW(currKW, list, false);
            if (historyKW != null) {
                this.replaceHistorySelected(historyKW);
            }
        }
    }

    /**
     * 获取历史词汇
     *
     * @param currKW 当前词汇
     * @param list   词汇列表
     * @param isUp   是否向上查找
     * @return 搜索词
     */
    private String getHistoryKW(String currKW, List<String> list, boolean isUp) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String kw;
        if (isUp) {
            // 获取首个
            if (currKW == null) {
                kw = CollUtil.getFirst(list);
            } else {
                int index = list.indexOf(currKW) + 1;
                // 获取最后一个
                if (index >= list.size()) {
                    kw = CollUtil.getLast(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        } else {
            // 获取最后一个
            if (currKW == null) {
                kw = CollUtil.getLast(list);
            } else {
                int index = list.indexOf(currKW) - 1;
                // 获取首个
                if (index <= 0) {
                    kw = CollUtil.getFirst(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        }
        return kw;
    }
}
