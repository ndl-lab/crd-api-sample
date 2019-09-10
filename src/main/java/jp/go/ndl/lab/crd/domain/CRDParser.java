/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.go.ndl.lab.crd.domain;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * レファ協APIの応答XMLのParser
 */
public class CRDParser {

    private SAXParserFactory saxParserFactory;
    private SAXParser parser;

    public CRDParser() {
        try {
            saxParserFactory = SAXParserFactory.newInstance();
            parser = saxParserFactory.newSAXParser();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet parseResult(InputStream xml) {
        CRDSaxHandler saxHandler = new CRDSaxHandler();

        try {
            parser.parse(xml, saxHandler);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        return saxHandler.result;
    }

    private class CRDSaxHandler extends DefaultHandler {

        private ResultSet result;
        private Reference r;

        private StringBuilder currentValue;

        @Override
        public void startDocument() throws SAXException {
            result = new ResultSet();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentValue = new StringBuilder();
            if (qName.equals("reference")) {
                r = new Reference();
                result.references.add(r);
            }
        }

        @Override
        public void characters(char[] ch, int offset, int length) throws SAXException {
            if (currentValue != null) {
                currentValue.append(ch, offset, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("reference")) {
                r = null;
//                System.out.println("----------------------");
            }
            if (!StringUtils.isBlank(currentValue)) {
                switch (qName) {
                    case "hit_num":
                        result.hitNum = NumberUtils.toInt(currentValue.toString());
                        break;
                    case "results_num":
                        result.resultsNum = NumberUtils.toInt(currentValue.toString());
                        break;
                    case "results_get_position":
                        result.resultsGetPosition = NumberUtils.toInt(currentValue.toString());
                        break;
                    case "question":
                        r.question = currentValue.toString();
                        break;
                    case "answer":
                        r.answer = currentValue.toString();
                        break;
                    case "pre-res":
                        r.preResearch = currentValue.toString();
                        break;
                    case "keyword":
                        r.keywords.add(currentValue.toString());
                        break;
                    case "class":
                        r.ndcs.add(currentValue.toString());
                        break;
                    case "bibl-desc":
                        r.bibs.add(currentValue.toString());
                        break;
                    case "note":
                        r.note = currentValue.toString();
                        break;
                    case "url":
                        r.url = currentValue.toString();
                        break;
                    case "res-type":
                        r.resType = currentValue.toString();
                        break;
                    case "con-type":
                        r.conType = currentValue.toString();
                        break;
                    case "ans-proc":
                        r.answerProcess = currentValue.toString();
                        break;
                    case "ptn-type":
                        r.patronType = currentValue.toString();
                        break;

                    case "lib-id":
                        r.libraryId = currentValue.toString();
                        break;
                    case "crt-date":
                        try {
                            r.date = new SimpleDateFormat("yyyyMMdd").parse(currentValue.toString());
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "results_cd":
                    case "solution":
                    case "bibl-isbn":
                    case "bibl-note":
                    case "reg-date":
                    case "lst-date":
                    case "referral":
                    case "contri":
                    case "reg-id":
                    case "sys-id":
                    case "file-num":
                    case "lib-name":
                        break;
                    default:
                        System.out.println(qName + "\t" + currentValue);
                        break;
                }
            }
            currentValue = null;
        }

        @Override
        public void endDocument() {
        }
    }
}
