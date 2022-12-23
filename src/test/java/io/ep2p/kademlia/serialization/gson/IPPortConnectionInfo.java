package io.ep2p.kademlia.serialization.gson;

import com.google.common.base.Objects;
import io.ep2p.kademlia.connection.ConnectionInfo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IPPortConnectionInfo implements ConnectionInfo {
    private String host;
    private int port;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPPortConnectionInfo that = (IPPortConnectionInfo) o;
        return getPort() == that.getPort() && Objects.equal(getHost(), that.getHost());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getHost(), getPort());
    }
}
