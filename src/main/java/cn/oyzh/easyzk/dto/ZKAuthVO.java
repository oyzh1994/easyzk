package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.common.Index;
import cn.oyzh.easyzk.domain.ZKAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * zk认证vo信息
 *
 * @author oyzh
 * @since 2022/6/6
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ZKAuthVO extends ZKAuth implements Index {

    /**
     * 索引
     */
    private int index;

    /**
     * 复制
     *
     * @param auth  zk认证信息
     * @param index 索引
     * @return zk认证vo
     */
    public static ZKAuthVO copy(ZKAuth auth, int index) {
        ZKAuthVO authVO = new ZKAuthVO();
        authVO.copy(auth);
        authVO.setIndex(index);
        return authVO;
    }

    /**
     * 转换
     *
     * @param list zk认证列表
     * @return zk认证vo列表
     */
    public static List<ZKAuthVO> convert(@NonNull List<ZKAuth> list) {
        List<ZKAuthVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            voList.add(copy(list.get(i), i + 1));
        }
        return voList;
    }
}
