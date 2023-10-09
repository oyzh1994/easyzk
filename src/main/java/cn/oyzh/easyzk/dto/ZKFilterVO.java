package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.common.Index;
import cn.oyzh.easyzk.domain.ZKFilter;
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
}
