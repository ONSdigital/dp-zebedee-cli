package com.github.onsdigital.zebedee.data.processing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static org.apache.commons.cli.Option.builder;

public class Cli {

    static final String CREATE_COLLECTION = "createcollection";
    static final String UPDATE_TIMESERIES_FROM_CSV = "updatetimeseriesfromcsv";
    static final String REMOVE_TIMESERIES_DATA = "removetimeseriesdata";
    static final String REMOVE_TIMESERIES_ENTRIES = "removetimeseriesentries";
    static final String FIND_TIMESERIES_FOR_SOURCE_DATASET = "findtimeseriesforsourcedataset";
    static final String MOVE_CONTENT = "movecontent";
    static final String LIST_TIMESERIES = "listtimeseries";
    static final String MIGRATE_TIMESERIES = "migratetimeseries";
    static final String DATASET_VERSION_HISTORY = "datasetversionhistory";
    static final String UPDATE_TIMESERIES = "updatetimeseries";
    static final String REMOVE_COLLECTION = "rmcollection";

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(builder(CREATE_COLLECTION)
                .desc("create a new unencrypted collection.")
                .argName("collections directory> <collection name")
                .numberOfArgs(2)
                .build());
        options.addOption(builder(UPDATE_TIMESERIES_FROM_CSV)
                .desc("update timeseries metadata from the given CSV.")
                .argName("source directory> <destination directory> <csv file")
                .numberOfArgs(3)
                .build());
        options.addOption(builder(REMOVE_TIMESERIES_DATA)
                .desc("Remove all timeseries data entries for the given resolution ( months | quarters | years )")
                .argName("source directory> <destination directory> <resolution> <CDID...")
                .hasArgs()
                .build());
        options.addOption(builder(REMOVE_TIMESERIES_ENTRIES)
                .desc("Remove specific timeseries entries for the given CDID")
                .argName("source directory> <destination directory> <CDID> <dataset id's> <labels...")
                .hasArgs()
                .build());
        options.addOption(builder(FIND_TIMESERIES_FOR_SOURCE_DATASET)
                .desc("find the timeseries files that exclusively have the given source dataset.")
                .argName("source directory> <source dataset ID")
                .numberOfArgs(2)
                .build());
        options.addOption(builder(MOVE_CONTENT)
                .desc("Move content into a new location in a collection")
                .argName("source directory> <destination directory> <source URI> <destination URI")
                .numberOfArgs(4)
                .build());
        options.addOption(builder(LIST_TIMESERIES)
                .desc("List time series")
                .argName("source directory> <destination file")
                .numberOfArgs(2)
                .build());
        options.addOption(builder(MIGRATE_TIMESERIES)
                .desc("Migrate time series from CDID based to CDID + dataset ID based format.")
                .argName("source directory> <destination file> <optional datasetID")
                .hasArgs()
                .build());
        options.addOption(builder(DATASET_VERSION_HISTORY)
                .desc("Find missing entries in CSV dataset versions")
                .argName("source directory")
                .hasArgs()
                .build());
        options.addOption(builder(UPDATE_TIMESERIES)
                .desc("Update content for the specified timeseries.")
                .argName("source directory> <destination directory> <CDID's> <dataset ID's")
                .numberOfArgs(4)
                .build());
        options.addOption(builder(REMOVE_COLLECTION)
                .desc("Force delete a collection directory")
                .argName("<collectiions path> <collection name>")
                .numberOfArgs(2)
                .build());
        options.addOption(builder("datafix")
                .desc("")
                .argName("<master path> <collectiions path> <collection name>")
                .numberOfArgs(2)
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption(CREATE_COLLECTION)) {
                CollectionCreator.createCollection(args);
            } else if (line.hasOption(UPDATE_TIMESERIES_FROM_CSV)) {
                CsvTimeseriesUpdater.updateTimeseriesFromCsv(args);
            } else if (line.hasOption(REMOVE_TIMESERIES_DATA)) {
                TimeseriesDataRemover.removeTimeseriesData(args);
            } else if (line.hasOption(REMOVE_TIMESERIES_ENTRIES)) {
                TimeseriesDataRemover.removeTimeseriesEntries(args);
            } else if (line.hasOption(FIND_TIMESERIES_FOR_SOURCE_DATASET)) {
                TimeseriesFinder.findTimeseriesForSourceDataset(args);
            } else if (line.hasOption(MOVE_CONTENT)) {
                ContentMover.moveContent(args);
            } else if (line.hasOption(LIST_TIMESERIES)) {
                TimeseriesLister.listTimeseries(args);
            } else if (line.hasOption(MIGRATE_TIMESERIES)) {
                TimeseriesMigration.migrateTimeseries(args);
            } else if (line.hasOption(DATASET_VERSION_HISTORY)) {
                DatasetVersionHistory.findDatasetsWithMissingVersionHistory(args);
            } else if (line.hasOption(UPDATE_TIMESERIES)) {
                TimeseriesUpdater.updateTimeseries(args);
            } else if (line.hasOption(REMOVE_COLLECTION)) {
                ForceDeleteCollection.delete(args);
            } else if (line.hasOption("datafix")) {
                DataFix.fix(args);
            }
            else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth(150);
                formatter.printHelp("zebedee-cli", options);
            }

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }


}
