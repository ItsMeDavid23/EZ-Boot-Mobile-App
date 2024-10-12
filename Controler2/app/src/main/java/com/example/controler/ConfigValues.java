// ConfigValues.java
package com.example.controler;

public class ConfigValues {
    private String macAddress;
    private String serverIp;
    private String serverPort;

    public ConfigValues(String macAddress, String serverIp, String serverPort) {
        this.macAddress = macAddress;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }
}