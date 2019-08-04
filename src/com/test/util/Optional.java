package com.test.util;

import com.test.util.function.Consumer;
import com.test.util.function.Function;
import com.test.util.function.Predicate;
import com.test.util.function.Supplier;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 可能包含或不包含非空值的容器对象
 * */
public final class Optional<T> {
    public static final Optional<?> EMPTY = new Optional();

    private final T value;

    private Optional(){this.value = null;}

    /**
     * 返回一个空的 Optional实例
     * */
    public static<T> Optional<T> empty(){
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    private Optional(T t){this.value = Objects.requireNonNull(t);}

    /**
     * 根据value返回一个Optional，如果value为空抛出异常
     * */
    public static <T> Optional<T> of(T value){return new Optional<>(value);}

    /**
     * 根据value返回一个Optional，如果value为空返回空
     * */
    public static <T> Optional<T> ofNullable(T value){return value == null ? empty() : of(value);}

    /**
     * 如果 Optional中有一个值，返回值，否则抛出 NoSuchElementException
     * */
    public T get(){
        if(value == null)
            throw new NoSuchElementException("No value present");
        return value;
    }

    /**
     * 判断value是否有值，为空返回false，不为空返回true
     * */
    public boolean isPresent(){ return value != null;}

    /**
     * 如果value不为空，action消费value
     * */
    public void ifPresent(Consumer<? super T> action){
        if(value != null)
            action.accept(value);
    }

    /**
     * 如果value为空，返回当前对象
     * 如果value不为空，谓词匹配为真，返回当前对象，否则返回一个value为空的Optional
     * */
    public Optional<T> filter(Predicate<? super T> predicate){
        Objects.requireNonNull(predicate);
        if(!isPresent())
            return this;
        return predicate.test(value) ? this : empty();
    }

    /**
     * 如果value为空，返回一个value为空的Optional
     * 如果value不为空，把this对象中的value根据mapper映射成一个新的value，
     * 然后根据这个新的value创建一个新的Optional，形成了this和新的Optional的一个映射关系
     * */
    public<U> Optional<U> map(Function<? super T,? extends U> mapper){
        Objects.requireNonNull(mapper);
        if(!isPresent())
            return empty();
        return Optional.ofNullable(mapper.apply(value));
    }

    /**
     * 如果value为空，返回一个value为空的Optional
     * 如果value不为空，把this对象中的value根据mapper映射成一个新的Optional
     * */
    public<U> Optional<U> flatMap(Function<? super T,? extends Optional<U>> mapper){
        Objects.requireNonNull(mapper);
        if(!isPresent())
            return empty();
        return Objects.requireNonNull(mapper.apply(value));
    }

    /**
     * 如果当前的value不为空，返回当前的value，否则返回other
     * */
    public T orElse(T other){return value != null ? value : other;}

    /**
     * 如果当前的value不为空，返回当前的value，否则返回供应商提供的数据
     * */
    public T orElseGet(Supplier<? extends T> other){return value != null ? value : Objects.requireNonNull(other).get();}

    /**
     * 如果当前的value不为空，返回当前的value，否则抛出供应商体提供的异常
     * */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier)throws X{
        if(isPresent())
            return value;
        else
            throw exceptionSupplier.get();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(!(obj instanceof Optional))
            return false;

        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value,other.value);
    }

    @Override
    public int hashCode(){return Objects.hashCode(value);}

    @Override
    public String toString(){
        return value != null ? String.format("Optional[%s%]",value) : "Optional.empty";
    }
}
