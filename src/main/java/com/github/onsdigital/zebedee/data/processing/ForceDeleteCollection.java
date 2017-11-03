package com.github.onsdigital.zebedee.data.processing;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedee.json.CollectionType;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Map;

import static java.text.MessageFormat.format;

/**
 * Forcefully delete a collection - don't check
 */
public class ForceDeleteCollection {

    static final String SCHEDULED_COLLECTION_ERR = "\n\tWarning! Cannot force delete collection {0} as it is" +
            " currently scheduled to publish at {1}.\n\tIf you wish to delete this collection you must unschedule it " +
            "first\n";

    public static void delete(String[] args) throws IOException {

        Path collectionsDir = Paths.get(args[1]);
        String collectionName = args[2];

        System.out.println(args[1]);
        System.out.println(args[2]);

        Path target = collectionsDir.resolve(collectionName);

        if (Files.exists(target)) {
            System.out.println(format("collection {0} exists, proceeding with delete", collectionName));


            try (InputStream in = Files.newInputStream(collectionsDir.resolve(collectionName + ".json"))) {
                Map<String, Object> m = Serialiser.deserialise(in, Map.class);

                CollectionType type = CollectionType.valueOf((String) m.get("type"));
                String publishDate = (String)m.get("publishDate");

                if (type == CollectionType.scheduled) {
                    System.out.println(format(SCHEDULED_COLLECTION_ERR, collectionName, publishDate));
                    System.exit(1);
                }
            }

            FileUtils.deleteDirectory(target.toFile());
            FileUtils.forceDelete(collectionsDir.resolve(collectionName + ".json").toFile());

            System.out.println("delete completed successful");

        } else {
            System.out.println(target.toString() + " does not exists, no action required");
        }
    }
}
