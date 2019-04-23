import java.util.List;

import org.junit.Test;

import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.interfaces.Link;

public class TestqueryGraph {
		static private String query_path="D:\\学习\\kou_exp\\query_graph\\small\\small.txt";
		static private String label_path="D:\\学习\\\\kou_exp\\query_graph\\small\\small_lable.txt";
		@Test
		/*
		 * 测试通过
		 */
		public void testfindNeighbors() throws Exception {
			query_graph q_graph=new query_graph(5,2,query_path,label_path);
			List<Link> find_test=q_graph.findNeighbors(3);
			for(Link i:find_test) {
				System.out.println(i.getToUserId()+" "+i.getLabel()+" "+i.getWeight());
			}
			for(int i=0;i<5;i++)
				System.out.print(q_graph.getLabel(i)+"  ");
			
			
		}
		
		
}
