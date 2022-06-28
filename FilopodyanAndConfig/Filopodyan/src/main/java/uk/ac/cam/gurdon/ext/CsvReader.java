package uk.ac.cam.gurdon.ext;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weiying
 * @date 2022/1/15 14:39
 */
public class CsvReader {

    private Map<String, Integer> headerMap = new HashMap<>();

    private List<List<String>> lines;

    //current line to read (当前读取第几行)
    private int curLine = 0;

    //total line of csv (csv 总行数)
    private int totalLine = 0;

    public CsvReader(File file) {
        init(file);
    }

    private void init(File file) {
        try {
            // initialization (初始化)
            List<String> text = FileUtils.readLines(file, Charset.forName("UTF-8"));
            lines = new ArrayList<>();
            for (String line : text) {
                String[] params = line.split(",");
                List<String> ps = new ArrayList<>(Arrays.asList(params));
                lines.add(ps);
                totalLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the table value of the current row based on the header of table
     * (根据表头获取当前行的表格值)
     *
     * @param text header (表头名)
     */
    public String get(String text) {
        Integer index = headerMap.get(text);
        return lines.get(curLine)
                .get(index);
    }

    /**
     * read a row (读取一行)
     */
    public boolean read() {
        curLine++;
        return curLine < totalLine;
    }

    /**
     * read header (读取表头信息)
     */
    public void readHeader() {
        List<String> titles = lines.get(0);
        for (int i = 0; i < titles.size(); i++) {
            headerMap.put(titles.get(i), i);
        }
    }

}
