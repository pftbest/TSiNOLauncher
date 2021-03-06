package amd.tsino.launcher.ui;

import amd.tsino.launcher.style.NewsPanelStyle;

import java.io.IOException;

@SuppressWarnings("serial")
class NewsPanel extends ImagePanel {
    public NewsPanel(NewsPanelStyle style) throws IOException {
        super(style);
        setLayout(null);
        setFocusable(false);
        add(new BrowserFrame(style.browser));
    }
}
