package com.kotlin.board.dto;

import lombok.Data;

//dto 를 만들 때는 lombock 을 사용합니다. 
@Data
public class MemberDto {
	private String m_id;
	private String m_pwd;
	private String m_name;
	private String m_birth;
	private String m_addr;
	private String m_phone;
	private int m_point;
	private String g_name;
	//point 는 g_name 이 들어갔다는 겁니다. 이 g_name 은 view의 m_info table에서 확인할 수 있습니다. 
}
