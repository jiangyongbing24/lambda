package test;

import java.io.Serializable;
import java.util.Date;

public final class Period1 implements Serializable{

    private static final long serialVersionUID = 1L;
    private final Date start;
    private final Date end;

    public Period1(Date start, Date end) {

        if(null == start || null == end || start.after(end)){

            throw new IllegalArgumentException("请传入正确的时间区间!");
        }
        this.start = start;
        this.end = end;
    }

    public Date start(){

        return new Date(start.getTime());
    }

    public Date end(){

        return new Date(end.getTime());
    }

    @Override
    public String toString(){

        return "起始时间：" + start + " , 结束时间：" + end;
    }
}
