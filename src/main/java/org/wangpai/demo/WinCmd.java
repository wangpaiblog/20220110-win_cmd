package org.wangpai.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @since 2021-12-21
 */
@Accessors(chain = true)
public class WinCmd {
    @Setter(AccessLevel.PUBLIC)
    private Charset outputCharset;

    @Setter(AccessLevel.PUBLIC)
    private Charset inputCharset;

    @Getter(AccessLevel.PROTECTED)
    private Process process;

    private List<String> originalCommands = new ArrayList<>();

    private OutputStream console;

    private String output;

    private int exitValue;

    /**
     * 判断向命令行的输入是否关闭
     *
     * @deprecated 此字段的判断不一定准确。当此字段为 false 时，真实值不一定如此
     * @since 2021-12-22
     */
    @Deprecated
    @Getter(AccessLevel.PUBLIC)
    private boolean closed;

    private WinCmd() {
        super();

        this.setDefaultOutputCharset();
        this.setDefaultInputCharset();
    }

    public static WinCmd getInstance() {
        return new WinCmd();
    }

    /**
     * 设置来自命令行的输出的字符集
     *
     * @since 2021-12-22
     */
    public void setDefaultOutputCharset() {
        // 获取操作系统（命令行）的字符集
        this.outputCharset = Charset.forName((String) System.getProperties().get("sun.jnu.encoding"));
    }

    /**
     * 设置向命令行的输入的字符集
     *
     * @since 2021-12-22
     */
    public void setDefaultInputCharset() {
        this.inputCharset = Charset.defaultCharset();
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
        this.closed = true;
        return this.exitValue;
    }

    /**
     * 如果调用本方法时，本方法会阻塞调用线程，直到命令执行结束
     *
     * 在调用本方法之后，输入流将关闭，这意味着不能再使用本对象执行新的命令
     *
     * @since 2021-12-21
     */
    public String getOutput() throws Exception {
        if (this.process == null) {
            throw new Exception("需要先执行命令后才能获取输出");
        }
        if (this.output != null) {
            return this.output;
        }

        // 如果不关闭 console，则从第二条命令开始，后续输入的命令将不被执行
        this.console.close();
        this.closed = true;

        var inputStream = process.getInputStream();

        // 将输出按行分成多个字符串。这原本不是想要的操作，但这没有办法
        var lines = new BufferedReader(new InputStreamReader(
                inputStream, this.outputCharset)).lines();
        this.output = lines.collect(Collectors.joining(System.lineSeparator()));
        // 将每行字符串以换行符拼接，还原出原始信息
        return this.output;
    }

    public List<String> getCommands() throws Exception {
        if (this.originalCommands == null) {
            throw new Exception("还没有输入命令");
        }

        return this.originalCommands;
    }

    public WinCmd execute(String command) throws Exception {
        return this.exec(command);
    }

    /**
     * 此方法将使用上次的 Process 对象来执行命令
     *
     * 如果程序已经结束、输入已经关闭，则使用本方法将引发异常
     *
     * @since 2021-12-22
     */
    public WinCmd next(String command) throws IOException {
        this.console.write(command.getBytes(this.inputCharset));
        this.originalCommands.add(command);

        return this;
    }

    /**
     * 此方法会开启一个新的 Process 对象来执行命令。
     * 此方法只能调用一次
     *
     * @since 2021-12-21
     * @lastModified 2021-12-22
     */
    private WinCmd exec(String command) throws Exception {
        if (this.process != null) {
            throw new Exception("已经调用过此方法");
        }
        this.originalCommands.add(command);
        this.process = Runtime.getRuntime().exec(command);
        this.console = this.process.getOutputStream();
        return this;
    }
}
