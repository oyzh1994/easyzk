package cn.oyzh.easyzk.search;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;


/**
 * @author oyzh
 * @since 2025/01/17
 */
@Data
public class ZKSearchParam {

    private String action;

    private String keyword;

    private boolean matchCase;

    private boolean matchFull;

    private boolean searchPath;

    private boolean searchData;

    public boolean isNext() {
        return this.action.equalsIgnoreCase("next");
    }

    @Override
    public boolean equals(Object param) {
        if (param == this) {
            return true;
        }
        if (param instanceof ZKSearchParam searchParam) {
            if (searchParam.matchCase && !this.matchCase) {
                return false;
            }
            if (searchParam.matchFull && !this.matchFull) {
                return false;
            }
            if (searchParam.searchData && !this.searchData) {
                return false;
            }
            if (searchParam.searchPath && !this.searchPath) {
                return false;
            }
            if (!StringUtil.equalsIgnoreCase(searchParam.action, this.action)) {
                return false;
            }
            return StringUtil.equalsIgnoreCase(searchParam.keyword, this.keyword);
        }
        return false;
    }
}
