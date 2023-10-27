package cn.oyzh.easyzk.parser;

import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import lombok.NonNull;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * zk状态解析器
 *
 * @author oyzh
 * @since 2020/3/26
 */
public class ZKStatParser implements Function<Stat, List<FriendlyInfo<Stat>>> {

    /**
     * 当前实例
     */
    public final static ZKStatParser INSTANCE = new ZKStatParser();

    @Override
    public List<FriendlyInfo<Stat>> apply(@NonNull Stat stat) {
        List<FriendlyInfo<Stat>> statInfos = new ArrayList<>();

        FriendlyInfo<Stat> czxid = new FriendlyInfo<>();
        czxid.name("czxid");
        czxid.value(stat.getCzxid());
        czxid.friendlyName("创建节点事务ID");
        czxid.friendlyValue(czxid.value());

        FriendlyInfo<Stat> mzxid = new FriendlyInfo<>();
        mzxid.name("mzxid");
        mzxid.value(stat.getMzxid());
        mzxid.friendlyName("最后修改节点事务ID");
        mzxid.friendlyValue(mzxid.value());

        FriendlyInfo<Stat> pzxid = new FriendlyInfo<>();
        pzxid.name("pzxid");
        pzxid.value(stat.getPzxid());
        pzxid.friendlyName("最后修改子节点事务ID");
        pzxid.friendlyValue(pzxid.value());

        FriendlyInfo<Stat> ctime = new FriendlyInfo<>();
        ctime.name("ctime");
        ctime.value(stat.getCtime());
        ctime.friendlyName("创建时间");
        if (ctime.value().equals(0L)) {
            ctime.friendlyValue("无");
        } else {
            ctime.friendlyValue(Const.DATE_FORMAT.format(ctime.value()));
        }

        FriendlyInfo<Stat> mtime = new FriendlyInfo<>();
        mtime.name("mtime");
        mtime.value(stat.getMtime());
        mtime.friendlyName("最后修改时间");
        if (mtime.value().equals(0L)) {
            mtime.friendlyValue("无");
        } else {
            mtime.friendlyValue(Const.DATE_FORMAT.format(mtime.value()));
        }

        FriendlyInfo<Stat> version = new FriendlyInfo<>();
        version.name("version");
        version.value(stat.getVersion());
        version.friendlyName("数据版本号");
        version.friendlyValue(version.value());

        FriendlyInfo<Stat> aversion = new FriendlyInfo<>();
        aversion.name("aversion");
        aversion.value(stat.getAversion());
        aversion.friendlyName("权限版本号");
        aversion.friendlyValue(aversion.value());

        FriendlyInfo<Stat> cversion = new FriendlyInfo<>();
        cversion.name("cversion");
        cversion.value(stat.getCversion());
        cversion.friendlyName("子节点版本号");
        cversion.friendlyValue(cversion.value());

        FriendlyInfo<Stat> numChildren = new FriendlyInfo<>();
        numChildren.name("numChildren");
        numChildren.value(stat.getNumChildren());
        numChildren.friendlyName("子节点数量");
        numChildren.friendlyValue(numChildren.value());

        FriendlyInfo<Stat> dataLength = new FriendlyInfo<>();
        dataLength.name("dataLength");
        dataLength.value(stat.getDataLength());
        dataLength.friendlyName("数据长度");
        dataLength.friendlyValue(dataLength.value());

        FriendlyInfo<Stat> ephemeralOwner = new FriendlyInfo<>();
        ephemeralOwner.name("ephemeralOwner");
        ephemeralOwner.value(stat.getEphemeralOwner());
        ephemeralOwner.friendlyName("会话ID");
        ephemeralOwner.friendlyValue(ephemeralOwner.value());

        statInfos.add(czxid);
        statInfos.add(mzxid);
        statInfos.add(pzxid);
        statInfos.add(ctime);
        statInfos.add(mtime);
        statInfos.add(version);
        statInfos.add(aversion);
        statInfos.add(cversion);
        statInfos.add(dataLength);
        statInfos.add(numChildren);
        statInfos.add(ephemeralOwner);
        return statInfos;
    }
}
