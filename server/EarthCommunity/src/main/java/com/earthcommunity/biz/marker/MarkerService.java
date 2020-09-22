package com.earthcommunity.biz.marker;

import java.util.List;


public interface MarkerService {
	void insertMarker(MarkerDataVO vo);
	void insertImages(MarkerImageVO vo);

	void updateMarker(MarkerDataVO vo);

	void deleteMarker(MarkerDataVO vo);
	void deleteImages(MarkerImageVO vo);

	MarkerDataVO getMarker(MarkerDataVO vo);
	Integer getLatestMarker(MarkerDataVO vo);
	List<MarkerDataVO> getMarkerWithSearch(MarkerDataVO vo);

	List<MarkerDataVO> getMarkerList(MarkerDataVO vo);
	List<MarkerImageVO> getMarkerImageList(MarkerDataVO vo);
}
