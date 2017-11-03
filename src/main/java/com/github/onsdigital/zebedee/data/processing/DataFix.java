package com.github.onsdigital.zebedee.data.processing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 02/11/2017.
 */
public class DataFix {

    //static final String currentPath = "/economy/economicoutputandproductivity/productivitymeasures/articles/davetest";
    static final String currentPath = "/economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket";

    // The directory the files are currently in.
    //static Path currentDir = Paths.get("/Users/dave/Desktop/zebedee-data/content/zebedee/master/economy/economicoutputandproductivity/productivitymeasures/articles/davetest");
    static Path currentDir = Paths.get("/economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket");

    // The directory to move them to
    //static Path targetDir = Paths.get("economy/economicoutputandproductivity/productivitymeasures/articles/davetest/whoop/");
    static Path targetDir = Paths.get("/economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket/octtodec2016");

    // The names of the files to move.
    //static String[] files = {"f3ebf62a.json", "f3ebf62a.html", "f3ebf62a.xls", "data.json", "page.pdf"};
    static String[] files = {"0d170e32.json", "0d170e32.png", "0d170e32.xls", "71153eee.json", "data.json", "page.pdf"};


    public static void fix(String[] args) throws IOException, InterruptedException {

        Path master = Paths.get(args[1]);
        Path collectionsDir = Paths.get(args[2]);
        String collectionName = args[3];

        System.out.println(format("master: {0}, collectionsDir: {1}, collectionName: {2}", master,
                collectionsDir, collectionName));

        System.out.println("creating collection...");
        CollectionCreator.CreateCollection(collectionsDir, collectionName);

        Path collectionRoot = collectionsDir.resolve(collectionName).resolve("inprogress");

        Path targetCollectionDir = collectionRoot.resolve("economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket/octtodec2016");
        targetCollectionDir.toFile().mkdirs();

        Path srcPath  = master.resolve("economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket");

        for (String filename : files) {
            File src = srcPath.resolve(filename).toFile();

            Path move = master.resolve(src.toPath());
            System.out.println(format("src:\n\texists? {0}\n\turi: {1}" + Files.exists(move), move.toString()));

            File dest = targetCollectionDir.resolve(filename).toFile();
            System.out.println(format("dest:\n\texists? {0}\n\turi: {1}" + Files.exists(dest.toPath()), dest.toString()));

            FileUtils.copyFile(src, dest);
        }

/*
        // fix the data in this file.
        System.out.println("Fixing this data.json links...");

        Path dataJsonPath = collectionRoot.resolve(targetDir).resolve("data.json");
        String dataJson = new String(Files.readAllBytes(dataJsonPath));

        dataJson = dataJson.replaceAll(currentPath, targetDir.toString());

        try (BufferedWriter br = Files.newBufferedWriter(dataJsonPath)) {
            br.write(dataJson);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("Searching for broken links for: " + currentPath);

        List<Path> masterJsonFiles = new DataJsonFinder().findJsonFiles(master);
        List<Path> brokenLinks = new ArrayList<>();


        Path skip = currentDir.resolve("data.json");
        System.out.println("skip uri: " + skip.toString());
        for (Path p : masterJsonFiles) {
            if (p.equals(skip)) {
                System.out.println("Skipping self....");
                continue;
            }

            String json = new String(Files.readAllBytes(p));
            if (StringUtils.contains(json, currentPath)) {
                System.out.println("Found broken link copying to collection: " + p.toString());

                json = json.replaceAll(currentPath,  targetDir.toString());
                try (InputStream in = new ByteArrayInputStream(json.getBytes())) {
                    FileUtils.copyInputStreamToFile(in, collectionRoot.resolve(master.relativize(p)).toFile());
                }
            }
        }*/
    }
}
