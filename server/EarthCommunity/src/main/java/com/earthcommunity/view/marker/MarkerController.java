package com.earthcommunity.view.marker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.earthcommunity.biz.board.BoardService;
import com.earthcommunity.biz.board.BoardVO;
import com.earthcommunity.biz.marker.MarkerDataVO;
import com.earthcommunity.biz.marker.MarkerImageVO;
import com.earthcommunity.biz.marker.MarkerService;
import com.earthcommunity.util.RandomStringBuilder;


@Controller
public class MarkerController {
	
	@Autowired
	private MarkerService markerService;
	
	
	@Resource(name = "uploadPath")
	String uploadPath;
	
	//마커등록 사진과 함께
	@RequestMapping("/insertMarker.do")
	@ResponseBody
	public MarkerDataVO insertMarker(@RequestPart(value = "image",  required = false) List<MultipartFile> fileList, MarkerDataVO vo) throws IOException{
		// Marker DB에 저장 후 저장된 값의 seq를 받아옴
		markerService.insertMarker(vo);
		int seq = markerService.getLatestMarker(vo); 
		
		if (fileList != null) {
			String randStr;
			MarkerImageVO imageVO = new MarkerImageVO();
			imageVO.setId(seq);
			for (MultipartFile file : fileList) {
				System.out.println(file.getOriginalFilename());
				randStr = new RandomStringBuilder().putLimitedChar(RandomStringBuilder.ALPHABET).setLength(32).build();
				String fileNmae = file.getOriginalFilename();
				file.transferTo(new File(uploadPath, randStr + fileNmae));
				
				// 저장된 파일의 주소와 파일명을 DB에 저장
				imageVO.setFile_address(uploadPath +"\\\\"+ randStr + fileNmae);
				markerService.insertImages(imageVO);
			}
		}
		vo.setSeq(seq);
		return markerService.getMarker(vo);
	}
	

	// 마커 클릭시 사진 받기
	@RequestMapping("/getImageAddress")
	@ResponseBody
	public List<MarkerImageVO> getImage(MarkerDataVO vo) throws Exception {
		List<MarkerImageVO> fileAddressList;
		List<MarkerImageVO> returnFileList = new ArrayList<MarkerImageVO>();

		System.out.println("전달받은 객체의 seq = " + vo.getSeq());
		fileAddressList = markerService.getMarkerImageList(vo);
		
		for(int i = 0; i < fileAddressList.size(); i++) {
			MarkerImageVO temp = fileAddressList.get(i);
			temp.setFile_address(temp.getFile_address().replace(uploadPath+"\\\\" , ""));
			returnFileList.add(temp);
		}
		
		return returnFileList;
//		for (MarkerImageVO imageVO : fileAddressList) {
//			System.out.println(imageVO.getFile_address());
//			fileName = imageVO.getFile_address().replace(uploadPath+"\\\\" , "");
//			System.out.println(fileName);
//			return "redirect:markerImg/" + fileName;
//		}
//		return "";
	}
	
	//이미지 업로드를 위해 테스트하기 위해 만듬
	@RequestMapping("/imageUpload")
	public String uploadImage(@RequestParam("image") MultipartFile file ) {
		System.out.println(file.getOriginalFilename());
		
		try {
			 String fileNmae = file.getOriginalFilename();
			 file.transferTo(new File(uploadPath, fileNmae));
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	//마커삭제
	@RequestMapping("/deleteMarker.do")
	public String deleteBoard(MarkerDataVO vo) {
		List<MarkerImageVO> fileAddressList;
		fileAddressList = markerService.getMarkerImageList(vo);
		for (MarkerImageVO imageVO : fileAddressList) {
			System.out.println(imageVO.getFile_address());
			File deleteFile = new File(imageVO.getFile_address());
			if(deleteFile.exists()) {
				deleteFile.delete();
				System.out.println("파일을 삭제했습니다.");
			}else {
				System.out.println("삭제하려는 파일이 존재하지 않습니다.");
			}
		}
		MarkerImageVO imageVO = new MarkerImageVO();
		imageVO.setId(vo.getSeq());
		markerService.deleteImages(imageVO);
		markerService.deleteMarker(vo);
		return "markerDataTransform";
	}
	
	//글 상세 조회
	@RequestMapping("/getMarker.do")
	@ResponseBody
	public MarkerDataVO getMarker(MarkerDataVO vo) {
		System.out.println("글 상세 조회 처리");
		return markerService.getMarker(vo);
	}
	
	//검색데이터 조회
	@RequestMapping("/getMarkerWithSearch.do")
	@ResponseBody
	public List<MarkerDataVO> getMarkerWithSearch(@RequestParam(value = "searchString", defaultValue = "", required = false)String searchString, MarkerDataVO vo) {
		System.out.println("검색데이터 조회");
		vo.setSearchString(searchString);
		return markerService.getMarkerWithSearch(vo);
	}
	
	//글 목록 조회
	@RequestMapping("/getMarkerList.do")
	public String getMarkerList( String keyword,	MarkerDataVO vo, Model model) {
		 model.addAttribute("markerList", markerService.getMarkerList(vo));
		return "getBoardList.jsp";
	}
	
	//글수정
	@RequestMapping("/updateMarker.do")
	public MarkerDataVO updateBoard(@ModelAttribute("board")MarkerDataVO vo ) {
		markerService.updateMarker(vo);
		return markerService.getMarker(vo);
	}
	
	@RequestMapping(value = "/markerDataTransform")
	@ResponseBody
	public List<MarkerDataVO> dataTransform(MarkerDataVO vo){
		List<MarkerDataVO> markerList = markerService.getMarkerList(vo);
		System.out.println(markerList.toString());
		return markerList;
		

	}

}
