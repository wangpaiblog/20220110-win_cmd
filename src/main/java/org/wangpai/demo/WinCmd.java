package org.wangpai.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @since 2021-12-21
 */
@Accessors(chain = true)
public class WinCmd {
    private Charset cmdCharset;

    @Getter(AccessLevel.PUBLIC)
    private Process process;

    private String originalCommandMsg;

    private String output;

    private int exitValue;

    private WinCmd() {
        super();
    }

    public static WinCmd getInstance() {
        return new WinCmd();
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
        if (this.output != null) {
            return this.output;
        }

        var inputStream = process.getInputStream();
        // 获取操作系统（命令行）的字符集
        this.cmdCharset = Charset.forName((String) System.getProperties().get("sun.jnu.encoding"));
        // 将输出按行分成多个字符串。这原本不是想要的操作，但这没有办法
        var lines = new BufferedReader(new InputStreamReader(
                inputStream, this.cmdCharset)).lines();
        this.output = lines.collect(Collectors.joining(System.lineSeparator()));
        // 将每行字符串以换行符拼接，还原出原始信息
        return this.output;
    }

    public String getCommand() throws Exception {
        if (this.originalCommandMsg == null) {
            throw new Exception("还没有输入命令");
        }

        return this.originalCommandMsg;
    }

    public WinCmd execute(String command) throws Exception {
        return this.exec(command);
    }

    private WinCmd exec(String originalCommand) throws IOException {
        this.output = null;
        this.originalCommandMsg = originalCommand;
        this.process = Runtime.getRuntime().exec(this.originalCommandMsg);
        return this;
    }
}
