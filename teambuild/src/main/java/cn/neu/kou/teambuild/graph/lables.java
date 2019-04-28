package cn.neu.kou.teambuild.graph;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class lables {
    public List<Integer> lable;									// 将标签存入对应的节点标签对应位
    public int[] lable_count;							//统计每类标签的数量，lable_count[0]即表示0类标签的节点数，依次类推
    List<Integer> withoutlable_node;					//记录没有标签的节点
    public lables(int length,int count,String label_path) throws Exception {
    	this.withoutlable_node=new ArrayList<Integer>();
        this.lable=new ArrayList<Integer>();
        this.lable_count=new int[count];
        List<Integer> mem=read_label(label_path);
       // int flag=0;
        for(int i:mem) {
        	 
            this.lable.add(i);						//将标签存入对应节点
            if(i!=Integer.MAX_VALUE)
            	this.lable_count[i]++;				//对应标签的数量增加一个
            else {
				this.withoutlable_node.add(i);
			}
        	

        }

    }
    public lables(String label_path,int length,int count,String fileName) throws Exception {
    	this.lable=new ArrayList<Integer>();
        this.lable_count=new int[count];
    	 this.change_lable(label_path, fileName);
    	 this.updata_lable(label_path+fileName);
	}
    /**
     * 
     * @param path
     * @return
     * @throws Exception
     */
    private List<Integer> read_label(String path) throws Exception{
    	List<Integer> result=new ArrayList<Integer>();
    	File file = new File(path);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件
        String line = "";
        int row=0,colume=0;
        while((line = reader.readLine()) != null) {
        	line=line.trim();
        	if(!line.isEmpty())
        		result.add(Integer.valueOf(line));
        }
        fis.close();
        return result;
    }
    public int getlable(int flag){
        return this.lable.get(flag);

    }
    public int getlangth() {
    	return this.lable.size();
    }
    public int getcount(int lable) {
    	if(lable==Integer.MAX_VALUE)
    		return this.withoutlable_node.size();
    	return this.lable_count[lable];
    	
    }
    /***
     *
     * @param userId
     * @param target_lable
     */
    public void setlabel(int userId,int target_lable){
    	if(target_lable!=Integer.MAX_VALUE) {
    	int pre_lable=this.getlable(userId);
    	//this.lable[userId]=target_lable;
    	this.lable.set(userId, target_lable);
    	this.lable_count[target_lable]++;
    	this.lable_count[pre_lable]--;
    	}else {
        	int pre_lable=this.getlable(userId);
        	//this.lable[userId]=target_lable;
        	this.lable.set(userId, target_lable);
        	this.lable_count[pre_lable]--;
    	}
    	
    	
    }
    /**
     * 修改便签文件，
     * @param filePath
     * @param fileName
     * @throws Exception 
     */
    public void change_lable(String filePath,String fileName) throws Exception{
    	FileWriter fileWriter = null;
		try {
			// 含文件名的全路径
            String fullPath = filePath + File.separator + fileName + ".txt";

            File file = new File(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            
			fileWriter = new FileWriter(fullPath);//创建文本文件
			int count=0;
			for(int i=0;i<this.getlangth();i++) {
				int label=(int)(Math.random()*(this.lable_count.length+2));
				if(label<this.lable_count.length)
					fileWriter.write(label+"\r\n");//写入 \r\n换行
				else {
					fileWriter.write(Integer.MAX_VALUE+"\r\n");//写入 \r\n换行
				}
				count++;
			}
			fileWriter.flush();
			fileWriter.close();
			this.updata_lable(fullPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * 根据文件更新标签
     * @param label_path 标签文件路径
     * @throws Exception
     */
    public void updata_lable(String label_path) throws Exception {
    	this.withoutlable_node.clear();
    	this.lable.clear();
    	for(int i=0;i<this.lable_count.length;i++)
    		this.lable_count[i]=0;
        List<Integer> mem=this.read_label(label_path);
        //int flag=0;
        for(int i:mem) { 
            //this.lable[flag++]=i;	
        	this.lable.add(i);					//将标签存入对应节点
        	if(i!=Integer.MAX_VALUE)
        		this.lable_count[i]++;				//对应标签的数量增加一个
            else {
				this.withoutlable_node.add(i);
			}
        	

        }
    	
    }

    public List<Integer> getLable() {
        return lable;
    }

    public void setLable(List<Integer> lable) {
        this.lable = lable;
    }
}
