package zipfile;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipReaderClass {
    ClassLoader classLoader = ZipReaderClass.class.getClassLoader(); // позволяет читать файлы из папки resources

    @Test
    void readZipFile() throws Exception {
        try (InputStream is = classLoader.getResourceAsStream("newzipfile.zip")) { // прочитали архив
            assert is != null;
            try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    if (zipEntry.getName().contains("csv")) {
                        CSVReader csvReader = new CSVReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
                        List<String[]> csv = csvReader.readAll();
                        assertThat(csv).contains(
                                new String[]{"username", "emailAddress", "TicketNumber", "Summary", "Status", "PhoneNumber"},
                                new String[]{"mFischer", "monika.fischer@gmail.com", "T-45612", "Problem with Internet", "Done", "+789564569"},
                                new String[]{"sKnapp", "sven.knapp@gmali.com", "T-45613", "Plugin not installed", "in Progress", "+7568956989"}
                        );
                    } else if (zipEntry.getName().contains("xlsx")) {
                        XLS xls = new XLS(zipInputStream);
                        assertThat(xls.excel.getSheetAt(0).getRow(5).getCell(1).getStringCellValue()).contains("California ");
                        assertThat(xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue()).contains("California");

                    } else if (zipEntry.getName().contains("pdf")) {
                        PDF pdf = new PDF(zipInputStream);
                        assertThat(pdf.title).isEqualTo("JUnit 5 User Guide");

                    }
                }
            }
        }


    }

}
