package cn.neu.kou.teambuild.main;

import java.io.File;
import java.io.FileWriter;

/**
 * 配置类
 * 实验中使用到的大部分配置信息都放到这个类中
 */
public class Config{
    public static float BLOOM_FALSE_POSTIVE_RATE = 0.1f;   //布隆过滤器的容错率
    public static int BLOOM_EXPERCT_SIZE = 1000;   // 长度应该和查询序列的长度相同

    //如果在不同的机器上跑修改这个路径就行了
    public static String BASE_PATH = "src/main/resources/";

    public static String EMAIL_ORIGIN_MATRIX_FILE_PATH = BASE_PATH + "email-Enron.txt"+File.separator+"email-Enron_test.txt .txt";
    public static String EMAIL_ORIGIN_LABEL_FILE_PATH = BASE_PATH  + "email-Enron.txt"+ File.separator +"email-Enron_label.txt";

    public static String FACEBOOK_ORIGIN_MATRIX_FILE_PATH = BASE_PATH + "facebook_combined.txt" + File.separator + "new_facebook_combined.txt";
    public static String FACEBOOK_ORIGIN_LABEL_FILE_PATH = BASE_PATH  + "facebook_combined.txt" + File.separator + "facebook_combined_label.txt";
    
    public static String JOURNAL_ORIDIN_MATRIX_FILE_PATH=BASE_PATH+"soc-LiveJournal1.txt" + File.separator + "new_soc-LiveJournal1.txt";
    public static String JOURNAL_ORIDIN_LABEL_FILE_PATH=BASE_PATH+"soc-LiveJournal1.txt" + File.separator + "soc-LiveJournal1_label.txt";
    
    
    public static String SMALL_SEARCH_MATRIX_FILE_PATH = BASE_PATH + "query_graph" + File.separator +"small" + File.separator + "small.txt";
    public static String SMALL_SEARCH_LABEL_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "small" + File.separator + "small_lable.txt";

    public static String MEDIUM_1_SEARCH_MATRIX_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "medium_1" + File.separator + "medium_1.txt";
    public static String MEDIUM_1_SEARCH_LABEL_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "medium_1" + File.separator + "medium_1_lable.txt";

    public static String MEDIUM_2_SEARCH_MATRIX_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "medium_2" + File.separator + "new_medium_2.txt";
    public static String MEDIUM_2_SEARCH_LABEL_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "medium_2" + File.separator + "medium_2_lable.txt";

    public static String LARGE_SEARCH_MATRIX_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "large" + File.separator + "large.txt";
    public static String LARGE_SEARCH_LABEL_FILE_PATH = BASE_PATH + "query_graph" + File.separator + "large" + File.separator + "large_lable.txt";
    
    public static boolean IS_SPLIT_TREE = true; 
    public static int User_id =157 ;
}