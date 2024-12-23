package com.etheller.warsmash.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.etheller.warsmash.datasources.DataSource;
public class WorldEditStrings implements StringBundle {
    // 用于存储字符串资源的 ResourceBundle 对象
    private ResourceBundle bundle;
    // 用于存储游戏字符串资源的 ResourceBundle 对象
    private ResourceBundle bundlegs;

    /**
     * 构造函数，初始化 WorldEditStrings 对象，并从指定的数据源加载字符串资源。
     *
     * @param dataSource 数据源对象，用于获取字符串资源文件。
     */
    public WorldEditStrings(final DataSource dataSource) {
        if (dataSource.has("UI\\WorldEditStrings.txt")) {
            try (InputStream fis = dataSource.getResourceAsStream("UI\\WorldEditStrings.txt");
                 InputStreamReader reader = new InputStreamReader(fis, "utf-8")) {
                // 加载 WorldEditStrings.txt 文件中的字符串资源
                this.bundle = new PropertyResourceBundle(reader);
            } catch (final IOException e) {
                // 捕获并重新抛出异常，以确保资源加载失败时程序能够正确处理
                throw new RuntimeException(e);
            }
        }
        try (InputStream fis = dataSource.getResourceAsStream("UI\\WorldEditGameStrings.txt");
             InputStreamReader reader = new InputStreamReader(fis, "utf-8")) {
            // 加载 WorldEditGameStrings.txt 文件中的字符串资源
            this.bundlegs = new PropertyResourceBundle(reader);
        } catch (final IOException e) {
            // 捕获并重新抛出异常，以确保资源加载失败时程序能够正确处理
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定字符串的本地化版本。如果字符串以 "WESTRING" 开头，则会递归调用 internalGetString 方法获取实际的字符串值。
     *
     * @param string 要获取的字符串。
     * @return 本地化后的字符串，如果未找到则返回原始字符串。
     * @throws MissingResourceException 如果资源包中未找到指定的字符串。
     */
    @Override
    public String getString(String string) {
        try {
            while (string.toUpperCase().startsWith("WESTRING")) {
                // 如果字符串以 "WESTRING" 开头，则递归获取实际的字符串值
                string = internalGetString(string);
            }
            return string;
        } catch (final MissingResourceException exc) {
            try {
                // 如果在主资源包中未找到字符串，则尝试从游戏资源包中获取
                return this.bundlegs.getString(string.toUpperCase());
            } catch (final MissingResourceException exc2) {
                // 如果在游戏资源包中也未找到字符串，则返回原始字符串
                return string;
            }
        }
    }

    /**
     * 内部方法，用于获取指定字符串的实际值。如果主资源包中存在该字符串，则返回主资源包中的值；否则返回游戏资源包中的值。
     *
     * @param key 要获取的字符串的键。
     * @return 实际的字符串值。
     * @throws MissingResourceException 如果资源包中未找到指定的字符串。
     */
    private String internalGetString(final String key) {
        if (this.bundle == null) {
            // 如果主资源包未加载，则直接从游戏资源包中获取字符串
            return this.bundlegs.getString(key.toUpperCase());
        }
        try {
            String string = this.bundle.getString(key.toUpperCase());
            if ((string.charAt(0) == '"') && (string.length() >= 2) && (string.charAt(string.length() - 1) == '"')) {
                // 如果字符串以双引号开头和结尾，则去除双引号
                string = string.substring(1, string.length() - 1);
            }
            return string;
        } catch (final MissingResourceException exc) {
            // 如果在主资源包中未找到字符串，则从游戏资源包中获取
            return this.bundlegs.getString(key.toUpperCase());
        }
    }

    /**
     * 获取指定字符串的本地化版本，不区分大小写。如果主资源包中存在该字符串，则返回主资源包中的值；否则返回游戏资源包中的值。
     *
     * @param key 要获取的字符串的键。
     * @return 本地化后的字符串，如果未找到则返回原始字符串。
     * @throws MissingResourceException 如果资源包中未找到指定的字符串。
     */
    @Override
    public String getStringCaseSensitive(final String key) {
        if (this.bundle == null) {
            // 如果主资源包未加载，则直接从游戏资源包中获取字符串
            return this.bundlegs.getString(key);
        }
        try {
            // 尝试从主资源包中获取字符串
            return this.bundle.getString(key);
        } catch (final MissingResourceException exc) {
            // 如果在主资源包中未找到字符串，则从游戏资源包中获取
            return this.bundlegs.getString(key);
        }
    }
}
