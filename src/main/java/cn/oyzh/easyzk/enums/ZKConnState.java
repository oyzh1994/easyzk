package cn.oyzh.easyzk.enums;

import org.apache.curator.framework.state.ConnectionState;

/**
 * zk连接状态
 *
 * @author oyzh
 * @since 2023/6/1
 */
public enum ZKConnState {
    CONNECTED {
        public boolean isConnected() {
            return true;
        }
    },
    CONNECTING {
        public boolean isConnected() {
            return false;
        }
    },
    SUSPENDED {
        public boolean isConnected() {
            return false;
        }
    },
    RECONNECTED {
        public boolean isConnected() {
            return true;
        }
    },
    LOST {
        public boolean isConnected() {
            return false;
        }
    },
    READ_ONLY {
        public boolean isConnected() {
            return true;
        }
    },
    CLOSED {
        public boolean isConnected() {
            return false;
        }
    },
    FAILED {
        public boolean isConnected() {
            return false;
        }
    };

    public abstract boolean isConnected();

    public static ZKConnState valueOf(ConnectionState state) {
        if (state == null) {
            return ZKConnState.CLOSED;
        }
        return switch (state) {
            case LOST -> ZKConnState.LOST;
            case CONNECTED -> ZKConnState.CONNECTED;
            case READ_ONLY -> ZKConnState.READ_ONLY;
            case SUSPENDED -> ZKConnState.SUSPENDED;
            case RECONNECTED -> ZKConnState.RECONNECTED;
        };
    }
}
