package com.jiawen.community.entity;


//封装分页相关的信息
//利用对象 使服务端接受页面传入的信息 要传入 当前的页码
public class Page {

    private int current = 1; //当前页码
    private int limit = 10; //显示上线

    //数据总数 用来计算总页数
    private int rows;

    //查询路径 每一个页码按键都有一个路径
    //方便复用页面链接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        //如果用户乱写 current大于1才是有效
        if(current>=1){
            this.current = current;
        }

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100){
            this.limit = limit;
        }
    }

    public int getRows() {

        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行 通过当前页来算

    public int getOffset(){
        return (current-1)*limit;
    }


    //获取总页数
    public int getTotal() {
        if(rows%limit == 0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }

    //显示当前页前两页和后两页
    public int getFrom(){
        int from = current-2;
        if(from<1){
            from = 1;
        }

        return from;
    }

    public int getTo(){
        int to = current+2;

        //如果to大于总页数 就让to等于总页数
        if(to>getTotal()){
            to = getTotal();
        }
        return to;
    }


}
