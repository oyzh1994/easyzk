package cn.oyzh.easyzk.vo;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.common.Index;
import cn.oyzh.common.date.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * zk数据历史vo信息
 *
 * @author oyzh
 * @since 2024/04/24
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ZKDataHistoryVO extends ZKDataHistory implements Index {

    /**
     * 索引
     */
    private int index;

    /**
     * 复制
     *
     * @param filter zk数据历史
     * @param index  索引
     * @return zk数据历史vo
     */
    public static ZKDataHistoryVO copy(ZKDataHistory filter, int index) {
        ZKDataHistoryVO vo = new ZKDataHistoryVO();
        vo.copy(filter);
        vo.setIndex(index);
        return vo;
    }

    /**
     * 转换
     *
     * @param list zk数据历史
     * @return zk数据历史列表
     */
    public static List<ZKDataHistoryVO> convert(@NonNull List<ZKDataHistory> list) {
        List<ZKDataHistoryVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            voList.add(copy(list.get(i), i + 1));
        }
        return voList;
    }

    public String getSaveTimeFormated() {
        return DateUtil.format("yy-MM-dd HH:mm:ss", this.getSaveTime());
    }
}
