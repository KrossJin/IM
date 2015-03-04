import java.sql.*;
import java.util.Vector;
public class Database {
	  String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
	  String dbURL = "jdbc:odbc:javaaccess";
	  String userName = "access";
	  String userPwd = "access";
	  static Connection dbConn = null;
	  static Statement st1,st2,st3,st4;
	  static ResultSet rsyonghu,rsfriend;
	  static String sql;
	  static Vector<Yonghu>yonghu;
	  static Vector<Integer>friend;
	  static int number=0;
	  public Database() {
	    try {
		    Class.forName(driverName);
		    dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
		    st1=dbConn.createStatement();
		    st4=dbConn.createStatement();
 //   	    addyonghu(new Yonghu(4,"jinteng","jinteng",new Vector<Integer>()));
 //           ceshi();
		
		    System.out.println("成功连接");
		   
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	  public void ceshi(){
	    sql="create table yonghu(id varchar(15),name varchar(15),password varchar(15))";
//	    sql="drop table yonghu";
//		sql="drop table 4";
	    try {
			st1.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	  }
	  public static void change(int id,String name,String password){
		sql="update yonghu set name = '"+name+"' where id = '"+id+"'";
		try {
			st1.execute(sql);
			sql="update yonghu set password = '"+password+"' where id = '"+id+"'";
			st1.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
//	public static void main(String[]args){
//		new Database();
//	}
	public static void deletefriend(int myid,int toid){
		sql="delete from "+myid+" where id='"+toid+"'";
		try {
			st1.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void addfriend(int myid,int toid){
		sql="insert into "+myid+" values('"+toid+"')";
		try {
			st1.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void addyonghu(Yonghu yonghu){
		try {
			sql="create table "+yonghu.getNumber()+"(id varchar(15))";
			st1.execute(sql);
			String name,password;
			String  id;
			id=""+yonghu.getNumber();
			name=yonghu.getName();
			password=yonghu.getPassward();
			
			sql="insert into yonghu values('"+id+"','"+name+"','"+password+"')";
			
			st1.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Vector<Yonghu> getTable(){
		 yonghu=new Vector<Yonghu>();
		 sql="select * from yonghu";
		try {
			st2=dbConn.createStatement();
			st3=dbConn.createStatement();
			rsyonghu=st2.executeQuery(sql);
			while(rsyonghu.next()){
                 sql="select * from "+number;
                 
                 rsfriend=st3.executeQuery(sql);
                
                 friend=new Vector<Integer>();
                
                 while(rsfriend.next()){
                	 
                	 friend.add(Integer.parseInt(rsfriend.getString(1)));
                 }
				 yonghu.add(new Yonghu(number,rsyonghu.getString(2),rsyonghu.getString(3),friend));
				 number++;
			}
			number=0;
			
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
		 return yonghu;
	}
   
}

