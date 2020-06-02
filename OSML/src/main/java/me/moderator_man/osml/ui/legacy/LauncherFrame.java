package me.moderator_man.osml.ui.legacy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;

import me.moderator_man.osml.auth.Authenticator;
import me.moderator_man.osml.launch.Launcher;

public class LauncherFrame extends Frame
{
	public static final int VERSION = 13;
	private static final long serialVersionUID = 1L;
	public Map<String, String> customParameters = new HashMap<String, String>();
	public LoginForm loginForm;

	public LauncherFrame()
	{
		super("Old School Minecraft Launcher");

		setBackground(Color.BLACK);
		this.loginForm = new LoginForm(this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(this.loginForm, "Center");

		p.setPreferredSize(new Dimension(854, 480));

		setLayout(new BorderLayout());
		add(p, "Center");

		pack();
		setLocationRelativeTo(null);
		try
		{
			setIconImage(ImageIO.read(getClass().getClassLoader().getResource("favicon.png")));
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent arg0)
			{
				new Thread()
				{
					public void run()
					{
						try
						{
							Thread.sleep(30000L);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						System.out.println("FORCING EXIT!");
						System.exit(0);
					}
				}.start();
				System.exit(0);
			}
		});
	}

	public void login(String username, String password)
	{
		try
		{
		    System.out.println("login 1");
			Authenticator auth = new Authenticator();
			System.out.println("login 2");
			auth.tryAuth(username, password);
			System.out.println("login 3");
			if (auth.isAuthenticated())
			{
			    System.out.println("login authed");
			    new Launcher().legacyLaunch(username, auth.getSessionID());
			    setVisible(false);
			    System.out.println("login launched");
			} else {
			    System.out.println("login no authed");
			    showError("Invalid username or password!");
			}
		} catch (Exception e) {
		    System.out.println("login error");
			e.printStackTrace();
			showError(e.toString());
			this.loginForm.setNoNetwork();
		}
	}

	private void showError(String error)
	{
		removeAll();
		add(this.loginForm);
		this.loginForm.setError(error);
		validate();
	}

	public boolean canPlayOffline(String userName)
	{
		return false;
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception localException)
		{
		}
		LauncherFrame launcherFrame = new LauncherFrame();
		launcherFrame.setVisible(true);
		launcherFrame.customParameters.put("stand-alone", "true");

		if (args.length >= 3)
		{
			String ip = args[2];
			String port = "25565";
			if (ip.contains(":"))
			{
				String[] parts = ip.split(":");
				ip = parts[0];
				port = parts[1];
			}

			launcherFrame.customParameters.put("server", ip);
			launcherFrame.customParameters.put("port", port);
		}

		if (args.length >= 1)
		{
			launcherFrame.loginForm.userName.setText(args[0]);
			if (args.length >= 2)
			{
				launcherFrame.loginForm.password.setText(args[1]);
				launcherFrame.loginForm.doLogin();
			}
		}
	}
}