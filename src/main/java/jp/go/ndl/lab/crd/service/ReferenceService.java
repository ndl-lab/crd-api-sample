package jp.go.ndl.lab.crd.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import jp.go.ndl.lab.crd.domain.CRDParser;
import jp.go.ndl.lab.crd.domain.Reference;
import jp.go.ndl.lab.crd.domain.ResultSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * レファ協APIからレファレンス事例を検索するサービス<br>
 * APIの詳細については、以下を確認 http://crd.ndl.go.jp/jp/help/general/api.html#api
 */
public class ReferenceService {

    CRDParser parser = new CRDParser();

    /**
     * CQLフォーマットに対応
     */
    public static class Query {

        public Query() {
        }

        public Query(String anywhere) {
            this.anywhere = anywhere;
        }
        /**
         * 任意箇所にマッチ
         */
        public String anywhere;
        /**
         * 質問にマッチ
         */
        public String question;
        /**
         * 回答にマッチ
         */
        public String answer;
        /**
         * NDC、前方一致
         */
        public String ndc;

        private String cql() {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(anywhere)) {
                sb.append("anywhere any ").append(anywhere);
            } else {
                if (StringUtils.isNotBlank(question)) {
                    sb.append("question any ").append(question);
                }
                if (StringUtils.isNotBlank(answer)) {
                    if (sb.length() != 0) sb.append(" and ");
                    sb.append("answer any").append(answer);
                }
            }
            return sb.toString();
        }

        /**
         * 図書館ID
         */
        public String libraryId;
        /**
         * 登録日付（From）
         */
        public Date dateFrom;
        /**
         * 登録日付（To）
         */
        public Date dateTo;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 指定されたQuery、From、Sizeで検索
     */
    public ResultSet search(Query query, int from, int size) throws IOException {
        try {
            URIBuilder builder = new URIBuilder("https://crd.ndl.go.jp/api/refsearch");
            builder.addParameter("type", "reference");
            builder.addParameter("results_get_position", "" + from);
            builder.addParameter("results_num", "" + size);
            if (StringUtils.isNotBlank(query.cql()))
                builder.addParameter("query", query.cql());
            if (StringUtils.isNotBlank(query.libraryId))
                builder.addParameter("lib-id", query.libraryId);
            if (query.dateFrom != null)
                builder.addParameter("reg-date_from", dateFormat.format(query.dateFrom));
            if (query.dateTo != null)
                builder.addParameter("reg-date_to", dateFormat.format(query.dateTo));
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(builder.build());
                try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);) {
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        ResultSet rs = parser.parseResult(httpResponse.getEntity().getContent());
                        return rs;
                    }
                }
            }
        } catch (URISyntaxException ex) {
        }
        throw new IOException();
    }

    /**
     * 検索結果の全データをiterateしてConsumerで処理する。
     */
    public void iterateAll(Query query, int max, Consumer<Reference> c) throws IOException {
        for (int start = 1;;) {
            ResultSet r = search(query, start, 200);
            System.out.println(start + "/" + r.hitNum);
            r.references.forEach(c);
            start += 200;
            if (r.hitNum < start) break;
            if (start > max) break;
        }
    }

}
