package jp.go.ndl.lab.crd.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
/**
 * レファレンス事例
 */
public class Reference {

    public String question;

    public String preResearch;
    public String regId;
    public String answer;
    public String answerProcess;
    public String resType;
    public String patronType;
    public String conType;
    public List<String> keywords = new ArrayList();
    public List<String> ndcs = new ArrayList();
    public List<String> bibs = new ArrayList();
    public String url;
    public String note;
    public Date date;
    public String libraryId;

}
