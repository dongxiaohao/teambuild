package cn.neu.kou.teambuild.graph;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;

public class Origin_graph implements OriginGraphInterface {
	   Edge[] graph;
	   lables graph_lable;
	   /**
	    * 封装了readMatrix方法
	    * @param count  节点数
	    * @param label_count 标签数
	    * @param origin_path 原图文件路径
	    * @param label_path  原图标签路径
	    * @throws Exception
	    */
	public Origin_graph(int count,int label_count,String origin_path,String label_path) throws Exception {
		graph=new Edge[count];
		this.readMatrix(origin_path);
		graph_lable=new lables(count,label_count,label_path);
		
	}
	public Origin_graph(int count,int label_count,String label_path) throws Exception {
		graph=new Edge[count];
		graph_lable=new lables(count,label_count,label_path);
		
	}
	/*
	 * 读入数据并生成对应的图
	 */
    public void readMatrix(String path) throws Exception {
        File file = new File(path);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件

        String line = "";
        while((line = reader.readLine()) != null){
            //将输入分割成图中对应两个边
            line=line.trim();
            
            /*
            if(line.charAt(0)<'0'|| line.charAt(0)>'9')
                continue;
            int i=0;
            while(line.charAt(i)>='0'&&line.charAt(i)<='9') {
                i++;
            }
            String s_e1=line.substring(0, i); //第一条边
            s_e1.trim();
            String s_e2=line.substring(i+1); //第二条边
            s_e2.trim();
            int e1= Integer.valueOf(s_e1).intValue(); //将第一条边转化为整数
            int e2= Integer.valueOf(s_e2).intValue(); //将第二调表转化为整数
            int weigth=(int)(Math.random()*3+1); //随机生成权重 范围从一到3
            */
            String[] mem=line.split(" ");
            int e1= Integer.valueOf(mem[0]).intValue(); //将第一条边转化为整数
            int e2= Integer.valueOf(mem[1]).intValue(); //将第二调表转化为整数
            int weigth=Integer.valueOf(mem[2]).intValue();      //随机生成权重 范围从一到3
            
            if(e1<e2) { //若e1<e2则表示还未添加，否则应已添加进邻接图中
                this.graph[e1]=new Edge(e2,weigth,this.graph[e1]); //将第二条边加入的一条边的邻接矩阵
                this.graph[e2]=new Edge(e1,weigth,this.graph[e2]); //将第一条边加入的二条边的邻接矩阵
            }
            //System.out.println(line+" "+e1+" "+e2+" "+weigth);

        }
        fis.close();

    }
    public Edge get_firstedge(int userId){
    	return this.graph[userId];
    }
    public void bianli(){
        int count=0;
        for(int i=0;i<this.graph.length;i++) {
        	Edge flag=this.graph[i];
            while(flag!=null) {
                //System.out.println(i+" "+flag.adjvex+" "+flag.weight);
                flag=flag.next;
                count++;
            }
        }
        System.out.println(count);
    }

	@Override
	/*
	 * 以Link集合的形式返回所有与userId直接相连的节点
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#findNeighbors(int)
	 */
	public List<Link> findNeighbors(int userId) {
		// TODO Auto-generated method stub
		ArrayList<Link> result=new ArrayList<>();
		Edge mem_edge=this.graph[userId];
		while(mem_edge!=null) {
			result.add(new Link(mem_edge.adjvex,this.graph_lable.getlable(mem_edge.adjvex),mem_edge.weight));//创建一个Link变量，并加入result中；
			mem_edge=mem_edge.next;
		}
		return result;
	}
	/*
	 * 返回userId相同标签的节点的数量
	 */
	public int getlable_count(int userId) {
		int lable=this.getLabel(userId);
		return this.graph_lable.getcount(lable);
	}

	@Override
	/*
	 * 返回两个直接相连节点的权重，若不直接相连返回零
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#getDirectWeight(int, int)
	 */
	public float getDirectWeight(int fromUserId, int toUserId) {
		Edge mem_edge=this.graph[fromUserId];
		while(mem_edge!=null) {
			if(mem_edge.adjvex==toUserId)
				return mem_edge.weight;
			mem_edge=mem_edge.next;
		}
		return 0;
	}
	@Override
	/*
	 * 返回当前节点的标签类型
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#getLabel(int)
	 */
	public int getLabel(int userId) {
		// TODO Auto-generated method stub
		return this.graph_lable.getlable(userId);
	}
	/*
	 * 返回所有与userId节点直接或间接相连的并满足weigth限制的节点  想法：使用广度优先
	 * @see cn.neu.kou.teambuild.interfaces.OriginGraphInterface#getAvailableNeighbors(int, float)
	 */
	@Override

