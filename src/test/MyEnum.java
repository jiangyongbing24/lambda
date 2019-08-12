package test;

public enum MyEnum{
    one,two,three;

    public static void main(String[] args) {
        System.out.println(MyEnum.one.ordinal());
        System.out.println(MyEnum.two.ordinal());
        System.out.println(MyEnum.three.ordinal());
    }
}
