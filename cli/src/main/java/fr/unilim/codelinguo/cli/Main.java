package fr.unilim.codelinguo.cli;

import fr.unilim.codelinguo.common.model.Word;
import fr.unilim.codelinguo.common.model.reader.FileReader;
import fr.unilim.codelinguo.common.service.WordAnalyticsService;
import fr.unilim.codelinguo.common.service.export.report.PDFReportExportService;
import fr.unilim.codelinguo.common.service.export.wordrank.CSVWordRankExportService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "codelinguo", mixinStandardHelpOptions = true, versionProvider = DynamicVersionProvider.class)
public class Main implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1", description = "The path to the file to analyze.")
    private String inputFilePath;

    @Option(names = {"-n", "--number"}, description = "Number of top words to display. Defaults to 10.")
    private int numberOfResults = 10;

    @Option(names = {"--no-csv"}, description = "Do not create a CSV file.")
    private boolean noCsv = false;

    @Option(names = {"--no-pdf"}, description = "Do not create a PDF file.")
    private boolean noPdf = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private static void processPath(String path, int numberOfResults, boolean noCsv, boolean noPdf) {
        try {
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                System.out.println("The path does not exist.");
                return;
            }

            List<Word> analysisWords = new FileReader().read(path);
            if (analysisWords.isEmpty()) {
                System.out.println("No words found. Analysis terminated.");
                return;
            }

            WordAnalyticsService analytics = new WordAnalyticsService();
            Map<Word, Integer> wordRank = analytics.wordRank(analysisWords);

            if (!noCsv) {
                String csvPath = new CSVWordRankExportService().save(
                        System.getProperty("user.dir"),
                        wordRank,
                        -1,
                        pathFile.getName(),
                        pathFile.getName()
                );
                System.out.println("CSV file has been saved to: " + csvPath);
            }

            if (!noPdf) {
                String pdfPath = new PDFReportExportService().createCodeAnalysisReport(
                        pathFile.getName(),
                        wordRank,
                        -1,
                        pathFile.getName(),
                        System.getProperty("user.dir")
                );
                System.out.println("PDF file has been saved to: " + pdfPath);
            }

            LinkedHashMap<Word, Integer> sortedWordRank = wordRank.entrySet().stream()
                    .sorted(Map.Entry.<Word, Integer>comparingByValue().reversed())
                    .limit(numberOfResults)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));

            System.out.println("------------------------------------------------");
            System.out.printf("%-20s | %s\n", "Word", "Frequency");
            System.out.println("------------------------------------------------");
            sortedWordRank.forEach((word, count) -> System.out.printf("%-20s | %d\n", word.getToken(), count));
            System.out.println("------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Error during file processing: " + e.getMessage());
        }
    }

    @Override
    public Integer call() {
        if (this.inputFilePath == null || this.inputFilePath.isEmpty()) {
            CommandLine.usage(this, System.out);
            return 0;
        }

        Main.processPath(this.inputFilePath, this.numberOfResults, this.noCsv, this.noPdf);
        return 0;
    }
}
