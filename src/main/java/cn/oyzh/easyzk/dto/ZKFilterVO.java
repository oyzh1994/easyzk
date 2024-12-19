package cn.oyzh.easyzk.dto;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.common.Index;
import cn.oyzh.fx.gui.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.gui.toggle.MatchToggleSwitch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * zk过滤vo信息
 *
 * @author oyzh
 * @since 2022/12/20
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ZKFilterVO extends ZKFilter implements Index {

    /**
     * 索引
     */
    private int index;

    /**
     * 复制
     *
     * @param filter zk过滤信息
     * @param index  索引
     * @return zk认证vo
     */
    public static ZKFilterVO copy(ZKFilter filter, int index) {
        ZKFilterVO authVO = new ZKFilterVO();
        authVO.copy(filter);
        authVO.setIndex(index);
        return authVO;
    }

    /**
     * 转换
     *
     * @param list zk过滤列表
     * @return zk过滤vo列表
     */
    public static List<ZKFilterVO> convert(@NonNull List<ZKFilter> list) {
        List<ZKFilterVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            voList.add(copy(list.get(i), i + 1));
        }
        return voList;
    }

    /**
     * 过滤储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 匹配模式控件
     */
    public MatchToggleSwitch getMatchModeControl() {
        MatchToggleSwitch toggleSwitch = new MatchToggleSwitch();
        toggleSwitch.fontSize(11);
        toggleSwitch.setSelected(this.isPartMatch());
        toggleSwitch.selectedChanged((obs, o, n) -> {
            this.setPartMatch(n);
            // if (this.filterStore.replace(this)) {
            //     ZKEventUtil.treeChildFilter();
            // }
        });
        return toggleSwitch;
    }

    /**
     * 状态控件
     */
    public EnabledToggleSwitch getStatusControl() {
        EnabledToggleSwitch toggleSwitch = new EnabledToggleSwitch();
        toggleSwitch.setFontSize(11);
        toggleSwitch.setSelected(this.isEnable());
        toggleSwitch.selectedChanged((abs, o, n) -> {
            this.setEnable(n);
            // if (this.filterStore.replace(this)) {
            //     ZKEventUtil.treeChildFilter();
            // }
        });
        return toggleSwitch;
    }
}
