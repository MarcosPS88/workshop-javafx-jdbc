package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoImplJDBC;
import model.dao.impl.SellerDaoImplJDBC;

public class DaoFactory {

	public static SellerDaoImplJDBC createSellerDao() {
		return new SellerDaoImplJDBC(DB.getConnection());
	}

	public static DepartmentDaoImplJDBC createDepartmentDao() {
		return new DepartmentDaoImplJDBC(DB.getConnection());
	}
}
