import java.util.List;

import org.junit.Test;

import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.interfaces.Link;

public class TestOrigin_graph {
	static private String originpath="D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt\\new_email-Enron.txt";
	static private String label_path="D:\\学习\\kou_exp\\数据集\\已下载数据\\email-Enron.txt\\email-Enron_label.txt";
	@Test
	public void testfindNeighbors() throws Exception{
		System.out.println("testfindNeighbors");
		Origin_graph graph=new Origin_graph(39962,20,originpath,label_path);
		List<Link> find_test=graph.findNeighbors(3);
		for(Link i:find_test) {
			System.out.println(i.getToUserId()+" "+i.getLabel()+" "+i.getWeight());
		}
		System.out.println("----------------");
		
	}
	@Test
	public void testgetDirectWeight() throws Exception {
		System.out.println("testgetDirectWeight");
		Origin_graph graph=new Origin_graph(39962,20,originpath,label_path);
		float i=graph.getDirectWeight(1,29);
		float b=graph.getDirectWeight(29, 20);
		System.out.println(i+" "+b);
		System.out.println("----------------");
	}
	@Test
	public void testgetAvailableNeighbors() throws Exception {
		System.out.println("testgetAvailableNeighbors0");
		Origin_graph graph=new Origin_graph(39962,20,originpath,label_path);
		List<Link> find_test=graph.getAvailableNeighbors(11, 15);
		System.out.println("--------------排序前--------------------");
		for(Link i:find_test) {
			System.out.println(i.getToUserId()+" "+i.getLabel()+" "+i.getWeight());
		}
		System.out.println("---------------排序后---------------------");
		find_test=find_test.get(0).sort_byweight(find_test);
		for(Link i:find_test) {
			System.out.println(i.getToUserId()+" "+i.getLabel()+" "+i.getWeight());
		}
		System.out.println("------- 带有特定标签：getAvailableNeighbors---------");
		find_test=graph.getAvailableNeighbors(11,15,6);
		for(Link i:find_test) {
			System.out.println(i.getToUserId()+" "+i.getLabel()+" "+i.getWeight());
		}
		System.out.println("-------new_testgetMinDistance---------");
		float new_getuser_test=graph.getMinDistance(28851, 11);
			System.out.println((int)new_getuser_test);
			new_getuser_test=graph.getMinDistance(4815,1);
			System.out.println((int)new_getuser_test);
		System.out.println("----------------");
		
		
		System.out.println("-------testgetMinDistance---------");
		System.out.println("testgetMinDistance");
		float getuser_test=graph.getMinDistance(56,15741,15);
			System.out.println(getuser_test);
			getuser_test=graph.getMinDistance(4815,1,10);
			System.out.println(getuser_test);
		System.out.println("----------------");
		
	}
	@Test
	public void testgetUserWithLabel() throws Exception{
		System.out.println("testgetUserWithLabel");
		Origin_graph graph=new Origin_graph(39962,20,originpath,label_path);
		List<Integer> getuser_test=graph.getUserWithLabel(3);
		for(int i:getuser_test)
			System.out.print(i+"  ");
		int j=0;
		System.out.println();
		while(j<30) {
			System.out.print(j+" ");
			System.out.print(graph.getLabel(j)+" ");
			System.out.println();
			j++;
		}
		System.out.println("----------------");
	}
	@Test
	public void testgetMinDistance() throws Exception{
		System.out.println("testgetMinDistance1");
		Origin_graph graph=new Origin_graph(39962,20,originpath,label_path);
		float getuser_test=graph.getMinDistance(10,1,3);
			System.out.println(getuser_test);
			getuser_test=graph.getMinDistance(10,29,3);
			System.out.println(getuser_test);
		System.out.println("----------------");
	}
}
