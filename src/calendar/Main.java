
/*
 *  ��ư ������ year, month�� ���� �����ǵ��� �޼��� ����� �� ��!
 * 
 * */

package calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Main extends JFrame implements ActionListener{
	
	// DB�� ���õ� �������
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:XE";
	String user = "admin";
	String password = "1234";
	public Connection con;
	
	// �������� ���� ������Ʈ�� ���õ� �������
	JPanel wrapper;
	JPanel p_selectArea;
	JPanel p_header;
	JPanel p_cal;
	JButton bt_next;
	JButton bt_prev;
	JLabel la_next;
	JLabel la_prev;
	JTextArea area;
	
	// �̹��� �����ܰ� ���õ� �������
	ImageIcon temp_next;
	ImageIcon temp_prev;
	URL path_next= getClass().getClassLoader().getResource("next_icon.png");
	URL path_prev= getClass().getClassLoader().getResource("back_icon.png");
	public static final int iconWIDTH=50;
	public static final int iconHEIGHT=38;
	Image img1, img2;
	ImageIcon next;
	ImageIcon prev;
	
	// �޷°� ���õ� �������
	// Calendar �ν��Ͻ� ����
    Calendar cal = Calendar.getInstance();
    ArrayList<Cell> cellArray = new ArrayList<Cell>(); // Cell�� ���� �迭
    int index=0; // �迭�� ��� Cell���� �ε�����
    
    // ���� ǥ�� ���
    String[] calHeader = {"��","��","ȭ","��","��","��","��"};
    String[] imgName = {"sun.png","mon.png","tuse.png","wed.png","thu.png","fri.png","sat.png"};
    // ��¥ ������ �迭
    String[] calDate = new String[42];
    
    // ����ڰ� ������ ��,�� �����ϴ� �������
    int year;
    int month;
    
    // ǥ���� ��¥�� ���õ� ����
    int width=calHeader.length; // �迭 ���� ����
    int startDay;   // �� ���� ����
    int lastDate;    // �� ������ ��¥
    int inputDate=0;  // Cell�� �Է��� �� ��¥. ���߿� Integer.toString() ���ֱ� 

	public Main() {
		
		// DB�� �����ϱ�
		connect();
		
		wrapper = new JPanel();
		p_selectArea = new JPanel();
		p_header = new JPanel();
		p_cal = new JPanel();
		bt_next = new JButton();
		bt_prev = new JButton();
		area = new JTextArea();
		setLayout(new BorderLayout());
		
		// ������ �����ϱ�
		Dimension p = new Dimension(800, 50);
		Dimension bt = new Dimension(50,40);
		wrapper.setPreferredSize(new Dimension(800,680));
		p_selectArea.setPreferredSize(p);
		p_header.setPreferredSize(p);
		p_cal.setPreferredSize(new Dimension(739,638));
		bt_next.setPreferredSize(bt);
		bt_prev.setPreferredSize(bt);
		area.setPreferredSize(new Dimension(200, 40));
		
		// ���� �����ϱ� (�ӽ÷�)
		wrapper.setBackground(Color.WHITE);
		p_selectArea.setBackground(Color.WHITE);
		p_header.setBackground(Color.WHITE);
		p_cal.setBackground(Color.WHITE);
		p_cal.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		// ������, ������ ��ư�� �̹��� �ֱ�
		temp_next = new ImageIcon(path_next);
		temp_prev = new ImageIcon(path_prev);
		// �̹��� ������ ����
		img1=temp_next.getImage().getScaledInstance(iconWIDTH,iconHEIGHT,Image.SCALE_DEFAULT);
		next=new ImageIcon(img1);
		img2=temp_prev.getImage().getScaledInstance(iconWIDTH,iconHEIGHT,Image.SCALE_DEFAULT);
		prev=new ImageIcon(img2);
		// ��ư�� ������ �׵θ� ���ֱ�
		bt_next.setBackground(Color.WHITE);
		bt_next.setBorderPainted(false);
		bt_prev.setBackground(Color.WHITE);
		bt_prev.setBorderPainted(false);
		//��ư�� �̹��� �ֱ�
		bt_next.setIcon(next);
		bt_prev.setIcon(prev);
		
		// ������Ʈ �����ϱ�
		p_selectArea.add(bt_prev);
		p_selectArea.add(area);
		p_selectArea.add(bt_next);
		wrapper.add(p_selectArea, BorderLayout.NORTH);
		wrapper.add(p_header, BorderLayout.CENTER);
		wrapper.add(p_cal, BorderLayout.SOUTH);
		add(wrapper);
		
		// ���� ��� �����ϱ�
		year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        System.out.println("���� ��¥�� "+year+"��, "+(month+1)+"��");   
        // �޷� ��¿� �ʿ��� �޼��� ȣ���ϱ�
		setDate(year, month);
        setCal(year, month);
        
	   // �޷� ��� ��� "�Ͽ�ȭ�������"
	   for(int i=0; i<width; i++){
		  URL path = getClass().getClassLoader().getResource(imgName[i]);
		  ImageIcon temp = new ImageIcon(path);
		  Image Img = temp.getImage().getScaledInstance(100, 40, Image.SCALE_DEFAULT);
		  ImageIcon Icon = new ImageIcon(Img);
		  JLabel week = new JLabel(Icon);
	  	 String day = calHeader[i];

	  	 week.setPreferredSize(new Dimension(100,50));
	  	 week.setFont(new Font("����", Font.BOLD, 30));
	  	 p_header.setLayout(new FlowLayout());
	  	 p_header.add(week);
	   }
		
		// ������Ʈ�� ������ �����ϱ�
		bt_next.addActionListener(this);
		bt_prev.addActionListener(this);
		
		setSize(800, 800);
		setLocationRelativeTo(null);
		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				disconnect();
			}
		});
	}
	// �޷� ��ܺ��� ��ư�� Ŭ���ϸ� ��,�� �Ѿ����....
	public void setDate(int year, int month) {
		area.setFont(new Font("����", Font.BOLD, 32));
		area.setText(" "+year+"��  "+(month+1)+"��");
		area.setForeground(new Color(150,126,188));
	}
	// �޷¿� ����ϱ� ���� ��¥ �����ϱ�
	public void setCal(int year, int month) {
		
		p_cal.removeAll();
		cellArray.clear();
		inputDate = 0;
		
        cal.set(year, month, 1);
        
        startDay = cal.get(Calendar.DAY_OF_WEEK); // �� ���� ����. 1���� ����. 
        lastDate = cal.getActualMaximum(Calendar.DATE); // �� ������ ��¥
        System.out.println("�̹����� �����ϴ� ������ "+startDay);
        System.out.println("�̹����� ������ ��¥�� "+lastDate);
        
        // 2�� �迭�� ��¥ �Է�
        for(int i=0;i<42;i++) {
        	if(i+1>=startDay && inputDate<lastDate) {
        		inputDate++;
        		Cell cell = new Cell(this, i, year, month, inputDate);
        		p_cal.add(cell);
        		System.out.print("��¥�� "+inputDate+", ");
        		cellArray.add(cell);
        		
        		if(i%7==0) {
        			// �Ͽ����̴ϱ� ����������!!
        			cell.setColor(Color.RED);
        		}else if(i%7==6){
        			// ������̴ϱ� �Ķ���
        			cell.setColor(Color.BLUE);
        		}
        	}else {
        		EmptyCell ec = new EmptyCell();
        		p_cal.add(ec);
        	}
        }
        p_cal.updateUI();
//        System.out.println("3. UI������Ʈ");
    }	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==bt_next) {
			if(month<11) {
				month++;
				System.out.println("����ڰ� ��ư�� ���� ���� "+(month+1));
			}else {
				month=0;
				year++;
			}
		}else if(obj==bt_prev) {
			if(1<=month) {
				month--;
				System.out.println("����ڰ� ��ư�� ���� ���� "+(month+1));
			}else {
				month=11;
				year--;
			}
		}
		setDate(year,month);
		setCal(year, month);
	}
	// DB�� ����
	// DB����
	public void connect() {
		try {
			Class.forName(driver);
			System.out.println("=adminManager driver �ε�=");
			con = DriverManager.getConnection(url, user, password);
			if (con == null) { // DB���� ���н�
				JOptionPane.showMessageDialog(this, "DB ���� ����");
			} else { // DB���� ������
				System.out.println("DB ���� ����");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// DB�������
	public void disconnect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new Main();
	}
}
