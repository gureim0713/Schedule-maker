
/*
 * 맨 처음에 해야할 것!
 * DB에 연결해서 table select 하고, year, month, day 컬럼에서 나와 맞는 날짜가 있다면
 * 해당 날짜의 memo와 sticker를 넘겨받는다.
 * 
 * f_memo에서 등록버튼 눌러준 다음에도 마찬가지로 DB 다시 뒤져서 컴포넌트 붙여줄 것
 * Main 전체를 updateUI 해줄 것!! 
 *  f_memo가 flag를 반환하게 한 다음에 
 *  memo_flag = f_memo.return();
 *  if(memo_flag){
 *  	main.setDate();
 *  	main.setCal();
 *  }
 * */

package calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Cell extends JPanel{

	// 달력 날짜 출력과 관련된 멤버변수
	JPanel p_date;
	JLabel la_date;
	int year;
	int month;
	int date;
	
	// 메모와 관련된 멤버변수
	JPanel p_memo;
	JLabel la_memo;
	String memo=null;
	
	// 날짜와 스티커를 붙여줄 패널
	JPanel p_DS;
	
	// 스티커와 관련된 멤버변수
	JPanel p_sticker;
	JLabel la_sticker;
	ImageIcon icon;
	Image img;
	String stickerName=null;
	
	// 메모 프레임과 관련된 변수
	Memo f_memo; // 새로 열리는 프레임
	
	// 넘겨 받은 매개변수와 관련된 멤버변수
	Main main;
	int index;
	
	// DB 조회 성공 여부
	boolean flag=false;
	
	public Cell(Main main, int index, int year, int month, int date) {
		this.main= main;
		this.index = index;
		this.year= year;
		this.month = month;
		this.date = date;
		this.stickerName = stickerName;
		this.memo= memo;
	
		getData();
		
		p_date = new JPanel();
		p_memo = new JPanel();
		p_DS = new JPanel();
		p_sticker = new JPanel();
		
		setLayout(new BorderLayout());
		
		// 컴포넌트 크기 지정
		p_date.setPreferredSize(new Dimension(40,40));
		p_sticker.setPreferredSize(new Dimension(45,40));
		p_DS.setPreferredSize(new Dimension(100,40));
		p_memo.setPreferredSize(new Dimension(90, 60));
	
		// 임시로 색깔 지정
		p_date.setBackground(Color.WHITE);
		p_sticker.setBackground(Color.WHITE);
		p_DS.setBackground(Color.WHITE);
		p_memo.setBackground(Color.WHITE);
				
		// 날짜 넣어주는 메서드
		setDate(date);
		// DB에서 조회한 스티커 이미지가 있다면...
		if(flag) {
			setSticker(stickerName);
		}
		// DB에서 조회한 메모가 있다면....
		if(flag) {
			setMemo(memo);
		}
		// 컴포넌트 조립하기
		p_DS.add(p_date, BorderLayout.WEST);
		p_DS.add(p_sticker, BorderLayout.EAST);
		add(p_DS, BorderLayout.NORTH);
		add(p_memo, BorderLayout.SOUTH);
		
		// 클릭 당하면.....
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				clicked();
			}
		});
		
		// Cell 하나의 크기 지정
		setPreferredSize(new Dimension(100,100));
		setBorder(BorderFactory.createLineBorder(new Color(150,126,188), 2));
	}
	// 스티커 붙여주기
	public void setSticker(String stickerName) {
		
		// 스티커 이미지 사이즈 조절하고 붙이기
		URL path=getClass().getClassLoader().getResource(stickerName);
		icon = new ImageIcon(path);
		// 사이즈 조절
		img=icon.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
		icon =new ImageIcon(img);
		la_sticker = new JLabel(icon);
		la_sticker.setPreferredSize(new Dimension(44,30));
		
		p_sticker.add(la_sticker);
	}
	// 날짜 붙여주기
	public void setDate(int date) {
		
		// 넘겨받은 날짜 넣어주기
		la_date = new JLabel(Integer.toString(date), SwingConstants.LEFT);
		la_date.setFont(new Font("돋움", Font.BOLD, 20));
		la_date.setForeground(new Color(183,156,218));
		la_date.setPreferredSize(new Dimension(40,30));
		
		p_date.add(la_date);
	}
	// 주말에 날짜 색깔 바꿔주기
	public void setColor(Color color) {
		la_date.setForeground(color);		
	}
	// 메모 붙여주기
	public void setMemo(String memo) {
		
		// 메모 넣어주기
		la_memo = new JLabel(memo, SwingConstants.LEFT);
		la_memo.setFont(new Font("돋움", Font.LAYOUT_LEFT_TO_RIGHT, 15));
		la_memo.setPreferredSize(new Dimension(90, 55));
		
		p_memo.add(la_memo);
	}
	public void clicked() {
		f_memo = new Memo(main, year,month,date);
	}
	// DB에서 나의 메모, 스티커 정보 조회하기
	public void getData() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql = "select * from calendar where year="+year+" and month="+month+" and day="+date;
		System.out.println("Cell의 211 line>>> select 쿼리문은 "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // 일치하는 날짜가 있고 메모가 있다면...
				memo = rs.getString("memo");
				stickerName = rs.getString("sticker");
				flag=true;
				System.out.println("■■■■■■■ 메모가 있는 나의 날짜는 "+year+"년, "+month+"월, "+date+"일 입니다."+"■■■■■■■");
				System.out.println("■■■■■■■ 메모가 있는 나의 스티커는 "+stickerName+", 메모는 <"+memo+"> 입니다."+"■■■■■■■");
			}else { 
				System.out.println("▶▶▶▶▶▶▶ "+year+"년, "+month+"월, "+date+"일에는 "+"DB에 일치하는 정보가 없습니다!! ");
				flag=false;
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
