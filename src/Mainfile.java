import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


/*
 *	�ܸĵĵط�:
 *	2. ��ͬ���� Planet �ò�ͬ��ɫͼƬ����ʾ
 *	4. ��ӹ���: ���� Planet ��ʾ����
 *	5. ��Ӷ���ԭʼ��ϵģ��
 *	6. ��ӹ���: ǿ�иı�������ʽ
 *	7.* �������ҷ�����
 *  8. ��ͣ����
 */
public class Mainfile extends Frame {
	Image bg = Toolkit.getDefaultToolkit().getImage("images/backg.png");
	BufferedImage planetBF,traceBF;
	Graphics2D planetBG,traceBG;
	ArrayList<Planet> planets;
	Planet vplanet; // ���ڴ����е�Planet,��껹ûrelease
	Mouse m;
	JPanel p;
	JButton cltrbt, clbt, Huge, Mid, Tiny, Show;
	static public double DEFAULT_M = 3e13;
	static int time = 0;
	boolean showT = false;
	int curx, cury;

	Mainfile(String title) {
		super(title);
		m = new Mouse(this);
		showT = true;
		ClearTrace ct = new ClearTrace(this);
		ClearAll ca = new ClearAll(this);
		CreateHuge chuge = new CreateHuge(this);
		CreateMid cmid = new CreateMid(this);
		CreateTiny ctiny = new CreateTiny(this);
		ShowTrace show = new ShowTrace(this);

		cltrbt = new JButton("Clear Traces");
		cltrbt.addActionListener(ct);
		cltrbt.setBounds(1650, 100, 120, 80);
		cltrbt.setVisible(true);

		clbt = new JButton("Clear all");
		clbt.addActionListener(ca);
		clbt.setBounds(1650, 220, 120, 80);
		clbt.setVisible(true);

		Huge = new JButton("Huge");
		Huge.addActionListener(chuge);
		Huge.setBounds(1670, 320, 100, 40);
		Huge.setVisible(true);

		Mid = new JButton("Medium");
		Mid.addActionListener(cmid);
		Mid.setBounds(1670, 370, 100, 40);
		Mid.setVisible(true);

		Tiny = new JButton("Tiny");
		Tiny.addActionListener(ctiny);
		Tiny.setBounds(1670, 420, 100, 40);
		Tiny.setVisible(true);

		Show = new JButton("Show Trace");
		Show.addActionListener(show);
		Show.setBounds(1650, 480, 120, 80);
		Show.setVisible(true);

		planets = new ArrayList<Planet>();
		planets.add(new Planet(DEFAULT_M, 0, 0, 0.6, 0.4,false));
		planets.add(new Planet(DEFAULT_M, 42097, 0, -0.2, 0.2,false));
		planets.add(new Planet(DEFAULT_M, 6097, 52097, -0.6, -0.3,false));
		
		p = new JPanel(null);
		p.setBackground(Color.DARK_GRAY);
		p.add(cltrbt);
		p.add(clbt);
		p.add(Huge);
		p.add(Mid);
		p.add(Tiny);
		p.add(Show);
		
		traceBF = new BufferedImage(1646, 1263,BufferedImage.TYPE_INT_ARGB);
		planetBF = new BufferedImage(1646, 1263,BufferedImage.TYPE_INT_ARGB);
		traceBG = traceBF.createGraphics();
		planetBG = planetBF.createGraphics();
		traceBG.setBackground(new Color(0,0,0,0));
		planetBG.setBackground(new Color(0,0,0,0));
        
		setSize(1846, 1500);
		setLocation(50, 50);

		addMouseListener(m);
		add(p);

		setVisible(true); // setVisibleд�����һ��
		p.setVisible(true);
	}

	public void ClearTrace() {
		traceBG.clearRect(0, 0, 1646, 1263);
	}

	public void ClearAll() { // ����
		planets.clear();
		traceBG.clearRect(0, 0, 1646, 1263);
		vplanet = null;
	}

