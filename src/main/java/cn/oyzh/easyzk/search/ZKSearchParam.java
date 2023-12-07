package cn.oyzh.easyzk.search;

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

    /**
     * 重写equals方法，判断当前对象是否与param相等
     *
     * @param param 要比较的对象
     * @return 如果当前对象与param相等，则返回true；否则返回false
     */
    @Override
    public boolean equalsTo(Object param) {
        // 如果super.equalsTo(param)返回true，并且param是ZKSearchParam类型的对象，则继续判断
        if (super.equalsTo(param) && param instanceof ZKSearchParam param1) {
            // 如果this.searchData与param1.searchData不相等，则返回false
            if (!Objects.equals(this.searchData, param1.searchData)) {
                return false;
            }
            // 返回this.searchPath与param1.searchPath是否相等
            return Objects.equals(this.searchPath, param1.searchPath);
        }
        // 如果不满足上述条件，则返回false
        return false;
    }
}
