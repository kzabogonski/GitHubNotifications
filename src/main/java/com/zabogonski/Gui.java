package com.zabogonski;

import java.awt.*;

public class Gui {

    private final TrayIcon trayIcon;

    public Gui() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit()
                .createImage(getClass().getResource("/logo.png"));

        trayIcon = new TrayIcon(image, "GitHub helper");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("GitHub helper");
        tray.add(trayIcon);
    }

    public void showNotification(String title, String text){
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
