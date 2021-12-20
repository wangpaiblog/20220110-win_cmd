package org.wangpai.demo;

import org.junit.jupiter.api.Test;

class WinCmdTest {

    public static void main(String[] args) throws Exception {
        var cmd = WinCmd.getInstance();
        System.out.println(cmd.execute("java --version").getOutput());
        System.out.println("退出码：" + cmd.getExitValue());
    }

    @Test
    void getExitValue() {
    }

    @Test
    void getOutput() {
    }

    @Test
    void execute() {
    }

    @Test
    void testExecute() {
    }

    @Test
    void getInstance() {
    }
}