package cn.neu.kou.teambuild.io;

import java.util.HashMap;
import java.util.Iterator;

import cn.neu.kou.teambuild.cpi.CpiTree;
import cn.neu.kou.teambuild.graph.Edge;
import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.lables;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.rarestFirst.labeltest;

public class big_test01 {

    public static void main(String args[]) throws Exception {
    
        String origin_path = "D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt\\new_email-Enron.txt";
        String origin_lable ="D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt\\email-Enron_label.txt ";
        String query_path_s="D:\\学习\\kou_exp\\query_graph\\small\\small.txt";
        String query_label="D:\\学习\\kou_exp\\query_graph\\small\\small_lable.txt";
       // String change_origin_test = "D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt";
       // String change_filename = "email-Enron_test.txt ";
        Origin_graph test=new Origin_graph(36692,20, origin_path, origin_lable);
        query_graph small = new query_graph(5,2, query_path_s, query_label);
        
        //test.change_weight(change_origin_test, change_filename);
        labeltest labeltest=new labeltest(small, test);
        labeltest.shixian();
        labeltest.print_result();
        /*
        small.CFL(test);
        CpiTree test_tree=new CpiTree(test,small);
        test_tree.buildTree();
        System.out.print(21);
        */
        /*
        small.printseq();
        small.printquery_matrix();
    	HashMap<Integer,Integer> result=new HashMap<>();
		int count=0;
		while(count<10) {
			result.put(count++, count);
		}
		*/
    	
}
}
