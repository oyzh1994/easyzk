package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.PageInfo;
import cn.oyzh.fx.common.store.ObjectFileStore;
import com.alibaba.fastjson.JSON;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/01/17
 */
//@Slf4j
public class PageInfoStore extends ObjectFileStore<PageInfo> {

    /**
     * 当前实例
     */
    public static final PageInfoStore INSTANCE = new PageInfoStore();

    /**
     * 当前设置
     */
    public static final PageInfo PAGE_INFO = INSTANCE.load();

    {
        this.filePath(ZKConst.STORE_PATH + "page_info.json");
        StaticLog.info("PageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized PageInfo load() {
        PageInfo pageInfo = null;
        try {
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isNotBlank(text)) {
                pageInfo = JSON.parseObject(text, PageInfo.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (pageInfo == null) {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }
}
