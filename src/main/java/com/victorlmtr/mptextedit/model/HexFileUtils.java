package com.victorlmtr.mptextedit.model;

import java.util.HashMap;
import java.util.Map;

public class HexFileUtils {

    public static final Map<Character, Integer> marioPartyMapping = new HashMap<>();
    public static final Map<Integer, Character> reverseMarioPartyMapping = new HashMap<>();
    public static final Map<String, String> Mp4PlayableCharactersMapping = new HashMap<>();

    static {
        marioPartyMapping.put(' ', 0x10);
        marioPartyMapping.put('…', 0x85);
        marioPartyMapping.put(',', 0x82);
        marioPartyMapping.put('œ', 0xE8);
        marioPartyMapping.put('è', 0xD5);
        marioPartyMapping.put('é', 0xD6);
        marioPartyMapping.put('ê', 0xD7);
        marioPartyMapping.put('î', 0xD9);
        marioPartyMapping.put('à', 0xD1);
        marioPartyMapping.put('â', 0xD2);
        marioPartyMapping.put('ç', 0xD4);
        marioPartyMapping.put('!', 0xC2);
        marioPartyMapping.put('?', 0xC3);
        marioPartyMapping.put('\'', 0x5C);
        marioPartyMapping.put('\"', 0x5B);
        marioPartyMapping.put('(', 0x5D);
        marioPartyMapping.put(')', 0x5E);
        marioPartyMapping.put('-', 0x3D);
        marioPartyMapping.put('°', 0xBE);
        for (char c = 'A'; c <= 'Z'; c++) {
            marioPartyMapping.put(c, (int) c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            marioPartyMapping.put(c, (int) c);
        }
        marioPartyMapping.forEach((k, v) -> reverseMarioPartyMapping.put(v, k));
    }

    public static char getCharacterFromByte(byte b) {
        return reverseMarioPartyMapping.getOrDefault((int) b & 0xFF, '*');
    }
    static {
        Mp4PlayableCharactersMapping.put("Mario", "4D 61 72 69 6F");
        Mp4PlayableCharactersMapping.put("Luigi", "4C 75 69 67 69");
        Mp4PlayableCharactersMapping.put("Peach", "50 65 61 63 68");
        Mp4PlayableCharactersMapping.put("Yoshi", "59 6F 73 68 69");
        Mp4PlayableCharactersMapping.put("Wario", "57 61 72 69 6F");
        Mp4PlayableCharactersMapping.put("DK", "44 4B");
        Mp4PlayableCharactersMapping.put("Daisy", "44 61 69 73 79");
        Mp4PlayableCharactersMapping.put("Waluigi", "57 61 6C 75 69 67 69");

    }

}
