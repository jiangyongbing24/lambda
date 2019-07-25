package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T,R> {

    R apply(T t);

    /**
     * 这个方法会返回一个新的Function
     * 示例：
     *  Function<Integer,Integer> function1 = x -> x * x;
     *  Function<Integer,Integer> function2 = x -> x + 2;
     *  Function<Integer,Integer> function3 = function1.compose(function2);
     *  function3.apply(3)执行返回的值等于25
     *
     * function3的apply会先执行function2的apply方法，得到返回结果，再把返回结果作为function1的apply方法的参数执行得到结果
     *
     * 上述例子的function1，function2，function3在compose方法中表现为
     * this 等于 function1
     * before 相当于 function2
     * (V v) -> apply(before.apply(v)) 相当于 function3
     *
     * 解释一下Function<? super V,? extends T> before中为什么入参类型是? super V，返回的结果的类型是? extends T
     * 因为 before.apply 的返回结果要作为 this.apply 的入参
     * 所以before.apply的返回结果类型一定等于this.apply接受的参数的类型，或者是它的子类型
     * 而为什么before.apply的入参是 ? super V
     * 因为对于返回的新的Function的apply方法来说，它接受一个V类型的参数之后，
     * 它先是移交给before.apply处理，得到结果后再把结果作为this.apply方法的入参得到结果，
     * 借助上述的function1，function2，function3 函数模型如下
     * function3.apply(V v){
     *     T t = function2.apply(v);
     *     return function1.apply(t);
     * }
     * function2.apply(E e){
     *     .....
     * }
     * function2.apply(E e)函数定义的入参为E类型
     * 但是再function3.apply中传递给function2.apply的参数v是V类型的
     * 所以E类型一定等于V类型或者是V类型的子类，否则是不可能把V类型的参数传递给E类型的
     * 所以E extends V，即V super E
     * */
    default <V> Function<V,R> compose(Function<? super V,? extends T> before){
        Objects.requireNonNull(before);
        //返回一个新的Function，先执行before.apply再执行this.apply
        return (V v) -> this.apply(before.apply(v));
    }

    default <V> Function<T,V> andThen(Function<? super R,? extends V> after){
        Objects.requireNonNull(after);
        return (T t) -> after.apply(this.apply(t));
    }

    static <T> Function<T,T> identity(){return t -> t;}
}