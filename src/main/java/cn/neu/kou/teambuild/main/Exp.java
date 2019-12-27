package cn.neu.kou.teambuild.main;

import java.awt.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import cn.neu.kou.teambuild.cpi.CpiTree;
import cn.neu.kou.teambuild.cpi.Search;
import cn.neu.kou.teambuild.graph.Edge;
import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.lables;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.prepare.AvailableNeighbor;
import cn.neu.kou.teambuild.rarestFirst.Sumfirst;
import cn.neu.kou.teambuild.rarestFirst.labeltest;
import cn.neu.kou.teambuild.rarestFirst.rarest_build;
import cn.neu.kou.teambuild.rarestFirst.steiner;


/**
 * @author : lq
 * @date : 2019/4/8
 */
public class Exp {

    public static void main(String[] args) throws Exception {
        test();
    }

    public static void test() throws Exception {
    	//lables journaLables=new lables("D:\\git\\TeamBuild\\src\\main\\resources\\soc-LiveJournal1.txt", 4847571, 20, "soc-LiveJournal1_label");
    	
    	
       Origin_graph origin_graph = new Origin_graph(36692, 20, Config.EMAIL_ORIGIN_MATRIX_FILE_PATH, Config.EMAIL_ORIGIN_LABEL_FILE_PATH);
//      Origin_graph origin_graph = new Origin_graph(4039, 20, Config.FACEBOOK_ORIGIN_MATRIX_FILE_PATH, "src/main/resources/facebook_combined.txt/new_facebook_label.txt");
//        Origin_graph origin_graph =new Origin_graph(4847571, 20,Config.JOURNAL_ORIDIN_MATRIX_FILE_PATH, Config.JOURNAL_ORIDIN_LABEL_FILE_PATH);
//         origin_graph.read_new_Matrix(Config.JOURNAL_ORIDIN_MATRIX_FILE_PATH);
//         origin_graph.bianli();
         // origin_graph.change_weight("/home/user1/teambuild/TeamBuild/src/main/resources/soc-LiveJournal1.txt", "new_soc-LiveJournal1");
         
      //query_graph query_graph = new query_graph(5, 2, Config.SMALL_SEARCH_MATRIX_FILE_PATH, Config.SMALL_SEARCH_LABEL_FILE_PATH);
     // query_graph query_graph = new query_graph(10, 4, Config.MEDIUM_1_SEARCH_MATRIX_FILE_PATH, Config.MEDIUM_1_SEARCH_LABEL_FILE_PATH);
        query_graph query_graph = new query_graph(15, 6, Config.MEDIUM_2_SEARCH_MATRIX_FILE_PATH, Config.MEDIUM_2_SEARCH_LABEL_FILE_PATH);
     // query_graph query_graph = new query_graph(20, 8, Config.LARGE_SEARCH_MATRIX_FILE_PATH, Config.LARGE_SEARCH_LABEL_FILE_PATH);
      	
        
       query_graph.CFL(origin_graph);
      // origin_graph.bianli();
       query_graph.printseq();
       
      // origin_graph.get_graphlabel().change_lable("src/main/resources/email-Enron.txt", "new_Enron_label");
       //System.out.println("6 label count: "+origin_graph.getlable_count(2817));

       //origin_graph.get_graphlabel().change_lable(filePath, fileName);
         
        
        /*
        Sumfirst sumtest=new Sumfirst(query_graph, origin_graph);
        labeltest testlable= new labeltest(query_graph, origin_graph);
        rarest_build testRarest_build=new rarest_build(query_graph, origin_graph);
        steiner testSteiner=new steiner(query_graph, origin_graph);
        
       
        testlable.shixian();
        //testlable.print_result();
        System.out.println("label  mindia:"+testRarest_build.get_maxdia(testlable.get_seq()));
        System.out.println("label  minsum:"+sumtest.com_Sum(testlable.get_seq()));
        System.out.println("label  steiner:"+testSteiner.prim( testSteiner.get_matrix(testlable.get_seq())));
        System.out.println("label  struct:"+testlable.com_struct(testlable.get_seq()));
       
      
        testRarest_build.set_result();
       // testRarest_build.print_result();
        System.out.println("rarest  mindia:"+testRarest_build.get_max_rarest());
        System.out.println("rarest  minsum:"+sumtest.com_Sum(testRarest_build.get_result_seq()));
        System.out.println("rarest  striner:"+testSteiner.prim( testSteiner.get_matrix(testRarest_build.get_result_seq())));
        System.out.println("rarest  struct:"+testlable.com_struct(testRarest_build.get_result_seq()));
        
        
        sumtest.set_result();
        //sumtest.print_seq();
        System.out.println("sumfirst  mindia:"+testRarest_build.get_maxdia(sumtest.get_seq()));
        System.out.println("sumfirst  minsum:"+sumtest.get_sum());
        System.out.println("sumfirst  striner:"+testSteiner.prim( testSteiner.get_matrix(sumtest.get_seq())));
        System.out.println("sumfirst  struct:"+testlable.com_struct(sumtest.get_seq()));
        
        
        testSteiner.set_result();
       // testSteiner.print_result();
        System.out.println("steiner  mindia:"+testRarest_build.get_maxdia(testSteiner.get_result_seq()));
        System.out.println("steiner  minsum:"+sumtest.com_Sum(testSteiner.get_result_seq()));
        System.out.println("steiner  striner:"+testSteiner.get_steiner());
        System.out.println("steiner  struct:"+testlable.com_struct(testSteiner.get_result_seq()));
        */
        
      AvailableNeighbor availableNeighbor=new AvailableNeighbor(query_graph, origin_graph);
//      availableNeighbor.init();
//      availableNeighbor.toFile(Config.EMAIL_ORIGIN_MATRIX_FILE_PATH_MEDIUM2);
      availableNeighbor.fromFile(Config.EMAIL_ORIGIN_MATRIX_FILE_PATH_MEDIUM2);
//    availableNeighbor.fromFile(Config.FACEBOOK_ORIGIN_MATRIX_FILE_PATH_MEDIUM2);
//     StringBuilder stringBuilder=new StringBuilder();
//
      System.gc();
      long total = Runtime.getRuntime().totalMemory(); // byte
      long m1 = Runtime.getRuntime().freeMemory();
     System.out.println("before:" + (total - m1)/1024.0);

      CpiTree cpiTree = new CpiTree(origin_graph, query_graph,availableNeighbor);


        //int a=s.nextInt();
        cpiTree.buildTree();

        System.gc();
        long total1 = Runtime.getRuntime().totalMemory();
        long m2 = Runtime.getRuntime().freeMemory();
        System.out.println("after creat tree:" + (total1 - m2)/1024.0);

        //a=s.nextInt();
       cpiTree.splitTree();

       System.gc();
       long total2 = Runtime.getRuntime().totalMemory();
       long m3 = Runtime.getRuntime().freeMemory();
       System.out.println("after top -dowm :" + (total2 - m3)/1024.0);

       // a=s.nextInt();
       cpiTree.bottom_up();

       System.gc();
       long total3 = Runtime.getRuntime().totalMemory();
       long m4 = Runtime.getRuntime().freeMemory();
       System.out.println("after bottom_up :" + (total3 - m4)/1024.0);

       // a=s.nextInt();
        System.out.println("spilt over!");

        long startTime_1 = System.currentTimeMillis();

        Search search = new Search(cpiTree,1000);
        search.generateAllTeam();
        search.getValidateTeamSet();
        System.out.println(search.getValidateTeamSet().size());

        Criteria criteria = new Criteria(origin_graph, query_graph);
        MinCostTeam minCostTeam = new MinCostTeam(criteria);
        minCostTeam.minStructRestrictTeam(search.getValidateTeamSet());
        minCostTeam.printMinStructRestricTeamAllCriteria();

        long endTime_1 = System.currentTimeMillis();
        System.out.println("不考虑匹配顺序:"+(endTime_1-startTime_1)+"ms");
        
        //删除标签的实现
        /*
        cpiTree.delete_node(origin_graph.delte_label(20));
        cpiTree.delete_node(origin_graph.delte_label(20));
        cpiTree.delete_node(origin_graph.delte_label(20));
        cpiTree.delete_node(origin_graph.delte_label(20));
        */
        
        
        



        long startTime_2 = System.currentTimeMillis();
        
        Search search_2 = new Search(cpiTree, 20000);
        search_2.generateAllTeam();
        System.out.println(search_2.getValidateTeamSet().size());
        
        minCostTeam.minStructRestrictTeam(search_2.getValidateTeamSet());
        minCostTeam.printMinStructRestricTeamAllCriteria();
        
        long endTime_2 = System.currentTimeMillis();
        System.out.println("考虑匹配顺序，对CPG进行了标准化，但未剪枝:"+(endTime_2-startTime_2)+"ms");
        
        
        long startTime_3 = System.currentTimeMillis();

        Search search_3=new Search(cpiTree, 1000);
        search_3.generateAllTeam();
        System.out.println(search_3.getValidateTeamSet().size());

        minCostTeam.minStructRestrictTeam(search_3.getValidateTeamSet());
        minCostTeam.printMinStructRestricTeamAllCriteria();

        long endTime_3 = System.currentTimeMillis();
        System.out.println("考虑匹配顺序，对CPG进行了标准化，且剪枝（本文）:"+(endTime_3-startTime_3)+"ms");
        

        
        /*
        Criteria criteria = new Criteria(origin_graph, query_graph);
        MinCostTeam minCostTeam = new MinCostTeam(criteria);
        minCostTeam.minStructRestrictTeam(search.getValidateTeamSet());
        minCostTeam.printMinStructRestricTeamAllCriteria();
        System.out.println(12);
       */
        
        //List<Integer> del_node=origin_graph.delte_label(20);
        //List<Integer> del_node=origin_graph.delte_label(50);
       //List<Integer> del_node=origin_graph.delte_label(75);
        //List<Integer> del_node=origin_graph.delte_label(100);
        /*
        long startTime_1 = System.currentTimeMillis(); 
        CpiTree cpiTree_1 = new CpiTree(origin_graph, query_graph);
        cpiTree_1.buildTree();
        cpiTree_1.splitTree();
        Search search_1 = new Search(cpiTree, 100);
        search_1.generateAllTeam();
        System.out.println(search_1.getValidateTeamSet().size());
        long endTime_1 = System.currentTimeMillis();
        
        System.out.println("重新构建时间为:"+(endTime_1-startTime_1)+"ms");
      
        
         long startTime_2 = System.currentTimeMillis(); 
         
         search.del_team(del_node);
         
         System.out.println(search.getValidateTeamSet().size());
         
         long endTime_2 = System.currentTimeMillis();
         
         System.out.println("增量式方法:"+(endTime_2-startTime_2)+"ms");
         
         long startTime_3 = System.currentTimeMillis(); 
         
         search_2.del_team(del_node);
         
         System.out.println(search_2.getValidateTeamSet().size());
         
         long endTime_3 = System.currentTimeMillis();
         System.out.println("增量式方法(考虑顺序):"+(endTime_3-startTime_3)+"ms");
         
         */
        
        
        
        
    }
}
