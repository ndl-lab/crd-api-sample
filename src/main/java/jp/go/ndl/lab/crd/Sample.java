package jp.go.ndl.lab.crd;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import jp.go.ndl.lab.crd.domain.Libraries;
import jp.go.ndl.lab.crd.domain.Library;
import jp.go.ndl.lab.crd.service.ReferenceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;

public class Sample {

    public static void main(String[] args) throws Exception {
        //レファ協APIからレファレンス事例を検索し、問と回答の出現単語を数える。
        ReferenceService rs = new ReferenceService();
        ReferenceService.Query q = new ReferenceService.Query("魔女");

        Counter<String> counter = new Counter<>();
        rs.iterateAll(q, 200, ref -> {
            Library lib = Libraries.getInstance().get(ref.libraryId);
            try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.Mode.NORMAL)) {
                tokenizer.setReader(new StringReader(ref.question + ref.answer));
                CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);
                PartOfSpeechAttribute partOfSpeech = tokenizer.addAttribute(PartOfSpeechAttribute.class);
                tokenizer.reset();
                while (tokenizer.incrementToken()) {
                    String t = term.toString();
                    String pos = partOfSpeech.getPartOfSpeech();
                    if (StringUtils.isNotBlank(t) && pos.startsWith("名詞") && pos.endsWith("一般")) {
                        counter.count(t);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        //数えた単語からWordCloudを生成
        List<WordFrequency> wordFrequencies = counter.keySet().stream().filter(key -> counter.get(key) > 20).map(key -> new WordFrequency(key, counter.get(key))).collect(Collectors.toList());
        wordFrequencies.forEach(System.out::println);
        final Dimension dimension = new Dimension(400, 400);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackgroundColor(Color.white);
        wordCloud.setKumoFont(new KumoFont(new Font("Meiryo", Font.PLAIN, 12)));
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        BufferedImage resultImage = wordCloud.getBufferedImage();
        ImageIO.write(resultImage, "png", new File("wc-" + System.currentTimeMillis() + ".png"));
    }
}

class Counter<T> {

    private Map<T, Integer> map = new HashMap<>();

    public void count(T key) {
        int v = get(key);
        map.put(key, v + 1);
    }

    public int get(T key) {
        return map.getOrDefault(key, 0);
    }

    public Set<T> keySet() {
        return map.keySet();
    }
}
