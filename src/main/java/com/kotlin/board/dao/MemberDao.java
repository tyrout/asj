package com.kotlin.board.dao;

import com.kotlin.board.dto.MemberDto;

public interface MemberDao {
	//join method
	public void memberInsert(MemberDto member);
	
	//getting encryptized password
	public String getEncPwd(String id);
	
	//bring memberinfo after login success 
	public MemberDto getMemeberInfo(String id);
	
	public int countId(String id);
}
