package cn.oyzh.easyzk.search;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;


@Data
public class ZKSearchParam {

    private String action;

    private String keyword;

    private boolean loadAll;

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
        if (param instanceof ZKSearchParam zkSearchParam) {
            if (zkSearchParam.loadAll && !this.loadAll) {
                return false;
            }
            if (zkSearchParam.searchData && !this.searchData) {
                return false;
            }
            if (zkSearchParam.searchPath && !this.searchPath) {
                return false;
            }
            if (!StringUtil.equalsIgnoreCase(zkSearchParam.action, this.action)) {
                return false;
            }
            return StringUtil.equalsIgnoreCase(zkSearchParam.keyword, this.keyword);
        }
        return false;
    }
}
