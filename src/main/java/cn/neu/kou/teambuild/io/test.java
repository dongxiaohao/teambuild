package cn.neu.kou.teambuild.io;

public class test {
	public static void main(String[] args) {
        Base base = new Derived();
        if (base instanceof Derived) {
            // 这里可以向下转换了
            System.out.println("ok");
        }
        else {
            System.out.println("not ok");
        }
    }

}
class Base { }
class Derived extends Base { }

