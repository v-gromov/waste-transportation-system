package one.vladimir.wts.BusinessLogic.POJO;

import java.util.List;

public class Group {

    public Integer koef;

    public List<Point> containedPoints;

    public Integer getKoef() {
        return koef;
    }

    public void setKoef(Integer koef) {
        this.koef = koef;
    }

    public List<Point> getContainedPoints() {
        return containedPoints;
    }

    public void setContainedPoints(List<Point> containedPoints) {
        this.containedPoints = containedPoints;
    }
}