package pw.seppoku;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Author: Ice
 * Date: 06-May-20
 * Project: "Seppoku Installer"
 */
public class Installer implements ActionListener {

    public File minecraftFolder = new File(System.getenv("APPDATA") + "/.minecraft/");
    public File minecraftVersionsFolder = new File(System.getenv("APPDATA") + "/.minecraft/versions/");
    public File forgeModsFolder = new File(System.getenv("APPDATA") + "/.minecraft/mods/1.12.2");
    public File seppukuConfigurationFolder = new File(System.getenv("APPDATA") + "/.minecraft/" + "Seppuku/");
    public File seppukuModulesFolder = new File(System.getenv("APPDATA") + "/.minecraft/" + "Seppuku" + "/" + "Modules/");

    public File installerCache = new File(System.getenv("APPDATA") + "/seppukuinstaller");


    public static Installer getInstance() {
        return new Installer();
    }

    public void init() {
        if(!forgeModsFolder.exists()) {
            forgeModsFolder.mkdir();
        }
        if(!installerCache.exists()) {
            installerCache.mkdir();
        }

        setupUI();

        while(true) {
            if(isInstallFinished()) {
                JOptionPane.showMessageDialog(null, "Finished installing!");
                System.exit(0);
            }
        }


    }

    public boolean isSeppukuInstalled() {
        if(seppukuConfigurationFolder.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /*
    Checks if 1.12.2-forge is installed
     */
    public boolean isForgeInstalled() {
        File[] versionsFile = minecraftVersionsFolder.listFiles();
        for(int i = 0;i < versionsFile.length;i++) {

           if(versionsFile[i].getName().contains("forge")) {
               String s = versionsFile[i].getName();
               if(s.contains("1.12.2")) {
                  return true;
               } else {
                   return false;
               }
           }


        }
        return false;
    }

    public void downloadForge() {
        try {
            FileUtils.copyURLToFile(new URL("https://github.com/ice-mineman/Seppuku-Installer-Assets/raw/master/forge-1.12.2-14.23.5.2768-installer.jar"), new File(installerCache.getAbsolutePath() + "/forge1.12.2.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean forgeInstallerExists() {
        if(new File(installerCache.getAbsolutePath() + "/forge1.12.2.jar").exists()) {
            return true;
        } else {
            return false;
        }
    }

    /*
    Downloads the client from the github repo
     */
    public void downloadClient() {
        try {
            System.out.println(forgeModsFolder.getAbsolutePath());
            FileUtils.copyURLToFile(new URL("https://github.com/seppukudevelopment/seppuku/releases/download/3.0.4/seppuku-3.0.4-beta.jar"), new File(forgeModsFolder.getAbsolutePath() + "/seppuku-3.0.4-beta.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Downloads various external mods
     */
    public void downloadExternalMods() {
        try {
            FileUtils.copyURLToFile(new URL("https://github.com/ice-mineman/Seppuku-Installer-Assets/raw/master/betterui-1.1-SNAPSHOT.jar"), new File(seppukuModulesFolder.getAbsolutePath() + "/betterui-1.1-SNAPSHOT.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isInstallFinished() {
        if(new File(seppukuModulesFolder.getAbsolutePath() + "/betterui-1.1-SNAPSHOT.jar").exists() && new File(forgeModsFolder.getAbsolutePath() + "/seppuku-3.0.4-beta.jar").exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void setupUI() {
        JFrame frame = new JFrame("Seppuku Installer");

        /*
        Sets up the JFrame
         */
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(null);

        JLabel label = new JLabel(new ImageIcon(this.getClass().getResource("/s.png")));
        label.setOpaque(true);
        label.setVisible(true);
        label.setBounds(80, 0, 240, 100);

        JButton githubButton = new JButton("Github");
        JButton websiteButton = new JButton("Website");
        JButton installButton = new JButton("Install");

        githubButton.setVisible(true);
        githubButton.setBounds(20, 100, 100, 40);
        githubButton.setFocusable(false);

        githubButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/seppukudevelopment/seppuku"));
                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        websiteButton.setVisible(true);
        websiteButton.setBounds(270, 100, 100, 40);
        websiteButton.setFocusable(false);

        websiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://seppuku.pw/"));
                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        installButton.setVisible(true);
        installButton.setBounds(146, 100, 100, 40);
        installButton.setFocusable(false);

        installButton.addActionListener(this);

        frame.setVisible(true);

        frame.add(label);
        frame.add(githubButton);
        frame.add(installButton);
        frame.add(websiteButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isForgeInstalled()) {
           downloadClient();
           downloadExternalMods();
        } else {
            JOptionPane.showMessageDialog(null, "Downloading forge (It will open automatically)");
            JOptionPane.showMessageDialog(null, "After forge installs, click the install button again");

            downloadForge();

            if(forgeInstallerExists()) {
                Process proc = null;
                try {
                    proc = Runtime.getRuntime().exec("java -jar " + new File(installerCache.getAbsolutePath() + "/forge1.12.2.jar").getAbsolutePath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                InputStream in = proc.getInputStream();
                InputStream err = proc.getErrorStream();

            }
        }
    }
}
