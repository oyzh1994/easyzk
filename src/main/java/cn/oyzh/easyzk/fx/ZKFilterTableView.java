package cn.oyzh.easyzk.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.vo.ZKFilterVO;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import lombok.Getter;

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
    @Getter
    private List<ZKFilterVO> filters;

    private String kw;

    public void setFilers(List<ZKFilter> filters) {
        this.filters = ZKFilterVO.convert(filters);
        this.initDataList();
    }

    public void setKw(String kw) {
        this.kw = kw;
        this.initDataList();
    }

    public List<ZKFilter> getFilers() {
        List<ZKFilter> filters = new ArrayList<>();
        for (ZKFilterVO filterVO : this.filters) {
            if (filterVO != null && StringUtil.isNotBlank(filterVO.getKw())) {
                filters.add(filterVO);
            }
        }
        return filters;
    }

    private void initDataList() {
        List<ZKFilterVO> list = new ArrayList<>();
        if (this.filters != null) {
            for (ZKFilterVO filter : this.filters) {
                if (StringUtil.isBlank(this.kw) || StringUtil.containsIgnoreCase(filter.getKw(), this.kw)) {
                    list.add(filter);
                }
            }
        }
        super.setItem(list);
    }

    public void addFilter(ZKFilter filter) {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        this.filters.add(ZKFilterVO.convert(filter));
        this.initDataList();
    }

    @Override
    public void removeItem(Object item) {
        super.removeItem(item);
        if (this.filters != null) {
            this.filters.remove(item);
        }
        this.initDataList();
    }
}
