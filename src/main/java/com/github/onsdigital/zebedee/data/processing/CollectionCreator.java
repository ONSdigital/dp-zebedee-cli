package com.github.onsdigital.zebedee.data.processing;

import com.github.davidcarboni.cryptolite.Random;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedee.json.CollectionDescription;
import com.github.onsdigital.zebedee.json.CollectionType;
import com.github.onsdigital.zebedee.json.Event;
import com.github.onsdigital.zebedee.json.EventType;
import com.github.onsdigital.zebedee.model.Collection;
import com.github.onsdigital.zebedee.model.PathUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.github.onsdigital.zebedee.data.processing.MyLogger.log;

public class CollectionCreator {

    public static void createCollection(String[] args) throws Exception {

        Path destination = Paths.get(args[1]);
        String collectionName = args[2];

        CreateCollection(destination, collectionName);
    }

    public static void CreateCollection(Path destination, String collectionName) throws IOException, InterruptedException {

        CollectionDescription collection = new CollectionDescription(collectionName);
        collection.type = CollectionType.manual;
        collection.isEncrypted = false;
        collection.name = collectionName;

        String filename = PathUtils.toFilename(collectionName);
        collection.id = filename + "-" + Random.id();
        log("creating collection collectionID: {0}", collection.id);
        Collection.CreateCollectionFolders(filename, destination);

        //collection.addEvent(new Event(new Date(), EventType.CREATED, "dp-zebedee-cli"));

        // Create the description:
        Path collectionDescriptionPath = destination.resolve(filename + ".json");
        try (OutputStream output = Files.newOutputStream(collectionDescriptionPath)) {
            Serialiser.serialise(output, collection);
        }
    }
}
