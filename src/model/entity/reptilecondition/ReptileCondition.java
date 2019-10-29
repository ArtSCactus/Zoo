package model.entity.reptilecondition;

import java.util.Date;

/**Contains reptile detention conditions.
 *
 */
public class ReptileCondition {
    private int normalTemperature;

    private Date hibernationStart;

    private Date hibernationEnd;

    public ReptileCondition(int normalTemperature, Date hibernationStart, Date hibernationEnd) {
        this.normalTemperature = normalTemperature;
        this.hibernationStart = hibernationStart;
        this.hibernationEnd = hibernationEnd;
    }

    public int getNormalTemperature() {
        return normalTemperature;
    }

    public Date getHibernationStart() {
        return hibernationStart;
    }

    public Date getHibernationEnd() {
        return hibernationEnd;
    }

    public void setNormalTemperature(int normalTemperature) {
        this.normalTemperature = normalTemperature;
    }

    public void setHibernationStart(Date hibernationStart) {
        this.hibernationStart = hibernationStart;
    }

    public void setHibernationEnd(Date hibernationEnd) {
        this.hibernationEnd = hibernationEnd;
    }

}
