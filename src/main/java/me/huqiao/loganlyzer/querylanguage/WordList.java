package me.huqiao.loganlyzer.querylanguage;

import java.util.Iterator;
import java.util.List;

public class WordList implements Iterator<String> {

    private int index;
    private List<String> wordList;

    public WordList(List<String> wordList) {
        this.wordList = wordList;
    }

    @Override
    public boolean hasNext() {
        if(wordList == null) {
            return false;
        }
        return index < wordList.size();
    }

    @Override
    public String next() {
        String next = wordList.get(index);
        index++;
        return next;
    }

    @Override
    public void remove() {
        wordList.remove(index);
    }
}
