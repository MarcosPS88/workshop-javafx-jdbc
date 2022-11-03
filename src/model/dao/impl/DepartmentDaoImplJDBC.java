package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoImplJDBC implements DepartmentDao {

	private Connection conn;
	
	
	
	public DepartmentDaoImplJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		
		String sql = "INSERT INTO department (Name) Values (?)";
		
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int rows = st.executeUpdate();
			conn.commit();
			
			if(rows > 0) {
				
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					obj.setId(rs.getInt(1));
				}
				
				DB.closeResultSet(rs);
				System.out.println("Rows Affected: " + rows );
				System.out.println("Addeded Id: " + obj.getId() );
				
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new DbException("Error : " + e.getMessage());
			
		}finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Department obj) {
		String sql = "UPDATE department SET Name = ? WHERE Id = ?";
		
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql);
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			int rows = st.executeUpdate();
			conn.commit();
			
				System.out.println("Rows Affected: " + rows );
				System.out.println("Data updated: " + obj );
				
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new DbException("Error : " + e.getMessage());
			
		}finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		String sql = "DELETE FROM department WHERE Id = ?";
		PreparedStatement st = null;
		
		try {
			
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			
			conn.commit();

			System.out.println("Data deleted!");
			
			if(rows == 0) {
				throw new DbException("Invalid Id!");
			}
			
			
		} catch (SQLException e) {
			
			throw new DbException("Error: " + e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			
		}
		
	}

	@Override
	public Department findById(Integer id) {
		
		String sql = "SELECT * FROM department WHERE Id = ?";
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				
				
				Department dep = instantiateDepartment(rs); 				
				return dep;
				
			}
			return null;
		} catch (SQLException e) {

			throw new DbException(e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			
		}
		
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		
		return dep;

	}

	@Override
	public List<Department> findAll() {
		String sql = "SELECT * FROM department";
		
		
		List<Department> listDep = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			while(rs.next()) {
								
				Department dep = instantiateDepartment(rs); 				
				
				listDep.add(dep);
				
			}
			return listDep;
		} catch (SQLException e) {

			throw new DbException(e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			
		}
	}

}
