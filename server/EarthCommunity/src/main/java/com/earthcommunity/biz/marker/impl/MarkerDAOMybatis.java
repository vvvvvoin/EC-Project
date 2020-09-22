package com.earthcommunity.biz.marker.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.earthcommunity.biz.marker.MarkerDataVO;
import com.earthcommunity.biz.marker.MarkerImageVO;

@Repository
public class MarkerDAOMybatis {

	@Autowired
	private SqlSessionTemplate mybatis;

	public void insertMarker(MarkerDataVO vo) {
		mybatis.insert("MarkerDAO.insertMarker", vo);
	}
	
	public void insertImages(MarkerImageVO vo) {
		mybatis.insert("MarkerDAO.insertImages", vo);
	}
	
	public void uploadImages(MarkerImageVO vo) {
		mybatis.insert("MarkerDAO.insertMarkerImages", vo);
	}

	public void updateMarker(MarkerDataVO vo) {
		mybatis.update("MarkerDAO.updateMarker", vo);
	}

	public void deleteMarker(MarkerDataVO vo) {
		mybatis.delete("MarkerDAO.deleteMarker", vo);
	}
	
	public void deleteImages(MarkerImageVO vo) {
		mybatis.delete("MarkerDAO.deleteMarkerImage", vo);	
	}
	
	public Integer getLatestMarker(MarkerDataVO vo) {
		return mybatis.selectOne("MarkerDAO.getLatestMarker", vo);
	}
	
	public MarkerDataVO getMarker(MarkerDataVO vo) {
		return (MarkerDataVO) mybatis.selectOne("MarkerDAO.getMarker", vo);
	}
	
	public List<MarkerDataVO> getMarkerWithSearch(MarkerDataVO vo) {
		return mybatis.selectList("getMarkerWithSearch", vo);
	}

	public List<MarkerDataVO> getMarkerList(MarkerDataVO vo) {
		return mybatis.selectList("MarkerDAO.getMarkerList", vo);
	}

	public List<MarkerImageVO> getMarkerImageList(MarkerDataVO vo) {
		return mybatis.selectList("MarkerDAO.getMarkerImageList", vo);
	}


	

	
}
