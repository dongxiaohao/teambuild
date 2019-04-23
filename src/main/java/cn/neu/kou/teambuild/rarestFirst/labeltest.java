package cn.neu.kou.teambuild.rarestFirst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.interfaces.Link;

public class labeltest {
	query_graph query_graph;
	Origin_graph origin_graph;
	List<Integer> result_seq;
	public labeltest(query_graph query_graph,Origin_graph origin_graph) {
		this.origin_graph=origin_graph;
		this.query_graph=query_graph;
		this.result_seq=new ArrayList<Integer>();
	}
	
	public void shixian() {
		List<Integer> query_seq=this.query_graph.getSeq_noCFL();
		int userId=this.query_graph.seq.get(this.query_graph.getLabel(query_seq.get(0)));
		List<Integer> list=this.origin_graph.getUserWithLabel(this.query_graph.getLabel(userId));
		int count_xunhuan=0;
		for(int user:list) {
			if(count_xunhuan++>25)
				break;
		List<Link> link= origin_graph.getAvailableNeighbors(user, 3); //得到点i的限制权重内的所有点
		this.sort_byweight(link);
		/*
		int[] count=new int[query_graph.query_lable.lable_count.length];
		this.result_seq.add(i);
		count[origin_graph.getLabel(i)]++;
		for(Link l:link) {
			if(l.getLabel()<count.length) {
				if(count[l.getLabel()]<this.query_graph.query_lable.getcount(l.getLabel()) && !result_seq.contains(l.getToUserId())) {
					count[l.getLabel()]++;
					this.result_seq.add(l.getToUserId());
				}
			}
		}*/
		
		this.result_seq.add(user);
		int count=1;
		for (Link l:link) {
			if(count>=this.query_graph.get_seqLength())
				break;
			if(l.getLabel()==this.query_graph.getLabel(query_seq.get(count)))		//如果当前节点标签为查询序列中对应节点的标签
			{

				count++;
				this.result_seq.add(l.getToUserId());
			}	
		}
		if(this.result_seq.size() ==  query_graph.get_seqLength())
			break;
		else {
			this.result_seq.clear();
		}
	}
	}
	public void print_result() {
		for(int i:this.result_seq)
			System.out.print(i+" ");
		System.out.println();
	}
	public List<Integer> get_seq(){
		return this.result_seq;
	}
	
	public int com_struct(List<Integer> seq) {
		int struct_sum=0;
		for(int i=0;i<seq.size();i++) {
			for(int j=i+1;j<seq.size();j++) {
				if(this.query_graph.have_edge(i, j)) {
					struct_sum+=(int)this.origin_graph.getMinDistance(seq.get(i), seq.get(j));
					
				}
				//System.out.println(flag+"  "+seq.get(i)+"   "+seq.get(j));
			}
		}
		return struct_sum;
	}
	 public void sort_byweight(List<Link> list) {
	    	Collections.sort(list,new Comparator<Link>() {
	    		@Override
	    		public int compare(Link l1,Link l2) {
	    			return (int)l2.getWeight()-(int )l1.getWeight();
	    		}
			});
		}

}
