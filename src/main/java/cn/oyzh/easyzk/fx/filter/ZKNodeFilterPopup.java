//package cn.oyzh.easyzk.fx.filter;
//
//import cn.oyzh.common.util.BooleanUtil;
//import cn.oyzh.fx.plus.controls.popup.ListViewPopup;
//import cn.oyzh.i18n.I18nHelper;
//
//import java.util.List;
//
///**
// * zk节点过滤类型弹窗
// *
// * @author oyzh
// * @since 2023/4/24
// */
//public class ZKNodeFilterPopup extends ListViewPopup<String> {
//
//    @Override
//    protected void initPopup() {
//        super.initContent();
//        this.showingProperty().addListener((observable, oldValue, newValue) -> {
//            if (BooleanUtil.isTrue(newValue)) {
//                super.calcListViewSize();
//            }
//        });
//    }
//
//    @Override
//    public List<String> getItems() {
//        return List.of(I18nHelper.contains(), I18nHelper.containsCaseSensitive(), I18nHelper.matchWholeWord(), I18nHelper.matchWholeWordCaseSensitive());
//    }
//}
