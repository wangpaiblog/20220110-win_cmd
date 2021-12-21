package org.wangpai.demo;

public class WinBatTest {
    public static void main(String[] args) throws Exception {
        var bat = WinBat.getInstance();
        var commands = new String[]{
                "ping www.baidu.com",
                "where java"
        };
        System.out.println(bat.execute(commands).getIntegrationOutput());
        System.out.println();
        for (var value : bat.getExitValues()) {
            System.out.println("退出码：" + value);
        }
    }
}