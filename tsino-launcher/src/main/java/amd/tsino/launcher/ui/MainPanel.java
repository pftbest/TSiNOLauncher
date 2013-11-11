package amd.tsino.launcher.ui;

import amd.tsino.launcher.download.DownloadManager;
import amd.tsino.launcher.download.UpdateListener;
import amd.tsino.launcher.style.MainPanelStyle;
import net.minecraft.launcher.Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@SuppressWarnings("serial")
public class MainPanel extends ImagePanel {
    private AuthPanel auth;
    private ProgressBar progress;

    public MainPanel(MainPanelStyle style) throws IOException {
        super(style);
        setLayout(null);
        setOpaque(true);

        auth = new AuthPanel(style.auth);
        progress = new ProgressBar(style.progress);
        progress.setVisible(false);
        Launcher.getInstance().getDownloads().addUpdateListener(new UpdateListener() {
            @Override
            public void updated(DownloadManager manager) {
                progress.setMaximum(manager.getTotal());
                progress.setValue(manager.getFinished() + manager.getFailed());
            }
        });

        auth.getEnter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                auth.enableAuth(false);
                progress.setIndeterminate(true);
                progress.setVisible(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Launcher.getInstance().launch();
                        progress.setVisible(false);
                        auth.enableAuth(true);
                    }
                }).start();
            }
        });

        add(new ImagePanel(style.header));
        add(auth);
        add(new NewsPanel(style.news));
        add(progress);
    }

    public AuthPanel getAuth() {
        return auth;
    }

    public ProgressBar getProgress() {
        return progress;
    }
}