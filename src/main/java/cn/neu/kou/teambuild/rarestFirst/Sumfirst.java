package cn.neu.kou.teambuild.rarestFirst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.interfaces.Link;

public class Sumfirst {
	query_graph query_graph;
	Origin_graph origin_graph;
	List<Integer> result_seq;
	int Sum;
	public Sumfirst(query_graph query_graph,Origin_graph origin_graph) throws Exception 
	{
		this.query_graph=query_graph;
		this.origin_graph=origin_graph;
		this.result_seq=new ArrayList<Integer>();
		this.Sum=Integer.MIN_VALUE;
	}

	public void read_origin(String origin_path) throws Exception {
		this.origin_graph.readMatrix(origin_path);
	}
	public void read_query(String query_path) throws Exception {
		this.query_graph.init_zhitu(query_path);
	}
	/**
	 * 得到一个序列的距离和
	 * @param seq 输入序列
	 * @return
	 */
	public int com_Sum(List<Integer> seq){
		int Sum=0;
		for(int i=0;i<seq.size();i++)
			for(int j=i+1;j<seq.size();j++) {
				Sum+=(int)this.origin_graph.getMinDistance(seq.get(i), seq.get(j));
				//System.out.println(flag+"  "+seq.get(i)+"   "+seq.get(j));
			}
		//System.out.println();
		return Sum;
	}
	public void set_result() {
		List<Integer> query_seq=this.query_graph.getSeq_noCFL();
		//for(int j:query_seq)
		//	System.out.print(j+"   ");
		int userId=this.query_graph.seq.get(this.query_graph.getLabel(query_seq.get(0)));
		List<Integer> minlabel_node=this.origin_graph.getUserWithLabel(this.query_graph.getLabel(userId));
		int count_xunhuan=0;
		for(int user:minlabel_node) {
			if(count_xunhuan++>25)
				break;
			//System.out.println(user);
			List<Integer> mem_rusult=new ArrayList<>();
			List<Link> link= origin_graph.getAvailableNeighbors(user, 3);	 //得到点i的限制权重内的所有点
			this.sort_byweight(link);      			 				//将link按升序排列,然后遍历Link找出符合要求的点
			
			/*
			int[] count=new int[query_graph.query_lable.lable_count.length];
			mem_rusult.add(user);
			count[origin_graph.getLabel(user)]++;
			for(Link l:link) {						// 遍历Link
				if(l.getLabel()<count.length) { 	//判断节点标签是否为团队所需的标签
					//int flag=0;				//标识节点是否符合约束，若符合为0，否则为1
					if(count[l.getLabel()]<this.query_graph.query_lable.getcount(l.getLabel())  && !mem_rusult.contains(l.getToUserId())) {	//判断当前标签数量是否满足要求
						
						for(int j:mem_rusult) {									    //对要加入的节点判断是否满足约束
							if(this.query_graph.have_edge(j, l.getToUserId())) {	//判断j与l.getToUserId()之间是否有边
								if((int)this.origin_graph.getMinDistance(j, l.getToUserId(), this.query_graph.get_weigth(j, l.getToUserId()))==0) //如果权重不满足约束，置flag为1，跳出循环
									{
										flag=1;
										break;
									}
							}
						}
						
						//if(flag==0) {			//满足条件，加入序列
						count[l.getLabel()]++;			//对应标签数量增加一
						mem_rusult.add(l.getToUserId());	//将标签加入队列
					//}
				}
				}
			}
			
			*/
			mem_rusult.add(user);
			int count=1;
			for (Link l:link) {
				int flag=0;
				if(count>=this.query_graph.get_seqLength())
					break;
				if(l.getLabel()==this.query_graph.getLabel(query_seq.get(count)))	//如果当前节点标签为查询序列中对应节点的标签
				{
					for(int index=0;index<count;index++) {
						if(this.query_graph.have_edge(query_seq.get(index), count))
						{
							if(this.origin_graph.getMinDistance(mem_rusult.get(index),l.getToUserId())>this.query_graph.get_weigth(query_seq.get(index), count)) {
								flag=1;
						}
								
						}
					}
				}
				if(flag==0) {
					count++;
					mem_rusult.add(l.getToUserId());
					
				}
			}		
			
			if(mem_rusult.size() == query_graph.get_seqLength()) { 	// 如果mem_rusult是一个完整序列
				if(this.result_seq.size()==0) {		    //判断result序列是否为空
					for(int nodeID:mem_rusult) {			//为空，将mem_rusult序列中值加入到result中
						this.result_seq.add(nodeID);  			//加入值
						//System.out.print(nodeID+" ");
					}
					this.set_sum(this.com_Sum(mem_rusult));		 //设置最大直径
					//System.out.println(this.get_sum());
				}else {
					int new_sum=this.com_Sum(mem_rusult); 		//得到mem_rusult的最大直径
					if(new_sum<this.get_sum()) {				//	判断当前最大直径是否为最小
						this.clear_seq();					//最小，则清空result序列
						for(int nodeID:mem_rusult)	{				//将mem_rusult中节点依次加入到result中
							this.result_seq.add(nodeID);			//
							//System.out.print(nodeID+" ");
						}
						this.set_sum(new_sum);				//将最大直径设为新值
						//System.out.println(this.get_sum());
					}
				}
			}
			mem_rusult.clear();					//清空mem_rusult，为下一次循环做准备
			

		}
		
	}
	public int get_sum(){
		return this.Sum;
	}
	public void set_sum(int new_sum){
		this.Sum=new_sum;
	}
	public List<Integer> get_seq() {
		return this.result_seq;
	}
	public void clear_seq() {
		this.result_seq.clear();
		
	}
	public void print_seq() {
		for(int i:this.result_seq)
			System.out.print(i+"  ");
		System.out.println();
		
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
