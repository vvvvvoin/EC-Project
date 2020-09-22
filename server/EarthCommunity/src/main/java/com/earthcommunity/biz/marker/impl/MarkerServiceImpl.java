package com.earthcommunity.biz.marker.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.earthcommunity.biz.board.BoardService;
import com.earthcommunity.biz.board.BoardVO;
import com.earthcommunity.biz.marker.MarkerDataVO;
import com.earthcommunity.biz.marker.MarkerImageVO;
import com.earthcommunity.biz.marker.MarkerService;


@Service("markerService")
public class MarkerServiceImpl implements MarkerService{
	@Autowired
	private MarkerDAOMybatis markerDAO;
	
	public void insertMarker(MarkerDataVO vo) {
		markerDAO.insertMarker(vo);
	}
	
	public void insertImages(MarkerImageVO vo) {
		markerDAO.insertImages(vo);
	}
	
	public void updateMarker(MarkerDataVO vo) {
		markerDAO.updateMarker(vo);		
	}

	public void deleteMarker(MarkerDataVO vo) {
		markerDAO.deleteMarker(vo);
	}
	
	public void deleteImages(MarkerImageVO vo) {
		markerDAO.deleteImages(vo);
	}
	public Integer getLatestMarker(MarkerDataVO vo) {
		return markerDAO.getLatestMarker(vo);
	}
	public MarkerDataVO getMarker(MarkerDataVO vo) {
		return markerDAO.getMarker(vo);
	}
	
	public List<MarkerDataVO> getMarkerWithSearch(MarkerDataVO vo) {
		return markerDAO.getMarkerWithSearch(vo);
	}
	
	public List<MarkerDataVO> getMarkerList(MarkerDataVO vo) {
		return markerDAO.getMarkerList(vo);
	}
		
	public List<MarkerImageVO> getMarkerImageList(MarkerDataVO vo) {
		return markerDAO.getMarkerImageList(vo);
	}

	
	


	

	



}
