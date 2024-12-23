package cn.oyzh.easyzk.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.vo.ZKFilterVO;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-19
 */
public class ZKFilterTableView extends FlexTableView<ZKFilterVO> {

    /**
     * 当前过滤列表
     */
    private List<ZKFilterVO> list;

    /**
     * 关键字
     */
    private String kw;

    public boolean hasData(){
        return list != null;
    }

    public void setFilters(List<ZKFilter> filters) {
        this.list = ZKFilterVO.convert(filters);
        this.initDataList();
    }

    public void setKw(String kw) {
        this.kw = kw;
        this.initDataList();
    }

    public List<ZKFilter> getFilters() {
        List<ZKFilter> list = new ArrayList<>();
        for (ZKFilterVO filterVO : this.list) {
            if (filterVO != null && StringUtil.isNotBlank(filterVO.getKw())) {
                list.add(filterVO);
            }
        }
        return list;
    }

    private void initDataList() {
        List<ZKFilterVO> list = new ArrayList<>();
        if (this.list != null) {
            for (ZKFilterVO filter : this.list) {
                if (StringUtil.isBlank(this.kw) || StringUtil.containsIgnoreCase(filter.getKw(), this.kw)) {
                    list.add(filter);
                }
            }
        }
        super.setItem(list);
    }

    public void addFilter(ZKFilterVO filter) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.add(filter);
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
