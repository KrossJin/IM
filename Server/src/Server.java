import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	ServerSocket serversocket;
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	Vector<Clientprocess>client=new Vector<Clientprocess>();
	Vector<Yonghu> table;//id与table的顺序一致
	Boolean duiying=false;//用于用户名与密码的验证时
	
   public Server(){
	    //连接好数据库
	     new Database();
	     table=Database.getTable();
	   //开启accept等待线程，每当有请求就在新建用户请求处理线程
		 new Thread(new Talkthread()).start();
		
   }
   public static void main(String[]args){
	   new Server();
   }
   
   
  //主要接受请求           处理登陆和注册请求
   private class Talkthread implements Runnable{
	   
	   String command,str2,password;
		int id;

	   
	   public String translate (int id){
		   return table.get(id).getName();
	   }
	 //为客户端发送好友列表
	   public void sendTable(int id){
		   //发送所有用户列表
		   writer.println(table.get(id).getName());
		   writer.println(""+id);
		    for(int a=0;a<table.size();a++){
		    	if(a!=id){
			       writer.println(table.get(a).getName());
			       writer.println(""+a);
			    }
			}
		   writer.println("over");
		   writer.flush();
		   //发送好友列表
		   for(int a=0;a<table.get(id).getFriends().size();a++){
			   writer.println(table.get(id).getFriends().get(a));
			}
		   writer.println("over");
		   writer.flush();
	   }
	   //本方法用于验证将要登录的账号是否已经在线了
	   public boolean thereis (int id){
		   
		   for(int a=0;a<client.size();a++){
			   if(id==client.get(a).getId())return true;
			 }
		   return false;
	  }
	   //talkthread只需处理处理登陆，注册请求        具体的添加   删除   更改请求留给  clientprocess就可以了
	   public void process(BufferedReader reader,PrintWriter writer){
		   try {
			 
			 command = reader.readLine();
			if(command.equals("登陆")){
			    str2= reader.readLine();
			    password= reader.readLine();
			    id=Integer.parseInt(str2);
			    if(id<table.size()&&password.equals(table.get(id).getPassward())&&!thereis(id)){
			    		 duiying=true;
			    		 writer.println("yes");
			    		 sendTable(id);
			    		 Clientprocess c=new Clientprocess(socket,id);
			    		 new Thread(c).start();
			    		 client.add(c);
			    		
			    	 }
			    if(!duiying) {
					writer.println("no");
					writer.flush();
					process(reader,writer);
				 }
				 duiying=false;//用于以后用户登录时布尔值不变
			}
			//这个if用于注册一个账户
			if(command.equals("注册")){
				str2= reader.readLine();
			    password= reader.readLine();
			    //更改数据库
			    table.add(new Yonghu(table.size(),str2,password,new Vector<Integer>()));//更改本地缓存的用户列表
			    Database.addyonghu(new Yonghu(table.size()-1,str2,password,new Vector<Integer>()));//更改数据库用户列表
			    yonghuupdate(str2,table.size()-1);//新注册的用户数据发给每一个客户端
			    //对注册的客户端做出反应
			    writer.println("yes");
			    writer.println(""+(table.size()-1));
			    sendTable(table.size()-1);
		        Clientprocess k=new Clientprocess(socket,table.size()-1);
		        new Thread(k).start();
		        client.add(k);
		    }
			
		} catch (IOException e) {
			System.out.println("客户下线了");
		}
	 }
	   //本方法是新注册的用户数据发给每一个客户端
	   public void yonghuupdate(String name,int id){
			  for(int a=0;a<client.size();a++){
				  client.get(a).update(name, id);
			  }
		  }
	   //线程主运行程序
	   public void run(){
		   try {
			 serversocket=new ServerSocket(8888);
		} catch (IOException e) {
			 e.printStackTrace();
		}
		   
		 while (true){
		     try {
				socket=serversocket.accept();
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer=new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		     process(reader,writer);
		 }
     }
  }
   
   //这个线程类包含用户的id和一个socket
   //将用户与socket绑定起来
 
   private class Clientprocess implements Runnable{
	  
	   private Socket socket;
	   private InputStream is;
	   private OutputStream os;
	   private BufferedReader reader;
	   private PrintWriter writer;
	   private String command,name,password;
	   private int id;
	   private boolean running=true;
	   
	   //构造方法实例化id，socket，reader。。。。。
	   public Clientprocess(Socket socket, int id)  {
		    this.socket = socket;
		    this.id = id;
		    try {
		    	 is=socket.getInputStream();
				 os=socket.getOutputStream();
				 reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			     writer=new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
	   }
	 
	   public void run() {
	          while (running){
			       try {
			    	   command=reader.readLine();
				       process(command);
			       } catch (IOException e) {
			    	   System.out.println("下线了"+id);
			    	   client.remove(translateint(id));
			    	   running=false;
			       }
			
		      }
		
	    } 
	   //主要是在删除进程集合某一进程时将id转换成在进程集合的位置
	    public int translateint(int targetid){
	    	  int q=0;
	    	  for(int a=0;a<client.size();a++){
		          if(targetid==client.get(a).getId())return a;
		      }
	          return q;
	    } 
	    //接受talkthread主线程发来的用户更新数据
	    public  void update(String name,int id){
	    	writer.println("用户更新");
	    	writer.println(name);
	    	writer.println(id);
	    	writer.flush();
	    }
	    //接受群聊天是消息
	    public void receivequn(String talk,int fromid){
	    	writer.println("群对话");
    		writer.println(""+fromid);
    		writer.println(talk);
    		writer.flush();
    		
	    }
	    //接受其他客户端发来的对话请求 并处理
	    public void receive(String command,String talk,int fromid){
	    	if(command.equals("对话")){
	    		writer.println("对话");
	    		writer.println(""+fromid);
	    		writer.println(talk);
	    		writer.flush();
	    		
	    	}
	    	
		}
	    //这是一个主要用在两个用户线程知道id获得线程处理类的方法
	    public Clientprocess translate(int targetid){
	    	  
	          for(int a=0;a<client.size();a++){
		          if(targetid==client.get(a).getId())return client.get(a);
		      }
	          return null;
	     }
	 
	   public int getId(){
		   return id;
	   }
	   public int translate(int myid,int toid){
		  
		   for(int a=0;a<table.get(myid).getFriends().size();a++){
			   if(toid==table.get(myid).getFriends().get(a))return a;
		   }
		   return 0;
	   }
	 //处理产生的各种请求，因为请求代码较长所以单独成为一个方法
	   public void process(String command){
		     int toid;
		     String talk;
		   //这俩变量是更改用户名密码时用的
		     String str3,str4;
		    
		     //下面的if语句可以添加功能
		     if(command.equals("对话")){               //面向本线程对应的客户端发给他人时使用
				 try {
					toid=Integer.parseInt(reader.readLine());
					talk=reader.readLine();
					Clientprocess cp=translate(toid);
					if(cp!=null){
						cp.receive(command,talk,id);
					}
					else {
						writer.println("错误");
						writer.println("好友未上线");
						writer.flush();
					}
				} catch (NumberFormatException e) {
					System.out.println("下线了"+id);
		            client.remove(translateint(id));
		            running=false;
				} catch (IOException e) {
					System.out.println("下线了"+id);
					client.remove(translateint(id));
					running=false;
				}
			  }
		   
		     else if(command.equals("添加")){
		    	 
		    	 try {
						toid=Integer.parseInt(reader.readLine());
						table.get(id).getFriends().add(toid);//更改缓存
						Database.addfriend(id, toid);//更改数据库
						writer.println("添加");
						writer.println("yes");
						writer.flush();
					} catch (NumberFormatException e) {
						System.out.println("下线了"+id);
			            client.remove(translateint(id));
			            running=false;
					} catch (IOException e) {
						System.out.println("下线了"+id);
						client.remove(translateint(id));
						running=false;
					}
		     }
		     else if(command.equals("删除")){
		    	 try {
		    		   
						toid=Integer.parseInt(reader.readLine());
						table.get(id).getFriends().remove(translate(id,toid));//更改缓存
						Database.deletefriend(id, toid);//更改数据库
						writer.println("删除");
						writer.println("yes");
						writer.flush();
					} catch (NumberFormatException e) {
						writer.println("删除");
						writer.println("no");
						writer.flush();
					} catch (IOException e) {
						System.out.println("下线了"+id);
						client.remove(translateint(id));
						running=false;
					}
		     }
              else if(command.equals("更改")){
            	  try {
					str3=reader.readLine();
					str4=reader.readLine();
					table.get(id).setName(str3);
					table.get(id).setPassward(str4);
					Database.change(id,str3,str4);
					
					writer.println("更改用户");
					writer.println("yes");
					writer.flush();
				} catch (IOException e) {
					System.out.println("下线了"+id);
					client.remove(translateint(id));
					running=false;
				}
             }
              else if(command.equals("群对话")) {
            	  try {
            		talk=reader.readLine();
					for(int a=0;a<client.size();a++){
						client.get(a).receivequn(talk,id);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
              }
		
	  }
			
		  
   }
	   
 
}