package cn.neu.kou.teambuild.prepare;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import org.apache.ibatis.ognl.OgnlRuntime.ArgsCompatbilityReport;

import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.main.Config;

public class creatdistanceMatrix {
	
	public static void main(String args[]) throws Exception {
       // Origin_graph origin_graph = new Origin_graph(36692, 20, Config.EMAIL_ORIGIN_MATRIX_FILE_PATH, Config.EMAIL_ORIGIN_LABEL_FILE_PATH);
     // Origin_graph origin_graph = new Origin_graph(5000, 20, Config.FACEBOOK_ORIGIN_MATRIX_FILE_PATH, "src/main/resources/facebook_combined.txt/new_facebook_label.txt");
        Origin_graph origin_graph =new Origin_graph(4847571, 20,Config.JOURNAL_ORIDIN_MATRIX_FILE_PATH, Config.JOURNAL_ORIDIN_LABEL_FILE_PATH);
         //origin_graph.read_new_Matrix(Config.JOURNAL_ORIDIN_MATRIX_FILE_PATH);
         //origin_graph.bianli();
         // origin_graph.change_weight("/home/user1/teambuild/TeamBuild/src/main/resources/soc-LiveJournal1.txt", "new_soc-LiveJournal1");
         
      query_graph query_graph = new query_graph(5, 2, Config.SMALL_SEARCH_MATRIX_FILE_PATH, Config.SMALL_SEARCH_LABEL_FILE_PATH);
     // query_graph query_graph = new query_graph(10, 4, Config.MEDIUM_1_SEARCH_MATRIX_FILE_PATH, Config.MEDIUM_1_SEARCH_LABEL_FILE_PATH);
       // query_graph query_graph = new query_graph(15, 6, Config.MEDIUM_2_SEARCH_MATRIX_FILE_PATH, Config.MEDIUM_2_SEARCH_LABEL_FILE_PATH);
     // query_graph query_graph = new query_graph(20, 8, Config.LARGE_SEARCH_MATRIX_FILE_PATH, Config.LARGE_SEARCH_LABEL_FILE_PATH);
      	
      
       query_graph.CFL(origin_graph);
       origin_graph.bianli();
       query_graph.printseq();
       
       AvailableNeighbor availableNeighbor=new AvailableNeighbor(query_graph, origin_graph);
       
       availableNeighbor.init();
       
       String filename=Config.BASE_PATH+"/soc-LiveJournal1.txt/soc-LivedistanceMatrix_small.txt";
       availableNeighbor.toFile(filename);
       //availableNeighbor.toFile(Config.BASE_PATH+"/soc-LiveJournal1.txt"+"soc-LivedistanceMatrix.txt");
       
	}

}
