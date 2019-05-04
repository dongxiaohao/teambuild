package cn.neu.kou.teambuild.rarestFirst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntToDoubleFunction;

import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.interfaces.Link;

public class steiner {
	query_graph query_graph;
	Origin_graph origin_graph;
	List<Integer> result_seq;
	int min_steiner;
	/**
	 * 构造函数
	 * @param query_graph	查询子图
	 * @param origin_graph	原图
	 */
	public steiner(query_graph query_graph,Origin_graph origin_graph) {
		// TODO Auto-generated constructor stub
		this.query_graph=query_graph;
		this.origin_graph=origin_graph;
		this.result_seq=new ArrayList<Integer>();
		this.min_steiner=Integer.MIN_VALUE;
	}
	/**
	 * 	设置斯坦纳树
	 * @param new_stenier
	 */
	 public void set_stenier(int new_stenier) {
		 this.min_steiner=new_stenier;
		
	}
	 /**
	  * 清空结果序列
	  */
	 public void clear_result_seq() {
		 this.result_seq.clear();
	 }
	 /**
	  * 得到斯坦纳树
	  * @return
	  */
	 public int  get_steiner() {
		 return this.min_steiner;
		
	}
	 /**
	  * 返回斯坦纳树的结果序列
	  * @return
	  */
	 public List<Integer> get_result_seq() {
		 return this.result_seq;
		
	}
	 /**
	  *打印斯坦纳树的结果序列 
	  */
		public void print_result() {
			for(int i:this.result_seq)
				System.out.print(i+" ");
			System.out.println();
		}
		/**
		 * 没用
		 * @param origin_path
		 * @throws Exception
		 */
		public void read_origin(String origin_path) throws Exception {
			this.origin_graph.readMatrix(origin_path);
		}
		
		/**
		 * 没用
		 * @param query_path
		 * @throws Exception
		 */
		
		public void read_query(String query_path) throws Exception {
			this.query_graph.init_zhitu(query_path);
		}
		
		/**
		 * 	输入图的邻接矩阵，返回最小生成树代价
		 * @param graph	图的邻接矩阵
		 * @return 最小生成树代价
		 */
		public int prim(int [][] graph) {
			int n=this.query_graph.get_seqLength();
			int start=0;
			int [][] mins=new int [n][2];//用于保存集合U到V-U之间的最小边和它的值，mins[i][0]值表示到该节点i边的起始节点
            //值为-1表示没有到它的起始点，mins[i][1]值表示到该边的最小值，
            //mins[i][1]=0表示该节点已将在集合U中
			int sum=0;
			for(int i=0;i<n;i++){//初始化mins
				
				if(i==start){
					mins[i][0]=-1;
					mins[i][1]=0;
				}else if( graph[start][i]!=-1){//说明存在（start，i）的边
					mins[i][0]=start;
					mins[i][1]= graph[start][i];
				}else{
					mins[i][0]=-1;
					mins[i][1]=Integer.MAX_VALUE;
				}
				//System.out.println("mins["+i+"][0]="+mins[i][0]+"||mins["+i+"][1]="+mins[i][1]);
			}
			for(int i=0;i<n-1;i++){
				int minV=-1,minW=Integer.MAX_VALUE;
				for(int j=0;j<n;j++){//找到mins中最小值，使用O(n^2)时间
				
					if(mins[j][1]!=0&&minW>mins[j][1]){
						minW=mins[j][1];
						minV=j;
					}
				}
				//System.out.println("minV="+minV);
				mins[minV][1]=0;
				//System.out.println("最小生成树的第"+i+"条最小边=<"+(mins[minV][0]+1)+","+(minV+1)+">，权重="+minW);
				sum+=minW;
				for(int j=0;j<n;j++){//更新mins数组
					if(mins[j][1]!=0){
						//System.out.println("MINV="+minV+"||tree[minV][j]="+tree[minV][j]);
							if( graph[minV][j]!=-1&& graph[minV][j]<mins[j][1]){
								mins[j][0]=minV;
								mins[j][1]= graph[minV][j];
							}
					}
				}
			}
				//System.out.println(sum);
			return sum;
		}
		
		
		/**
		 * 根据输入序列得到相应的邻接矩阵
		 * @param seq	输入序列
		 * @return	邻接矩阵
		 */
		public int[][] get_matrix(List<Integer> seq){
			int[][] result=new int[this.query_graph.get_seqLength()][this.query_graph.get_seqLength()];
			for(int i=0;i<seq.size();i++) {
				result[i][i]=-1;
				for(int j=i+1;j<seq.size();j++) {
					if(this.query_graph.have_edge(i, j)) {
						int flag=(int)this.origin_graph.getMinDistance(seq.get(i), seq.get(j));
						result[i][j]=flag;
						result[j][i]=flag;
					}else {
						result[i][j]=-1;
						result[j][i]=-1;
					}
					//System.out.println(flag+"  "+seq.get(i)+"   "+seq.get(j));
				}
			}
			//System.out.println();
			//for(int i=0;i<seq.size();i++) {
			//	for(int j=0;j<seq.size();j++) {
			//		System.out.print(result[i][j]+" ");
			//	}
			//	System.out.println();
			//	}
			return result;
			
		}
		
		
		
		/**
		 * 得到最小生成树团队
		 */
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
						
						
						
						this.min_steiner=this.prim(this.get_matrix(mem_rusult));		 			//设置最大直径
						//System.out.println(this.get_max_rarest());
					}else {
						
						int new_steiner=this.prim(this.get_matrix(mem_rusult)); 		//得到mem_rusult的最大直径
						if(new_steiner<this.get_steiner()) {						//	判断当前最大直径是否为最小
							this.clear_result_seq();							//最小，则清空result序列
							for(int nodeID:mem_rusult)	{						//将mem_rusult中节点依次加入到result中
								this.result_seq.add(nodeID);					//
								//System.out.print(nodeID+" ");
							}
							this.set_stenier(new_steiner);				//将最大直径设为新值
							//System.out.println(this.get_max_rarest());
						}
						
					}
				}
				mem_rusult.clear();					//清空mem_rusult，为下一次循环做准备
			}
			
		}
		/**
		 * 
		 * @param list
		 */
		 public void sort_byweight(List<Link> list) {
		    	Collections.sort(list,new Comparator<Link>() {
		    		@Override
		    		public int compare(Link l1,Link l2) {
		    			return (int)l2.getWeight()-(int )l1.getWeight();
		    		}
				});
			}

}
