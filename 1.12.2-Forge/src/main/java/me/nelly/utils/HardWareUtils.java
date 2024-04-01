package me.nelly.utils;

import nellyobfuscator.NellyClassObfuscator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;


// form csdn
@NellyClassObfuscator
public class HardWareUtils {

    /**
     * 获取主板序列号30
     * @return
     * <p>
     */

    public static String getMotherboardSN() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"

                    + "Set colItems = objWMIService.ExecQuery _ \n"

                    + " (\"Select * from Win32_BaseBoard\") \n"

                    + "For Each objItem in colItems \n"

                    + " Wscript.Echo objItem.SerialNumber \n"

                    + " exit for ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);

            }
            input.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result.toString().trim();

    }

    /**
     * 获取硬盘序列号
     *@paramdrive
     * 盘符
     *@return
     * <p>
     */

    public static String getHardDiskSN(String drive) {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);


            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"

                    + "Set colDrives = objFSO.Drives\n"

                    + "Set objDrive = colDrives.item(\""

                    + drive + "\")\n"

                    + "Wscript.Echo objDrive.SerialNumber"; //see note

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);

            }
            input.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result.toString().trim();

    }

    /**
     * 101 * 获取CPU序列号102 *103 *@return
     * <p>
     * 104
     */

    public static String getCPUSerial() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"

                    + "Set colItems = objWMIService.ExecQuery _ \n"

                    + " (\"Select * from Win32_Processor\") \n"

                    + "For Each objItem in colItems \n"

                    + " Wscript.Echo objItem.ProcessorId \n"

                    + " exit for ' do the first cpu only! \n" + "Next \n";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.toString().trim().isEmpty() || result == null) {
            result = new StringBuilder("无CPU_ID被读取");
        }
        return result.toString().trim();
    }


    /**
     * 139 * 获取MAC地址
     */

    public static String getMac() {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            return "";
        }

    }


    public static void main(String[] args) throws Exception {



        String encode = DesUtils.encode( getCPUSerial()
                + getMotherboardSN().replace(".", "")
                + getHardDiskSN("c")
                + getMac().replace("-", ""));
        System.out.println("加密：" + encode);
        String decode = DesUtils.decode(encode);
        System.out.println("解密：" + decode);
    }
}

