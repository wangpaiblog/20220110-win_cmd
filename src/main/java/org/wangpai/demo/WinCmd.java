package org.wangpai.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class WinCmd {
    private Process process;

    private int exitValue;

    private Object command;

    private InputStream inputStream;

    private Charset cmdCharset;

    private WinCmd() {
        super();
    }

    /**
     * 如果调用本方法时，本方法会阻塞调用线程，直到命令执行结束
     *
     * @since 2021-12-21
     */
    public int getExitValue() throws Exception {
        if (this.process == null) {
            throw new Exception("需要先执行命令后才能获取退出码");
        }

        this.exitValue = process.waitFor();
        return this.exitValue;
    }

    public String getOutput() throws Exception {
        if (this.process == null) {
            throw new Exception("需要先执行命令后才能获取输出");
        }

        this.inputStream = process.getInputStream();
        // 获取操作系统（命令行）的字符集
        this.cmdCharset = Charset.forName((String) System.getProperties().get("sun.jnu.encoding"));
        var in = new BufferedReader(new InputStreamReader(this.inputStream, this.cmdCharset));
        var line = in.readLine();
        var output = new StringBuilder();

        while (line != null) {
            output.append(line).append(System.lineSeparator());
            line = in.readLine();
        }

        return output.toString();
    }

    private WinCmd exec(Object command) throws Exception {
        this.command = command;
        if (command instanceof String) {
            this.process = Runtime.getRuntime().exec((String) this.command);
        } else if (command instanceof String[]) {
            this.process = Runtime.getRuntime().exec((String[]) this.command);
        } else {
            throw new Exception("提供了无法识别的命令实参类型");
        }

        return this;
    }

    public WinCmd execute(String command) throws Exception {
        return this.exec(command);
    }

    public WinCmd execute(String[] command) throws Exception {
        return this.exec(command);
    }

    public static WinCmd getInstance() {
        return new WinCmd();
    }
}