	public List<Link> getAvailableNeighbors(int userId, float weight) {
		// TODO Auto-generated method stub
		ArrayList<Integer> node=new ArrayList<>();
		ArrayList<Integer> node_wei=new ArrayList<>();
		Edge user=this.graph[userId];
		while(user!=null) {
			if(user.weight<=weight) {
				node.add(user.adjvex);
				node_wei.add(user.weight);
				//System.out.println(user.adjvex+"   "+user.weight);
			}
			user=user.next;
		}
		for(int i=0;i<node.size();i++) {																	//使用广度优先添加节点
			user=this.graph[node.get(i)];
			//System.out.println(user.adjvex+"   "+user.weight);
			while(user!=null) {
				if(node_wei.get(i)+user.weight<=weight) {
					if(node.contains(user.adjvex)) {														//如果当前节点已加入链表，判断权重是否为最小
						int node_index=node.indexOf(user.adjvex);
						int node_wei_i = node_wei.get(i);

						if( node_wei_i +user.weight<node_wei.get(i)) {					//如果当前节点已加入链表 且与源节点权重小于之前添加的权重，则修改之前权重
							node_wei.remove(node_index);												//删除之前节点的权重
							node.remove(node_index);													//删除之前节点
							//为了测试，我先改回昨天的版本，记得自己改回来哟 ------by lq
							if(i>node_index)
								i = i - 1;

							node.add(user.adjvex);														//重新添加节点
							node_wei.add(  node_wei_i +user.weight);										//重新添加权重
					}
					}else {
						node.add(user.adjvex);
						node_wei.add(node_wei.get(i)+user.weight);
						
					}
				}
				user=user.next;
		}
		}
		ArrayList<Link> result=new ArrayList<>();
		for(int i=0;i<node.size();i++) {
			result.add(new Link(node.get(i),this.graph_lable.lable.get(node.get(i)),node_wei.get(i)));	//创建一个Link对象并加入，分别为节点Id，节点类型，节点与源节点之间权重；
		}
		
		
		return result;
	}

	@Override
	/*
	 * 返回所有在weight范围内的标签为label的节点，只在最后添加了一个标签的判定，对接果进行过滤
	 * @see cn.neu.kou.teambuild.interfaces.OriginGraphInterface#getAvailableNeighbors(int, float, int)
	 */
	public List<Link> getAvailableNeighbors(int userId, float weight, int label) {
		ArrayList<Integer> node=new ArrayList<>();												//使用两个链表，本链表存储所有可达的节点
		ArrayList<Integer> node_wei=new ArrayList<>();											//本链表存储与node对应位置的节点到userId的权重
		Edge user=this.graph[userId];
		while(user!=null) {																		//将所有与uaerId直接相连的节点加入链表中
			if(user.weight<=weight) {
				node.add(user.adjvex);
				node_wei.add(user.weight);
			}
			user=user.next;
		}
		for(int i=0;i<node.size();i++) {																	//使用广度优先添加节点
			user=this.graph[node.get(i)];
			while(user!=null) {
				if(node_wei.get(i)+user.weight<=weight) {
					if(node.contains(user.adjvex)) {														//如果当前节点已加入链表，判断权重是否为最小
						int node_index=node.indexOf(user.adjvex);
						int node_wei_i = node_wei.get(i);
						if( node_wei_i + user.weight < node_wei.get(node_index)) {							//如果当前节点已加入链表 且与源节点权重小于之前添加的权重，则修改之前权重
							node_wei.remove(node_index);												//删除之前节点的权重
							node.remove(node_index);													//删除之前节点
							//为了测试，我先改回昨天的版本，记得自己改回来哟 ------by lq
							if(i>node_index)							
								i = i - 1;
							node.add(user.adjvex);														//重新添加节点
							node_wei.add( node_wei_i +user.weight);										//重新添加权重
					}
					}else {
						node.add(user.adjvex);
						node_wei.add(node_wei.get(i)+user.weight);
						
					}
				}
			
				user=user.next;
		}
		}
		ArrayList<Link> result=new ArrayList<>();
		for(int i=0;i<node.size();i++) {
			if(this.graph_lable.getlable(node.get(i))==label) 										//如果节点lebel为目标label则将此节点加入结果队列
			result.add(new Link(node.get(i),this.graph_lable.lable.get(node.get(i)),node_wei.get(i)));	//创建一个Link对象并加入，分别为节点Id，节点类型，节点与源节点之间权重；
		}
		
		
		return result;
	}

	@Override
	/*
	 * 返回所有带有目标标签的节点
	 * @see cn.neu.kou.teambuild.interfaces.OriginGraphInterface#getUserWithLabel(int)
	 */
	public List<Integer> getUserWithLabel(int label) {
		// TODO Auto-generated method stub
		List<Integer> re_link=new ArrayList<>();
		int len=this.graph_lable.getlangth();
		for(int i=0;i<len;i++) {
			if(this.graph_lable.getlable(i)==label)
				re_link.add(i);
		}
		return re_link;
	}

