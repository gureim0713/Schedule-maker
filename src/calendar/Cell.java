
/*
 * �� ó���� �ؾ��� ��!
 * DB�� �����ؼ� table select �ϰ�, year, month, day �÷����� ���� �´� ��¥�� �ִٸ�
 * �ش� ��¥�� memo�� sticker�� �Ѱܹ޴´�.
 * 
 * f_memo���� ��Ϲ�ư ������ �������� ���������� DB �ٽ� ������ ������Ʈ �ٿ��� ��
 * Main ��ü�� updateUI ���� ��!! 
 *  f_memo�� flag�� ��ȯ�ϰ� �� ������ 
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

	// �޷� ��¥ ��°� ���õ� �������
	JPanel p_date;
	JLabel la_date;
	int year;
	int month;
	int date;
	
	// �޸�� ���õ� �������
	JPanel p_memo;
	JLabel la_memo;
	String memo=null;
	
	// ��¥�� ��ƼĿ�� �ٿ��� �г�
	JPanel p_DS;
	
	// ��ƼĿ�� ���õ� �������
	JPanel p_sticker;
	JLabel la_sticker;
	ImageIcon icon;
	Image img;
	String stickerName=null;
	
	// �޸� �����Ӱ� ���õ� ����
	Memo f_memo; // ���� ������ ������
	
	// �Ѱ� ���� �Ű������� ���õ� �������
	Main main;
	int index;
	
	// DB ��ȸ ���� ����
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
		
		// ������Ʈ ũ�� ����
		p_date.setPreferredSize(new Dimension(40,40));
		p_sticker.setPreferredSize(new Dimension(45,40));
		p_DS.setPreferredSize(new Dimension(100,40));
		p_memo.setPreferredSize(new Dimension(90, 60));
	
		// �ӽ÷� ���� ����
		p_date.setBackground(Color.WHITE);
		p_sticker.setBackground(Color.WHITE);
		p_DS.setBackground(Color.WHITE);
		p_memo.setBackground(Color.WHITE);
				
		// ��¥ �־��ִ� �޼���
		setDate(date);
		// DB���� ��ȸ�� ��ƼĿ �̹����� �ִٸ�...
		if(flag) {
			setSticker(stickerName);
		}
		// DB���� ��ȸ�� �޸� �ִٸ�....
		if(flag) {
			setMemo(memo);
		}
		// ������Ʈ �����ϱ�
		p_DS.add(p_date, BorderLayout.WEST);
		p_DS.add(p_sticker, BorderLayout.EAST);
		add(p_DS, BorderLayout.NORTH);
		add(p_memo, BorderLayout.SOUTH);
		
		// Ŭ�� ���ϸ�.....
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				clicked();
			}
		});
		
		// Cell �ϳ��� ũ�� ����
		setPreferredSize(new Dimension(100,100));
		setBorder(BorderFactory.createLineBorder(new Color(150,126,188), 2));
	}
	// ��ƼĿ �ٿ��ֱ�
	public void setSticker(String stickerName) {
		
		// ��ƼĿ �̹��� ������ �����ϰ� ���̱�
		URL path=getClass().getClassLoader().getResource(stickerName);
		icon = new ImageIcon(path);
		// ������ ����
		img=icon.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
		icon =new ImageIcon(img);
		la_sticker = new JLabel(icon);
		la_sticker.setPreferredSize(new Dimension(44,30));
		
		p_sticker.add(la_sticker);
	}
	// ��¥ �ٿ��ֱ�
	public void setDate(int date) {
		
		// �Ѱܹ��� ��¥ �־��ֱ�
		la_date = new JLabel(Integer.toString(date), SwingConstants.LEFT);
		la_date.setFont(new Font("����", Font.BOLD, 20));
		la_date.setForeground(new Color(183,156,218));
		la_date.setPreferredSize(new Dimension(40,30));
		
		p_date.add(la_date);
	}
	// �ָ��� ��¥ ���� �ٲ��ֱ�
	public void setColor(Color color) {
		la_date.setForeground(color);		
	}
	// �޸� �ٿ��ֱ�
	public void setMemo(String memo) {
		
		// �޸� �־��ֱ�
		la_memo = new JLabel(memo, SwingConstants.LEFT);
		la_memo.setFont(new Font("����", Font.LAYOUT_LEFT_TO_RIGHT, 15));
		la_memo.setPreferredSize(new Dimension(90, 55));
		
		p_memo.add(la_memo);
	}
	public void clicked() {
		f_memo = new Memo(main, year,month,date);
	}
	// DB���� ���� �޸�, ��ƼĿ ���� ��ȸ�ϱ�
	public void getData() {
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
				flag=true;
				System.out.println("�������� �޸� �ִ� ���� ��¥�� "+year+"��, "+month+"��, "+date+"�� �Դϴ�."+"��������");
				System.out.println("�������� �޸� �ִ� ���� ��ƼĿ�� "+stickerName+", �޸�� <"+memo+"> �Դϴ�."+"��������");
			}else { 
				System.out.println("�������������� "+year+"��, "+month+"��, "+date+"�Ͽ��� "+"DB�� ��ġ�ϴ� ������ �����ϴ�!! ");
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
