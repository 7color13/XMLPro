package service;

import resources.Path;
import util.XmlSwitchArray;
import util.XsdVerify;
import vo.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XmlService {
    public static List<Customer> customerList=new ArrayList<>();

    public static void main(String[] args) {
        toCustomer();
        int choice = menu();
        while(true) {
            switch (choice) {
                case 1:
                    addInfo();
                    break;
                case 2:
                    decreaseInfo();
                    break;
                case 3:
                    searchInfo();
                    break;
                case 4:
                    updateInfo();
                    break;
                case 5:
                    showInfo();
                    break;
                case 6:
                    System.out.println("程序已退出");
                    System.exit(0);
                default:
                    System.out.println("无效的选择");

            }
            choice=menu();
        }

    }


    private static void addInfo() {
        Scanner sc = new Scanner(System.in);
        Customer customer = new Customer();
        System.out.println("请输入顾客ID：");
        customer.setId(sc.next());
        System.out.println("请输入增加顾客的姓名：");
        customer.setName(sc.next());
        System.out.println("请输入顾客的地址：");
        customer.setAddress(sc.next());
        System.out.println("请输入顾客的年龄");
        customer.setAge(sc.nextInt());
        customerList.add(customer);
        XmlSwitchArray.arrayToXML(toObject(customerList),Path.pathName);
       if (!XsdVerify.validateXMLByXSD()){
           customerList.remove(customerList.get(customerList.size()-1));
           XmlSwitchArray.arrayToXML(toObject(customerList),Path.pathName);
           addInfo();
       }
       else{
           System.out.println("写入文件成功");
       }
    }
   public static List<Object> toObject(List<Customer> customers){
        List<Object> objList = new ArrayList<>();
        for (Customer customer:customers){
            Object object = (Customer) customer;
            objList.add(object);
        }
        return objList;
   }
   public static void toCustomer(){
        customerList=new ArrayList<>();
       List<Object> objList = XmlSwitchArray.getXMLAsArray(Path.pathName, "vo.Customer");
       for (Object object : objList) {
           Customer customer = (Customer) object;
           customerList.add(customer);
       }
   }
    public static void decreaseInfo() {
        System.out.println("请输入你想删除的顾客的id");
        Scanner sc = new Scanner(System.in);
        String id=sc.next();
        toCustomer();
        int flag=-1;
        for(int i=0;i<customerList.size();i++){
            if (customerList.get(i).getId().equals(id)){
                 flag=i;
            }
        }
        if (flag!=-1) {
            customerList.remove(customerList.get(flag));
            System.out.println("已成功删除此顾客的信息");
        }
        else{
            System.out.println("很抱歉并未查到id为"+id+"的顾客");
        }
        XmlSwitchArray.arrayToXML(toObject(customerList), Path.pathName);
    }
    public static void searchInfo() {
        System.out.println("请输入需要查询的用户名或id或地址或年龄");
        Scanner sc = new Scanner(System.in);
        String info = sc.nextLine();
        toCustomer();
        for (int i=0;i<customerList.size();i++){
            if (customerList.get(i).toString().indexOf(info)!=-1){
                System.out.println(customerList.get(i).toString());
            }
        }
    }

    public static void updateInfo() {
        toCustomer();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你想更新的用户的id");
        String id = sc.next();
        int flag=-1;
        String str[] = new String[100];
        for (int i = 0; i < customerList.size(); i++) {
            if (customerList.get(i).getId().equals(id)) {
                System.out.println("该用户信息："+customerList.get(i).toString());
                flag=i;
                str[0]=customerList.get(i).getId();
                str[1]=customerList.get(i).getName();
                str[2]=customerList.get(i).getAddress();
                str[3]= String.valueOf(customerList.get(i).getAge()) ;
            }
        }
        if (flag==-1){
            System.out.println("顾客信息中并没有为id为"+id+"的");
            updateInfo();
        }
        else{
            System.out.println("请依次输入你想在更新后所呈现的该用户的内容，如（#0001,张三,湖北武汉,38）");
            String info = sc.next();
            String arr[] = info.split(",|，");
            customerList.get(flag).setId(arr[0]);
            customerList.get(flag).setName(arr[1]);
            customerList.get(flag).setAddress(arr[2]);
            customerList.get(flag).setAge(Integer.parseInt(arr[3]));

            XmlSwitchArray.arrayToXML(toObject(customerList),Path.pathName);
            if (!XsdVerify.validateXMLByXSD()){
                customerList.get(flag).setId(str[0]);
                customerList.get(flag).setName(str[1]);
                customerList.get(flag).setAddress(str[2]);
                customerList.get(flag).setAge(Integer.parseInt(str[3]) );
                System.out.println(customerList);
                XmlSwitchArray.arrayToXML(toObject(customerList),Path.pathName);
                System.out.println("你输入的信息不符合校验，请重新输入");
            }
        }
    }

    public  static void showInfo() {
       toCustomer();
       customerList.forEach(System.out::println);
    }

    public static int menu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你的选择：");
        System.out.println("1.在顾客信息中增加数据");
        System.out.println("2.在顾客信息中删除数据");
        System.out.println("3.在顾客表中查询数据");
        System.out.println("4.在顾客表中修改数据");
        System.out.println("5.显示所有顾客信息");
        System.out.println("6.退出");
        System.out.println("请输入您的选项：");
        int choice = sc.nextInt();
        return choice;
    }
}
