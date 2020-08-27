package com.asteroid.duck.cefact.cc.data;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class ExcelReader implements Closeable {

    private final InputStream excelSource;
    private final Workbook workbook;

    public ExcelReader(InputStream excelSource) throws IOException {
        this.excelSource = excelSource;
        this.workbook = new HSSFWorkbook(excelSource);
    }

    protected Stream<Row> streamSheet(int sheet, int skipRows) {
        Sheet targetSheet = workbook.getSheetAt(sheet);

        Iterator<Row> rowIterator = targetSheet.rowIterator();
        for (int i = 0; i < skipRows; i++) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            else {
                return Stream.empty();
            }
        }
        Spliterator<Row> spliterator = Spliterators.spliterator(rowIterator, targetSheet.getLastRowNum() - skipRows, Spliterator.SIZED | Spliterator.DISTINCT);
        return StreamSupport.stream(spliterator, true);
    }

    public Stream<Code> readAnnexOne() {
        return streamSheet(1, 1)
                .map(row -> parse(row, 0));
    }

    public static Code parse(Row row, int startIndex) {
        Code.CodeBuilder builder = Code.builder();
        for (int i = startIndex; i < Code.NUM_FIELDS; i++) {
            Cell cell = row.getCell(i - startIndex, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            String cellValue = "";
            if (cell != null) {
                switch (cell.getCellType()) {
                    case FORMULA:
                    case STRING:
                        cellValue = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        cellValue = Integer.toString(Double.valueOf(cell.getNumericCellValue()).intValue());
                        break;
                }
            }
            builder.setByIndex(i, cellValue);
        }
        return builder.build();
    }

    public Stream<Code> readAnnexTwoAndThree() {
        return streamSheet(1, 1)
                .map(row -> parse(row, 5));
    }

    public Stream<Code> readAll() {
        return Stream.concat(readAnnexOne(), readAnnexTwoAndThree());
    }

    @Override
    public void close() {
        try {
            workbook.close();
        }
        catch (IOException e) {
            log.error("Unable to close workbook", e);
        }
        try {
            excelSource.close();
        }
        catch (IOException e) {
            log.error("Unable to close input stream", e);
        }
    }

    public static void main(String[] args) throws IOException {
        URL url = ExcelReader.class.getResource("/rec20_Rev9e_2014.xls");
        if (args != null && args.length > 0 ) {
            url = new URL(args[0]);
        }
        try (ExcelReader reader = new ExcelReader(url.openStream())) {
            reader.readAnnexOne().forEach(code -> {
                System.out.println(code);
            });
        }
    }
}
