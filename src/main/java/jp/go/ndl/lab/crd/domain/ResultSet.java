package jp.go.ndl.lab.crd.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ResultSet {

    public int hitNum;
    public int resultsGetPosition;
    public int resultsNum;
    public List<Reference> references = new ArrayList<>();

}
