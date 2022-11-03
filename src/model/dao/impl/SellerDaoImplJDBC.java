package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoImplJDBC implements SellerDao {

	private Connection conn;
	
	public SellerDaoImplJDBC(Connection conn){
		
		this.conn =conn;
	}
	
	@Override
	public void insert(Seller obj) {
		
		PreparedStatement st = null;
		
		String sql = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) "
				+ "Values(?,?,?,?,?) ";
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					
					obj.setId(id);
					
					DB.closeResultSet(rs);
					
				}else {
					throw new DbException("Unexpected error! No rows affected!");
				}
				
			}

			System.out.println("Rows Affected: " + rowsAffected);
			
			conn.commit();
		} catch (SQLException e) {
			
			try {
				
				conn.rollback();
				
			} catch (SQLException e1) {
				
				throw new DbException("Insert Erro: " + e.getMessage());
			}
			
			throw new DbException("Insert Erro: " + e.getMessage());
			
		}finally {
			DB.closeStatement(st);
			
		}
		
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		String sql = "UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
				+ "WHERE Id = ?";
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			int rowsAffected = st.executeUpdate();
			
			System.out.println("Rows Affected: " + rowsAffected);
			
			conn.commit();
		} catch (SQLException e) {
			
			try {
				
				conn.rollback();
				
			} catch (SQLException e1) {
				
				throw new DbException("Insert Erro: " + e.getMessage());
			}
			
			throw new DbException("Insert Erro: " + e.getMessage());
			
		}finally {
			DB.closeStatement(st);
			
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		
		String sql = "DELETE FROM seller WHERE Id = ?";
		PreparedStatement st = null;
		
		try {
			
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			
			conn.commit();
			
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
	public Seller findById(Integer id) {

		String sql = "SELECT seller.*,department.Name as DepName "
				+ "FROM seller INNER JOIN department "
				+ "ON seller.DepartmentId = department.ID "
				+ "WHERE seller.Id = ?";
				
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
		
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				
				Department dep = instatiateDepartment(rs);				
				
				Seller sel = instantiateSeller(rs, dep);
				
				return sel;
							
			}
			return null;
			
		} catch (SQLException e) {

			throw new DbException(e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller sel = new Seller();
		
		sel.setId(rs.getInt("Id"));
		sel.setName(rs.getString("Name"));
		sel.setEmail(rs.getString("Email"));
		sel.setBaseSalary(rs.getDouble("BaseSalary"));
		
		Timestamp tsmt = rs.getTimestamp("BirthDate");
		
		if(tsmt != null) {
			sel.setBirthDate(new Date(tsmt.getTime()));
		}
		
		sel.setDepartment(dep);
		return sel;
	}

	private Department instatiateDepartment(ResultSet rs) throws SQLException {
		
		Department dep = new Department();
		
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));

		return dep;
	}

	@Override
	public List<Seller> findAll() {
		String sql = "SELECT seller.*,department.Name as DepName "
				+ "FROM seller INNER JOIN department "
				+ "ON seller.DepartmentId = department.ID "
				+ "ORDER BY Name";
				
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
		
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();

			List<Seller> listSeller = new ArrayList<>();
			
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					
					dep = instatiateDepartment(rs);			
					
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				
				Seller sel = instantiateSeller(rs, dep);
				
				listSeller.add(sel);
				
			}

			return listSeller;
			
		} catch (SQLException e) {

			throw new DbException(e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	
	}

	@Override
	public List<Seller> findByDepartement(Department department) {
		
		
		String sql = "SELECT seller.*,department.Name as DepName "
				+ "FROM seller INNER JOIN department "
				+ "ON seller.DepartmentId = department.ID "
				+ "WHERE DepartmentId = ? "
				+ "ORDER BY Name";
				
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
		
			st = conn.prepareStatement(sql);
			st.setInt(1, department.getId());
			rs = st.executeQuery();

			List<Seller> listSeller = new ArrayList<>();
			
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					
					dep = instatiateDepartment(rs);			
					
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				
				Seller sel = instantiateSeller(rs, dep);
				
				listSeller.add(sel);
				
			}

			return listSeller;
			
		} catch (SQLException e) {

			throw new DbException(e.getMessage());
			
		}finally {
			
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	
}
