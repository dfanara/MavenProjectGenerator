package me.fanara.mavenprojectcreator;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ProjectCreator {

    private JFrame jframe;

    private JTextField pluginName = new JTextField("", 30);
    private JTextField pluginPackage = new JTextField("com.shdwlf", 30);
    private JTextField pluginLocation = new JTextField("D:/Development/", 30);

    private JCheckBox dependencySpigot = new JCheckBox("Spigot");
    private JCheckBox dependencyVault = new JCheckBox("Vault");

    public ProjectCreator() {
        setupWindow();
    }

    private void setupWindow() {
        jframe = new JFrame("Maven Project Creator");

        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel jpanel;

        jpanel = new JPanel();
        jpanel.setLayout(new MigLayout("wrap 3", "[grow, fill]", ""));
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Project Locations"));

        jpanel.add(new JLabel("Plugin Name"), "wrap");
        jpanel.add(pluginName, "span 3");

        jpanel.add(new JLabel("Package Prefix"), "wrap");
        jpanel.add(pluginPackage, "span 3");

        jpanel.add(new JLabel("Project Location"), "wrap");
        jpanel.add(pluginLocation, "span 3");
        mainPanel.add(jpanel);

        jpanel = new JPanel();
        jpanel.setLayout(new MigLayout("wrap 3", "[grow, fill]", ""));
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Dependencies"));
        jpanel.add(dependencySpigot);
        jpanel.add(dependencyVault);
        mainPanel.add(jpanel);

        jpanel = new JPanel();
        jpanel.setLayout(new MigLayout("wrap 2", "[grow, fill]", ""));
        jpanel.add(new JButton("Cancel"));
        JButton create = new JButton("Create Project");
        create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createProject(
                        pluginName.getText(),
                        pluginPackage.getText(),
                        pluginLocation.getText(),
                        dependencySpigot.isSelected(),
                        dependencyVault.isSelected()
                );
            }
        });
        jpanel.add(create);
        mainPanel.add(jpanel);

        mainPanel.add(jpanel);

        jframe.add(mainPanel);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
    }

    /**
     * Build dat project 'fore I go insane.
     */
    public void createProject(String name, String pluginPackage, String directory, boolean spigot, boolean vault) {
        try {
            String pom = Defaults.POM;
            String yml = Defaults.YML;
            String main = Defaults.MAIN;

            main = main.replace("$$NAME$$", name).replace("$$PACKAGE$$", pluginPackage + "." + name.toLowerCase());

            pom = pom.replace("$$NAME$$", name).replace("$$PACKAGE$$", pluginPackage);

            String repos = "";
            String dependencies = "";
            if(spigot) {
                repos +=
                        "       <repository>\n" +
                        "            <id>spigot</id>\n" +
                        "            <url>http://hub.spigotmc.org/nexus/content/groups/public/</url>\n" +
                        "        </repository>\n";
                dependencies +=
                        "       <dependency>\n" +
                        "            <groupId>org.spigotmc</groupId>\n" +
                        "            <artifactId>spigot-api</artifactId>\n" +
                        "            <version>1.8.7-R0.1-SNAPSHOT</version>\n" +
                        "            <scope>provided</scope>\n" +
                        "        </dependency>\n";
            }

            if(vault) {
                //Add vault later, we'll probably never use it anyways.
            }

            pom = pom.replace("$$REPOS$$", repos).replace("$$DEPENDENCIES$$", dependencies);
            yml = yml.replace("$$NAME$$", name).replace("$$MAIN$$", pluginPackage + "." + name.toLowerCase() + "." + name);

            //Create the project file structure
            File base = new File(directory + File.separator + name);
            if(!base.exists())
                base.mkdirs();

            File pack = new File(base + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + pluginPackage.replace(".", File.separator) + File.separator + name.toLowerCase());
            if(!pack.exists())
                pack.mkdirs();

            File resources = new File(base + File.separator + "src" + File.separator + "main" + File.separator + "resources");
            if(!resources.exists())
                resources.mkdir();

            File mvnPom = new File(base + File.separator + "pom.xml");
            if(!mvnPom.exists())
                mvnPom.createNewFile();

            writeFile(pom, mvnPom);

            File pluginYml = new File(resources + File.separator + "plugin.yml");
            if(!pluginYml.exists())
                pluginYml.createNewFile();

            writeFile(yml, pluginYml);

            File mainClass = new File(pack + File.separator + name + ".java");
            if(!mainClass.exists())
                mainClass.createNewFile();

            writeFile(main, mainClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(String string, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(string);
        fileWriter.flush();
        fileWriter.close();
    }

    private String readBuffer(BufferedReader buffIn) throws IOException {
        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = buffIn.readLine()) != null) {
            everything.append(line);
        }
        return everything.toString();
    }
}

class Defaults {
    public static final String POM = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <groupId>$$PACKAGE$$</groupId>\n" +
            "    <artifactId>$$NAME$$</artifactId>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "    <packaging>jar</packaging>\n" +
            "\n" +
            "    <name>$$NAME$$</name>\n" +
            "\n" +
            "    <properties>\n" +
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "    </properties>\n" +
            "\n" +
            "    <build>\n" +
            "        <defaultGoal>clean package install</defaultGoal>\n" +
            "        <finalName>$$NAME$$</finalName>\n" +
            "        <sourceDirectory>${basedir}/src/main/java</sourceDirectory>\n" +
            "        <resources>\n" +
            "            <resource>\n" +
            "                <targetPath>.</targetPath>\n" +
            "                <filtering>true</filtering>\n" +
            "                <directory>${basedir}/src/main/resources/</directory>\n" +
            "                <includes>\n" +
            "                    <include>*.yml</include>\n" +
            "                </includes>\n" +
            "            </resource>\n" +
            "        </resources>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <artifactId>maven-compiler-plugin</artifactId>\n" +
            "                <version>2.3.2</version>\n" +
            "                <configuration>\n" +
            "                    <source>1.7</source>\n" +
            "                    <target>1.7</target>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "    <dependencies>\n" +
            "        $$DEPENDENCIES$$\n" +
            "    </dependencies>\n" +
            "    <repositories>\n" +
            "        $$REPOS$$\n" +
            "    </repositories>\n" +
            "</project>";

    public static final String YML = "name: $$NAME$$\n" +
            "author: Shadowwolf97\n" +
            "version: 1.0\n" +
            "main: $$MAIN$$";

    public static final String MAIN =
            "package $$PACKAGE$$;\n\n" +
                "import org.bukkit.plugin.java.JavaPlugin;\n\n" +
                "public class $$NAME$$ extends JavaPlugin {\n\n" +
                "\t@Override\n\tpublic void onEnable() {\t\n\n\t}\n\n" +
                "\t@Override\n\tpublic void onDisable() {\t\n\n\t}\n\n}";

}
