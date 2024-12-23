package cn.oyzh.easyzk.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.vo.ZKAuthVO;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-19
 */
public class ZKAuthTableView extends FlexTableView<ZKAuthVO> {

    /**
     * 当前过滤列表
     */
    private List<ZKAuthVO> list;

    /**
     * 关键字
     */
    private String kw;

    public boolean hasData(){
        return list != null;
    }

    public void setAuths(List<ZKAuth> auths) {
        this.list = ZKAuthVO.convert(auths);
        this.initDataList();
    }

    public void setKw(String kw) {
        this.kw = kw;
        this.initDataList();
    }

    public List<ZKAuth> getAuths() {
        List<ZKAuth> list = new ArrayList<>();
        for (ZKAuthVO authVO : this.list) {
            if (authVO != null && StringUtil.isNotBlank(authVO.getUser()) && StringUtil.isNotBlank(authVO.getPassword())) {
                list.add(authVO);
            }
        }
        return list;
    }

    private void initDataList() {
        List<ZKAuthVO> list = new ArrayList<>();
        if (this.list != null) {
            for (ZKAuthVO authVO : this.list) {
                if (StringUtil.isBlank(this.kw) || StringUtil.containsIgnoreCase(authVO.getUser(), this.kw)
                        || StringUtil.containsIgnoreCase(authVO.getPassword(), this.kw)) {
                    list.add(authVO);
                }
            }
        }
        super.setItem(list);
    }

    public void addAuth(ZKAuthVO authVO) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.add(authVO);
        this.initDataList();
    }

    @Override
    public void removeItem(Object item) {
        super.removeItem(item);
        if (this.list != null) {
            this.list.remove(item);
        }
        this.initDataList();
    }
}
