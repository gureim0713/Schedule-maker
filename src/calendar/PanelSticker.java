package calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelSticker extends JPanel{
	Memo memo;
	public int indexNum;
	JLabel la;
	
	int clickCount=0; // ���� ���� ���ߴ��� �ƴ��� ī��Ʈ ����
	
	public PanelSticker(Memo memo, int indexNum) {
		
		this.memo=memo;
		this.indexNum=indexNum;
		
		URL path= getClass().getClassLoader().getResource(indexNum+".png");
		ImageIcon tempIcon = new ImageIcon(path);
		Image img = tempIcon.getImage().getScaledInstance(40,40,Image.SCALE_DEFAULT);
		ImageIcon stickerIcon =  new ImageIcon(img);
		la = new JLabel(stickerIcon, SwingConstants.CENTER);
		la.setPreferredSize(new Dimension(40,60));
		
		setBackground(Color.WHITE);
		
		setBorder(BorderFactory.createLineBorder(new Color(183,156,218), 1));
		
		add(la);
		
		// ���콺�� Ŭ���ϸ� ȿ�� �ֱ�
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {				
				memo.mClickCount++;
				
				if(memo.mClickCount==1) {
					System.out.println("�ߺ� Ŭ�� �����ϰ� stickerIndex ����??"+memo.stickerIndex);
					if(memo.stickerIndex!=100) {
						memo.stickerArray.get(memo.stickerIndex).setBorder(BorderFactory.createLineBorder(new Color(183,156,218), 1));
					}
					memo.stickerIndex=indexNum;
					memo.stickerName = indexNum+".png";
					PanelSticker.this.setBorder(BorderFactory.createLineBorder(new Color(25,122,4), 3));
					System.out.println("����ڰ� ������ ��ƼĿ �̸��� "+memo.stickerName);
					System.out.println("Ŭ�� ī��Ʈ ����?? "+memo.mClickCount);
				}
				if(memo.mClickCount>1) {
					if(memo.stickerName.length()!=0) {
						System.out.println("�ߺ� Ŭ�� ī��Ʈ ����?? "+memo.mClickCount);
						System.out.println("�ߺ� Ŭ���� �� stickerIndex ����??"+memo.stickerIndex);
						memo.stickerArray.get(memo.stickerIndex).setBorder(BorderFactory.createLineBorder(new Color(183,156,218), 1));
						memo.stickerName = "";
						memo.stickerIndex=100;
						PanelSticker.this.setBorder(BorderFactory.createLineBorder(new Color(25,122,4), 3));
						memo.stickerName = indexNum+".png";
						memo.stickerIndex=indexNum;
						System.out.println("���� ��ƼĿ �̸���?? "+memo.stickerName);
						memo.mClickCount=0;
					}

				}
			}
		});
	}
}
