package cn.oyzh.easyzk.filter;

import lombok.Data;


/**
 * @author oyzh
 * @since 2025/01/22
 */
@Data
public class ZKNodeFilterParam {

    private boolean matchCase;

    private boolean matchFull;

    private boolean searchPath = true;

    private boolean searchData = true;

    @Override
    public boolean equals(Object param) {
        if (param == this) {
            return true;
        }
        if (param instanceof ZKNodeFilterParam searchParam) {
            if (searchParam.matchCase && !this.matchCase) {
                return false;
            }
            if (searchParam.matchFull && !this.matchFull) {
                return false;
            }
            if (searchParam.searchData && !this.searchData) {
                return false;
            }
            return !searchParam.searchPath || this.searchPath;
        }
        return false;
    }
}
