import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Chatclient extends Frame {
    //用户基本信息引用
	String me;
	int meid,targetid;
    Vector<Yonghu>table;//所有用户名集合                        //id与table的顺序不一致
    Vector<Integer>friend;//好友的id集合
    static Vector<Framethread>thread=new Vector<Framethread>();//用于管理对话窗口进程
    Qunthread qt;//用于聊天室管理
    //通信部分引用
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    Login login;
    //ui组件引用
    Vector<JButton> button=new Vector<JButton>();
    JPanel jpanel=new JPanel();//显示用户基本信息界面用
	JPanel jpanel2=new JPanel();//装用户的button
    JScrollPane jsp;
    JList list;
    Icon icon1,icon2,icon3;
    JLabel foot,mypicture,myname;
    JButton add,remove,change,haoyou,qun,aa;//aa是建立好友用的
	public Chatclient() {
		new Thread(new Loginthread()).start();//打开登陆界面并建立一个线程
	
		while(!Login.tuichu){                 //阻止程序在登陆界面未结束前向下执行
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		 //登陆界面不可见
		 //把登陆界面的socket等传到聊天界面
		 login.setVisible(false);
		 
	    this.table=Login.table;
	    this.friend=Login.friend;
	    this.socket=login.getSocket();   
		this.me=table.get(0).getName();
		this.meid=table.get(0).getId();
		this.targetid=meid;
		
		try {
			this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer=new PrintWriter(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//设置出对话界面
		this.setTitle("我是"+me);           
		this.setLocation(300, 100);
		this.setSize(200,400);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n=JOptionPane.showConfirmDialog(null,"想要退出程序吗？","退出提示", JOptionPane.YES_NO_OPTION);
				if(n==0)System.exit(0);  
				
			}

		});
		this.setResizable(false);
		this.setVisible(true);
		this.setLayout(null);
	    ui();//本方法用于集中构造用户界面
		 
       //启动输入线程，输出不需要线程在事件处理时发出就可以了
        new Thread(new Inthread()).start();
	}

    
    public void ui(){
    	
		
		//添加各种组件以及事件监听器
		
		icon1=new ImageIcon("image\\football.jpg");
		icon2=new ImageIcon("image\\M3.jpg");
		foot=new JLabel(icon1);
		mypicture=new JLabel(icon2);
		myname=new JLabel(me);
		this.add(mypicture);
		mypicture.setBounds(15,25,40,40);
		this.add(myname);
		myname.setBounds(65,30,60,20);
		//以上是qq界面的头
		
		//以下是qq界面的滚动面板部分
		this.add(jpanel);
        jpanel.setBounds(10,100,180,250);
        jpanel.setLayout(new BorderLayout());
        listupdate();
        jsp=new JScrollPane(jpanel2);
        jpanel.add(jsp,BorderLayout.CENTER);
        //以下是qq界面功能区部分
        add=new JButton("添加");
        remove=new JButton("删除");
        change=new JButton("更改");
        haoyou=new JButton("好友列表");
        qun=new JButton("群列表");
		this.add(add);
		this.add(remove);
		this.add(change);
		this.add(haoyou);
		this.add(qun);
		add.addActionListener(new MyListener());
		remove.addActionListener(new MyListener());
		change.addActionListener(new MyListener());
		haoyou.addActionListener(new MyListener());
		qun.addActionListener(new MyListener());
		add.setBounds(2,360,65,30);
		remove.setBounds(68,360,65,30);
		change.setBounds(134,360,65,30);
		haoyou.setBounds(2,68,100,30);
		qun.setBounds(103,68,95,30);
		//以下是背景，所以放在最后
		this.add(foot);
	    foot.setBounds(0,0,200,400);
	    
    }
    //本方法绘制群列表
    public void listupdatequn(){
    	jpanel2.setLayout(new FlowLayout());
    	jpanel2.setSize(180,500);
    	jpanel2.removeAll();
    	aa=new JButton("开始群对话");
    	aa.addActionListener(new MyListener());
    	jpanel2.add(aa);
    	jpanel2.updateUI();
    }
    //本方法用于好友列表绘制
    public void listupdate(){
    	icon3=new ImageIcon("image\\头像.jpg");
    	jpanel2.setLayout(new GridLayout(15,1));
    	jpanel2.setSize(180,500);
    	jpanel2.removeAll();
    	for(int a=0;a<friend.size();a++){
    	     aa=new JButton(translatename(friend.get(a)));
    	     aa.setSize(160,20);
    	     aa.setIcon(icon3);
    	     
    	     aa.setActionCommand(""+friend.get(a));
    	     aa.addActionListener(new MyListener());
    	     button.add(aa);
    	     jpanel2.add(aa);
    	}
    
    	jpanel2.updateUI();
    }
	public static void main(String[] args) {
	   
		new Chatclient();
	    
	}
	//把用户的用户名转换成id
	public int translate(String str){
		for(int a=0;a<table.size();a++){
			if(str.equals(table.get(a).getName()))return table.get(a).getId();
		}
			return 0;
	}
	//把好友的id转换成jbutton引用
	public JButton translatebutton(int id){
		for(int a=0;a<button.size();a++){
			if(button.get(a).getActionCommand().equals(""+id))return button.get(a);
		}
		return button.get(0);
	}
	//在窗口关闭以后删掉窗口类
	public static void delete(int id){
		for(int a=0;a<thread.size();a++){
			if(id==thread.get(a).getId())thread.remove(a);
		}
	}
	//把用户的id转换成用户名
	public String translatename(int id){
     	 for(int a=0;a<table.size();a++){
     		 if(table.get(a).getId()==id)return table.get(a).getName();
     	 }
     	 return table.get(0).getName();
     }
	public int translate(int id){
		for(int a=0;a<friend.size();a++){
			if(id==friend.get(a))return a;
		}
		return 0;
	}
	private class MyListener implements ActionListener{
		 Framethread fthread;
         public void actionPerformed(ActionEvent e) {
			String s=e.getActionCommand();
			
			if(s.equals("添加")){
			    String  id=JOptionPane.showInputDialog(null,"输入要添加的账号", null);
			    if(id!=null){
				        writer.println("添加");
				        writer.println(id);
				        friend.add(Integer.parseInt(id));
				        writer.flush();
			    }
			}
			else if(s.equals("删除")){
				String id=JOptionPane.showInputDialog(null,"输入要删除的账号", null);
				if(id!=null){
				      writer.println("删除");
				      writer.println(id);
				     friend.remove(translate(Integer.parseInt(id)));
				     writer.flush();
				}
			}
            else if(s.equals("更改")){
            	String name=JOptionPane.showInputDialog(login,"输入用户名", null);
    			String password=JOptionPane.showInputDialog(login,"输入密码", null);
    			if(name!=null&&password!=null){
    				me=name;
    				listupdate();
    				writer.println("更改");
    			    writer.println(name);
    			    writer.println(password);
    			    writer.flush();
    			}
			}
            else if(s.equals("好友列表")){
            	listupdate();
            }
            else if(s.equals("群列表")){
            	listupdatequn();
            }
            else if(s.equals("开始群对话")){
            	   qt=new Qunthread(reader,writer,me);
            }
            else {
            	 Object[] options = {"对话","删除"};
            	 int response=JOptionPane.showOptionDialog ( null, " 选择功能","对话框",JOptionPane.YES_OPTION ,JOptionPane.PLAIN_MESSAGE,
            	 null, options, options[0] ) ;
            	 targetid=Integer.parseInt(s);
            	 if (response == 0){
            		fthread=new Framethread(reader,writer,translatename(targetid),targetid,meid);
            		thread.add(fthread);
            	 }
            	 else if(response == 1){
            		 writer.println("删除");
     				writer.println(targetid);
     				friend.remove(translate(targetid));
     				writer.flush();
            	 }
            	 
            	 
            }
		}
		
	}
	private class Inthread implements Runnable{
        String str;
         int targetid;
       
        //把id转换成id的相应进程
     public Framethread translate(int id){
        	    Play.play();
        		for(int a=0;a<thread.size();a++){
        			System.out.println(id+"     "+thread.get(a).getId());
        			if(thread.get(a).getId()==id)return thread.get(a);
        		}
        		
        		Framethread fthread=new Framethread(reader,writer,translatename(id),id,meid);
        		thread.add(fthread);
  	            return fthread;
        }
        //用来接收群对话的消息
        public void qunreceive(){
        	 try {
				 targetid=Integer.parseInt(reader.readLine());
				 str=reader.readLine();
				 if(qt!=null)qt.setText(str,translatename(targetid));
			 } catch (IOException e) {
				e.printStackTrace();
			 }
        }
        //用于标准规范接收服务器发来的信息
		public void receive(){
			 try {
				 targetid=Integer.parseInt(reader.readLine());
				 str=reader.readLine();
				 translate(targetid).setText(str);
			 } catch (IOException e) {
				e.printStackTrace();
			 }
		}
		public void run() {
			while(true){
				try {
					str=reader.readLine(); 
					//  下面通过添加if语句来增加功能
					if(str.equals("对话")){
						receive();
					}
					else if(str.equals("群对话")){
						qunreceive();
					}
					else if(str.equals("添加")){
						if(reader.readLine().equals("yes"))JOptionPane.showMessageDialog(null, "添加成功","添加成功",JOptionPane.ERROR_MESSAGE);
						listupdate();
					}
					else if(str.equals("用户更新")){
						String a=reader.readLine();//新注册用户的用户名
						int s=Integer.parseInt(reader.readLine());//新注册用户的id
						table.add(new Yonghu(a,s));
					}
					else if(str.equals("错误")){
						JOptionPane.showMessageDialog(null, reader.readLine(),"错误",JOptionPane.ERROR_MESSAGE);
					}
					else if(str.equals("删除")){
						if(reader.readLine().equals("yes")){
							JOptionPane.showMessageDialog(null, "删除成功","删除成功",JOptionPane.ERROR_MESSAGE);
							listupdate();
						}
						else JOptionPane.showMessageDialog(null, "请输入账号（数字）","删除失败",JOptionPane.ERROR_MESSAGE);
                    }
					else if(str.equals("更改")){
						if(reader.readLine().equals("yes"))
						JOptionPane.showMessageDialog(null,"更改成功","更改成功",JOptionPane.ERROR_MESSAGE);
					}
					
					
					
					
				} catch (IOException e) {
					System.out.println("duifangxiaxianle");
					System.exit(0);
				} 
			}
			
		}
		   
	}
	
	private class Loginthread implements Runnable{

		public void run() {
			login=new Login();
		}
		
	}
	
}
