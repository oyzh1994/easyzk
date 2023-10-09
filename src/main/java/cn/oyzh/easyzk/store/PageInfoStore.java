package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.FileStore;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.PageInfo;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/01/17
 */
@Slf4j
public class PageInfoStore extends FileStore<PageInfo> {

    /**
     * 当前实例
     */
    public static final PageInfoStore INSTANCE = new PageInfoStore();

    /**
     * 当前设置
     */
    public static final PageInfo PAGE_INFO = INSTANCE.loadOne();

    {
        this.filePath(ZKConst.STORE_PATH + "page_info.json");
        log.info("PageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<PageInfo> load() {
        PageInfo pageInfo = null;
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isNotBlank(text)) {
            try {
                pageInfo = JSON.parseObject(text, PageInfo.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (pageInfo == null) {
            pageInfo = new PageInfo();
        }
        return List.of(pageInfo);
    }

    @Override
    public boolean add(@NonNull PageInfo data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(@NonNull PageInfo data) {
        return this.save(data);
    }

    @Override
    public boolean delete(@NonNull PageInfo data) {
        throw new UnsupportedOperationException();
    }
}