	@Override
	/*
	 * 使用深度优先方法进行搜索
	 * @see cn.neu.kou.teambuild.interfaces.OriginGraphInterface#getMinDistance(int, int, float)
	 */
	public float getMinDistance(int fromUserId, int toUserId, float maxDistance) {
		// TODO Auto-generated method stub
		Edge mem=this.graph[fromUserId];
		int minDistance=1000;					//将最小距离设置为最大值1000
		while(mem!=null) {
			if(mem.weight<=maxDistance) {		//查看权重是否满足限制
				if(mem.adjvex==toUserId){		//如果当前节点为目标节点，则与minDistance进行比较，小于则更改minDistance
					if(mem.weight<minDistance)
						minDistance=mem.weight;
				}else {
					int min=mem.weight;																		//初始化min为当前权重
					min+=getMinDistance(mem.adjvex,toUserId,maxDistance-mem.weight);						//不为目标节点，则以当前节点进行深度优先遍历
					if(min!=mem.weight && min<minDistance) {												//如果min小于	minDistance，且目标点可达的话，赋值																							
						minDistance=min;													
				}
				}
			}
				
			mem=mem.next;
		}
		if(minDistance>maxDistance)
			return 0;
		else 
			return minDistance;
	}
	public float getMinDistance(int fromUserId, int toUserId) {
		int weight=Integer.MAX_VALUE;
		ArrayList<Integer> node=new ArrayList<>();												//使用两个链表，本链表存储所有可达的节点
		ArrayList<Integer> node_wei=new ArrayList<>();											//本链表存储与node对应位置的节点到userId的权重
		Edge user=this.graph[fromUserId];
		while(user!=null) {																		//将所有与uaerId直接相连的节点加入链表中
			if(user.weight<=weight) {
				node.add(user.adjvex);
				node_wei.add(user.weight);
			}
			if(user.adjvex==toUserId && weight>user.weight) {
				weight=user.weight;
			}
			user=user.next;
		}
		for(int i=0;i<node.size();i++) {																	//使用广度优先添加节点
			user=this.graph[node.get(i)];
			while(user!=null) {
				if(node_wei.get(i)+user.weight<=weight) {
					if(user.adjvex==toUserId) {
						weight=node_wei.get(i)+user.weight;
					}
					if(node.contains(user.adjvex)) {														//如果当前节点已加入链表，判断权重是否为最小
						int node_index=node.indexOf(user.adjvex);
						int node_wei_i = node_wei.get(i);
						if( node_wei_i + user.weight < node_wei.get(node_index)) {							//如果当前节点已加入链表 且与源节点权重小于之前添加的权重，则修改之前权重
							node_wei.remove(node_index);												//删除之前节点的权重
							node.remove(node_index);													//删除之前节点
							//为了测试，我先改回昨天的版本，记得自己改回来哟 ------by lq
							if(i>node_index)							
								i = i - 1;
							node.add(user.adjvex);														//重新添加节点
							node_wei.add( node_wei_i +user.weight);										//重新添加权重
					}
					}else {
						node.add(user.adjvex);
						node_wei.add(node_wei.get(i)+user.weight);
						
					}
				}
			
				user=user.next;
		}
		}
		return weight;
	}
	/*
	 * 更改两个相连节点之间的权重
	 */
	public void setWeight(int node1,int node2,int targat_weight) {
		Edge user=this.graph[node1];
		while(user!=null && user.getadjvex()!=node2) {
			user=user.getnext();
		}
		if(user==null) {
			System.out.println("The edge not exist!!");
		}else {
		user.setweight(targat_weight);
		user=this.graph[node2];
		while(user.getadjvex()!=node1) {
			user=user.getnext();
		}
		user.setweight(targat_weight);
		}
	}
	/*
	 * 添加边
	 */
	public void addEdge(int node1,int node2,int weight){
		Edge user=this.graph[node1];
		int flag=0;
		while(user!=null) {								//判断边是否已存在
			if(user.getadjvex()==node2)
			{
				flag=1;
				break;
			}
			user=user.getnext();
		}
		if(flag==1) {
			System.out.println("The edge has exist!!");
		}else {
			 this.graph[node1]=new Edge(node2,weight,this.graph[node1]); //将第二条边加入的一条边的邻接矩阵
             this.graph[node2]=new Edge(node1,weight,this.graph[node2]); //将第一条边加入的二条边的邻接矩阵
		}
		
	}
	@Override
	/*
	 *删除节点标签，实现即将对应节点标签设为整数最大值
	 * @see cn.neu.kou.teambuild.interfaces.OriginGraphInterface#del_label(java.util.List)
	 */
	public void del_label(List<Integer> userId) {
		// TODO Auto-generated method stub
		for(int i:userId) {
			this.graph_lable.setlabel(i, Integer.MAX_VALUE);
		}
		
	}
	/**
	 * 可以修改权重（全部更新），或增加行的文件（不能重名）
	 * @param filePath 文件路径
	 * @param fileName 文件名
	 */
	
