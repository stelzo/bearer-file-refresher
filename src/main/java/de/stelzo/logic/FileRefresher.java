package de.stelzo.logic;

import org.eclipse.microprofile.config.ConfigProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRefresher {

    public static void updateRefreshFile(String bearer) {
        String filePath = ConfigProvider.getConfig().getValue("bearer.file.path", String.class);
        Path path = Paths.get(filePath);

        try {
            File f = new File(path.toString());
            FileWriter fileWriter = new FileWriter(f, false);

            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();

            fileWriter.write(bearer);
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
