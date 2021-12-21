package org.wangpai.demo;

public class WinCmdTest {
    public static void main(String[] args) throws Exception {
        var cmd = WinCmd.getInstance();

        System.out.println(cmd.execute("ping baidu.com").getOutput());
        System.out.println(System.lineSeparator() + "退出码：" + cmd.getExitValue());

        System.out.println();
        System.out.println(cmd.execute("where java").getOutput());
        System.out.println(System.lineSeparator() + "退出码：" + cmd.getExitValue());
    }
}