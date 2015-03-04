import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	ServerSocket serversocket;
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	Vector<Clientprocess>client=new Vector<Clientprocess>();
	Vector<Yonghu> table;//id��table��˳��һ��
	Boolean duiying=false;//�����û������������֤ʱ
	
   public Server(){
	    //���Ӻ����ݿ�
	     new Database();
	     table=Database.getTable();
	   //����accept�ȴ��̣߳�ÿ������������½��û��������߳�
		 new Thread(new Talkthread()).start();
		
   }
   public static void main(String[]args){
	   new Server();
   }
   
   
  //��Ҫ��������           �����½��ע������
   private class Talkthread implements Runnable{
	   
	   String command,str2,password;
		int id;

	   
	   public String translate (int id){
		   return table.get(id).getName();
	   }
	 //Ϊ�ͻ��˷��ͺ����б�
	   public void sendTable(int id){
		   //���������û��б�
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
		   //���ͺ����б�
		   for(int a=0;a<table.get(id).getFriends().size();a++){
			   writer.println(table.get(id).getFriends().get(a));
			}
		   writer.println("over");
		   writer.flush();
	   }
	   //������������֤��Ҫ��¼���˺��Ƿ��Ѿ�������
	   public boolean thereis (int id){
		   
		   for(int a=0;a<client.size();a++){
			   if(id==client.get(a).getId())return true;
			 }
		   return false;
	  }
	   //talkthreadֻ�账�����½��ע������        ��������   ɾ��   ������������  clientprocess�Ϳ�����
	   public void process(BufferedReader reader,PrintWriter writer){
		   try {
			 
			 command = reader.readLine();
			if(command.equals("��½")){
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
				 duiying=false;//�����Ժ��û���¼ʱ����ֵ����
			}
			//���if����ע��һ���˻�
			if(command.equals("ע��")){
				str2= reader.readLine();
			    password= reader.readLine();
			    //�������ݿ�
			    table.add(new Yonghu(table.size(),str2,password,new Vector<Integer>()));//���ı��ػ�����û��б�
			    Database.addyonghu(new Yonghu(table.size()-1,str2,password,new Vector<Integer>()));//�������ݿ��û��б�
			    yonghuupdate(str2,table.size()-1);//��ע����û����ݷ���ÿһ���ͻ���
			    //��ע��Ŀͻ���������Ӧ
			    writer.println("yes");
			    writer.println(""+(table.size()-1));
			    sendTable(table.size()-1);
		        Clientprocess k=new Clientprocess(socket,table.size()-1);
		        new Thread(k).start();
		        client.add(k);
		    }
			
		} catch (IOException e) {
			System.out.println("�ͻ�������");
		}
	 }
	   //����������ע����û����ݷ���ÿһ���ͻ���
	   public void yonghuupdate(String name,int id){
			  for(int a=0;a<client.size();a++){
				  client.get(a).update(name, id);
			  }
		  }
	   //�߳������г���
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
   
   //����߳�������û���id��һ��socket
   //���û���socket������
 
   private class Clientprocess implements Runnable{
	  
	   private Socket socket;
	   private InputStream is;
	   private OutputStream os;
	   private BufferedReader reader;
	   private PrintWriter writer;
	   private String command,name,password;
	   private int id;
	   private boolean running=true;
	   
	   //���췽��ʵ����id��socket��reader����������
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
			    	   System.out.println("������"+id);
			    	   client.remove(translateint(id));
			    	   running=false;
			       }
			
		      }
		
	    } 
	   //��Ҫ����ɾ�����̼���ĳһ����ʱ��idת�����ڽ��̼��ϵ�λ��
	    public int translateint(int targetid){
	    	  int q=0;
	    	  for(int a=0;a<client.size();a++){
		          if(targetid==client.get(a).getId())return a;
		      }
	          return q;
	    } 
	    //����talkthread���̷߳������û���������
	    public  void update(String name,int id){
	    	writer.println("�û�����");
	    	writer.println(name);
	    	writer.println(id);
	    	writer.flush();
	    }
	    //����Ⱥ��������Ϣ
	    public void receivequn(String talk,int fromid){
	    	writer.println("Ⱥ�Ի�");
    		writer.println(""+fromid);
    		writer.println(talk);
    		writer.flush();
    		
	    }
	    //���������ͻ��˷����ĶԻ����� ������
	    public void receive(String command,String talk,int fromid){
	    	if(command.equals("�Ի�")){
	    		writer.println("�Ի�");
	    		writer.println(""+fromid);
	    		writer.println(talk);
	    		writer.flush();
	    		
	    	}
	    	
		}
	    //����һ����Ҫ���������û��߳�֪��id����̴߳�����ķ���
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
	 //��������ĸ���������Ϊ�������ϳ����Ե�����Ϊһ������
	   public void process(String command){
		     int toid;
		     String talk;
		   //���������Ǹ����û�������ʱ�õ�
		     String str3,str4;
		    
		     //�����if��������ӹ���
		     if(command.equals("�Ի�")){               //�����̶߳�Ӧ�Ŀͻ��˷�������ʱʹ��
				 try {
					toid=Integer.parseInt(reader.readLine());
					talk=reader.readLine();
					Clientprocess cp=translate(toid);
					if(cp!=null){
						cp.receive(command,talk,id);
					}
					else {
						writer.println("����");
						writer.println("����δ����");
						writer.flush();
					}
				} catch (NumberFormatException e) {
					System.out.println("������"+id);
		            client.remove(translateint(id));
		            running=false;
				} catch (IOException e) {
					System.out.println("������"+id);
					client.remove(translateint(id));
					running=false;
				}
			  }
		   
		     else if(command.equals("���")){
		    	 
		    	 try {
						toid=Integer.parseInt(reader.readLine());
						table.get(id).getFriends().add(toid);//���Ļ���
						Database.addfriend(id, toid);//�������ݿ�
						writer.println("���");
						writer.println("yes");
						writer.flush();
					} catch (NumberFormatException e) {
						System.out.println("������"+id);
			            client.remove(translateint(id));
			            running=false;
					} catch (IOException e) {
						System.out.println("������"+id);
						client.remove(translateint(id));
						running=false;
					}
		     }
		     else if(command.equals("ɾ��")){
		    	 try {
		    		   
						toid=Integer.parseInt(reader.readLine());
						table.get(id).getFriends().remove(translate(id,toid));//���Ļ���
						Database.deletefriend(id, toid);//�������ݿ�
						writer.println("ɾ��");
						writer.println("yes");
						writer.flush();
					} catch (NumberFormatException e) {
						writer.println("ɾ��");
						writer.println("no");
						writer.flush();
					} catch (IOException e) {
						System.out.println("������"+id);
						client.remove(translateint(id));
						running=false;
					}
		     }
              else if(command.equals("����")){
            	  try {
					str3=reader.readLine();
					str4=reader.readLine();
					table.get(id).setName(str3);
					table.get(id).setPassward(str4);
					Database.change(id,str3,str4);
					
					writer.println("�����û�");
					writer.println("yes");
					writer.flush();
				} catch (IOException e) {
					System.out.println("������"+id);
					client.remove(translateint(id));
					running=false;
				}
             }
              else if(command.equals("Ⱥ�Ի�")) {
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