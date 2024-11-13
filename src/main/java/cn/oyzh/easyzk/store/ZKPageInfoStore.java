package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKPageInfo;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.json.ObjectFileStore;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/01/17
 */
//@Slf4j
@Deprecated
public class ZKPageInfoStore extends ObjectFileStore<ZKPageInfo> {

    /**
     * 当前实例
     */
    public static final ZKPageInfoStore INSTANCE = new ZKPageInfoStore();

    // /**
    //  * 当前设置
    //  */
    // public static final ZKPageInfo PAGE_INFO = INSTANCE.load();

    // {
    //     this.filePath(ZKConst.STORE_PATH + "page_info.json");
    //     JulLog.info("PageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    // }

    public ZKPageInfoStore() {
        this.filePath(ZKConst.STORE_PATH + "page_info.json");
    }

    @Override
    public synchronized ZKPageInfo load() {
        ZKPageInfo pageInfo = null;
        try {
            // 从文件中读取文本内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StringUtil.isNotBlank(text)) {
                // 将文本内容转换为页面信息对象
                pageInfo = JSONUtil.toBean(text, ZKPageInfo.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果页面信息为空，则创建一个新的页面信息对象
        if (pageInfo == null) {
            pageInfo = new ZKPageInfo();
        }
        return pageInfo;
    }
}
