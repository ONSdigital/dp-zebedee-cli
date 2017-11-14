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
import java.util.Scanner;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 02/11/2017.
 */
public class DataFix {

/*    static String[] files = {"f3ebf62a.json", "f3ebf62a.html", "f3ebf62a.xls", "data.json", "page.pdf"};
    static final String CURRENT_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/davetest";
    static final String DEST_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/davetest/whoop";*/

    static String[] files = {"0d170e32.json", "0d170e32.png", "0d170e32.xls", "71153eee.json", "data.json", "page.pdf"};
    static final String CURRENT_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket";
    static final String DEST_DIR = "economy/economicoutputandproductivity/productivitymeasures/articles/gdpandthelabourmarket/octtodec2016";


    public static void fix(String[] args) throws Exception {

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
    }

    public static void fixPartDeux(String[] args) throws Exception {
        Path master = Paths.get(args[1]);

        System.out.println("");
        System.out.println(format("[Datafix] delete moved files from {0}\n", CURRENT_DIR));

        System.out.println(format("[config]\n\tmaster: {0}\n", master));

        List<String> verificationFailures = new ArrayList<>();
        List<String> incollection = new ArrayList<>();
        System.out.println("[verifying safe to delete files]");

        for (String f : files) {
            Path target = master.resolve(DEST_DIR).resolve(f);
            if (!Files.exists(target)) {
                verificationFailures.add(target.toString());
            }
        }

        if (!verificationFailures.isEmpty()) {
            StringBuilder sb = new StringBuilder("[verification failure: cannot proceed as some of the files do not " +
                    "exit in the updated location]");
            verificationFailures.stream().forEach(failure -> sb.append("\n\t" + failure));
            sb.append("\nfix and try again");
            System.out.println(sb.toString());
            System.exit(1);
        }

        System.out.println("[Checking for broken links]");

        List<String> brokenLinks = findLinks(master, new DataJsonFinder().findJsonFiles(master), false);
        if (!brokenLinks.isEmpty()) {
            System.out.println("[Found files in master containing a direct/partial match of the uri of moved the content]");
            System.out.println("[Please check each file to ensure no links are broken]");

            brokenLinks.stream().forEach(link -> System.out.println("\t" + link));

            if (!stdiin("[Do to want to continue Y/n?]")) {
                System.out.println("Cancelling delete");
                System.exit(1);
            }
        }

        System.out.println("[Proceeding with delete]");
        System.out.println("[The following files will be deleted]");

        for (String f : files) {
            Path target = master.resolve(CURRENT_DIR).resolve(f);
            System.out.println("\t" + target.toString());
        }

        if (stdiin("[Are you sure you wish to continue?]")) {
            for (String f : files) {
                Path target = master.resolve(CURRENT_DIR).resolve(f);
                System.out.println("deleting: " + target.toString());
                Files.delete(target);
            }
            System.out.println("[data fix completed]");
        } else {
            System.out.println("Cancelling delete");
            System.exit(1);
        }
    }

    public static boolean stdiin(String message) {
        Scanner sc = new Scanner(System.in);
        String proceed = "";
        boolean valid = false;

        do {
            System.out.println(message);
            proceed = sc.next().toUpperCase();
            valid = proceed.equalsIgnoreCase("Y") || proceed.equalsIgnoreCase("N");
        } while (!valid);

        return proceed.equalsIgnoreCase("Y");
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
