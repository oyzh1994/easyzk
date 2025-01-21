package cn.oyzh.easyzk.dto;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;

/**
 * zk查询参数
 *
 * @author oyzh
 * @since 2025/01/20
 */
@Data
public class ZKQueryParam {

    private String content;

    public String[] getPrams() {
        return this.content.split(" ");
    }

    public boolean isLs() {
        String[] prams = this.getPrams();
        return "ls".equalsIgnoreCase(prams[0]);
    }

    public boolean isLs2() {
        String[] prams = this.getPrams();
        return "ls2".equalsIgnoreCase(prams[0]);
    }

    public boolean isGet() {
        String[] prams = this.getPrams();
        return "get".equalsIgnoreCase(prams[0]);
    }

    public boolean isSet() {
        String[] prams = this.getPrams();
        return "set".equalsIgnoreCase(prams[0]);
    }

    public boolean isGetACL() {
        String[] prams = this.getPrams();
        return "getAcl".equalsIgnoreCase(prams[0]);
    }

    public String getPath() {
        String[] prams = this.getPrams();
        if (this.isLs2()) {
            return prams[1];
        }
        if (this.isLs()) {
            if (this.hasParamStat()) {
                return prams[2];
            }
            return prams[1];
        }
        if (this.isGet()) {
            if (this.hasParamStat()) {
                return prams[2];
            }
            return prams[1];
        }
        if (this.isSet()) {
            if (this.hasParamStat()) {
                return prams[2];
            }
            return prams[1];
        }
        if (this.isGetACL()) {
            if (this.hasParamStat()) {
                return prams[2];
            }
            return prams[1];
        }
        return null;
    }

    public String getData() {
        String[] prams = this.getPrams();
        if (this.isSet()) {
            if (this.hasParamStat()) {
                return prams[3];
            }
            return prams[2];
        }
        return null;
    }

    public boolean hasParamStat() {
        if(this.isLs2()){
            return true;
        }
        String[] prams = this.getPrams();
        return "-s".equals(prams[1]);
    }
}
