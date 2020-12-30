package com.kotlin.board.dao;

import java.util.*;

import com.kotlin.board.dto.BfileDto;
import com.kotlin.board.dto.BoardDto;
import com.kotlin.board.dto.MemberDto;
import com.kotlin.board.dto.ReplyDto;

public interface BoardDao {
	
	//게시글 목록 구하기 메소드
	//db꺼내올 때 한 행이 하나의 dto 가 되지요. 
	public List<BoardDto> getList(int pageNum);
	
	//게시글 전체 개수 구하기 
	public int getBoardCnt();
	
	//게시글 저장 메소드 
	public boolean boardInsert(BoardDto board);
	
	//게시글 파일 저장 메소드
	public boolean fileInsert(Map<String, String> fmap);
	// 디비에 저장하는 게 아니라, file 은 업로드 받아서, 해당 서버에 업로드 폴더를 만들어서 파일을 저장할 거구요 
	// 그 때 저장할 때 파일을 시간값으로 바꿔서 저장할 겁니다 (sysname) 그리고 디비에는 실제 original name 을 저장할 겁니다. 
	// 이 두 가ㅣㅈㅣ 저장을 위해 map 을 사용합니다.
	
	//게시글 내용 (렠드 1행 가져오기)
	public BoardDto getContents(Integer bnum);
	
	//게시글 해당 파일 목록 가져오기
	public List<BfileDto> getBfList(Integer bnum);
	
	//게시글 해당 댓글 목록 가져오기 
	public List<ReplyDto> getReplyList(Integer bnum);
	
	//댓글 저장 메소드  - json 에서 받을 거구요, 
	public boolean replyInsert(ReplyDto reply);

	public void viewUpdate(Integer bnum);

	public void collectPoint(String id);
	
	// 파일 원래 이름 구하기
	public String getOriName(String sysName);
	
	public void updatePostProc(BoardDto board);
	
	public void deleteAllComments(Integer postNum);
	
	public void deleteFileUploaded(Integer postNum);
	
	public void deleteBoardPost(Integer postNum);
	
	public void deleteFileBySysName(String sysname);
}

