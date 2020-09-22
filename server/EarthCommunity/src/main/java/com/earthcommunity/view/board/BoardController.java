package com.earthcommunity.view.board;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.earthcommunity.biz.board.BoardService;
import com.earthcommunity.biz.board.BoardVO;


@Controller
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	//검색 조건 목록 설정
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap(){
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("제목", "TITLE");
		conditionMap.put("내용", "CONTENT");
		return conditionMap;
	}
	
	
	/*
	 * //글등록
	 * 
	 * @RequestMapping("/insertBoard.do") public String insertBoard(BoardVO vo)
	 * throws IOException{ System.out.println("글 등록 처리");
	 * boardService.insertBoard(vo); return "getBoardList.do"; }
	 */
	
	//글등록
		@RequestMapping("/insertBoard.do")
		@ResponseBody
		public List<BoardVO> insertBoard(BoardVO vo) throws IOException{
			System.out.println("글 등록 처리");
			boardService.insertBoard(vo);
			List<BoardVO> boardList = boardService.getBoardList(vo);
			return boardList;
		}
	
	//글삭제
	@RequestMapping("/deleteBoard.do")
	public String deleteBoard(BoardVO vo) {
		boardService.deleteBoard(vo);
		return "getBoardList.do";
	}
	
	//글 상세 조회
	@RequestMapping("/getBoard.do")
	public String getBoard(BoardVO vo, Model model) {
		System.out.println("글 상세 조회 처리");
		model.addAttribute("board", boardService.getBoard(vo));
		return "getBoard.jsp";
	}
	
	//글 목록 조회
	@RequestMapping("/getBoardList.do")
	public String getBoardList( String keyword,	BoardVO vo, Model model) {
		
//		 if(vo.getSearchCondition() == null) vo.setSearchCondition("TITLE");
//		 if(vo.getSearchKeyword() == null) vo.setSearchKeyword("");
		 model.addAttribute("boardList", boardService.getBoardList(vo));
		return "getBoardList.jsp";
	}
	
	//글수정
	@RequestMapping("/updateBoard.do")
	public String updateBoard(@ModelAttribute("board")BoardVO vo ) {
		System.out.println("글 수정 처리");
		System.out.println("작성자 이름 : " + vo.getWriter());
		boardService.updateBoard(vo);
		return "getBoardList.do";
	}
	
	@RequestMapping(value = "/dataTransform")
	@ResponseBody
	public List<BoardVO> dataTransform(BoardVO vo){
//		vo.setSearchCondition("TITLE");
//		vo.setSearchKeyword("");
		List<BoardVO> boardList = boardService.getBoardList(vo);
		return boardList;
		

	}

}
