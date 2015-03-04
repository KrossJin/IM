import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.net.*;
import java.util.Vector;

import javax.swing.*;

public class Login extends JFrame {

	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	TextField tf=new TextField();
	JPasswordField  passward=new JPasswordField();
	JLabel zhanghao,mima;
	JButton login=new JButton("��½");
	JButton zhuce=new JButton("ע���˺�");
	static Vector<Yonghu> table=new Vector<Yonghu>();
	static Vector<Integer>friend=new Vector<Integer>();
	static boolean tuichu=false;
	JLabel foot,messi;
	Icon icon1,icon2;
	public Login(){
		 
		  this.setTitle("��½����");
		  this.setLocation(300, 100);
		  this.setSize(378,291);
		  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  this.setResizable(false);
		  this.setVisible(true);
		  this.setLayout(null);
		  icon1=new ImageIcon("image\\football.jpg");
		  foot=new JLabel(icon1);
		   
		  zhanghao=new JLabel("�˺�");
		  mima=new JLabel("����");
		  this.add(zhanghao);
		  zhanghao.setBounds(10,20,30,20);
			this.add(tf);
			tf.setBounds(80,20,200,20);
			this.add(mima);
			mima.setBounds(10,60,30,20);
			this.add(passward);
			passward.setBounds(80,60,200,20);
			this.add(login);
			login.setBounds(150,160,80,30);
			login.addActionListener(new MyListener());
			this.add(zhuce);
			zhuce.setBounds(140,200,100,30);
			this.add(foot);
			foot.setBounds(0,0,378,291);
			zhuce.addActionListener(new MyListener());
			try {
                socket=new Socket("211.87.230.104",8888);
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer=new PrintWriter(socket.getOutputStream());
				
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	}
	//getsocket������socket���ô���chatclient��ȥ
	public Socket getSocket(){
		return socket;
	}
	
   private class MyListener implements ActionListener{
     String name;
     int id;//�����û��˺Ŷ�Ӧ����
     //receive������Ҫ�ǽ��ܺ����б�
	 public void receive(){
		try {
			 name=reader.readLine();
			while(name!=null){
				id=Integer.parseInt(reader.readLine());
				table.add(new Yonghu(name,id));
				name=reader.readLine();
				if(name.equals("over"))name=null;
			}
			name=reader.readLine();
			while(name!=null){
				if(!name.equals("over")){
				     id=Integer.parseInt(name);
				      friend.add(id);
				     name=reader.readLine();
				 }
				if(name.equals("over"))name=null;
			}
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	 public void actionPerformed(ActionEvent e) {
		String str=e.getActionCommand();
		Boolean error=true;
		if(str.equals("��½")){
			if(tf.getText().equals(""))JOptionPane.showMessageDialog(null,"�û�������Ϊ��","����",JOptionPane.ERROR_MESSAGE);
			else {
				try{
				    Integer.parseInt(tf.getText());
				}
				catch(Exception d){
					JOptionPane.showMessageDialog(null,"�˺Ų���Ϊ��ĸ","����",JOptionPane.ERROR_MESSAGE);
					error=false;
				}
				if(error){
				     writer.println("��½");
				     writer.println(tf.getText());
				     writer.println(passward.getText());
				     writer.flush();
				     try {
					     if(reader.readLine().equals("yes")){
						     receive();
						     Login.tuichu=true;
						 }
					     else JOptionPane.showMessageDialog(null,"�û������������","����",JOptionPane.ERROR_MESSAGE);
				     } catch (HeadlessException e1) {
					     e1.printStackTrace();
				     } catch (IOException e1) {
					     e1.printStackTrace();
				     }
				}
			}
		}
		if(str.equals("ע���˺�")){
			String name=JOptionPane.showInputDialog(login,"�����û���", null);
			String password=JOptionPane.showInputDialog(login,"��������", null);
			String queren=JOptionPane.showInputDialog(login,"ȷ����������", null);
			if(name!=null&&password!=null&&queren!=null){
	           if(password.equals(queren)){
			       writer.println("ע��");
			       writer.println(name);
			       writer.println(password);
			       writer.flush();
			       try {
				
				       if(reader.readLine().equals("yes")){
					       JOptionPane.showMessageDialog(null,"ע��ɹ�"+"\n����˺���"+reader.readLine(),"֪ͨ",JOptionPane.ERROR_MESSAGE);
					       receive();
					       Login.tuichu=true;
				       }
				       else JOptionPane.showMessageDialog(null,"ע��ʧ��","֪ͨ",JOptionPane.ERROR_MESSAGE);
			       } catch (HeadlessException e1) {
				       e1.printStackTrace();
			       } catch (IOException e1) {
				       e1.printStackTrace();
			       }
	            }
	        }
		}
	 }
	  
   }
  }
  
