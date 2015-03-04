import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Chatclient extends Frame {
    //�û�������Ϣ����
	String me;
	int meid,targetid;
    Vector<Yonghu>table;//�����û�������                        //id��table��˳��һ��
    Vector<Integer>friend;//���ѵ�id����
    static Vector<Framethread>thread=new Vector<Framethread>();//���ڹ���Ի����ڽ���
    Qunthread qt;//���������ҹ���
    //ͨ�Ų�������
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    Login login;
    //ui�������
    Vector<JButton> button=new Vector<JButton>();
    JPanel jpanel=new JPanel();//��ʾ�û�������Ϣ������
	JPanel jpanel2=new JPanel();//װ�û���button
    JScrollPane jsp;
    JList list;
    Icon icon1,icon2,icon3;
    JLabel foot,mypicture,myname;
    JButton add,remove,change,haoyou,qun,aa;//aa�ǽ��������õ�
	public Chatclient() {
		new Thread(new Loginthread()).start();//�򿪵�½���沢����һ���߳�
	
		while(!Login.tuichu){                 //��ֹ�����ڵ�½����δ����ǰ����ִ��
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		 //��½���治�ɼ�
		 //�ѵ�½�����socket�ȴ����������
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
		
		//���ó��Ի�����
		this.setTitle("����"+me);           
		this.setLocation(300, 100);
		this.setSize(200,400);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n=JOptionPane.showConfirmDialog(null,"��Ҫ�˳�������","�˳���ʾ", JOptionPane.YES_NO_OPTION);
				if(n==0)System.exit(0);  
				
			}

		});
		this.setResizable(false);
		this.setVisible(true);
		this.setLayout(null);
	    ui();//���������ڼ��й����û�����
		 
       //���������̣߳��������Ҫ�߳����¼�����ʱ�����Ϳ�����
        new Thread(new Inthread()).start();
	}

    
    public void ui(){
    	
		
		//��Ӹ�������Լ��¼�������
		
		icon1=new ImageIcon("image\\football.jpg");
		icon2=new ImageIcon("image\\M3.jpg");
		foot=new JLabel(icon1);
		mypicture=new JLabel(icon2);
		myname=new JLabel(me);
		this.add(mypicture);
		mypicture.setBounds(15,25,40,40);
		this.add(myname);
		myname.setBounds(65,30,60,20);
		//������qq�����ͷ
		
		//������qq����Ĺ�����岿��
		this.add(jpanel);
        jpanel.setBounds(10,100,180,250);
        jpanel.setLayout(new BorderLayout());
        listupdate();
        jsp=new JScrollPane(jpanel2);
        jpanel.add(jsp,BorderLayout.CENTER);
        //������qq���湦��������
        add=new JButton("���");
        remove=new JButton("ɾ��");
        change=new JButton("����");
        haoyou=new JButton("�����б�");
        qun=new JButton("Ⱥ�б�");
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
		//�����Ǳ��������Է������
		this.add(foot);
	    foot.setBounds(0,0,200,400);
	    
    }
    //����������Ⱥ�б�
    public void listupdatequn(){
    	jpanel2.setLayout(new FlowLayout());
    	jpanel2.setSize(180,500);
    	jpanel2.removeAll();
    	aa=new JButton("��ʼȺ�Ի�");
    	aa.addActionListener(new MyListener());
    	jpanel2.add(aa);
    	jpanel2.updateUI();
    }
    //���������ں����б����
    public void listupdate(){
    	icon3=new ImageIcon("image\\ͷ��.jpg");
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
	//���û����û���ת����id
	public int translate(String str){
		for(int a=0;a<table.size();a++){
			if(str.equals(table.get(a).getName()))return table.get(a).getId();
		}
			return 0;
	}
	//�Ѻ��ѵ�idת����jbutton����
	public JButton translatebutton(int id){
		for(int a=0;a<button.size();a++){
			if(button.get(a).getActionCommand().equals(""+id))return button.get(a);
		}
		return button.get(0);
	}
	//�ڴ��ڹر��Ժ�ɾ��������
	public static void delete(int id){
		for(int a=0;a<thread.size();a++){
			if(id==thread.get(a).getId())thread.remove(a);
		}
	}
	//���û���idת�����û���
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
			
			if(s.equals("���")){
			    String  id=JOptionPane.showInputDialog(null,"����Ҫ��ӵ��˺�", null);
			    if(id!=null){
				        writer.println("���");
				        writer.println(id);
				        friend.add(Integer.parseInt(id));
				        writer.flush();
			    }
			}
			else if(s.equals("ɾ��")){
				String id=JOptionPane.showInputDialog(null,"����Ҫɾ�����˺�", null);
				if(id!=null){
				      writer.println("ɾ��");
				      writer.println(id);
				     friend.remove(translate(Integer.parseInt(id)));
				     writer.flush();
				}
			}
            else if(s.equals("����")){
            	String name=JOptionPane.showInputDialog(login,"�����û���", null);
    			String password=JOptionPane.showInputDialog(login,"��������", null);
    			if(name!=null&&password!=null){
    				me=name;
    				listupdate();
    				writer.println("����");
    			    writer.println(name);
    			    writer.println(password);
    			    writer.flush();
    			}
			}
            else if(s.equals("�����б�")){
            	listupdate();
            }
            else if(s.equals("Ⱥ�б�")){
            	listupdatequn();
            }
            else if(s.equals("��ʼȺ�Ի�")){
            	   qt=new Qunthread(reader,writer,me);
            }
            else {
            	 Object[] options = {"�Ի�","ɾ��"};
            	 int response=JOptionPane.showOptionDialog ( null, " ѡ����","�Ի���",JOptionPane.YES_OPTION ,JOptionPane.PLAIN_MESSAGE,
            	 null, options, options[0] ) ;
            	 targetid=Integer.parseInt(s);
            	 if (response == 0){
            		fthread=new Framethread(reader,writer,translatename(targetid),targetid,meid);
            		thread.add(fthread);
            	 }
            	 else if(response == 1){
            		 writer.println("ɾ��");
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
       
        //��idת����id����Ӧ����
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
        //��������Ⱥ�Ի�����Ϣ
        public void qunreceive(){
        	 try {
				 targetid=Integer.parseInt(reader.readLine());
				 str=reader.readLine();
				 if(qt!=null)qt.setText(str,translatename(targetid));
			 } catch (IOException e) {
				e.printStackTrace();
			 }
        }
        //���ڱ�׼�淶���շ�������������Ϣ
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
					//  ����ͨ�����if��������ӹ���
					if(str.equals("�Ի�")){
						receive();
					}
					else if(str.equals("Ⱥ�Ի�")){
						qunreceive();
					}
					else if(str.equals("���")){
						if(reader.readLine().equals("yes"))JOptionPane.showMessageDialog(null, "��ӳɹ�","��ӳɹ�",JOptionPane.ERROR_MESSAGE);
						listupdate();
					}
					else if(str.equals("�û�����")){
						String a=reader.readLine();//��ע���û����û���
						int s=Integer.parseInt(reader.readLine());//��ע���û���id
						table.add(new Yonghu(a,s));
					}
					else if(str.equals("����")){
						JOptionPane.showMessageDialog(null, reader.readLine(),"����",JOptionPane.ERROR_MESSAGE);
					}
					else if(str.equals("ɾ��")){
						if(reader.readLine().equals("yes")){
							JOptionPane.showMessageDialog(null, "ɾ���ɹ�","ɾ���ɹ�",JOptionPane.ERROR_MESSAGE);
							listupdate();
						}
						else JOptionPane.showMessageDialog(null, "�������˺ţ����֣�","ɾ��ʧ��",JOptionPane.ERROR_MESSAGE);
                    }
					else if(str.equals("����")){
						if(reader.readLine().equals("yes"))
						JOptionPane.showMessageDialog(null,"���ĳɹ�","���ĳɹ�",JOptionPane.ERROR_MESSAGE);
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
