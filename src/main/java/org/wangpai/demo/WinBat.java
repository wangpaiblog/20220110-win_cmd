package org.wangpai.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 2021-12-21
 */
public class WinBat {
    private int commandNum;

    private Charset cmdCharset;

    private String[] originalCommandMsg;

    private Process[] processArray;

    private String[] outputs;

    private int[] exitValues;

    private WinBat() {
        super();
    }

    public static WinBat getInstance() {
        return new WinBat();
    }

    public String getIntegrationOutput() throws Exception {
        if (this.processArray == null) {
            throw new Exception("需要先执行命令后才能获取输出");
        }

        if (this.outputs == null) {
            // 获取操作系统（命令行）的字符集
            this.cmdCharset = Charset.forName((String) System.getProperties().get("sun.jnu.encoding"));
            this.outputs = new String[this.commandNum];
            InputStream inputStream;
            for (int index = 0; index < this.commandNum; ++index) {
                inputStream = this.processArray[index].getInputStream();
                // 将输出按行分成多个字符串。这原本不是想要的操作，但这没有办法
                var linesOutput = new BufferedReader(new InputStreamReader(
                        inputStream, this.cmdCharset)).lines();
                // 将每行字符串以换行符拼接，还原出原始信息
                this.outputs[index] = linesOutput.collect(Collectors.joining(System.lineSeparator()));
            }
        }

        var stream = Stream.of(this.outputs);
        // 多加一个空行作不同命令输出之间的分隔
        return stream.collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }

    public String getIntegrationCommand() throws Exception {
        if (this.originalCommandMsg == null) {
            throw new Exception("还没有输入命令");
        }

        var stream = Stream.of(this.originalCommandMsg);
        return stream.collect(Collectors.joining(System.lineSeparator()));
    }

    public String[] getOriginalCommand() throws Exception {
        if (this.originalCommandMsg == null) {
            throw new Exception("还没有输入命令");
        }

        return this.originalCommandMsg;
    }

    /**
     * 如果调用本方法时，本方法会阻塞调用线程，直到命令执行结束
     *
     * @since 2021-12-21
     */
    public int[] getExitValues() throws Exception {
        if (this.processArray == null) {
            throw new Exception("需要先执行命令后才能获取退出码");
        }

        this.exitValues = new int[this.commandNum];
        for (int index = 0; index < this.commandNum; ++index) {
            this.exitValues[index] = this.processArray[index].waitFor();
        }

        return this.exitValues;
    }

    /**
     * 如果调用本方法时，本方法会阻塞调用线程，直到命令执行结束
     *
     * @since 2021-12-21
     */
    public int getLastExitValues() throws Exception {
        if (this.processArray == null) {
            throw new Exception("需要先执行命令后才能获取退出码");
        }

        var exitValues = this.getExitValues();
        return exitValues[this.commandNum - 1];
    }

    private WinBat exec(String[] originalCommand) throws Exception {
        this.outputs = null;
        this.originalCommandMsg = originalCommand;
        this.commandNum = originalCommand.length;
        this.processArray = new Process[this.commandNum];

        for (int index = 0; index < this.commandNum; ++index) {
            this.processArray[index] = WinCmd.getInstance()
                    .execute(this.originalCommandMsg[index]).getProcess();
        }

        return this;
    }

    public WinBat execute(String[] command) throws Exception {
        return this.exec(command);
    }
}
