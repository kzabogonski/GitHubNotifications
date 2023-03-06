package com.zabogonski;

import javax.management.Descriptor;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

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

    public void setMenu(String login, List<RepositoryDescription> repos){
        PopupMenu popup = new PopupMenu();

        MenuItem accountMI = new MenuItem(login);
        accountMI.addActionListener(e -> openInBrowser("https://github.com/" + login));

        MenuItem notificationsMI = new MenuItem("notifications");
        accountMI.addActionListener(e -> openInBrowser("https://github.com/notifications"));

        Menu repositoryMI = new Menu("repositories");
        repos.forEach(
                repo -> {
                    String name = repo.getPrs().size() > 0
                            ? String.format("(%d) %s", repo.getPrs().size(), repo.getName())
                            : repo.getName();
                    Menu repoSM = new Menu(name);

                    MenuItem openInBrowser = new MenuItem("Open in browser");
                    openInBrowser.addActionListener(e -> openInBrowser(repo.getRepository().getHtmlUrl().toString()));

                    if (repo.getPrs().size() > 0){
                        repoSM.addSeparator();
                    }

                    repo.getPrs().forEach(
                            pr -> {
                                MenuItem prMI = new MenuItem(pr.getTitle());
                                prMI.addActionListener(e -> openInBrowser(pr.getHtmlUrl().toString()));
                                repoSM.add(prMI);
                            }
                    );
                    repositoryMI.add(repoSM);
                }
        );

        popup.add(accountMI);
        popup.addSeparator();
        popup.add(notificationsMI);
        popup.add(repositoryMI);
        trayIcon.setPopupMenu(popup);
    }

    public void openInBrowser(String url){
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
