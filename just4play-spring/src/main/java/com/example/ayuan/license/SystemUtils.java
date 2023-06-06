package com.example.ayuan.license;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Enumeration;

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
                Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "system_profiler SPHardwareDataType |grep 'r (system)' | awk '{print $NF}'"});
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


    @SneakyThrows
    protected static String getMacAddress() {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                }
                return sb.toString();
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String macAddress = getMacAddress();
//        System.out.println(macAddress);

        String cpuSerialNumber = getCpuSerialNumber();
        System.out.println(cpuSerialNumber);

    }
}
