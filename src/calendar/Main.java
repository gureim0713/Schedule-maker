
/*
 *  버튼 누르면 year, month에 값이 조절되도록 메서드 만들어 줄 것!
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
	
	// DB와 관련된 멤버변수
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:XE";
	String user = "admin";
	String password = "1234";
	public Connection con;
	
	// 디자인을 입힐 컴포넌트와 관련된 멤버변수
	JPanel wrapper;
	JPanel p_selectArea;
	JPanel p_header;
	JPanel p_cal;
	JButton bt_next;
	JButton bt_prev;
	JLabel la_next;
	JLabel la_prev;
	JTextArea area;
	
	// 이미지 아이콘과 관련된 멤버변수
	ImageIcon temp_next;
	ImageIcon temp_prev;
	URL path_next= getClass().getClassLoader().getResource("next_icon.png");
	URL path_prev= getClass().getClassLoader().getResource("back_icon.png");
	public static final int iconWIDTH=50;
	public static final int iconHEIGHT=38;
	Image img1, img2;
	ImageIcon next;
	ImageIcon prev;
	
	// 달력과 관련된 멤버변수
	// Calendar 인스턴스 생성
    Calendar cal = Calendar.getInstance();
    ArrayList<Cell> cellArray = new ArrayList<Cell>(); // Cell을 담을 배열
    int index=0; // 배열에 담긴 Cell들의 인덱스값
    
    // 요일 표시 헤더
    String[] calHeader = {"일","월","화","수","목","금","토"};
    String[] imgName = {"sun.png","mon.png","tuse.png","wed.png","thu.png","fri.png","sat.png"};
    // 날짜 데이터 배열
    String[] calDate = new String[42];
    
    // 사용자가 선택한 년,월 세팅하는 멤버변수
    int year;
    int month;
    
    // 표시할 날짜와 관련된 변수
    int width=calHeader.length; // 배열 가로 넓이
    int startDay;   // 월 시작 요일
    int lastDate;    // 월 마지막 날짜
    int inputDate=0;  // Cell에 입력해 줄 날짜. 나중에 Integer.toString() 해주기 

	public Main() {
		
		// DB와 연결하기
		connect();
		
		wrapper = new JPanel();
		p_selectArea = new JPanel();
		p_header = new JPanel();
		p_cal = new JPanel();
		bt_next = new JButton();
		bt_prev = new JButton();
		area = new JTextArea();
		setLayout(new BorderLayout());
		
		// 사이즈 조정하기
		Dimension p = new Dimension(800, 50);
		Dimension bt = new Dimension(50,40);
		wrapper.setPreferredSize(new Dimension(800,680));
		p_selectArea.setPreferredSize(p);
		p_header.setPreferredSize(p);
		p_cal.setPreferredSize(new Dimension(739,638));
		bt_next.setPreferredSize(bt);
		bt_prev.setPreferredSize(bt);
		area.setPreferredSize(new Dimension(200, 40));
		
		// 배경색 지정하기 (임시로)
		wrapper.setBackground(Color.WHITE);
		p_selectArea.setBackground(Color.WHITE);
		p_header.setBackground(Color.WHITE);
		p_cal.setBackground(Color.WHITE);
		p_cal.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		// 이전달, 다음달 버튼에 이미지 넣기
		temp_next = new ImageIcon(path_next);
		temp_prev = new ImageIcon(path_prev);
		// 이미지 사이즈 조절
		img1=temp_next.getImage().getScaledInstance(iconWIDTH,iconHEIGHT,Image.SCALE_DEFAULT);
		next=new ImageIcon(img1);
		img2=temp_prev.getImage().getScaledInstance(iconWIDTH,iconHEIGHT,Image.SCALE_DEFAULT);
		prev=new ImageIcon(img2);
		// 버튼의 배경색과 테두리 없애기
		bt_next.setBackground(Color.WHITE);
		bt_next.setBorderPainted(false);
		bt_prev.setBackground(Color.WHITE);
		bt_prev.setBorderPainted(false);
		//버튼에 이미지 넣기
		bt_next.setIcon(next);
		bt_prev.setIcon(prev);
		
		// 컴포넌트 조립하기
		p_selectArea.add(bt_prev);
		p_selectArea.add(area);
		p_selectArea.add(bt_next);
		wrapper.add(p_selectArea, BorderLayout.NORTH);
		wrapper.add(p_header, BorderLayout.CENTER);
		wrapper.add(p_cal, BorderLayout.SOUTH);
		add(wrapper);
		
		// 현재 년월 셋팅하기
		year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        System.out.println("현재 날짜는 "+year+"년, "+(month+1)+"월");   
        // 달력 출력에 필요한 메서드 호출하기
		setDate(year, month);
        setCal(year, month);
        
	   // 달력 헤더 출력 "일월화수목금토"
	   for(int i=0; i<width; i++){
		  URL path = getClass().getClassLoader().getResource(imgName[i]);
		  ImageIcon temp = new ImageIcon(path);
		  Image Img = temp.getImage().getScaledInstance(100, 40, Image.SCALE_DEFAULT);
		  ImageIcon Icon = new ImageIcon(Img);
		  JLabel week = new JLabel(Icon);
	  	 String day = calHeader[i];

	  	 week.setPreferredSize(new Dimension(100,50));
	  	 week.setFont(new Font("돋움", Font.BOLD, 30));
	  	 p_header.setLayout(new FlowLayout());
	  	 p_header.add(week);
	   }
		
		// 컴포넌트와 리스너 연결하기
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
	// 달력 상단부의 버튼을 클릭하면 년,월 넘어가도록....
	public void setDate(int year, int month) {
		area.setFont(new Font("돋움", Font.BOLD, 32));
		area.setText(" "+year+"년  "+(month+1)+"월");
		area.setForeground(new Color(150,126,188));
	}
	// 달력에 출력하기 위한 날짜 세팅하기
	public void setCal(int year, int month) {
		
		p_cal.removeAll();
		cellArray.clear();
		inputDate = 0;
		
        cal.set(year, month, 1);
        
        startDay = cal.get(Calendar.DAY_OF_WEEK); // 월 시작 요일. 1부터 시작. 
        lastDate = cal.getActualMaximum(Calendar.DATE); // 월 마지막 날짜
        System.out.println("이번달의 시작하는 요일은 "+startDay);
        System.out.println("이번달의 마지막 날짜는 "+lastDate);
        
        // 2차 배열에 날짜 입력
        for(int i=0;i<42;i++) {
        	if(i+1>=startDay && inputDate<lastDate) {
        		inputDate++;
        		Cell cell = new Cell(this, i, year, month, inputDate);
        		p_cal.add(cell);
        		System.out.print("날짜는 "+inputDate+", ");
        		cellArray.add(cell);
        		
        		if(i%7==0) {
        			// 일요일이니까 빨간색으로!!
        			cell.setColor(Color.RED);
        		}else if(i%7==6){
        			// 토요일이니까 파랑색
        			cell.setColor(Color.BLUE);
        		}
        	}else {
        		EmptyCell ec = new EmptyCell();
        		p_cal.add(ec);
        	}
        }
        p_cal.updateUI();
//        System.out.println("3. UI업데이트");
    }	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==bt_next) {
			if(month<11) {
				month++;
				System.out.println("사용자가 버튼을 누른 달은 "+(month+1));
			}else {
				month=0;
				year++;
			}
		}else if(obj==bt_prev) {
			if(1<=month) {
				month--;
				System.out.println("사용자가 버튼을 누른 달은 "+(month+1));
			}else {
				month=11;
				year--;
			}
		}
		setDate(year,month);
		setCal(year, month);
	}
	// DB와 연결
	// DB연결
	public void connect() {
		try {
			Class.forName(driver);
			System.out.println("=adminManager driver 로드=");
			con = DriverManager.getConnection(url, user, password);
			if (con == null) { // DB접속 실패시
				JOptionPane.showMessageDialog(this, "DB 접속 실패");
			} else { // DB접속 성공시
				System.out.println("DB 접속 성공");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// DB연결끊기
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
