package com.etheller.warsmash.util;

// 定义一个名为StringBundle的接口，用于获取字符串资源
public interface StringBundle {
    // 根据键获取字符串的方法，如果找不到则可能返回null或默认值
    String getString(String string);

    // 获取字符串的方法，但是是区分大小写的
    String getStringCaseSensitive(final String key);

    // 定义一个StringBundle接口的空实现，用于在没有具体实现时提供默认行为
    StringBundle EMPTY = new StringBundle() {
        // 实现区分大小写的getStringCaseSensitive方法，直接返回传入的key
        @Override
        public String getStringCaseSensitive(final String key) {
            return key;
        }

        // 实现不区分大小写的getString方法，直接返回传入的string
        @Override
        public String getString(final String string) {
            return string;
        }
    };
}
