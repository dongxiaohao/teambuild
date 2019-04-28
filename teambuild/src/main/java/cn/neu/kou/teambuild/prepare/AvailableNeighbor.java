package cn.neu.kou.teambuild.prepare;

import cn.neu.kou.teambuild.cpi.SearchGraphHelper;
import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;

import java.io.*;
import java.util.*;

public class AvailableNeighbor {
    private HashMap<Integer, TreeMap<Integer, SpecialDistanceCandidate>> availableIdsMap; //具体的数据结构为{节点id:{距离（权重）：该距离的用户节点集合}}
    private SearchGraphHelper searchGraphHelper;
    private OriginGraphInterface originGraphInterface;
    private int maxWeight;
    private int totalNodeNum;

    public AvailableNeighbor(SearchGraphInterface searchGraphInterface, OriginGraphInterface originGraphInterface) {
        this.searchGraphHelper = new SearchGraphHelper(searchGraphInterface);
        this.originGraphInterface = originGraphInterface;
        totalNodeNum = originGraphInterface.getNodeNumber();
        this.availableIdsMap = new HashMap<>(totalNodeNum);
        this.maxWeight = 3;
    }

    /**
     * 初始化，找到所有的可达邻居存储在当前的数据结构中
     */
    public void init() {
        for(int id=0; id<this.totalNodeNum; id++) {
            List<Link> neighbors = originGraphInterface.getAvailableNeighbors(id, maxWeight);
            if(neighbors != null) {
                for (Link link : neighbors) {
                	//System.out.println("currnet creat: "+id+"current add node: "+link.getToUserId());
                    addOneAvailableId(id, link.getToUserId(), link.getLabel(), (int) link.getWeight());
                  //  System.out.println("currnet creat: "+id+"current add node: "+link.getToUserId());
                }
            }
        }
    }

    /**
     * 向当前map中添加一条数据（当前用户curId      在距离distance下    标签为label   的邻居neighbor）
     * @param curId      当前用户id
     * @param neighbor   邻居id
     * @param label      当前标签
     * @param distance   当前距离
     */
    public void addOneAvailableId(int curId, int neighbor, int label, int distance) {
        //获取当前节点可达的节点组成的map结构（距离----距离为该值的所有节点集合）
        TreeMap<Integer, SpecialDistanceCandidate> map;
        if(availableIdsMap.containsKey(curId)) {
            map = availableIdsMap.get(curId);
        }
        else {
            map = new TreeMap<Integer, SpecialDistanceCandidate>();
            availableIdsMap.put(curId, map);
        }

        //确保当前的距离下应用户数据能够有容器存放
        SpecialDistanceCandidate specialDistanceCandidate;
        if(map.containsKey(distance)) {
            specialDistanceCandidate = map.get(distance);
        }
        else {
            specialDistanceCandidate = new SpecialDistanceCandidate();
            map.put(distance, specialDistanceCandidate);
        }

        //向当前距离用户节点中添加用户节点
        specialDistanceCandidate.addIdWithLabel(label, neighbor);
    }

    /**
     * 向某个节点添加一组指定距离的用户节点
     * @param id                用户节点
     * @param distance          距离
     * @param specialDistanceCandidate 制定距离的用户节点集
     */
    public void addOneDistanceAvailable(int id, int distance, SpecialDistanceCandidate specialDistanceCandidate) {
        TreeMap<Integer, SpecialDistanceCandidate> map ;
        if(availableIdsMap.containsKey(id)) {
            map = availableIdsMap.get(id);
        }
        else {
            map = new TreeMap<Integer, SpecialDistanceCandidate>();
            availableIdsMap.put(id, map);
        }
        map.put(distance, specialDistanceCandidate);
    }

    /**
     * 获取某个用户的在权重约束下的多跳邻居集合
     * @param curId   某个用户
     * @param weight  权重约束（边约束）
     * @param label   标签
     * @return        符合条件的邻居集合
     */
    public List<Link> getAvailableNeighbor(int curId, Float weight, int label) {
        List<Link> links = new ArrayList<Link>();
        TreeMap<Integer, SpecialDistanceCandidate> map = this.availableIdsMap.get(curId);
        //有序Map
        for(int distance : map.keySet()) {
            if(distance <= weight) {
                Set<Integer> specialDistanceIdSet = map.get(distance).getAtSpecialDistanceIdsWithLabel(label);
                for(int id : specialDistanceIdSet) {
                    links.add(new Link(id, label, distance));
                }
            }
        }
        return  links;
    }

    /**
     * 从文件中读取数据
     * @param fileName    文件名
     */
    public void  fromFile(String fileName) {
        this.availableIdsMap.clear();

        File file = new File(fileName);
        if(!file.exists() || file.isDirectory()) {
            throw new RuntimeException(AvailableNeighbor.class.getName() + ".fromFile(fileName) 文件不合法或者文件错误");
        }
        FileReader fr = null;
        BufferedReader bf = null;
        try {
            fr = new FileReader(file);
            bf = new BufferedReader(fr);
            String line;
            while((line=bf.readLine()) != null && !"".equals(line=line.trim())) {
                String[] idNeighbor = line.split("#");
                int id = Integer.parseInt(idNeighbor[0]);
                String[] eachDistances = idNeighbor[1].split("@");
                for(String eachDistance : eachDistances) {
                    String[] distanceIds = eachDistance.split(":");
                    int distance = Integer.parseInt(distanceIds[0]);
                    SpecialDistanceCandidate specialDistanceCandidate = SpecialDistanceCandidate.fromString(distanceIds[1]);
                    addOneDistanceAvailable(id, distance, specialDistanceCandidate);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将数据写到文件中
     * @param fileName   文件名
     */
    public void toFile(String fileName) {
        File file = new File(fileName);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, false);//写入覆盖
            bw = new BufferedWriter(fw);
            for(int i=0; i<totalNodeNum; i++) {
                if(!availableIdsMap.containsKey(i)) {
                    continue;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(i);
                stringBuilder.append("#");
                TreeMap<Integer, SpecialDistanceCandidate> eachUserMap = availableIdsMap.get(i);
                for(Integer distace : eachUserMap.keySet()) {
                    stringBuilder.append(distace);
                    stringBuilder.append(":");
                    stringBuilder.append(eachUserMap.get(distace).toString());
                    stringBuilder.append("@");
                }
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                stringBuilder.append("\n");
                bw.write(stringBuilder.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
