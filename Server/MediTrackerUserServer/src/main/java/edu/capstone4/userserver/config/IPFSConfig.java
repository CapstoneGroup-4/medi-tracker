package edu.capstone4.userserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPFSConfig {

    @Value("${remote.ipfs.host}")
    private String ipfsHost;

    @Value("${remote.ipfs.port}")
    private int ipfsPort;

    @Value("${remote.ipfs.protocol}")
    private String ipfsProtocol;

    public String getIpfsHost() {
        return ipfsHost;
    }

    public void setIpfsHost(String ipfsHost) {
        this.ipfsHost = ipfsHost;
    }

    public int getIpfsPort() {
        return ipfsPort;
    }

    public void setIpfsPort(int ipfsPort) {
        this.ipfsPort = ipfsPort;
    }

    public String getIpfsProtocol() {
        return ipfsProtocol;
    }

    public void setIpfsProtocol(String ipfsProtocol) {
        this.ipfsProtocol = ipfsProtocol;
    }
}
