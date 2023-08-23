package com.app.univchat.batch;

import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.List;

public class customDelimitedLineTokenizer extends DelimitedLineTokenizer {

    private static final char DEFAULT_QUOTE_CHARACTER = '"';

    @Override
    protected boolean isQuoteCharacter(char c) {
        return c == DEFAULT_QUOTE_CHARACTER;
    }

//    @Override
//    public DefaultFieldSet tokenize(String line) {
//        return (DefaultFieldSet) super.tokenize(line.replace("," + DEFAULT_QUOTE_CHARACTER, ""));
//    }
}
