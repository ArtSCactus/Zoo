package util;

import javafx.scene.control.TableView;
import model.entity.animals.Bird;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ObjectFactory {

    public static ArrayList<Bird> buildBirdObjects(TableView table,ResultSet requestResult) throws SQLException {
        ArrayList<Bird> objectList = new ArrayList<>();
        while (requestResult.next()){
            objectList.add(new Bird(requestResult.getInt("unique_number"),
                    requestResult.getString("name"),
                    requestResult.getDate("birthday", new GregorianCalendar()),
                    requestResult.getString("sex")));
        }
        return objectList;
    }
}
