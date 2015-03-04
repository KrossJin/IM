import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;

public class Qunthread {

	TextField textfield=new TextField();//�����ı���
	TextArea textarea=new TextArea();//��ʾ�ı���
	JPanel jpanel=new JPanel();//��ʾ�û�������Ϣ������
	JButton delete=new JButton("delete");
	JButton enter=new JButton("enter");
	BufferedReader reader;
	PrintWriter writer;
	Frame frame;
	String me;
	 Icon icon1;
	JLabel foot;
	DateFormat df;//������ȡϵͳʱ��
	public Qunthread(BufferedReader reader,PrintWriter writer,String me){
		this.me=me;
		frame=new Frame("������  ����"+me);
		frame.setLocation(300, 100);
		frame.setSize(600,400);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();       
			}

		});
		
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLayout(null);
		
		   icon1=new ImageIcon("C:\\Documents and Settings\\Administrator\\����\\�ز�\\football.jpg");
		   foot=new JLabel(icon1);
	
		frame.add(textarea);
        textarea.setBounds(5,30,400,270);
        frame.add(textfield);
        textfield.setBounds(5,310,400,40);
        textfield.addKeyListener(new MyAction());
        frame.add(delete);
        delete.setBounds(20,360,70,25);
        delete.addActionListener(new MyAction());
        frame.add(enter);
        enter.setBounds(250,360,70,25);
        enter.addActionListener(new MyAction());
        frame.add(foot);
		foot.setBounds(0,0,600,400);
		this.reader=reader;
		this.writer=writer;
		
	}
	public void close(){
		frame.setVisible(false);
	}
	
	public void setText(String str,String target){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(textarea.getText().equals(""))textarea.append(target+": "+str+"    "+df.format(new Date()));
		else textarea.append("\n"+target+": "+str+"    "+df.format(new Date()));
	}
	private class MyAction extends KeyAdapter implements ActionListener  {
        public void send(String str){
        	writer.println("Ⱥ�Ի�");
			writer.println(str);
			writer.flush();
			
        }
		public void keyPressed(KeyEvent ke) {
			int key=ke.getKeyCode();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(key==KeyEvent.VK_ENTER&&!textfield.getText().equals("")){
				
				send(textfield.getText());
				textfield.setText("");
			}
			
		}

		public void actionPerformed(ActionEvent e) {
			int ww;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str=e.getActionCommand();
			if(str.equals("enter")&&!textfield.getText().equals("")){
				
				send(textfield.getText());
				textfield.setText("");
			}
			if(str.equals("delete")){
				ww=textfield.getText().length()-1;
				if(ww>0)
				textfield.setText(textfield.getText().substring(0,ww));
				else textfield.setText("");
			}
		}
		
	}
	
}