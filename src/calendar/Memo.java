
/*
 * �޸� ������ �Ѽ� �Է��� ���� '���' ��ư ������ DB�� �������ֱ�
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
	
	// ����ڰ� ������ ��¥ ���
	JPanel p_selectDay;
	JLabel la_selectDay;
	
	// ����ڰ� �� �� �ֵ��� ��ƼĿ ���
	JPanel p_sticker;
	public String stickerName="";
	public int stickerIndex=100;
	public ArrayList<PanelSticker> stickerArray = new ArrayList();
	public int mClickCount=0;
	
	// �޸� ������ ���� ����
	JPanel p_text;
	JTextArea area;
	String memo="";
	String keyValue="";
	String beforeValue="";
	
	// DB���� ���� �޸� ����
	boolean memoFlag=false;
	
	// DB�� ������ ���, ��� ��ư
	JButton bt_regist;
	JButton bt_del;
	boolean flag=false;
	boolean deleteFlag=false;
	
	Main main; // ����� ������ main�� �޷� ������ �������ֱ� ���ؼ�
	int year;
	int month;
	int date;
	
	public Memo(Main main, int year, int month, int date) {
		this.main = main;
		this.year = year;
		this.month=month;
		this.date=date;
		
		getMemoData(); // ������ ����� �޸� �ִ��� ������ ���� ������ֱ�
		
		p_upper= new JPanel();
		p_bottom = new JPanel();
		// ����ڰ� ������ ��¥
		p_selectDay = new JPanel();
		// ����ڰ� �� �� �ֵ��� ��ƼĿ ����ϱ�
		p_sticker = new JPanel();
		
		// ����ڰ� �޸� ���� ����
		p_text = new JPanel();
		area = new JTextArea();
		// ���, ��� ��ư
		bt_regist = new JButton("���");
		bt_del = new JButton("����");
		
		setLayout(new BorderLayout());
		p_upper.setLayout(new BorderLayout());
		
		// ũ�� �����ϱ�
		p_upper.setPreferredSize(new Dimension(400,460));
		p_bottom.setPreferredSize(new Dimension(400,40));
		// ����ڰ� ������ ��¥ ���� 
		p_selectDay.setPreferredSize(new Dimension(400, 50));
		// ����ڰ� �� �� �ִ� ��ƼĿ 
		p_sticker.setPreferredSize(new Dimension(400, 150));
		// ����ڰ� �޸� ���� ����
		p_text.setPreferredSize(new Dimension(400, 200));
		area.setPreferredSize(new Dimension(350,180));
		// ��ư ũ��
		bt_regist.setPreferredSize(new Dimension(60,30));
		bt_del.setPreferredSize(new Dimension(60,30));
		// �ӽ÷� ���� �����ϱ�
		p_selectDay.setBackground(Color.WHITE);
		p_sticker.setBackground(Color.WHITE);
		p_text.setBackground(Color.WHITE);
		p_bottom.setBackground(Color.WHITE);
		
		// ����ڰ� ������ ��¥ �ֱ�
		la_selectDay = new JLabel(year+"�� "+(month+1)+"�� "+date+"��", SwingConstants.CENTER);
		la_selectDay.setPreferredSize(new Dimension(400, 50));
		la_selectDay.setFont(new Font("����", Font.BOLD, 20));
		la_selectDay.setForeground(new Color(183,156,218));
	
		// ��ƼĿ �־��ֱ�
		p_sticker.setLayout(new GridLayout(2,5));
		for(int i=0;i<10;i++) {
			PanelSticker ps = new PanelSticker(this, i);
			p_sticker.add(ps);
			stickerArray.add(ps);
		}
		// �޸� ���� area�� �׵θ� �ֱ�
		area.setBorder(BorderFactory.createLineBorder(new Color(183,156,218), 1));
		area.setFont(new Font("����", Font.PLAIN, 30));
		area.setForeground(new Color(25,122,4));
		
		// ������ �Է��� �޸� ������ �ִ��� DB���� ��ȸ�� �ִٸ� ������ֱ�
		setMemoData();
		
		// ������Ʈ �����ϱ�
		p_selectDay.add(la_selectDay);
		p_text.add(area);
		p_upper.add(p_selectDay, BorderLayout.NORTH);
		p_upper.add(p_sticker, BorderLayout.CENTER);
		p_upper.add(p_text, BorderLayout.SOUTH);
		p_bottom.add(bt_regist);
		p_bottom.add(bt_del);
		add(p_upper, BorderLayout.CENTER);
		add(p_bottom, BorderLayout.SOUTH);
	
		// ��� ��ư�� �̺�Ʈ �������ֱ�
		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(insertData()) {
					callCal(); // main�� �޷� �ٽ� �����ϱ� ���� setCal(); ȣ���ϱ�
					Memo.this.dispose(); // â �ݾ��ֱ�
				}
			}
		});
		// ���� ��ư�� �̺�Ʈ �������ֱ�
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteQuery(); // �������ֱ�
				deleteAlert();
				callCal(); // main�� �޷� �ٽ� �����ϱ� ���� setCal(); ȣ���ϱ�
				Memo.this.dispose(); // â �ݾ��ֱ�
			}
		});
		// �޸� �Է�â�� �̺�Ʈ �������ֱ�
		area.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				inputLimit();
			}
		});
		setSize(400, 500);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // ���� �����Ӹ� �ݱ�
	}
	// Ű���� ���ڼ� �Է� ������ �ִ� �޼���
	public void inputLimit() {
		keyValue=area.getText();
		if(keyValue.length()>18) {
			JOptionPane.showMessageDialog(this, "20�� �̳��� �Է��ϼ���");
			area.setText(beforeValue);
		}
		beforeValue=area.getText();
	}
	// main�������� �޷��� �缳�� ���ִ� �޼���
	public void callCal() {
		main.setCal(year, month);
	}
	// DB�� �־��ֱ� ���� ������ ����
	public boolean insertData() {
		
		boolean insertFlag=false;
		memo = area.getText();
		
		if(memo.length()!=0 && stickerName.length()!=0) {
			duplicateCheck();
			insertFlag=true;
		}else if(memo.length()==0){
			JOptionPane.showMessageDialog(this, "�޸� �Է��ϼ���!");
		}else if(stickerName.length()==0) {
			JOptionPane.showMessageDialog(this, "��ƼĿ�� �Է��ϼ���!");
		}
		return insertFlag;
	}
	// �ߺ� �˻� �����ϰ� DB�� insert�ϱ�
	public void insertQuery() {
		PreparedStatement pstmt = null;
		String sql = "insert into calendar(seq_cal, year, month, day, memo, sticker)";
		sql+=" values(seq_cal.nextval,"+year+","+month+","+date+",'"+memo+"','"+stickerName+"')";
		System.out.println("�ڡڡڡڡ� Memo JFrame�� SQL�� : "+sql);
		try {
			pstmt = main.con.prepareStatement(sql); 
			int result = pstmt.executeUpdate(sql);
			if(result!=0) { //���̸� insert�� ����
				System.out.println("Memo JFrame : insert�� ���� ����");
				flag = true;
				JOptionPane.showMessageDialog(this, "������ ����߽��ϴ�.");
			}else { //�����̸� ���� ���Ф̤�
				System.out.println("Memo JFrame : insert�� ���� ����");
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
	// �ش� �ϴ� ��¥�� �̹� �޸� �ִٸ� �����ϱ�!! �׸��� �ٽ� insert ���ֱ�
	public void duplicateCheck() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql = "select * from calendar where year="+year+" and month="+month+" and day="+date;
		System.out.println("Memo_Frame >>> select �������� "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // ��ġ�ϴ� ��¥�� �ְ� �޸� �ִٸ�...
				System.out.println("��������  "+year+"��, "+month+"��, "+date+"���� �ߺ�üũ �Ϸ�."+"��������");
				deleteQuery(); // �ߺ��Ǵ� ������ �����ֱ�
			}else { 
				System.out.println("�������������� "+year+"��, "+month+"��, "+date+"�Ͽ��� "+"DB�� ��ġ�ϴ� ������ �����ϴ�!! ");
			}
			insertQuery(); // ���ο� �޸� �־��ֱ�
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
		System.out.println("Memo_Frame >>> delete �������� "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // ��ġ�ϴ� ��¥�� �ְ� �޸� �ִٸ�...
				deleteFlag=true;
				System.out.println("��������  "+year+"��, "+month+"��, "+date+"���� �޸� �������ϴ�."+"��������");
			}else { 
				System.out.println("�������������� "+year+"��, "+month+"��, "+date+"�Ͽ��� "+"DB�� ��ġ�ϴ� ������ �����ϴ�!! ");
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
	// ���� ��ư �������� ���� ��� �˷��ֱ�
	public void deleteAlert() {
		if(deleteFlag) {
			JOptionPane.showMessageDialog(this, "������ �����߽��ϴ�.");
		}else {
			JOptionPane.showMessageDialog(this, "��¥�� ��ϵ� ������ �����ϴ�.");
		}
	}
	// ����ڰ� ������ ��¥�� �̹� �Է��� ������ �ִٸ� �� ���� ����ϱ�
	public void getMemoData() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql = "select * from calendar where year="+year+" and month="+month+" and day="+date;
		System.out.println("Cell�� 211 line>>> select �������� "+sql);		
		try {
			pstmt = main.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { // ��ġ�ϴ� ��¥�� �ְ� �޸� �ִٸ�...
				memo = rs.getString("memo");
				stickerName = rs.getString("sticker");
				memoFlag=true;
				System.out.println("�������� Memo Frame. ù��° �޼���. ���� �޸�� <"+memo+">, ��ƼĿ�� <"+stickerName+"> �Դϴ�."+"��������");
			}else { 
//				System.out.println("�������������� "+year+"��, "+month+"��, "+date+"�Ͽ��� "+"DB�� ��ġ�ϴ� ������ �����ϴ�!! ");
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
