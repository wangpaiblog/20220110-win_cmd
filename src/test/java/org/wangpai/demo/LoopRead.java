package org.wangpai.demo;

import java.util.Scanner;

import static java.lang.System.exit;

public class LoopRead {
    public static void main(String[] args) {
        System.out.println("请输入：");
        var in = new Scanner(System.in);
        while (true) {
            var str = in.nextLine();
            System.out.println(str);

            if (str.equals("quit")) {
                exit(-1);
                break;
            }
        }
    }
}