	/* ��ʵ�������� cvt2 ��Ļ��ʾ���� */
	public static int cvt(double x) {
		double red = x / (100);
		return (int) red + 400;
	}

	/* ��Ļ��ʾ���� cvt2 ��ʵ�������� */
	public static double recvt(int n) {
		return (double) (n - 400) * 100;
	}

	/* ��ʾ�����е�Planet */
	public void DrawVplanet(Graphics g) {
		if (vplanet != null) {
			g.setColor(vplanet.drawColor);
			int tmpdiam=vplanet.diam;
			g.fillOval(cvt(vplanet.x)-tmpdiam/2, cvt(vplanet.y)-tmpdiam/2, tmpdiam, tmpdiam);
		}
			//g.drawImage(vplanet.self, cvt(vplanet.x), cvt(vplanet.y), null);
	}
	
	/* �ڻ����ϻ��� */
	void paintFG() {
		planetBG.clearRect(0, 0, 1646, 1263);
		double dt = 120; // ʱ�䲽��, ��λ:s
		for (Planet p : planets) // ��ÿ������, !visible���ڷ���������⴦��
			p.DrawPlanet(planetBG);
		if (m.Clicking) { // ������click��ûrelease, �ͻ�������
			planetBG.setColor(vplanet.drawColor);
			planetBG.drawLine(m.gotx, m.goty, curx, cury);
		}
		DrawVplanet(planetBG); // �������е�����, ֻ������, ��������������
		/* ����ÿ������, ���������ܺ���(Fx, Fy) */
		for (Planet p : planets) {
			if (!p.visible)
				continue;
			for (Planet q : planets) {
				if (!q.visible) // �����Ѿ���merge������
					continue;
				if (p == q) // �Լ������Լ�ʩ��
					continue;
				if (!p.MergeOK(q)) // ���Merge�˾Ͳ�����������, ��Ϊ�������غ���
					p.AddForce(q);
			}
			p.Forced(dt); // p�������ܺ���(Fx, Fy)�ı�p�����ٶ�(vx, vy)
		}

		/* ����ÿ������, ���ٶȼ�����λ�Ƹı� */
		for (Planet p : planets) {
			if (!p.visible) // ���������������
				continue;
			p.Move(dt); // λ�Ƹı䣡
			if (showT) { // ����ʾ�켣, ����ӵ�ǰλ�õ��켣(log)��
				if(p.hasTrace) {
					traceBG.setColor(p.drawColor);
					traceBG.drawLine(p.lastX, p.lastY, cvt(p.x), cvt(p.y));
				}
				else {
					p.hasTrace=true;
				}
				p.AddTrace();
			}
		}
	}

	/* �����ڵķ���, ÿ���ػ����ڵ���һ��paint */  
	public void paint(Graphics g) {
		paintFG();
		g.drawImage(bg, 0, 0, null); // ������
		g.drawImage(traceBF, 0, 0, null); //���켣
		g.drawImage(planetBF, 0, 0, null); //������
	}
	
	/* ���ڼ��ط���, ����ʱһֱ���������������ѭ�� */
	void launchFrame() throws Exception {
		addWindowListener(new WindowAdapter() { // ���ǵ���رհ�ťʹ������ֹ
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		while (true) { // �����ػ�����, ��ѭ��
			repaint();
			Thread.sleep(1);
		}
	}

	/* ��д update �������Ը��ƻ���, ԭ����Ҳ����, copy from CSDN */
	private Image offScreenImage = null;

	public void update(Graphics g) {
		if (offScreenImage == null)
			offScreenImage = this.createImage(1646, 1263);
		Graphics gOff = offScreenImage.getGraphics();
		paint(gOff);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Planets test version! ");
		Mainfile galaxy = new Mainfile("Planets in galaxy");
		galaxy.launchFrame();

	}
}