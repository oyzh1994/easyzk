package cn.oyzh.easyzk.vo;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.gui.toggle.MatchToggleSwitch;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * zk过滤vo信息
 *
 * @author oyzh
 * @since 2022/12/20
 */
public class ZKFilterVO extends ZKFilter {

    /**
     * 转换
     *
     * @param filter zk过滤对象
     * @return zk过滤vo对象
     */
    public static ZKFilterVO convert(@NonNull ZKFilter filter) {
        ZKFilterVO filterVO = new ZKFilterVO();
        filterVO.copy(filter);
        return filterVO;
    }

    /**
     * 转换
     *
     * @param list zk过滤列表
     * @return zk过滤vo列表
     */
    public static List<ZKFilterVO> convert(@NonNull List<ZKFilter> list) {
        List<ZKFilterVO> voList = new ArrayList<>(list.size());
        for (ZKFilter filter : list) {
            voList.add(convert(filter));
        }
        return voList;
    }

    /**
     * 关键字控件
     */
    public ClearableTextField getKwControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setFlexWidth("100% - 12");
        textField.setValue(this.getKw());
        textField.addTextChangeListener((obs, o, n) -> this.setKw(n));
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 匹配模式控件
     */
    public MatchToggleSwitch getMatchModeControl() {
        MatchToggleSwitch toggleSwitch = new MatchToggleSwitch();
        toggleSwitch.fontSize(11);
        toggleSwitch.setSelected(this.isPartMatch());
        toggleSwitch.selectedChanged((obs, o, n) -> this.setPartMatch(n));
        TableViewUtil.selectRowOnMouseClicked(toggleSwitch);
        return toggleSwitch;
    }

    /**
     * 状态控件
     */
    public EnabledToggleSwitch getStatusControl() {
        EnabledToggleSwitch toggleSwitch = new EnabledToggleSwitch();
        toggleSwitch.setFontSize(11);
        toggleSwitch.setSelected(this.isEnable());
        toggleSwitch.selectedChanged((abs, o, n) -> this.setEnable(n));
        TableViewUtil.selectRowOnMouseClicked(toggleSwitch);
        return toggleSwitch;
    }
}
