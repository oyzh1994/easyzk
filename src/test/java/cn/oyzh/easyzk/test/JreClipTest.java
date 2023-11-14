package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.clip.clipper.JreClipConfig;
import cn.oyzh.fx.pkg.clip.clipper.JreClipper;
import org.junit.Test;

import java.io.IOException;

/**
 * @author oyzh
 * @since 2023/3/15
 */
public class JreClipTest {

    @Test
    public void test_jre_clip() throws IOException {
        String jreClipConfig = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\win_jre_clip_config.json";
        JreClipConfig config = JreClipConfig.fromConfig(jreClipConfig);
        JreClipper jreClipper = new JreClipper();
        jreClipper.clip(config);
    }
}
