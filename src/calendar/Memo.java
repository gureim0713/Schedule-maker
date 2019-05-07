
/*
 * 메모 프레임 켜서 입력한 값을 '등록' 버튼 누르면 DB에 저장해주기
 * 
 * */

package calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class Memo extends JFrame{
	
	JPanel p_upper;
	JPanel p_bottom;
	
	// 사용자가 선택한 날짜 출력
	JPanel p_selectDay;
	JLabel la_selectDay;
	
	// 사용자가 고를 수 있도록 스티커 출력
	JPanel p_sticker;
	public String stickerName="";
	public int stickerIndex=100;
	public ArrayList<PanelSticker> stickerArray = new ArrayList();
	public int mClickCount=0;
	
	// 메모 내용을 적을 공간
	JPanel p_text;
	JTextArea area;
	String memo="";
	String keyValue="";
	String beforeValue="";
	
	// DB에서 얻어온 메모 내용
	boolean memoFlag=false;
	
	// DB와 연동할 등록, 취소 버튼
	JButton bt_regist;
	JButton bt_del;
	boolean flag=false;
	boolean deleteFlag=false;
	
	Main main; // 등록이 끝나고 main의 달력 정보를 갱신해주기 위해서
	int year;
	int month;
	int date;
	
	public Memo(Main main, int year, int month, int date) {
		this.main = main;
		this.year = year;
		this.month=month;
		this.date=date;
		
		getMemoData(); // 이전에 등록한 메모가 있는지 없는지 여부 출력해주기
		
		p_upper= new JPanel();
		p_bottom = new JPanel();
		// 사용자가 선택한 날짜
		p_selectDay = new JPanel();
		// 사용자가 고를 수 있도록 스티커 출력하기
		p_sticker = new JPanel();
		
		// 사용자가 메모 적을 공간
		p_text = new JPanel();
		area = new JTextArea();
		// 등록, 취소 버튼
		bt_regist = new JButton("등록");
		bt_del = new JButton("삭제");
		
		setLayout(new BorderLayout());
		p_upper.setLayout(new BorderLayout());
		
		// 크기 조절하기
		p_upper.setPreferredSize(new Dimension(400,460));
		p_bottom.setPreferredSize(new Dimension(400,40));
		// 사용자가 선택한 날짜 정보 
		p_selectDay.setPreferredSize(new Dimension(400, 50));
		// 사용자가 고를 수 있는 스티커 
		p_sticker.setPreferredSize(new Dimension(400, 150));
		// 사용자가 메모 적을 공간
		p_text.setPreferredSize(new Dimension(400, 200));
		area.setPreferredSize(new Dimension(350,180));
		// 버튼 크기
		bt_regist.setPreferredSize(new Dimension(60,30));
		bt_del.setPreferredSize(new Dimension(60,30));
		// 임시로 배경색 지정하기
		p_selectDay.setBackground(Color.WHITE);
		p_sticker.setBackground(Color.WHITE);
		p_text.setBackground(Color.WHITE);
		p_bottom.setBackground(Color.WHITE);
		
		// 사용자가 선택한 날짜 넣기
		la_selectDay = new JLabel(year+"년 "+(month+1)+"월 "+date+"일", SwingConstants.CENTER);
		la_selectDay.setPreferredSize(new Dimension(400, 50));
		la_selectDay.setFont(new Font("돋움", Font.BOLD, 20));
		la_selectDay.setForeground(new Color(183,156,218));
	
		// 스티커 넣어주기
		p_sticker.setLayout(new GridLayout(2,5));
		for(int i=0;i<10;i++) {
			PanelSticker ps = new PanelSticker(this, i);
			p_sticker.add(ps);
			stickerArray.add(ps);
		}
		// 메모 적을 area의 테두리 주기
		area.setBorder(BorderFactory.createLineBorder(new Color(183,156,218), 1));
		area.setFont(new Font("돋움", Font.PLAIN, 30));
		area.setForeground(new Color(25,122,4));
		
		// 이전에 입력한 메모 내용이 있는지 DB에서 조회후 있다면 출력해주기
		setMemoData();
		
		// 컴포넌트 조립하기
		p_selectDay.add(la_selectDay);
		p_text.add(area);
		p_upper.add(p_selectDay, BorderLayout.NORTH);
		p_upper.add(p_sticker, BorderLayout.CENTER);
		p_upper.add(p_text, BorderLayout.SOUTH);
		p_bottom.add(bt_regist);
		p_bottom.add(bt_del);
		add(p_upper, BorderLayout.CENTER);
		add(p_bottom, BorderLayout.SOUTH);
	
		// 등록 버튼에 이벤트 연결해주기
		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(insertData()) {
					callCal(); // main의 달력 다시 셋팅하기 위해 setCal(); 호출하기
					Memo.this.dispose(); // 창 닫아주기
				}
			}
		});
		// 삭제 버튼에 이벤트 연결해주기
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteQuery(); // 삭제해주기
				deleteAlert();
				callCal(); // main의 달력 다시 셋팅하기 위해 setCal(); 호출하기
				Memo.this.dispose(); // 창 닫아주기
			}
		});
		// 메모 입력창에 이벤트 연결해주기
		area.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				inputLimit();
			}
		});
		setSize(400, 500);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 현재 프레임만 닫기
	}
	// 키보드 글자수 입력 제한을 주는 메서드
	public void inputLimit() {
		keyValue=area.getText();
		if(keyValue.length()>18) {
			JOptionPane.showMessageDialog(this, "20자 이내로 입력하세요");
			area.setText(beforeValue);
		}
		beforeValue=area.getText();
	}
	// main프레임의 달력을 재설정 해주는 메서드
	public void callCal() {
		main.setCal(year, month);
	}
	// DB에 넣어주기 위한 데이터 수집
	public boolean insertData() {
		
		boolean insertFlag=false;
		memo = area.getText();
		
		if(memo.length()!=0 && stickerName.length()!=0) {
			duplicateCheck();
			insertFlag=true;
		}else if(memo.length()==0){
			JOptionPane.showMessageDialog(this, "메모를 입력하세요!");
		}else if(stickerName.length()==0) {
			JOptionPane.showMessageDialog(this, "스티커를 입력하세요!");
		}
		return insertFlag;
	}
	// 중복 검사 수행하고 DB에 insert하기
	public void insertQuery() {
		PreparedStatement pstmt = null;
		String sql = "insert into calendar(seq_cal, year, month, day, memo, sticker)";
		sql+=" values(seq_cal.nextval,"+year+","+month+","+date+",'"+memo+"','"+stickerName+"')";
		System.out.println("★★★★★ Memo JFrame의 SQL문 : "+sql);
		try {
			pstmt = main.con.prepareStatement(sql); 
			int result = pstmt.executeUpdate(sql);
			if(result!=0) { //참이면 insert문 실행
				System.out.println("Memo JFrame : insert문 쿼리 성공");
				flag = true;
				JOptionPane.showMessageDialog(this, "일정을 등록했습니다.");
			}else { //거짓이면 수행 실패ㅜㅜ
				System.out.println("Memo JFrame : insert문 쿼리 실패");
				flag = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		}finally {
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	// 해당 하는 날짜에 이미 메모가 있다면 삭제하기!! 그리고 다시 insert 해주기
	public void duplicateCheck() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql = "select * from calendar where year="+year+" and month="+month+" and day="+date;
		System.out.println("Memo_Frame >>> select 쿼리문은 "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // 일치하는 날짜가 있고 메모가 있다면...
				System.out.println("■■■■■■■  "+year+"년, "+month+"월, "+date+"일의 중복체크 완료."+"■■■■■■■");
				deleteQuery(); // 중복되는 데이터 지워주기
			}else { 
				System.out.println("▶▶▶▶▶▶▶ "+year+"년, "+month+"월, "+date+"일에는 "+"DB에 일치하는 정보가 없습니다!! ");
			}
			insertQuery(); // 새로운 메모 넣어주기
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
	public void deleteQuery() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql = "delete from calendar where year="+year+" and month="+month+" and day="+date;
		System.out.println("Memo_Frame >>> delete 쿼리문은 "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // 일치하는 날짜가 있고 메모가 있다면...
				deleteFlag=true;
				System.out.println("■■■■■■■  "+year+"년, "+month+"월, "+date+"일의 메모를 지웠습니다."+"■■■■■■■");
			}else { 
				System.out.println("▶▶▶▶▶▶▶ "+year+"년, "+month+"월, "+date+"일에는 "+"DB에 일치하는 정보가 없습니다!! ");
				deleteFlag=false;
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
	// 삭제 버튼 눌렀을때 수행 결과 알려주기
	public void deleteAlert() {
		if(deleteFlag) {
			JOptionPane.showMessageDialog(this, "일정을 삭제했습니다.");
		}else {
			JOptionPane.showMessageDialog(this, "날짜에 등록된 일정이 없습니다.");
		}
	}
	// 사용자가 선택한 날짜에 이미 입력한 일정이 있다면 그 내용 출력하기
	public void getMemoData() {
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
				memoFlag=true;
				System.out.println("■■■■■■■ Memo Frame. 첫번째 메서드. 나의 메모는 <"+memo+">, 스티커는 <"+stickerName+"> 입니다."+"■■■■■■■");
			}else { 
//				System.out.println("▶▶▶▶▶▶▶ "+year+"년, "+month+"월, "+date+"일에는 "+"DB에 일치하는 정보가 없습니다!! ");
				memoFlag=false;
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
	public void setMemoData() {
		if(memoFlag) {
			area.setText(memo);
			for(int i=0;i<10;i++) {
				int selectSticker = stickerArray.get(i).indexNum;
				String selectStickerName = selectSticker+".png";
				if(selectStickerName.equals(stickerName)) {
					stickerArray.get(i).setBorder(BorderFactory.createLineBorder(new Color(25,122,4), 3));
					stickerIndex= selectSticker;
					mClickCount= 1;
				}
			}
		}
	}
}
