package com.github.onsdigital.zebedee.data.processing;

import java.nio.file.Path;
import java.nio.file.Paths;

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

        for (String src : targets) {
            collectionName = Paths.get(src).getFileName().toString();

            Path collectionInProgPath = collectionsDir.resolve(collectionName).resolve("inprogress");

            try {
                CollectionCreator.CreateCollection(collectionsDir, collectionName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Path collectionDir = collectionsDir.resolve(collectionName);

            String dest = src.replace("cuturalidentity", "culturalidentity");
            try {
                System.out.println("");
                ContentMover.moveContent(new String[]{
                        "",
                        masterPath.toString(),
                        collectionInProgPath.toString(),
                        src,
                        dest
                });
                System.out.println("move completed for " + src + " collection: " + collectionName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("all taxonomy moves completed successfully");
        }
    }
}
