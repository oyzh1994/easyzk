package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.plus.search.SearchParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * zk搜索参数
 *
 * @author oyzh
 * @since 2023/3/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ZKSearchParam extends SearchParam {

    /**
     * 搜索数据
     */
    private boolean searchData;

    /**
     * 搜索路径
     */
    private boolean searchPath;

    @Override
    public boolean equalsTo(Object param) {
        if (super.equalsTo(param) && param instanceof ZKSearchParam param1) {
            if (!Objects.equals(this.searchData, param1.searchData)) {
                return false;
            }
            if (!Objects.equals(this.searchPath, param1.searchPath)) {
                return false;
            }
        }
        return false;
    }
}
