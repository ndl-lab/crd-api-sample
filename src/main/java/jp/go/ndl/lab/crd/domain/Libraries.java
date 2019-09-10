package jp.go.ndl.lab.crd.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 参加館データの集合（データはローカルのTSV）
 */
public class Libraries {

    private static Libraries LIBRARIES = new Libraries();

    public static Libraries getInstance() {
        return LIBRARIES;
    }

    public Library get(String id) {
        return map.get(id);
    }

    public List<Library> list() {
        return new ArrayList<>(map.values());
    }

    private Map<String, Library> map = new HashMap<>();

    private Libraries() {
        try {
            for (String s : Files.readAllLines(Paths.get("libraries.tsv"))) {
                String[] ss = s.split("\t", 8);
                Library r = new Library(ss[0], ss[1], NumberUtils.toInt(ss[2]), ss[3], ss[4], ss[5], ss[6], ss[7]);
                map.put(r.id, r);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
