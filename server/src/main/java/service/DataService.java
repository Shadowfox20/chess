package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class DataService {
    public static void Clear(DataAccess dataAccess) throws DataAccessException {
        dataAccess.clear();
    }
}