    public void change_weight(String filePath,String fileName){
    	FileWriter fileWriter = null;
		try {
			// 含文件名的全路径
            String fullPath = filePath + File.separator + fileName + ".txt";

            File file = new File(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
			fileWriter = new FileWriter(fullPath); //创建文本文件
			int count=0;
			for (int i=0;i<this.graph.length;i++) {
				Edge memEdge=this.graph[i];
				while (memEdge!=null) {
					int new_weight=(int)(Math.random()*3+1);
					String out=i+" "+memEdge.getadjvex()+" "+new_weight;
					fileWriter.write(out+"\r\n");//写入 \r\n换行
					memEdge=memEdge.getnext();
					count++;
					fileWriter.flush();
				}
			}
			System.out.println(count);
			
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int[][] toAdjMatrix() {
		int[][] originGraph = new int[this.graph.length][this.graph.length];

		//初始化
		for(int i=0; i<this.graph.length; i++) {
			originGraph[i] = new int[this.graph.length];
			Arrays.fill(originGraph[i], -1);
		}

		for(int i=0; i<this.graph.length; i++) {
			Edge edge = this.graph[i];
			while(edge != null) {
				originGraph[i][edge.adjvex] = edge.weight;
				originGraph[edge.adjvex][i] = edge.weight;
				edge = edge.next;
			}
		}

		return originGraph;
	}

	@Override
	public List<Integer> getLabelsArr() {
		return this.graph_lable.getLable();
	}

	@Override
	public int getNodeNumber() {
		return this.graph.length;
	}
	/**
	 * 随即删除节点数目的标签
	 */
	public List<Integer> delte_label(int count) {
		List<Integer> dele_node=new ArrayList<Integer>();
		for(int i=0;i<count;i++) {
			int user=(int)(Math.random()*this.getNodeNumber());
			if(this.getLabel(user)==Integer.MAX_VALUE)
				i--;
			else {
				this.graph_lable.setlabel(user, Integer.MAX_VALUE);
				dele_node.add(user);
			}
		}
		return dele_node;
	}
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
    public void read_new_Matrix(String path) throws Exception {
    	int count=0;
        File file = new File(path);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件

        String line = "";
        while((line = reader.readLine()) != null){
            //将输入分割成图中对应两个边
            line=line.trim();
            String[] mem=line.split("	");
            
            if(line.charAt(0)<'0'|| line.charAt(0)>'9')
                continue;
            /*
            int i=0;
            while(line.charAt(i)>='0'&& line.charAt(i)<='9') {
                i++;
            }
            String s_e1=line.substring(0, i); //第一条边
            s_e1.trim();
            String s_e2=line.substring(i+1); //第二条边
            s_e2.trim();
            int e1= Integer.valueOf(s_e1).intValue(); //将第一条边转化为整数
            int e2= Integer.valueOf(s_e2).intValue(); //将第二调表转化为整数
            int weigth=(int)(Math.random()*3+1); //随机生成权重 范围从一到3
            */
            
           // String[] mem=line.split(" ");
            int e1= Integer.valueOf(mem[0].trim()).intValue(); //将第一条边转化为整数
            int e2= Integer.valueOf(mem[1].trim()).intValue(); //将第二调表转化为整数
           // int weigth=Integer.valueOf(mem[2]).intValue();      //随机生成权重 范围从一到3
            int weigth=(int)(Math.random()*3+1); //随机生成权重 范围从一到3
            
            
            if(e1<e2) { 						//若e1<e2则表示还未添加，否则应已添加进邻接图中
                this.graph[e1]=new Edge(e2,weigth,this.graph[e1]); //将第二条边加入的一条边的邻接矩阵
                this.graph[e2]=new Edge(e1,weigth,this.graph[e2]); //将第一条边加入的二条边的邻接矩阵
                count++;
            }
            //System.out.println(line+" "+e1+" "+e2+" "+weigth);


        }
        fis.close();
        System.out.println(count);

    }
    /**
     * 判断两个节点之间是否存在边，存在返回true，否则返回false
     * (有问题）
     * @param node1
     * @param node2
     * @return
     */
    public boolean ifExist_edge(int node1,int node2) {
    	Edge user=this.graph[node1];
		int flag=0;
		while(user!=null) {								//判断边是否已存在
			if(user.getadjvex()==node2)
			{
				flag=1;
				break;
			}
			user=user.getnext();
		}
		if(flag==1) 
			return true;
		else 
			return false;
    }
    
    public lables get_graphlabel() {
    	return this.graph_lable;
    }
}
