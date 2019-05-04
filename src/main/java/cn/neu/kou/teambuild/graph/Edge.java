package cn.neu.kou.teambuild.graph;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;
import cn.neu.kou.teambuild.io.big_test01;

public class Edge {
    int adjvex;
    int weight;
    Edge next;
    public Edge(){
        this.next=null;
    }
    //无权图
    public Edge(int adj,Edge nt){
        this.adjvex=adj;
        this.next=nt;
        this.weight=1;
    }
    //有权图
    public Edge(int adj,int wgt,Edge nt){
        this.adjvex=adj;
        this.weight=wgt;
        this.next=nt;
    }
    public void setadjvex(int new_adj) {
    	this.adjvex=new_adj;
    }
    public void setweight(int new_weight) {
    	this.weight=new_weight;
    }
    public void setnext(Edge new_next) {
    	this.next=new_next;
    }
    public int getweight() {
    	return this.weight;
    }
    public int getadjvex() {
    	return this.adjvex;
    }
    public Edge getnext() {
    	return this.next;
    }
}

