package com.example.ayuan.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ayuan
 * @Description: 获取系统信息工具
 * @date 2023/5/25 12:51
 */
public class SystemUtils {

    // 获取 CPU 序列号
    public static String getCpuSerialNumber() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            // Windows 操作系统下获取 CPU 序列号
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"reg", "query", "HKEY_LOCAL_MACHINE\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0", "/v", "ProcessorNameString"});
                process.getOutputStream().close();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = br.readLine();
                if (line != null) {
                    line = br.readLine();
                    if (line != null) {
                        String[] vals = line.split("\\s{2,}");
                        if (vals.length > 1) {
                            return vals[1];
                        }
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (os.startsWith("linux")) {
            // Linux/Unix 操作系统下获取 CPU 序列号
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cat /proc/cpuinfo | grep 'serial\\|Serial' | sed -n 1p | awk '{print $NF}'"});
                process.getOutputStream().close();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = br.readLine();
                if (line != null) {
                    return line.trim();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (os.startsWith("mac")) {
            //Mac OS X 操作系统下获取 CPU 序列号
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "system_profiler SPHardwareDataType |grep \"r (system)"});
                process.getOutputStream().close();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = br.readLine();
                if (line != null) {
                    return line.trim();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> env = NetworkInterface.getNetworkInterfaces();
            while (env.hasMoreElements()) {
                NetworkInterface face = env.nextElement();
                if (!face.isUp() || face.isLoopback() || face.isVirtual()) {
                    continue;
                }
                byte[] mac = face.getHardwareAddress();
                if (mac == null) {
                    continue;
                }
                return Stream.of(mac)
                        .map(b -> String.format("%02X", b))
                        .collect(Collectors.joining("-"))
                        .toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String macAddress = getMacAddress();
        System.out.println(macAddress);
    }
}
