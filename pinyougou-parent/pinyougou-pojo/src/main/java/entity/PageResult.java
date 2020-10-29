package entity;

import java.io.Serializable;
import java.util.List;

/**
 * <h3>pinyougou</h3>
 * <p>分页结果封装类</p>
 *
 * @author : 沈疴
 * @date : 2020-09-21 10:17
 **/
public class PageResult implements Serializable {
    private long total;//总记录数
    private List rows;//当前页记录

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
