package com.etheller.warsmash.util;
public final class War3ID implements Comparable<War3ID> {
    // 定义一个常量，表示没有War3ID
    public static final War3ID NONE = new War3ID(0);
    // 存储War3ID的整数值
    private final int value;

    /**
     * 构造函数，用于创建一个新的War3ID实例
     *
     * @param value War3ID的整数值
     */
    public War3ID(final int value) {
        this.value = value;
    }

    /**
     * 获取War3ID的整数值
     *
     * @return War3ID的整数值
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 从字符串创建一个War3ID实例
     *
     * @param idString 表示War3ID的字符串
     * @return 对应的War3ID实例
     * @throws IllegalArgumentException 如果字符串长度不是4
     */
    public static War3ID fromString(String idString) {
        if (idString.length() == 3) {
            System.err.println("Loaded custom data for the ability CURSE whose MetaData field, 'Crs', is the only 3 letter War3ID in the game. This might cause unexpected errors, so watch your % chance to miss in custom curse abilities carefully.");
            // 在字符串末尾添加一个空字符，使其长度变为4
            idString += '\0';
        }
        if (idString.length() != 4) {
            // 如果字符串长度不是4，则抛出异常
            throw new IllegalArgumentException("A War3ID must be 4 ascii characters in length (got " + idString.length() + ") '" + idString + "'");
        }
        // 将字符串转换为整数，并创建一个新的War3ID实例
        return new War3ID(RawcodeUtils.toInt(idString));
    }

    /**
     * 将War3ID转换为字符串表示
     *
     * @return War3ID的字符串表示
     */
    public String asStringValue() {
        // 将整数转换为字符串
        String string = RawcodeUtils.toString(this.value);
        // 如果字符串的第4个字符是空字符或空格，并且第3个字符不是空字符，则截取前3个字符
        if (((string.charAt(3) == '\0') || (string.charAt(3) == ' ')) && (string.charAt(2) != '\0')) {
            string = string.substring(0, 3);
        }
        return string;
    }

    /**
     * 在指定位置设置War3ID的字符
     *
     * @param index 要设置的字符的位置
     * @param c     要设置的字符
     * @return 新的War3ID实例，其中指定位置的字符已被替换
     */
    public War3ID set(final int index, final char c) {
        // 获取当前War3ID的字符串表示
        final String asStringValue = asStringValue();
        // 创建一个新的字符串，将指定位置的字符替换为新字符
        String result = asStringValue.substring(0, index);
        result += c;
        // 拼接剩余部分的字符串
        result += asStringValue.substring(index + 1, asStringValue.length());
        // 从新字符串创建一个新的War3ID实例并返回
        return War3ID.fromString(result);
    }

    /**
     * 获取War3ID在指定位置的字符
     *
     * @param index 要获取的字符的位置
     * @return 指定位置的字符
     */
    public char charAt(final int index) {
        // 将整数右移(3-index)*8位，然后与0xFF进行与操作，得到指定位置的字符
        return (char) ((this.value >>> ((3 - index) * 8)) & 0xFF);
    }

    /**
     * 返回War3ID的字符串表示
     *
     * @return War3ID的字符串表示
     */
    @Override
    public String toString() {
        return asStringValue();
    }

    /**
     * 计算War3ID的哈希码
     *
     * @return War3ID的哈希码
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // 将War3ID的整数值乘以31，然后加上结果
        result = (prime * result) + this.value;
        return result;
    }

    /**
     * 比较两个War3ID实例是否相等
     *
     * @param obj 要比较的对象
     * @return 如果两个War3ID实例相等，则返回true，否则返回false
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final War3ID other = (War3ID) obj;
        // 如果两个War3ID实例的整数值不相等，则返回false
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    /**
     * 比较当前War3ID实例与另一个War3ID实例的大小
     *
     * @param o 要比较的War3ID实例
     * @return 如果当前实例小于、等于或大于另一个实例，则分别返回-1、0或1
     */
    @Override
    public int compareTo(final War3ID o) {
        // 使用Integer类的compare方法比较两个War3ID实例的整数值
        return Integer.compare(this.value, o.value);
    }
}
