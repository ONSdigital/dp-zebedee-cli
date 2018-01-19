package com.github.onsdigital.zebedee.data.processing;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.onsdigital.zebedee.data.processing.MyLogger.log;

/**
 * Created by dave on 16/01/2018.
 */
public class TaxonomyFix {

    static final String[] targets = {
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/cigarettesmokingbysexualidentitybycountry",
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/sexualidentitybyagegroupbycountry",
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/sexualidentitybycountry",
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/sexualidentitybygenderbycountry",
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/sexualidentitybyregionuk",
            "peoplepopulationandcommunity/cuturalidentity/sexuality/datasets/sexualidentitylesbiangayandbisexualpopulationbyoccupationbycountry",
    };

    public static void Go(String[] args) {
        Path masterPath = Paths.get(args[1]);
        Path collectionsDir = Paths.get(args[2]);

        String collectionName = "";
        String src = "";


        for (int i=0; i < targets.length; i++) {
            src = targets[i];
            collectionName = "move_" + Paths.get(src).getFileName().toString();

            log("beginning move {0} of {1}, collection: {2}", i + 1, targets.length, collectionName);
            Path collectionInProgPath = collectionsDir.resolve(collectionName).resolve("inprogress");

            try {
                CollectionCreator.CreateCollection(collectionsDir, collectionName);
            } catch (Exception e) {
                log("error creating collection: {0}", e.getMessage());
                System.exit(1);
            }

            Path collectionDir = collectionsDir.resolve(collectionName);

            String dest = src.replace("cuturalidentity", "culturalidentity");
            try {
                ContentMover.moveContent(new String[]{
                        "",
                        masterPath.toString(),
                        collectionInProgPath.toString(),
                        src,
                        dest
                });
                log("move completed for src: {0} collection: {1}", src, collectionName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("");
        }
        System.out.println("all taxonomy moves completed successfully");
    }
}
