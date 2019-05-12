import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/*
 *	�ܸĵĵط�:
 *	1. ���� Planet ʱ��ʾ����켣��
 *	2. ��ͬ���� Planet �ò�ͬ��ɫͼƬ����ʾ
 *	4. ��ӹ���: ���� Planet ��ʾ����
 *	5. ��Ӷ���ԭʼ��ϵģ��
 *	6. ��ӹ���: ǿ�иı�������ʽ
 *	7.* �������ҷ�����
 */
public class Mainfile extends Frame {
	Image bg = Toolkit.getDefaultToolkit().getImage("images/backg.png");
	ArrayList<Planet> planets;
	Planet vplanet; // �����е�Planet,���һ��
	Mouse m;
	JPanel p;
	static String impath;
	JButton cltrbt, clbt, Huge, Mid, Tiny, Show;
	static public double DEFAULT_M = 3e13;
	static int time = 0;
	boolean showT = false;
	int curx, cury;

	Mainfile(String title) {
		super(title);
		m = new Mouse(this);

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
		planets.add(new Planet(DEFAULT_M, 0, 0, 0, 0.3, "images/earth.png"));
		planets.add(new Planet(DEFAULT_M, 42097, 0, -0.2, 0.2, "images/earth.png"));
		planets.add(new Planet(DEFAULT_M, 6097, 52097, 0.05, -0.3, "images/earth.png"));

		p = new JPanel(null);
		p.setBackground(Color.DARK_GRAY);
		p.add(cltrbt);
		p.add(clbt);
		p.add(Huge);
		p.add(Mid);
		p.add(Tiny);
		p.add(Show);

		setSize(1846, 1500);
		setLocation(50, 50);

		addMouseListener(m);
		add(p);

		setVisible(true); // setVisibleд�����һ��
		p.setVisible(true);
	}

	public void ClearTrace() {
		for (int i = 0; i < planets.size(); i++) {
			Planet p = planets.get(i);
			p.log.clear();
			if (!p.visible) {
				planets.remove(p);
				i--;
			}
		}
	}

	public void ClearAll() { // ����
		planets.clear();
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

	public void DrawVplanet(Graphics g) {
		if (vplanet != null)
			g.drawImage(vplanet.self, cvt(vplanet.x), cvt(vplanet.y), null);
	}

	// �����ڵķ���
	public void paint(Graphics g) {
		g.drawImage(bg, 0, 0, null);
		double dt = 60; // 1 min

		for (Planet p : planets)
			p.DrawPlanet(g);
		DrawVplanet(g);
		if (m.Clicking) {
			g.setColor(Color.YELLOW);
			g.drawLine(m.gotx + 15, m.goty + 15, curx, cury);
		}
		for (Planet p : planets) {
			if (!p.visible)
				continue;
			for (Planet q : planets) {
				if (!q.visible)
					continue;
				if (p == q)
					continue;
				if (!p.MergeOK(q))
					p.AddForce(q);
			}
			p.Forced(dt);
		}
		for (Planet p : planets) {
			if (!p.visible)
				continue;
			p.Move(dt);
			if (showT)
				p.AddTrace();
		}
	}

	// ���ڼ���
	void launchFrame() throws Exception {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		while (true) { // �ػ�����
			repaint();
			Thread.sleep(1);
		}
	}

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