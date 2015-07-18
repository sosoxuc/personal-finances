package personal;

import java.util.Collection;

public class ListPage<T> {

    private Collection<T> list;

    private Integer count;

    public ListPage() {

    }

    public ListPage(Collection<T> list, Integer count) {
        this.list = list;
        this.count = count;
    }

    public Collection<T> getList() {
        return list;
    }

    public void setList(Collection<T> list) {
        this.list = list;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
