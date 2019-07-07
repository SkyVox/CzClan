package com.skydhs.czclan.clan.manager.format;

import com.skydhs.czclan.clan.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigInteger;
import java.util.*;

public class NumberFormat {
    private final BigInteger THOUSAND = BigInteger.valueOf(1000);
    private final NavigableMap<BigInteger, String> MAP = new TreeMap<>();
    private final List<String> NAMES = new LinkedList<>();

    public NumberFormat(FileConfiguration file) {
        for (String str : FileUtils.get().getSection(FileUtils.Files.CONFIG, "NumberFormat")) {
            NAMES.add(FileUtils.get().getString(FileUtils.Files.CONFIG, "NumberFormat." + str).get());
        }

        for (int i = 0; i < NAMES.size(); i++) {
            MAP.put(THOUSAND.pow(i+1), NAMES.get(i));
        }
    }

    public String format(BigInteger value) {
        Map.Entry<BigInteger, String> entry = MAP.floorEntry(value);
        if (entry == null) return value.toString();

        BigInteger key = entry.getKey();
        BigInteger divide = key.divide(THOUSAND);
        BigInteger divide1 = value.divide(divide);
        float f = divide1.floatValue() / 1000.0F;
        float rounded = ((int)(f * 100.0))/100.0F;

        if (rounded % 1 == 0) return ((int) rounded) + "" + entry.getValue();
        return rounded + "" + entry.getValue();
    }
}