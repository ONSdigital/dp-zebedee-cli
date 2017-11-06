package com.github.onsdigital.zebedee.data.processing;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 02/11/2017.
 */
public class DataFix {

    /*static String[] files = {"f3ebf62a.json", "f3ebf62a.html", "f3ebf62a.xls", "data.json", "page.pdf"};
    static final String CURRENT_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/davetest";
    static final String DEST_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/davetest/whoop";*/

    static String[] files = {"0d170e32.json", "0d170e32.png", "0d170e32.xls", "71153eee.json", "data.json", "page.pdf"};
    static final String CURRENT_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket";
    static final String DEST_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket/octtodec2016";


    public static void fix(String[] args) throws IOException, InterruptedException {

        Path master = Paths.get(args[1]);
        Path collectionsDir = Paths.get(args[2]);
        String collectionName = args[3];

        System.out.println("");
        System.out.println(format("[Datafix] moving {0} to {1}\n", CURRENT_DIR, DEST_DIR));

        System.out.println(format("[config]\n\tmaster: {0}\n\tcollectionsDir: {1}\n\tcollectionName: {2}\n", master,
                collectionsDir, collectionName));

        CollectionCreator.CreateCollection(collectionsDir, collectionName);
        Path collectionRoot = collectionsDir.resolve(collectionName).resolve("inprogress");

        // The new location
        Path targetCollectionDir = collectionRoot.resolve(DEST_DIR);
        targetCollectionDir.toFile().mkdirs();

        // The current location
        Path srcPath = master.resolve(CURRENT_DIR);


        for (String filename : files) {
            File src = srcPath.resolve(filename).toFile();
            Path move = master.resolve(src.toPath());
            File dest = targetCollectionDir.resolve(filename).toFile();

            System.out.println("[move]");
            System.out.println(format("\tsrc: uri: {0}", move.toString()));
            System.out.println(format("\tdest: uri: {0}", dest.toString()));

            FileUtils.copyFile(src, dest);
        }
        System.out.println("");

        // fix the data in this file.
        System.out.println("Fixing moved data.json links");
        Path dataJsonPath = collectionRoot.resolve(targetCollectionDir).resolve("data.json");
        String dataJson = new String(Files.readAllBytes(dataJsonPath));
        dataJson = dataJson.replaceAll(CURRENT_DIR + "/", DEST_DIR);

        try (BufferedWriter br = Files.newBufferedWriter(dataJsonPath)) {
            br.write(dataJson);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        List<Path> masterJsonFiles = new DataJsonFinder().findJsonFiles(master);

        System.out.println("\nsearching for potential broken links...");
        System.out.println(format("[{0}]", master.toString()));

        List<String> brokenLinks = findLinks(master, masterJsonFiles, false);
        brokenLinks.stream().forEach(link -> System.out.println("\t" + link));
        System.out.println("");

        List<Path> brokenFilesInCollections = new DataJsonFinder().findJsonFiles(collectionsDir);
        System.out.println(format("[{0}]", collectionsDir.toString()));

        for (Path uri : new DataJsonFinder().findJsonFiles(collectionsDir)) {
            String pureURI = removeCollectionPrefix(collectionsDir, uri);
            if (brokenLinks.contains(pureURI)) {
                System.out.println("\t" + collectionsDir.relativize(uri).toString());
            }
        }

        System.out.println("\n[complete]");
    }

    public static List<String> findLinks(Path master, List<Path> masterJsonFiles, boolean debug) throws IOException {
        List<String> brokenLinks = new ArrayList<>();
        for (Path p : masterJsonFiles) {
            if (debug) {
                System.out.println("\t[uri]: " + p.toString());
            }
            if (p.toString().contains(CURRENT_DIR)) {
                continue;
            }

            String json = new String(Files.readAllBytes(p));
            if (json.contains(CURRENT_DIR)) {
                brokenLinks.add(Paths.get("/").resolve(master.relativize(p)).toString());
            }
        }
        return brokenLinks;
    }

    static String removeCollectionPrefix(Path collectionsDir, Path uri) {
        String collectionURI = uri.toString();
        String splitPoint = "";

        if (collectionURI.contains("inprogress")) {
            splitPoint = "inprogress";
        } else if (collectionURI.contains("complete")) {
            splitPoint = "complete";
        } else if (collectionURI.contains("reviewed")) {
            splitPoint = "reviewed";
        }
        return collectionURI.substring(collectionURI.indexOf(splitPoint)).replace(splitPoint, "");
    }
}
