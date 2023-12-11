package cn.oyzh.easyzk.search;

import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.controls.rich.FlexRichTextArea;
import cn.oyzh.fx.plus.controls.rich.RichControlUtil;
import cn.oyzh.fx.plus.search.SearchHandler;
import cn.oyzh.fx.plus.search.SearchParam;
import cn.oyzh.fx.plus.search.SearchValue;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemValue;
import cn.oyzh.fx.plus.util.ControlUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * zk主页搜索处理器
 *
 * @author oyzh
 * @since 2023/03/12
 */
@Lazy
@Component
@Accessors(chain = true, fluent = true)
public class ZKSearchHandler extends SearchHandler {

    /**
     * 路径索引
     */
    private Integer pathIndex;

    /**
     * 数据索引
     */
    private Integer dataIndex;

    /**
     * tab组件
     */
    private ZKTabPane tabPane;

    /**
     * 树组件
     */
    private ZKTreeView treeNode;

    /**
     * 数据组件
     */
    private FlexRichTextArea dataNode;

    /**
     * 初始化
     *
     * @param treeNode zk树组件
     * @param tabPane  zk面板组件
     */
    public void init(@NonNull ZKTreeView treeNode, @NonNull ZKTabPane tabPane) {
        this.index = 0;
        this.tabPane = tabPane;
        this.treeNode = treeNode;
    }

    @Override
    public ZKSearchParam searchParam() {
        return (ZKSearchParam) super.searchParam();
    }

    @Override
    protected void resetSearch() {
        super.resetSearch();
        this.pathIndex = null;
        this.dataIndex = null;
    }

    @Override
    public void preSearch(SearchParam param) {
        this.treeNode.disable();
        super.preSearch(param);
        this.treeNode.enable();
    }

    /**
     * 替换
     *
     * @param replaceKW 替换词
     * @return 结果
     */
    public boolean replace(String replaceKW) {
        // 如果当前节点不符合分析条件，则寻找下一个节点
        if (!this.dataAnalyse()) {
            do {
                // 获取匹配节点
                List<SearchValue> matchItems = this.getMatchValues();
                // 如果找不到任何匹配数据的节点，则直接回调false
                if (matchItems.parallelStream().noneMatch(SearchValue::isMatchData)) {
                    // 更新搜索结果
                    this.updateResult();
                    return false;
                }
                // 搜索下一个
                this.searchNext(this.searchParam);
                // 如果当前节点匹配数据，则执行数据分析
                if (this.searchResult().isMatchData()) {
                    this.dataAnalyse();
                    break;
                }
            } while (true);
        }
        if (this.dataNode != null) {
            // 记录旧的索引位置，替换数据后，这个索引值会变成0
            int index = this.dataIndex;
            // 替换选中内容
            this.dataNode.replaceSelection(replaceKW);
            // 更新数据索引，防止索引错位导致重复替换
            int len = replaceKW.length() - this.searchParam.getKw().length();
            this.dataIndex = index + len;
        }
        return true;
    }

    @Override
    protected void doSearch(SearchParam param, String action) {
        this.treeNode.disable();
        super.doSearch(param, action);
        this.treeNode.enable();
    }

    @Override
    protected void applyValue(SearchValue value, int index) {
        super.applyValue(value, index);
        // 选中并滚动到此节点
        this.treeNode.selectAndScroll(value.getItem());
    }

    @Override
    protected void updateCurrentItem(TreeItem<?> item) {
        // 取消文本组件的选中
        RichControlUtil.deselect(this.dataNode);
        this.pathIndex = 0;
        this.dataIndex = 0;
        super.updateCurrentItem(item);
    }

    @Override
    protected List<SearchValue> getMatchValues() {
        return super.getMatchValues(this.treeNode.root());
    }

    /**
     * 寻找数据节点
     *
     * @return 数据节点
     */
    private FlexRichTextArea findDataNode() {
        ZKNodeTab itemTab = this.tabPane.getNodeTab(this.currentItem);
        if (itemTab != null) {
            itemTab.selectDataTab();
            return itemTab.getDataNode();
        }
        return null;
    }

    /**
     * 执行分析
     */
    @Override
    public void doAnalyse() {
        try {
            // 判断节点是否存在
            if (this.currentItem == null) {
                return;
            }
            // 执行路径分析
            if (this.searchParam().isSearchPath() && this.pathIndex != -100 && this.pathAnalyse()) {
                return;
            }
            // 执行数据分析
            if (this.searchParam().isSearchData() && this.dataIndex != -100 && this.dataAnalyse()) {
                return;
            }
            // 初始化索引及文本组件
            this.dataIndex = 0;
            this.pathIndex = 0;
            ZKTreeItemValue value = (ZKTreeItemValue) this.currentItem.getValue();
            RichControlUtil.deselect(this.dataNode);
            ControlUtil.deselect(value.text());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 路径节点分析
     *
     * @return 结果
     */
    private boolean pathAnalyse() {
        try {
            String kw = this.searchParam.getKw();
            RichTreeItemValue value = (RichTreeItemValue) this.currentItem.getValue();
            Text text = value.text();
            String path = value.name();
            // 搜索索引
            int index = TextUtil.findIndex(path, kw, this.pathIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
            if (index != -1) {
                int end = index + kw.length();
                text.setSelectionStart(index);
                text.setSelectionEnd(end);
                text.setSelectionFill(Color.CHARTREUSE);
                this.pathIndex = end;
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.pathIndex = -100;
        return false;
    }

    /**
     * 数据节点分析
     *
     * @return 结果
     */
    private boolean dataAnalyse() {
        try {
            this.dataNode = this.findDataNode();
            if (this.dataNode == null) {
                return false;
            }
            String kw = this.searchParam.getKw();
            String text = this.dataNode.getText();
            // 搜索索引
            int index = TextUtil.findIndex(text, kw, this.dataIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
            if (index != -1) {
                int end = index + kw.length();
                this.dataNode.selectRange(index, end);
                this.dataIndex = end;
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.dataIndex = -100;
        return false;
    }

    @Override
    public String getMatchType(TreeItem<?> item) {
        if (item == null || this.searchParam == null) {
            return null;
        }
        boolean m1 = false, m2 = false;
        // 路径
        if (this.searchParam().isSearchPath() && item instanceof RichTreeItem<?> treeItem) {
            String value = treeItem.getValue().name();
            m1 = this.searchParam.isMatch(value);
        }
        // 数据
        if (this.searchParam().isSearchData() && item instanceof ZKNodeTreeItem treeItem) {
            String data = treeItem.dataStr();
            m2 = this.searchParam.isMatch(data);
        }
        // 匹配全部
        if (m1 && m2) {
            return "all";
        }
        // 匹配名称
        if (m1) {
            return "name";
        }
        // 匹配数据
        if (m2) {
            return "data";
        }
        return null;
    }
}
