package cn.neu.kou.teambuild.io;

import java.io.*;

public class zhijieliu_in_test {

    public static void main(String[] args) {
        // 1.定义目标文件
        File srcFile = new File("D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt\\email-Enron.txt");
        // 2.创建一个流，指向目标文件
        InputStream is = null;
        try {
            is = new FileInputStream(srcFile);
            // 3.循环往外流
            int content = is.read();
            // 4.循环打印
            while (content != -1) {
                System.out.print((char) content);
                content = is.read();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭io流
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
