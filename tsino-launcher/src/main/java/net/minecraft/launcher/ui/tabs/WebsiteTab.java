package net.minecraft.launcher.ui.tabs;

import java.awt.Color;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.OperatingSystem;

public class WebsiteTab extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4904842198124329148L;
	private final JTextPane blog = new JTextPane();
	private final Launcher launcher;

	public WebsiteTab(Launcher launcher) {
		this.launcher = launcher;

		this.blog.setEditable(false);
		this.blog.setMargin(null);
		this.blog.setBackground(Color.DARK_GRAY);
		this.blog.setContentType("text/html");
		this.blog
				.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center><h1>Загрузка страницы..</h1></center></font></body></html>");
		this.blog.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent he) {
				if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						OperatingSystem.openLink(he.getURL().toURI());
					} catch (Exception e) {
						Launcher.getInstance().println(
								"Unexpected exception opening link "
										+ he.getURL(), e);
					}
			}
		});
		setViewportView(this.blog);
	}

	public Launcher getLauncher() {
		return this.launcher;
	}

	public void setPage(final String url) {
		Thread thread = new Thread("Update website tab") {
			@Override
			public void run() {
				try {
					WebsiteTab.this.blog.setPage(new URL(url));
				} catch (Exception e) {
					Launcher.getInstance().println(
							"Unexpected exception loading " + url, e);
					WebsiteTab.this.blog
							.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center><h1>Ошибка!</h1><br>"
									+ e.toString()
									+ "</center></font></body></html>");
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.ui.tabs.WebsiteTab JD-Core Version: 0.6.2
 */