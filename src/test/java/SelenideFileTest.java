import net.lingala.zip4j.ZipFile;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

import static java.nio.file.Files.exists;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SelenideFileTest {
    //проверка txt-файл
    @Test
    void testTextFile() throws Exception {
        String result;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("Test-2.txt")) {
            result = new String(stream.readAllBytes(), "UTF-8");
            assertThat(result).contains("Имеется спорная точка зрения, гласящая примерно следующее:");
        }
    }
    //проверка pdf-файла
    @Test
    void testPdfFile() throws Exception {
        PDF result;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("Test-3.pdf")) {
            result = new PDF(stream);
            assertThat(result.text).contains("Имеется спорная точка зрения, гласящая примерно следующее:");
        }
    }
    //проверка  excel-файла
    @Test
    void testExcelFile() throws Exception {
        XLS result;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("Test-5.xlsx")) {
            result = new XLS(stream);
            assertThat(result.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("Иванов");
        }
    }
    //проверка zip-файла
    @Test
    void zipFileTest() throws Exception {
        ZipFile zipFile;
        zipFile = new ZipFile("./src/test/resources/Test.zip");
        String destination = "./src/test/resources/TestZip/";
        String filename="Test-2.txt";
        if (zipFile.isEncrypted()) {
            zipFile.setPassword("123".toCharArray());
            System.out.println("Пароль подошел");
        }
        zipFile.extractAll(destination);
        try (Stream<Path> paths = Files.walk(Paths.get(destination))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
            if (Files.exists(Paths.get(destination + filename))){
             System.out.println( "Файл "+filename+" есть в архиве");
            }
            else {System.out.println( "Файл "+filename+" не найден в архиве");}
        }
    }

    //проверка doc-файла
    @Test
    void docsFileTest() throws Exception {
        WordprocessingMLPackage wordMLPackage =
                WordprocessingMLPackage.load(new java.io.File("./src/test/resources/Test-4.docx"));
        String text = wordMLPackage.getMainDocumentPart().getContent().toString();
        assertThat(text).contains("Имеется спорная точка зрения, гласящая примерно следующее:");
    }
}
